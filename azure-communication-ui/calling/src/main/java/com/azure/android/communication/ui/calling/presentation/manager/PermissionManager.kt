// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.android.communication.ui.calling.presentation.manager
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.action.PermissionAction
import com.azure.android.communication.ui.calling.redux.state.PermissionState
import com.azure.android.communication.ui.calling.redux.state.PermissionStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class PermissionManager(
    private val store: Store<ReduxState>,
) {
    private lateinit var activity: Activity
    private lateinit var audioPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var cameraPermissionLauncher: ActivityResultLauncher<String>
    private var previousPermissionState: PermissionState? = null

    fun start(
        activity: Activity,
        audioPermissionLauncher: ActivityResultLauncher<Array<String>>,
        cameraPermissionLauncher: ActivityResultLauncher<String>,
        coroutineScope: CoroutineScope,
    ) {
        this.activity = activity
        this.audioPermissionLauncher = audioPermissionLauncher
        this.cameraPermissionLauncher = cameraPermissionLauncher
        coroutineScope.launch {
            store.getStateFlow().collect {
                if (previousPermissionState == null || previousPermissionState != it.permissionState) {
                    onPermissionStateChange(it.permissionState)
                }
                previousPermissionState = it.permissionState
            }
        }
    }

    private fun createAudioPermissionRequest() {
        if (getAudioPermissionState(activity) == PermissionStatus.NOT_ASKED) {
            audioPermissionLauncher.launch(getPermissionsList())
        } else {
            setAudioPermissionsState()
        }
    }

    private fun createCameraPermissionRequest() {
        if (getCameraPermissionState(activity) == PermissionStatus.NOT_ASKED) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            setCameraPermissionsState()
        }
    }

    private fun onPermissionStateChange(permissionState: PermissionState) {
        when (permissionState.audioPermissionState) {
            PermissionStatus.REQUESTING -> createAudioPermissionRequest()
            PermissionStatus.UNKNOWN -> setAudioPermissionsState()
            else -> when (permissionState.cameraPermissionState) {
                PermissionStatus.REQUESTING -> createCameraPermissionRequest()
                PermissionStatus.UNKNOWN, PermissionStatus.GRANTED, PermissionStatus.DENIED -> {
                    setCameraPermissionsState()
                }
                else -> {}
            }
        }
    }

    private fun getAudioPermissionState(activity: Activity): PermissionStatus {
        val audioAccess = isPermissionGranted(Manifest.permission.RECORD_AUDIO)
        var isAudioPermissionPreviouslyDenied = previousPermissionState?.audioPermissionState == PermissionStatus.REQUESTING ||
            previousPermissionState?.audioPermissionState == PermissionStatus.DENIED
        if (!audioAccess && !isAudioPermissionPreviouslyDenied && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isAudioPermissionPreviouslyDenied = activity.shouldShowRequestPermissionRationale(
                Manifest.permission.RECORD_AUDIO
            )
        }

        return when {
            audioAccess -> PermissionStatus.GRANTED
            isAudioPermissionPreviouslyDenied -> PermissionStatus.DENIED
            else -> PermissionStatus.NOT_ASKED
        }
    }

    private fun getCameraPermissionState(activity: Activity): PermissionStatus {
        val cameraAccess = isPermissionGranted(Manifest.permission.CAMERA)
        var isCameraPermissionPreviouslyDenied = false
        if (!cameraAccess && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isCameraPermissionPreviouslyDenied = activity.shouldShowRequestPermissionRationale(
                Manifest.permission.CAMERA
            )
        }
        return when {
            cameraAccess -> PermissionStatus.GRANTED
            isCameraPermissionPreviouslyDenied -> PermissionStatus.DENIED
            else -> PermissionStatus.NOT_ASKED
        }
    }

    fun setAudioPermissionsState() {
        store.dispatch(PermissionAction.AudioPermissionIsSet(getAudioPermissionState(activity)))
    }

    fun setCameraPermissionsState() {
        store.dispatch(PermissionAction.CameraPermissionIsSet(getCameraPermissionState(activity)))
    }

    private fun isPermissionGranted(permission: String) =
        ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED

    private fun getPermissionsList(): Array<String> {
        val permissions = mutableListOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            permissions.add(Manifest.permission.FOREGROUND_SERVICE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)

        return permissions.toTypedArray()
    }
}
