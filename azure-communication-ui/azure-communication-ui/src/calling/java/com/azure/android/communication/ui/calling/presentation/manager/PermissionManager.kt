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
import kotlinx.coroutines.flow.collect

internal class PermissionManager(
    private val store: Store<ReduxState>,
) {
    private lateinit var activity: Activity
    private lateinit var audioPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var cameraPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var phonePermissionLauncher: ActivityResultLauncher<String>
    private var previousPermissionState: PermissionState? = null

    private val audioPermissions =
         arrayOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.FOREGROUND_SERVICE
        )

    suspend fun start(
        activity: Activity,
        audioPermissionLauncher: ActivityResultLauncher<Array<String>>,
        cameraPermissionLauncher: ActivityResultLauncher<String>,
        phonePermissionLauncher: ActivityResultLauncher<String>,
    ) {
        this.activity = activity
        this.audioPermissionLauncher = audioPermissionLauncher
        this.cameraPermissionLauncher = cameraPermissionLauncher
        this.phonePermissionLauncher = phonePermissionLauncher
        store.getStateFlow().collect {
            if (previousPermissionState == null || previousPermissionState != it.permissionState) {
                onPermissionStateChange(it.permissionState)
            }
            previousPermissionState = it.permissionState
        }
    }

    private fun createAudioPermissionRequest() {
        if (getAudioPermissionState(activity) == PermissionStatus.NOT_ASKED) {
            audioPermissionLauncher.launch(audioPermissions)
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

    private fun createPhonePermissionRequest() {
        if (getPhonePermissionState(activity) == PermissionStatus.NOT_ASKED) {
            phonePermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
        } else {
            setPhonePermissionState()
        }
    }

    private fun onPermissionStateChange(permissionState: PermissionState) {
        when (permissionState.micPermissionState) {
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
        var isAudioPermissionPreviouslyDenied = false
        if (!audioAccess && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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

    private fun getPhonePermissionState(activity: Activity): PermissionStatus {
        val phoneAccess = isPermissionGranted(Manifest.permission.READ_PHONE_STATE)
        var isPhonePermissionPreviouslyDenied = false
        if (!phoneAccess && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isPhonePermissionPreviouslyDenied = activity.shouldShowRequestPermissionRationale(
                Manifest.permission.READ_PHONE_STATE
            )
        }
        return when {
            phoneAccess -> PermissionStatus.GRANTED
            isPhonePermissionPreviouslyDenied -> PermissionStatus.DENIED
            else -> PermissionStatus.NOT_ASKED
        }
    }

    fun setAudioPermissionsState() {
        store.dispatch(PermissionAction.AudioPermissionIsSet(getAudioPermissionState(activity)))
    }

    fun setCameraPermissionsState() {
        store.dispatch(PermissionAction.CameraPermissionIsSet(getCameraPermissionState(activity)))
    }

    fun setPhonePermissionState() {
        store.dispatch(PermissionAction.PhonePermissionIsSet(getPhonePermissionState(activity)))
    }

    private fun isPermissionGranted(permission: String) =
        ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED

    private fun getPermissionsList(): Array<String> =
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
                arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.WAKE_LOCK,
                    Manifest.permission.MODIFY_AUDIO_SETTINGS,
                    Manifest.permission.FOREGROUND_SERVICE
                )
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ->
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.WAKE_LOCK,
                    Manifest.permission.MODIFY_AUDIO_SETTINGS,
                    Manifest.permission.FOREGROUND_SERVICE
                )
            else ->
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.WAKE_LOCK,
                    Manifest.permission.MODIFY_AUDIO_SETTINGS,
                )
        }
}
