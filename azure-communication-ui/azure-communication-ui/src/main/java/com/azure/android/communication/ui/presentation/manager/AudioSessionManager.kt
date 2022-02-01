// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.manager

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioManager
import com.azure.android.communication.ui.redux.Store
import com.azure.android.communication.ui.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.redux.state.ReduxState
import kotlinx.coroutines.flow.collect

internal class AudioSessionManager(
    private val store: Store<ReduxState>,
) : BluetoothProfile.ServiceListener, BroadcastReceiver() {

    private var bluetoothAudioProxy: BluetoothHeadset? = null
    private lateinit var audioManager: AudioManager
    private var activity: Activity? = null
    private var audioDevices : Array<AudioDeviceInfo> = emptyArray();
    private val isBluetoothScoAvailable get() =  (bluetoothAudioProxy?.connectedDevices?.size ?: 0 > 0)


    private var previousAudioDeviceSelectionStatus: AudioDeviceSelectionStatus? = null
    suspend fun start(
        activity: Activity,
        audioManager: AudioManager,
    ) {
        this.activity = activity
        this.audioManager = audioManager
        BluetoothAdapter.getDefaultAdapter().getProfileProxy(activity,
            this, BluetoothProfile.HEADSET)

        activity.volumeControlStream = AudioManager.STREAM_VOICE_CALL
        initializeAudioDeviceState()
        refreshDevices()
        store.getStateFlow().collect {
            if (previousAudioDeviceSelectionStatus == null ||
                previousAudioDeviceSelectionStatus != it.localParticipantState.audioState.device
            ) {
                onAudioDeviceStateChange(it.localParticipantState.audioState.device)
            }
            previousAudioDeviceSelectionStatus = it.localParticipantState.audioState.device
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        refreshDevices()
    }

    private fun refreshDevices() {
        audioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        store.dispatch(LocalParticipantAction.AudioDeviceBluetoothSCOAvailable(isBluetoothScoAvailable))
    }

    private fun initializeAudioDeviceState() {
        when {
            audioManager.isSpeakerphoneOn ->
                store.dispatch(LocalParticipantAction.AudioDeviceChangeSucceeded(AudioDeviceSelectionStatus.SPEAKER_SELECTED))
            audioManager.isBluetoothScoOn ->
                store.dispatch(LocalParticipantAction.AudioDeviceChangeSucceeded(AudioDeviceSelectionStatus.BLUETOOTH_SCO_SELECTED))
            else ->
                store.dispatch(LocalParticipantAction.AudioDeviceChangeSucceeded(AudioDeviceSelectionStatus.RECEIVER_SELECTED))
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
                store.dispatch(LocalParticipantAction.AudioDeviceChangeSucceeded(AudioDeviceSelectionStatus.SPEAKER_SELECTED))
            }
            AudioDeviceSelectionStatus.RECEIVER_REQUESTED -> {
                enableEarpiece()
                store.dispatch(LocalParticipantAction.AudioDeviceChangeSucceeded(AudioDeviceSelectionStatus.RECEIVER_SELECTED))
            }
            AudioDeviceSelectionStatus.BLUETOOTH_SCO_REQUESTED -> {
                enableBluetooth()
                store.dispatch(LocalParticipantAction.AudioDeviceChangeSucceeded(AudioDeviceSelectionStatus.BLUETOOTH_SCO_SELECTED))
            }
        }
    }

    private fun enableSpeakerPhone() {
        audioManager.stopBluetoothSco()
        audioManager.isBluetoothScoOn = false
        audioManager.isSpeakerphoneOn = true
    }

    private fun enableEarpiece() {
        audioManager.stopBluetoothSco();
        audioManager.isBluetoothScoOn = false
        audioManager.isSpeakerphoneOn = false
    }

    private fun enableBluetooth() {
        audioManager.startBluetoothSco();
        audioManager.isBluetoothScoOn = true
        audioManager.isSpeakerphoneOn = false
    }

    fun stop() {
        activity = null
        BluetoothAdapter.getDefaultAdapter().closeProfileProxy(BluetoothProfile.HEADSET, bluetoothAudioProxy)
        activity?.unregisterReceiver(this)
    }

    override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
        bluetoothAudioProxy = proxy as BluetoothHeadset
        val filter = IntentFilter(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)
        activity?.registerReceiver(this, filter)

    }

    override fun onServiceDisconnected(profile: Int) {
        bluetoothAudioProxy = null

    }
}
