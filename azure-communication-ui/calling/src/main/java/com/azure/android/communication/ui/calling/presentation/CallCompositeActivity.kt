// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation

import android.annotation.SuppressLint
import android.app.PictureInPictureParams
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.util.LayoutDirection
import android.util.Rational
import android.view.MenuItem
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.calling.CallCompositeException
import com.azure.android.communication.ui.calling.implementation.R
import com.azure.android.communication.ui.calling.CallCompositeInstanceManager
import com.azure.android.communication.ui.calling.models.CallCompositeSupportedLocale
import com.azure.android.communication.ui.calling.models.CallCompositeSupportedScreenOrientation
import com.azure.android.communication.ui.calling.models.CallCompositeUserReportedIssueEvent
import com.azure.android.communication.ui.calling.models.setCallTimer
import com.azure.android.communication.ui.calling.onExit
import com.azure.android.communication.ui.calling.presentation.fragment.calling.CallingFragment
import com.azure.android.communication.ui.calling.presentation.fragment.calling.support.SupportView
import com.azure.android.communication.ui.calling.presentation.fragment.calling.support.SupportViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.setup.SetupFragment
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.action.NavigationAction
import com.azure.android.communication.ui.calling.redux.action.PipAction
import com.azure.android.communication.ui.calling.redux.state.NavigationStatus
import com.azure.android.communication.ui.calling.redux.state.VisibilityStatus
import com.azure.android.communication.ui.calling.utilities.collect
import com.azure.android.communication.ui.calling.utilities.isAndroidTV
import com.azure.android.communication.ui.calling.utilities.launchAll
import com.microsoft.fluentui.util.activity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.util.Locale

internal open class CallCompositeActivity : AppCompatActivity() {
    private val diContainerHolder: CallCompositeActivityViewModel by viewModels {
        CallCompositeActivityViewModelFactory(
            this@CallCompositeActivity.application,
        )
    }

    private val container by lazy { diContainerHolder.container }

    private val navigationRouter get() = container.navigationRouter
    private val store get() = container.appStore
    private val configuration get() = container.configuration
    private val supportView by lazy { SupportView(this) }
    private val supportViewModel by lazy {
        SupportViewModel(store::dispatch, this::forwardSupportEventToUser).also {
            it.init(store.getCurrentState().navigationState)
        }
    }
    private val localOptions get() = configuration.callCompositeLocalOptions
    private val permissionManager get() = container.permissionManager
    private val audioSessionManager get() = container.audioSessionManager
    private val audioFocusManager get() = container.audioFocusManager
    private val audioModeManager get() = container.audioModeManager
    private val lifecycleManager get() = container.lifecycleManager
    private val multitaskingManager get() = container.multitaskingManager
    private val errorHandler get() = container.errorHandler
    private val callStateHandler get() = container.callStateHandler
    private val remoteParticipantJoinedHandler get() = container.remoteParticipantHandler
    private val notificationService get() = container.notificationService
    private val callingMiddlewareActionHandler get() = container.callingMiddlewareActionHandler
    private val videoViewManager get() = container.videoViewManager
    private val instanceId get() = intent.getIntExtra(KEY_INSTANCE_ID, -1)
    private val callHistoryService get() = container.callHistoryService
    private val logger get() = container.logger
    private val compositeManager get() = container.compositeExitManager
    private val compositeDataModel get() = container.captionsDataManager
    private val callDurationManager get() = container.callDurationManager

    private lateinit var visibilityStatusFlow: MutableStateFlow<VisibilityStatus>

