package com.azure.android.communication.ui.calling.redux.middleware.bluetooth

import android.content.Context
import android.media.AudioManager

interface AudioSwitchingAdapter {
    fun disconnectAudio()
    fun enableSpeakerPhone(): Boolean
    fun enableEarpiece(): Boolean
    fun enableBluetooth(): Boolean
}

class AndroidAudioSwitchAdapter(context: Context) : AudioSwitchingAdapter{
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    override fun disconnectAudio() {
        audioManager.stopBluetoothSco()
        audioManager.isBluetoothScoOn = false
        audioManager.isSpeakerphoneOn = false
    }


    override fun enableSpeakerPhone(): Boolean {
        audioManager.stopBluetoothSco()
        audioManager.isBluetoothScoOn = false
        audioManager.isSpeakerphoneOn = true
        return true
    }

    override fun enableEarpiece(): Boolean {
        audioManager.stopBluetoothSco()
        audioManager.isBluetoothScoOn = false
        audioManager.isSpeakerphoneOn = false
        return true
    }

    override fun enableBluetooth(): Boolean {

        if (!audioManager.isBluetoothScoOn) {
            audioManager.startBluetoothSco()
            audioManager.isBluetoothScoOn = true
            audioManager.isSpeakerphoneOn = false
            return true
        }
        return false
    }
}