// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
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
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.calling.CallCompositeInstanceManager
import com.azure.android.communication.ui.calling.models.CallCompositeSupportedLocale
import com.azure.android.communication.ui.calling.models.CallCompositeSupportedScreenOrientation
import com.azure.android.communication.ui.calling.models.CallCompositeUserReportedIssueEvent
import com.azure.android.communication.ui.calling.presentation.fragment.calling.CallingFragment
import com.azure.android.communication.ui.calling.presentation.fragment.calling.support.SupportView
import com.azure.android.communication.ui.calling.presentation.fragment.calling.support.SupportViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.setup.SetupFragment
import com.azure.android.communication.ui.calling.presentation.navigation.BackNavigation
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.action.NavigationAction
import com.azure.android.communication.ui.calling.redux.state.NavigationStatus
import com.azure.android.communication.ui.calling.utilities.TestHelper
import com.azure.android.communication.ui.calling.utilities.ScreenshotHelper
import com.azure.android.communication.ui.calling.utilities.isAndroidTV
import com.microsoft.fluentui.util.activity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.lang.ref.WeakReference
import java.util.Locale

internal class CallCompositeActivity : AppCompatActivity() {
    private val diContainerHolder: DependencyInjectionContainerHolder by viewModels {
        DependencyInjectionContainerHolderFactory(
            this@CallCompositeActivity.application,
            TestHelper.callingSDK,
            TestHelper.videoStreamRendererFactory,
            TestHelper.coroutineContextProvider
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        volumeControlStream = AudioManager.STREAM_VOICE_CALL

        // Assign the Dependency Injection Container the appropriate instanceId,
        // so it can initialize it's container holding the dependencies
        try {
            diContainerHolder.instanceId = instanceId
            diContainerHolder.container.callCompositeActivityWeakReference = WeakReference(this)
        } catch (invalidIDException: IllegalArgumentException) {
            finish() // Container has vanished (probably due to process death); we cannot continue
            return
        }

        lifecycleScope.launch { errorHandler.start() }
        lifecycleScope.launch { remoteParticipantJoinedHandler.start() }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        configureLocalization()
        configureActionBar()
        setStatusBarColor()
        setNavigationBarColor()
        setActionBarVisibility()

        configuration.themeConfig?.let {
            theme.applyStyle(it, true)
        }

        setContentView(R.layout.azure_communication_ui_calling_activity_call_composite)

        val activity = this
        permissionManager.start(
            activity,
            getAudioPermissionLauncher(),
            getCameraPermissionLauncher(),
            lifecycleScope
        )

        audioSessionManager.onCreate(savedInstanceState)

        lifecycleScope.launch { container.accessibilityManager.start(activity) }

        lifecycleScope.launchWhenStarted {
            navigationRouter.getNavigationStateFlow().collect { onNavigationStateChange(it) }
        }

        lifecycleScope.launch {
            navigationRouter.start()
        }

        lifecycleScope.launch {
            audioFocusManager.start()
        }

        lifecycleScope.launch {
            audioModeManager.start()
        }

        notificationService.start(lifecycleScope)
        callHistoryService.start(lifecycleScope)


        lifecycleScope.launch {
            supportView.start(supportViewModel, this@CallCompositeActivity)
        }

        lifecycleScope.launch {
            store.getStateFlow().collect {
                supportViewModel.update(it.navigationState)
            }
        }
        callStateHandler.start(lifecycleScope)
    }

    override fun onStart() {
        super.onStart()
        audioSessionManager.onStart(this)
        lifecycleScope.launch { lifecycleManager.resume() }
        permissionManager.setCameraPermissionsState()
        permissionManager.setAudioPermissionsState()
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
            if (isFinishing) {
                store.dispatch(CallingAction.CallEndRequested())
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

    private fun configureActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this,
                    R.color.azure_communication_ui_calling_color_background
                )
            )
        )
        supportActionBar?.setHomeAsUpIndicator(R.drawable.azure_communication_ui_calling_ic_fluent_arrow_left_24_filled)
    }

    private fun forwardSupportEventToUser(userText: String, screenshot: Boolean) {
        val debugInfo = container.debugInfoManager.getDebugInfo()
        val event = CallCompositeUserReportedIssueEvent(
            userText,
            if (screenshot) (container.callCompositeActivityWeakReference.get()?.let { ScreenshotHelper.captureActivity(this) }) else null,
            debugInfo
        )
        supportView.visibility = View.GONE

        supportView.visibility = View.VISIBLE
        container.configuration.callCompositeEventsHandler.getOnUserReportedHandlers().forEach {
            try {
                it.handle(
                    event
                )
            } catch (e: Exception) {
                // Ignore any exception from the user handler
            }
        }
        // Pass through local config
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
    }

    private fun setActionBarVisibility() {
        if (store.getCurrentState().navigationState.navigationState != NavigationStatus.SETUP) {
            supportActionBar?.hide()
        } else {
            supportActionBar?.show()
        }
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

    override fun onBackPressed() {
        val fragment = supportFragmentManager.fragments.firstOrNull()
        if (fragment !== null) {
            (fragment as BackNavigation).onBackPressed()
        } else {
            super.onBackPressed()
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
                finish()
            }
            NavigationStatus.IN_CALL -> {
                supportActionBar?.setShowHideAnimationEnabled(false)
                supportActionBar?.hide()
                val callScreenOrientation: Int? = getScreenOrientation(configuration.callScreenOrientation)
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
                val setupScreenOrientation: Int? = getScreenOrientation(configuration.setupScreenOrientation)
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

    companion object {
        const val KEY_INSTANCE_ID = "InstanceID"
    }
}
