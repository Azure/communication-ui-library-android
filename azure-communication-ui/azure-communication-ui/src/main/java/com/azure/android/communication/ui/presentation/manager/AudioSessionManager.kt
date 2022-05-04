// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.manager

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.redux.Store
import com.azure.android.communication.ui.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.redux.state.ReduxState
import com.azure.android.communication.ui.utilities.implementation.FeatureFlags
import kotlinx.coroutines.flow.collect
import android.media.AudioDeviceInfo
import android.os.Build
import android.os.Bundle

import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import com.azure.android.communication.ui.redux.state.PermissionStatus
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

internal class AudioSessionManager(
    private val store: Store<ReduxState>,
    private val context: Context,
) : BluetoothProfile.ServiceListener, BroadcastReceiver() {

    private val audioManager by lazy { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    private var bluetoothAudioProxy: BluetoothHeadset? = null
    private var initialized = false

    private val isBluetoothScoAvailable
        get() = try {
            FeatureFlags.BluetoothAudio.active &&
                (bluetoothAudioProxy?.connectedDevices?.size ?: 0 > 0)
        } catch (exception: SecurityException) {
            false
        }

    private val bluetoothDeviceName: String
        get() = try {
            bluetoothAudioProxy?.connectedDevices?.firstOrNull()?.name
                ?: context.getString(R.string.azure_communication_ui_audio_device_drawer_bluetooth)
        } catch (exception: SecurityException) {
            context.getString(R.string.azure_communication_ui_audio_device_drawer_bluetooth)
        }

    private var previousPermissionState: PermissionStatus = PermissionStatus.UNKNOWN

    private var previousAudioDeviceSelectionStatus: AudioDeviceSelectionStatus? = null
    private var priorToBluetoothAudioSelectionStatus: AudioDeviceSelectionStatus? = null

    private val btAdapter: BluetoothAdapter? get() {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        return manager.adapter
    }

    fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            initializeAudioDeviceState()

            // Listeners we need to rebind with Activity (Bluetooth, Headset, State Updates)
            btAdapter?.run {
                getProfileProxy(context, this@AudioSessionManager, BluetoothProfile.HEADSET)
            }

            val filter = IntentFilter(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)
            filter.addAction(AudioManager.ACTION_HEADSET_PLUG)
            context.registerReceiver(this@AudioSessionManager, filter)
        }
    }

    fun onStart(activity: Activity) {
        if (activity !is LifecycleOwner) {
            throw IllegalArgumentException("Activity must be a LifecycleOwner)")
        }
        (activity as LifecycleOwner).lifecycle.coroutineScope.launch {
            // On first launch we need to init the redux-state, check Bluetooth and Headset status

            store.getStateFlow().collect {
                if (previousAudioDeviceSelectionStatus == null ||
                    previousAudioDeviceSelectionStatus != it.localParticipantState.audioState.device
                ) {
                    onAudioDeviceStateChange(it.localParticipantState.audioState.device)
                }

                // After permission is granted, double check bluetooth status
                if (it.permissionState.audioPermissionState == PermissionStatus.GRANTED &&
                    previousPermissionState != PermissionStatus.GRANTED
                ) {
                    updateBluetoothStatus()
                }

                previousAudioDeviceSelectionStatus = it.localParticipantState.audioState.device
                previousPermissionState = it.permissionState.audioPermissionState
            }
        }
    }

    // Call when the Activity is finishing (i.e. call is done)
    fun onDestroy(activity: Activity) {
        if (activity.isFinishing) {
            btAdapter?.run {
                closeProfileProxy(BluetoothProfile.HEADSET, bluetoothAudioProxy)
            }
            context.unregisterReceiver(this)

            if (audioManager.isBluetoothScoOn) {
                audioManager.stopBluetoothSco()
            }
            audioManager.isBluetoothScoOn = false
            audioManager.isSpeakerphoneOn = false
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.apply {
            when (action) {
                BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED -> updateBluetoothStatus()
                AudioManager.ACTION_HEADSET_PLUG -> updateHeadphoneStatus()
            }
        }
    }

    private fun updateHeadphoneStatus() {
        store.dispatch(LocalParticipantAction.AudioDeviceHeadsetAvailable(isHeadsetActive()))
    }

    // Update the status of bluetooth
    // Connect a headset automatically if bluetooth is connected
    // When disconnected revert to "Speaker"
    // When disconnected (and not selected), just update availability
    private fun updateBluetoothStatus() {
        val audioState = store.getCurrentState().localParticipantState.audioState

        // Bluetooth is no longer available
        // Fallback to previous device selection
        if (!isBluetoothScoAvailable &&
            audioState.bluetoothState.available &&
            audioState.device == AudioDeviceSelectionStatus.BLUETOOTH_SCO_SELECTED
        ) {
            // Request the Previous Device
            store.dispatch(
                LocalParticipantAction.AudioDeviceChangeRequested(
                    when (priorToBluetoothAudioSelectionStatus) {
                        AudioDeviceSelectionStatus.RECEIVER_SELECTED -> AudioDeviceSelectionStatus.RECEIVER_REQUESTED
                        else -> AudioDeviceSelectionStatus.SPEAKER_REQUESTED
                    }
                )
            )
        }

        // Auto-Connect to Bluetooth if it wasn't available but now is
        if (isBluetoothScoAvailable && !audioState.bluetoothState.available) {

            store.dispatch(
                LocalParticipantAction.AudioDeviceBluetoothSCOAvailable(
                    isBluetoothScoAvailable,
                    bluetoothDeviceName
                )
            )

            priorToBluetoothAudioSelectionStatus = store.getCurrentState().localParticipantState.audioState.device
            store.dispatch(
                LocalParticipantAction.AudioDeviceChangeRequested(
                    AudioDeviceSelectionStatus.BLUETOOTH_SCO_REQUESTED
                )
            )
        }

        // Update the Bluetooth Status in the store
        store.dispatch(
            LocalParticipantAction.AudioDeviceBluetoothSCOAvailable(
                isBluetoothScoAvailable,
                bluetoothDeviceName
            )
        )
    }

    private fun isHeadsetActive(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val audioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            for (deviceInfo in audioDevices) {
                if (deviceInfo.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES ||
                    deviceInfo.type == AudioDeviceInfo.TYPE_WIRED_HEADSET
                ) {
                    return true
                }
            }
            return false
        } else {
            return audioManager.isWiredHeadsetOn
        }
        return false
    }

    private fun initializeAudioDeviceState() {
        if (initialized) return
        initialized = true

        updateHeadphoneStatus()

        when {
            audioManager.isSpeakerphoneOn ->
                store.dispatch(
                    LocalParticipantAction.AudioDeviceChangeSucceeded(
                        AudioDeviceSelectionStatus.SPEAKER_SELECTED
                    )
                )
            audioManager.isBluetoothScoOn ->
                store.dispatch(
                    LocalParticipantAction.AudioDeviceChangeSucceeded(
                        AudioDeviceSelectionStatus.BLUETOOTH_SCO_SELECTED
                    )
                )
            else ->
                store.dispatch(
                    LocalParticipantAction.AudioDeviceChangeSucceeded(
                        AudioDeviceSelectionStatus.RECEIVER_SELECTED
                    )
                )
        }
    }

    private fun onAudioDeviceStateChange(audioDeviceSelectionStatus: AudioDeviceSelectionStatus) {
        when (audioDeviceSelectionStatus) {
            AudioDeviceSelectionStatus.SPEAKER_REQUESTED, AudioDeviceSelectionStatus.RECEIVER_REQUESTED, AudioDeviceSelectionStatus.BLUETOOTH_SCO_REQUESTED ->
                switchAudioDevice(audioDeviceSelectionStatus)
        }
    }

    private fun switchAudioDevice(audioDeviceSelectionStatus: AudioDeviceSelectionStatus) {
        when (audioDeviceSelectionStatus) {
            AudioDeviceSelectionStatus.SPEAKER_REQUESTED -> {
                enableSpeakerPhone()
                store.dispatch(
                    LocalParticipantAction.AudioDeviceChangeSucceeded(
                        AudioDeviceSelectionStatus.SPEAKER_SELECTED
                    )
                )
            }
            AudioDeviceSelectionStatus.RECEIVER_REQUESTED -> {
                enableEarpiece()
                store.dispatch(
                    LocalParticipantAction.AudioDeviceChangeSucceeded(
                        AudioDeviceSelectionStatus.RECEIVER_SELECTED
                    )
                )
            }
            AudioDeviceSelectionStatus.BLUETOOTH_SCO_REQUESTED -> {
                enableBluetooth()
                store.dispatch(
                    LocalParticipantAction.AudioDeviceChangeSucceeded(
                        AudioDeviceSelectionStatus.BLUETOOTH_SCO_SELECTED
                    )
                )
            }
        }
    }

    private fun enableSpeakerPhone() {
        audioManager.stopBluetoothSco()
        audioManager.isBluetoothScoOn = false
        audioManager.isSpeakerphoneOn = true
    }

    private fun enableEarpiece() {
        audioManager.stopBluetoothSco()
        audioManager.isBluetoothScoOn = false
        audioManager.isSpeakerphoneOn = false
    }

    private fun enableBluetooth() {
        if (!audioManager.isBluetoothScoOn) {
            audioManager.startBluetoothSco()
            audioManager.isBluetoothScoOn = true
            audioManager.isSpeakerphoneOn = false
        }
    }

    override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
        bluetoothAudioProxy = proxy as BluetoothHeadset
        updateBluetoothStatus()
    }

    override fun onServiceDisconnected(profile: Int) {
        bluetoothAudioProxy = null
    }
}
