package com.azure.android.communication.ui.calling.presentation.manager
import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.azure.android.communication.ui.calling.presentation.navigation.NavigationRouter
import com.azure.android.communication.ui.calling.redux.state.NavigationStatus
import com.azure.android.communication.ui.calling.utilities.isAndroidTV
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

internal class RotationManager(private val navigationRouter: NavigationRouter) {
    // Since this is a lock after each activity creation, 2.5s isn't very noticeable, even when stress testing
    // It'll always catch up with the users intended orientation, while mitigating a race
    // when recreating some resources like VideoRenderViews
    private val lockDurationMillis = 2500L
    private var locked = true;
    private var activityRef: WeakReference<Activity>? = null
    private var lastNavigationStatus: NavigationStatus = NavigationStatus.SETUP

    suspend fun start(activity: Activity) {
        activityRef = WeakReference(activity)
        lockOrientation()

        withContext(Dispatchers.Main) {
            navigationRouter.getNavigationStateFlow().collect { onNavigationStateChange(it) }
        }
    }

    private fun onNavigationStateChange(navigationStatus: NavigationStatus) {
        // If the rotation lock is still in effect, store the navigation status
        // and defer setting the orientation until the lock expires
        lastNavigationStatus = navigationStatus
        if (!locked || lastNavigationStatus == NavigationStatus.SETUP) {
            setOrientationForNavigationStatus(navigationStatus);
        }
    }

    private fun lockOrientation() {
        Toast.makeText(activityRef?.get()!!, "Locking orientation", Toast.LENGTH_SHORT).show()
        activityRef?.get()?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
        // After the lock duration, unlock the orientation
        // If the navigation status changed during the lock, set the orientation according to the last status
        Handler(Looper.getMainLooper()).postDelayed({
            setOrientationForNavigationStatus(lastNavigationStatus!!)
            locked = false
        }, lockDurationMillis)
    }

    private fun setOrientationForNavigationStatus(navigationStatus: NavigationStatus) {
        activityRef?.get()?.apply {
            when (navigationStatus) {
                NavigationStatus.IN_CALL -> {
                    requestedOrientation = if (isAndroidTV(this)) {
                        ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
                    } else {
                        ActivityInfo.SCREEN_ORIENTATION_USER
                    }
                }
                NavigationStatus.SETUP -> {
                    requestedOrientation = if (isAndroidTV(this)) {
                        ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
                    } else {
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    }
                }
                else -> {
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                }
            }
        }
    }

}