    override fun onCreate(savedInstanceState: Bundle?) {
        // Before super, we'll set up the DI injector and check the PiP state
        try {
            diContainerHolder.instanceId = instanceId
            diContainerHolder.container.callCompositeActivityWeakReference = WeakReference(this)
        } catch (invalidIDException: CallCompositeException) {
            super.onCreate(savedInstanceState)
            finish() // Container has vanished (probably due to process death); we cannot continue
            return
        }

        val listeningPair = Pair(lifecycleScope, store)
        visibilityStatusFlow = MutableStateFlow(store.getCurrentState().visibilityState.status)

        // Call super
        super.onCreate(savedInstanceState)
        syncPipMode()
        // Inflate everything else
        volumeControlStream = AudioManager.STREAM_VOICE_CALL

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        configureLocalization()
        setStatusBarColor()
        setNavigationBarColor()

        configuration.themeConfig?.let {
            theme.applyStyle(it, true)
        }

        configuration.callScreenOptions?.headerOptions?.timer?.setCallTimer(callDurationManager)

        setContentView(R.layout.azure_communication_ui_calling_activity_call_composite)

        permissionManager.start(
            this,
            getAudioPermissionLauncher(),
            getCameraPermissionLauncher(),
            lifecycleScope
        )

        audioSessionManager.onCreate(savedInstanceState)

        lifecycleScope.launchWhenStarted {
            navigationRouter.getNavigationStateFlow().collect { onNavigationStateChange(it) }
        }

        lifecycleScope.launchAll(
            navigationRouter::start,
            audioFocusManager::start,
            audioModeManager::start,
            errorHandler::start,
            remoteParticipantJoinedHandler::start,
            { container.accessibilityManager.start(this) },
            {
                supportView.start(supportViewModel, this@CallCompositeActivity)
            },
            {
                visibilityStatusFlow.collect {
                    if (it == VisibilityStatus.HIDE_REQUESTED) {
                        hide()
                        store.dispatch(PipAction.HideEntered())
                    }
                }
            }
        )

        // Probably can follow the above pattern now with function declarations
        multitaskingManager.start(lifecycleScope)
        notificationService.start(lifecycleScope, instanceId)
        callHistoryService.start(lifecycleScope)
        callStateHandler.start(lifecycleScope)
        compositeDataModel.start(lifecycleScope)

        listeningPair.collect {
            supportViewModel.update(it.navigationState)
            visibilityStatusFlow.value = it.visibilityState.status
        }
    }

    override fun onStart() {
        super.onStart()
        audioSessionManager.onStart(this)
        lifecycleScope.launch { lifecycleManager.resume() }
        permissionManager.setCameraPermissionsState()
        permissionManager.setAudioPermissionsState()
    }

    override fun onResume() {
        super.onResume()

        // when PiP is closed, Activity is not re-created, so onCreate is not called,
        // need to call initPipMode from onResume as well
        initPipMode()
    }

