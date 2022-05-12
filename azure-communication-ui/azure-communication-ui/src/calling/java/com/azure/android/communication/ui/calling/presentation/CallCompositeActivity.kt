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
import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.calling.models.CommunicationUISupportedLocale
import com.azure.android.communication.ui.calling.presentation.fragment.calling.CallingFragment
import com.azure.android.communication.ui.calling.presentation.fragment.setup.SetupFragment
import com.azure.android.communication.ui.calling.presentation.navigation.BackNavigation
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.state.NavigationStatus
import com.microsoft.fluentui.util.activity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.util.Locale

internal class CallCompositeActivity : AppCompatActivity() {

    private val diContainerHolder: DependencyInjectionContainerHolder by viewModels()
    private val container by lazy { diContainerHolder.container }

    private val navigationRouter get() = container.navigationRouter
    private val store get() = container.appStore
    private val configuration get() = container.configuration
    private val permissionManager get() = container.permissionManager
    private val audioSessionManager get() = container.audioSessionManager
    private val lifecycleManager get() = container.lifecycleManager
    private val errorHandler get() = container.errorHandler
    private val remoteParticipantJoinedHandler get() = container.remoteParticipantHandler
    private val notificationService get() = container.notificationService
    private val callingMiddlewareActionHandler get() = container.callingMiddlewareActionHandler
    private val videoViewManager get() = container.videoViewManager
    private val instanceId get() = intent.getIntExtra(KEY_INSTANCE_ID, -1)

    override fun onDestroy() {
        // Covers edge case where Android tries to recreate call activity after process death
        // (e.g. due to revoked permission).
        // If no configs are detected we can just exit without cleanup.
        if (CallCompositeConfiguration.hasConfig(instanceId)) {
            if (isFinishing) {
                store.dispatch(CallingAction.CallEndRequested())
                CallCompositeConfiguration.putConfig(instanceId, null)
            }
            audioSessionManager.stop()
        }
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        volumeControlStream = AudioManager.STREAM_VOICE_CALL

        // Assign the Dependency Injection Container the appropriate instanceId,
        // so it can initialize it's container holding the dependencies
        try {
            diContainerHolder.instanceId = instanceId
        } catch (invalidIDException: IllegalArgumentException) {
            finish() // Container has vanished (probably due to process death); we cannot continue
            return
        }

        lifecycleScope.launch { errorHandler.start() }
        lifecycleScope.launch { remoteParticipantJoinedHandler.start() }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        configureActionBar()
        configureLocalization()
        setStatusBarColor()
        setActionBarVisibility()

        if (configuration.themeConfig?.theme != null) {
            theme.applyStyle(
                configuration.themeConfig?.theme!!, true
            )
        }

        setContentView(R.layout.azure_communication_ui_calling_activity_call_composite)

        val activity = this
        lifecycleScope.launch {
            permissionManager.start(
                activity,
                getAudioPermissionLauncher(),
                getCameraPermissionLauncher(),
                getPhonePermissionLauncher(),
            )
        }

        lifecycleScope.launch { audioSessionManager.start() }
        lifecycleScope.launch { container.accessibilityManager.start(activity) }
        lifecycleScope.launch { container.reduxHookManager.start(activity) }

        lifecycleScope.launchWhenStarted {
            navigationRouter.getNavigationStateFlow().collect { onNavigationStateChange(it) }
        }

        lifecycleScope.launch {
            navigationRouter.start()
        }

        notificationService.start(lifecycleScope)
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

    override fun onStop() {
        super.onStop()
        if (!isChangingConfigurations) {
            lifecycleScope.launch { lifecycleManager.pause() }
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch { lifecycleManager.resume() }

        permissionManager.setCameraPermissionsState()
        permissionManager.setAudioPermissionsState()
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

    private fun getPhonePermissionLauncher(): ActivityResultLauncher<String> {
        return registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            permissionManager.setPhonePermissionState()
        }
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.fragments.first()
        if (fragment !== null) {
            (fragment as BackNavigation).onBackPressed()
        } else {
            super.onBackPressed()
        }
    }

    @SuppressLint("SourceLockedOrientationActivity", "RestrictedApi")
    private fun onNavigationStateChange(navigationState: NavigationStatus) {
        when (navigationState) {
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
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
                launchFragment(CallingFragment::class.java.name)
            }
            NavigationStatus.SETUP -> {
                notificationService.removeNotification()
                supportActionBar?.show()
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
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

    private fun supportedOSLocale(): Locale {
        val languageCode = Locale.getDefault().language
        val countryCode = Locale.getDefault().country
        for (language in CommunicationUISupportedLocale.getSupportedLocales()) {
            if (language.language == "$languageCode-$countryCode") {
                return Locale(languageCode, countryCode)
            }
        }
        return Locale.US
    }

    companion object {
        const val KEY_INSTANCE_ID = "InstanceID"
    }
}
