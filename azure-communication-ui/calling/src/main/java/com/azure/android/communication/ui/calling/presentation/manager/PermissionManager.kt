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
import com.azure.android.communication.ui.calling.redux.state.PermissionStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class PermissionManager(
    private val store: Store<ReduxState>,
) {
    private lateinit var activity: Activity
    private lateinit var audioPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var cameraPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var cameraPermissionFlow: MutableStateFlow<PermissionStatus>
    private lateinit var audioPermissionFlow: MutableStateFlow<PermissionStatus>

    fun start(
        activity: Activity,
        audioPermissionLauncher: ActivityResultLauncher<Array<String>>,
        cameraPermissionLauncher: ActivityResultLauncher<String>,
        coroutineScope: CoroutineScope,
    ) {
        this.activity = activity
        this.audioPermissionLauncher = audioPermissionLauncher
        this.cameraPermissionLauncher = cameraPermissionLauncher
        val currentState = store.getCurrentState()
        cameraPermissionFlow = MutableStateFlow(currentState.permissionState.cameraPermissionState)
        audioPermissionFlow = MutableStateFlow(currentState.permissionState.audioPermissionState)

        coroutineScope.launch {
            store.getStateFlow().collect {
                cameraPermissionFlow.value = it.permissionState.cameraPermissionState
                audioPermissionFlow.value = it.permissionState.audioPermissionState
            }
        }

        coroutineScope.launch {
            cameraPermissionFlow.collect {
                onCameraPermissionStateChange(it)
            }
        }
        coroutineScope.launch {
            audioPermissionFlow.collect {
                onAudioPermissionStateChange(it)
            }
        }
    }

    private fun createAudioPermissionRequest() {
        if (getAudioPermissionState() == PermissionStatus.NOT_ASKED) {
            audioPermissionLauncher.launch(getPermissionsList())
        } else {
            setAudioPermissionsState()
        }
    }

    private fun createCameraPermissionRequest() {
        if (getCameraPermissionState() == PermissionStatus.NOT_ASKED) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            setCameraPermissionsState()
        }
    }

    private fun onAudioPermissionStateChange(permissionStatus: PermissionStatus) {
        when (permissionStatus) {
            PermissionStatus.REQUESTING -> createAudioPermissionRequest()
            PermissionStatus.UNKNOWN -> setAudioPermissionsState()
            else -> { }
        }
    }

    private fun onCameraPermissionStateChange(permissionStatus: PermissionStatus) {
        when (permissionStatus) {
            PermissionStatus.REQUESTING -> createCameraPermissionRequest()
            PermissionStatus.UNKNOWN, PermissionStatus.GRANTED, PermissionStatus.DENIED -> {
                setCameraPermissionsState()
            }
            else -> {}
        }
    }

    private fun getAudioPermissionState(): PermissionStatus =
            getPermissionState(Manifest.permission.RECORD_AUDIO)

    private fun getCameraPermissionState(): PermissionStatus =
            getPermissionState(Manifest.permission.CAMERA)


    private fun getPermissionState(permission: String): PermissionStatus {
        if (isPermissionGranted(permission))
            return PermissionStatus.GRANTED

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val isPermissionPreviouslyDenied = activity.shouldShowRequestPermissionRationale(permission)
            if (isPermissionPreviouslyDenied)
                return PermissionStatus.DENIED
        }
        return PermissionStatus.NOT_ASKED
    }

    fun setAudioPermissionsState() {
        store.dispatch(PermissionAction.AudioPermissionIsSet(getAudioPermissionState()))
    }

    fun setCameraPermissionsState() {
        store.dispatch(PermissionAction.CameraPermissionIsSet(getCameraPermissionState()))
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            permissions.add(Manifest.permission.FOREGROUND_SERVICE)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.addAll(
                    arrayOf(
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.POST_NOTIFICATIONS,
                    )
            )
        }

        return permissions.toTypedArray()
    }
}