    private fun initPipMode() {
        if (configuration.enableSystemPiPWhenMultitasking &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N &&
            activity?.packageManager?.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE) == true
        ) {
            store.dispatch(
                if (isInPictureInPictureMode) PipAction.PipModeEntered()
                else PipAction.ShowNormalEntered()
            )
        }
    }

    override fun onStop() {
        super.onStop()
        if (!isChangingConfigurations) {
            lifecycleScope.launch {
                lifecycleManager.pause()
            }
        }
    }

    override fun onDestroy() {
        // Covers edge case where Android tries to recreate call activity after process death
        // (e.g. due to revoked permission).
        // If no configs are detected we can just exit without cleanup.
        if (CallCompositeInstanceManager.hasCallComposite(instanceId)) {
            audioFocusManager.stop()
            audioSessionManager.onDestroy(this)
            audioModeManager.onDestroy()

            if (isFinishing && store.getCurrentState().navigationState.navigationState == NavigationStatus.EXIT) {
                store.dispatch(CallingAction.CallEndRequested())
                callDurationManager.onReset()
                compositeManager.onCompositeDestroy()
                CallCompositeInstanceManager.removeCallComposite(instanceId)
            }
        }

        diContainerHolder.container.callCompositeActivityWeakReference = WeakReference(null)
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onUserLeaveHint() {
        try {
            if (configuration.enableSystemPiPWhenMultitasking &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                activity?.packageManager?.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE) == true &&
                store.getCurrentState().navigationState.navigationState == NavigationStatus.IN_CALL
            ) {
                val params = PictureInPictureParams
                    .Builder()
                    .setAspectRatio(Rational(1, 1))
                    .build()

                if (enterPictureInPictureMode(params))
                    reduxStartPipMode()
            }
        } catch (_: Exception) {
            // on some samsung devices(API 26) enterPictureInPictureMode crashes even FEATURE_PICTURE_IN_PICTURE is true
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration?
    ) {
        store.dispatch(if (isInPictureInPictureMode) PipAction.PipModeEntered() else PipAction.ShowNormalEntered())
    }

    private fun syncPipMode() {
        if (configuration.enableSystemPiPWhenMultitasking &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            activity?.packageManager?.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE) == true &&
            store.getCurrentState().navigationState.navigationState == NavigationStatus.IN_CALL
        ) {
            store.dispatch(if (isInPictureInPictureMode) PipAction.PipModeEntered() else PipAction.ShowNormalEntered())
        }
    }

    private fun reduxStartPipMode() {
        if (configuration.enableSystemPiPWhenMultitasking &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            activity?.packageManager?.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE) == true &&
            store.getCurrentState().navigationState.navigationState == NavigationStatus.IN_CALL
        ) {
            store.dispatch(if (isInPictureInPictureMode) PipAction.PipModeEntered() else PipAction.ShowNormalEntered())
        }
    }

    fun hide() {
        if (!configuration.enableMultitasking)
            return

        // TODO: should we enter PiP if we are on the setup screen?
        if (configuration.enableSystemPiPWhenMultitasking &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            activity?.packageManager?.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE) == true
        ) {
            val params = PictureInPictureParams
                .Builder()
                .setAspectRatio(Rational(1, 1))
                .build()
            var enteredPiPSucceeded = false
            try {
                enteredPiPSucceeded = enterPictureInPictureMode(params)
            } catch (_: Exception) {
                // on some samsung devices(API 26) enterPictureInPictureMode crashes even FEATURE_PICTURE_IN_PICTURE is true
            }
            if (enteredPiPSucceeded)
                reduxStartPipMode()
            else
                activity?.moveTaskToBack(true)
        } else {
            activity?.moveTaskToBack(true)
        }
    }

    private fun forwardSupportEventToUser(userText: String) {
        val debugInfo = container.debugInfoManager.getDebugInfo()

        val event = CallCompositeUserReportedIssueEvent(userText, debugInfo)

        container.configuration.callCompositeEventsHandler.getOnUserReportedHandlers().forEach {
            try {
                it.handle(
                    event
                )
            } catch (e: Exception) {
                // Ignore any exception from the user handler
            }
        }
    }

    private fun configureLocalization() {
        val config: Configuration = resources.configuration
        val locale = when (configuration.localizationConfig) {
            null -> {
                supportedOSLocale()
            }

            else -> {
                configuration.localizationConfig!!.layoutDirection?.let {
                    window?.decorView?.layoutDirection = it
                }
                configuration.localizationConfig!!.locale
            }
        }
        config.setLocale(locale)

        resources.updateConfiguration(config, resources.displayMetrics)

        supportView.layoutDirection =
            activity?.window?.decorView?.layoutDirection ?: LayoutDirection.LOCALE
    }

    private fun getCameraPermissionLauncher(): ActivityResultLauncher<String> {
        return registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            permissionManager.setCameraPermissionsState()
        }
    }

    private fun getAudioPermissionLauncher(): ActivityResultLauncher<Array<String>> {
        return registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            permissionManager.setAudioPermissionsState()
        }
    }

    @SuppressLint("SourceLockedOrientationActivity", "RestrictedApi")
    private fun onNavigationStateChange(navigationState: NavigationStatus) {
        when (navigationState) {
            NavigationStatus.NONE -> {
                if (localOptions?.isSkipSetupScreen == true) {
                    store.dispatch(action = NavigationAction.CallLaunchWithoutSetup())
                } else {
                    store.dispatch(action = NavigationAction.SetupLaunched())
                }
            }

            NavigationStatus.EXIT -> {
                notificationService.removeNotification()
                store.end()
                callingMiddlewareActionHandler.dispose()
                videoViewManager.destroy()
                callStateHandler.onCompositeExit()
                container.callComposite.onExit()
                finish()
            }

            NavigationStatus.IN_CALL -> {
                supportActionBar?.setShowHideAnimationEnabled(false)
                supportActionBar?.hide()
                val callScreenOrientation: Int? =
                    getScreenOrientation(configuration.callScreenOrientation)
                requestedOrientation =
                    when {
                        (callScreenOrientation != null) -> callScreenOrientation
                        isAndroidTV(this) -> ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
                        else -> ActivityInfo.SCREEN_ORIENTATION_USER
                    }
                launchFragment(CallingFragment::class.java.name)
            }

            NavigationStatus.SETUP -> {
                notificationService.removeNotification()
                supportActionBar?.show()
                val setupScreenOrientation: Int? =
                    getScreenOrientation(configuration.setupScreenOrientation)
                requestedOrientation =
                    when {
                        (setupScreenOrientation != null) -> setupScreenOrientation
                        isAndroidTV(this) -> ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
                        else -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    }
                launchFragment(SetupFragment::class.java.name)
            }
        }
    }

    private fun launchFragment(fragmentClassName: String) {
        activity?.supportFragmentManager?.fragments?.let {
            if (it.isNotEmpty()) {
                // during screen rotate below logic helps to avoid launching fragment twice
                if (it.last().javaClass.name.equals(fragmentClassName)) {
                    return
                }
            }
        }

        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            classLoader,
            fragmentClassName
        )
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()

        // For accessibility, we are going to turn it off during the transaction
        // this is because it reads "toggle camera" after the transaction, which isn't really
        // useful to the user. After the transaction we re-enable it so that the screen reader
        // works as normal
        val containerView = findViewById<View>(R.id.azure_communication_ui_fragment_container_view)
        val oldAccessibilityValue = containerView.importantForAccessibility
        containerView.importantForAccessibility =
            View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
        transaction.replace(R.id.azure_communication_ui_fragment_container_view, fragment)
        transaction.runOnCommit {
            containerView.importantForAccessibility = oldAccessibilityValue
        }
        transaction.commit()
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.statusBarColor = ContextCompat.getColor(
                this,
                R.color.azure_communication_ui_calling_color_status_bar
            )
            val isNightMode = this.resources.configuration.uiMode
                .and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

            if (isNightMode) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    window.insetsController?.setSystemBarsAppearance(
                        0,
                        APPEARANCE_LIGHT_STATUS_BARS
                    )
                } else {
                    window.clearFlags(0)
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    window.insetsController?.setSystemBarsAppearance(
                        APPEARANCE_LIGHT_STATUS_BARS,
                        APPEARANCE_LIGHT_STATUS_BARS
                    )
                } else {
                    @Suppress("DEPRECATION")
                    window.decorView.systemUiVisibility =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                        } else {
                            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        }
                }
            }
        }
    }

    private fun setNavigationBarColor() {
        window.navigationBarColor =
            ContextCompat.getColor(
                this,
                R.color.azure_communication_ui_calling_color_status_bar,
            )
    }

    private fun supportedOSLocale(): Locale {
        val languageCode = Locale.getDefault().language
        val countryCode = Locale.getDefault().country
        for (language in CallCompositeSupportedLocale.getSupportedLocales()) {
            if (language.language == "$languageCode-$countryCode") {
                return Locale(languageCode, countryCode)
            }
        }
        return Locale.US
    }

    private fun getScreenOrientation(orientation: CallCompositeSupportedScreenOrientation?): Int? {
        return when (orientation) {
            CallCompositeSupportedScreenOrientation.PORTRAIT ->
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

            CallCompositeSupportedScreenOrientation.LANDSCAPE ->
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

            CallCompositeSupportedScreenOrientation.REVERSE_LANDSCAPE ->
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE

            CallCompositeSupportedScreenOrientation.USER ->
                ActivityInfo.SCREEN_ORIENTATION_USER

            CallCompositeSupportedScreenOrientation.FULL_SENSOR ->
                ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR

            CallCompositeSupportedScreenOrientation.USER_LANDSCAPE ->
                ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE

            null -> null
            else -> {
                logger.warning("Not supported screen orientation")
                null
            }
        }
    }

    internal companion object {
        const val KEY_INSTANCE_ID = "InstanceID"
    }
}
