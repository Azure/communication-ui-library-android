// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.di.DIContainer
import com.azure.android.communication.ui.error.ErrorHandler
import com.azure.android.communication.ui.presentation.fragment.calling.CallingFragment
import com.azure.android.communication.ui.presentation.fragment.setup.SetupFragment
import com.azure.android.communication.ui.presentation.manager.AudioSessionManager
import com.azure.android.communication.ui.presentation.manager.LifecycleManager
import com.azure.android.communication.ui.presentation.manager.PermissionManager
import com.azure.android.communication.ui.presentation.navigation.BackNavigation
import com.azure.android.communication.ui.presentation.navigation.NavigationRouter
import com.azure.android.communication.ui.redux.Store
import com.azure.android.communication.ui.redux.action.CallingAction
import com.azure.android.communication.ui.redux.middleware.handler.CallingMiddlewareActionHandler
import com.azure.android.communication.ui.redux.state.NavigationStatus
import com.azure.android.communication.ui.redux.state.ReduxState
import com.azure.android.communication.ui.service.calling.InCallService
import kotlinx.coroutines.launch

internal class MainActivity : AppCompatActivity() {

    private val diContainerHolder: DependencyInjectionContainerHolder by viewModels()

    private lateinit var navigationRouter: NavigationRouter
    private lateinit var fragmentFactory: FragmentFactory
    private lateinit var store: Store<ReduxState>
    private lateinit var configuration: CallCompositeConfiguration
    private lateinit var permissionManager: PermissionManager
    private lateinit var audioSessionManager: AudioSessionManager
    private lateinit var lifecycleManager: LifecycleManager
    private lateinit var errorHandler: ErrorHandler
    private lateinit var callingMiddlewareActionHandler: CallingMiddlewareActionHandler
    private lateinit var videoViewManager: VideoViewManager

    override fun onDestroy() {
        super.onDestroy()
        navigationRouter.removeOnNavigationStateChanged(this::onNavigationStateChange)

        if (isFinishing) {
            DIContainer = null
            store.dispatch(CallingAction.CallEndRequested())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependencies()
        lifecycleScope.launch { errorHandler.start() }
        supportFragmentManager.fragmentFactory = fragmentFactory
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        configureActionBar()
        setStatusBarColor()
        setActionBarVisibility()
        if (configuration.themeConfig?.theme != null) {
            theme.applyStyle(
                configuration.themeConfig?.theme!!, true
            )
        }
        setContentView(R.layout.azure_communication_ui_activity_main)

        val activity = this
        lifecycleScope.launch {
            permissionManager.start(
                activity,
                getAudioPermissionLauncher(),
                getCameraPermissionLauncher()
            )
        }

        lifecycleScope.launch {
            audioSessionManager.start(
                activity,
                getAudioManager()
            )
        }

        navigationRouter.addOnNavigationStateChanged(this::onNavigationStateChange)

        lifecycleScope.launch { navigationRouter.start() }
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

    private fun injectDependencies() {

        if (diContainerHolder.container == null) {
            diContainerHolder.container = DIContainer
        }

        val diContainer = diContainerHolder.container!!

        navigationRouter = diContainer.provideNavigationRouter()
        fragmentFactory = diContainer.provideFragmentFactory()
        store = diContainer.provideStore()
        configuration = diContainer.provideConfiguration()
        permissionManager = diContainer.providePermissionManager()
        audioSessionManager = diContainer.provideAudioSessionManager()
        lifecycleManager = diContainer.provideLifecycleManager()
        errorHandler = diContainer.provideErrorHandler()
        callingMiddlewareActionHandler = diContainer.provideCallingMiddlewareActionHandler()
        videoViewManager = diContainer.provideVideoViewManager()
    }

    private fun configureActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this,
                    R.color.azure_communication_ui_color_background
                )
            )
        )
        supportActionBar?.setHomeAsUpIndicator(R.drawable.azure_communication_ui_ic_fluent_arrow_left_24_filled)
        supportActionBar?.elevation = 0F
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

    private fun getAudioManager(): AudioManager {
        return applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
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
                stopService()
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
                startService()
            }
            NavigationStatus.SETUP -> {
                stopService()
                supportActionBar?.show()
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                launchFragment(SetupFragment::class.java.name)
            }
        }
    }

    private fun launchFragment(fragmentClassName: String) {
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            classLoader,
            fragmentClassName
        )
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.azure_communication_ui_fragment_container_view, fragment)
        transaction.commit()
    }

    private fun setStatusBarColor() {
        window.statusBarColor = ContextCompat.getColor(
            this,
            R.color.azure_communication_ui_color_status_bar
        )

        val isNightMode = this.resources.configuration.uiMode
            .and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        if (isNightMode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.setSystemBarsAppearance(0, APPEARANCE_LIGHT_STATUS_BARS)
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

    private fun startService() {
        val inCallServiceIntent = Intent(this, InCallService::class.java)
        startService(inCallServiceIntent)
    }

    private fun stopService() {
        val inCallServiceIntent = Intent(this, InCallService::class.java)
        stopService(inCallServiceIntent)
    }
}
