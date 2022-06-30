package com.azure.android.communication.ui.calling.redux.middleware.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build

abstract class HeadsetDetector(val context: Context, val isActiveCallback: (Boolean) -> Unit) {
    abstract fun start()
    abstract fun stop()
}

class HeadsetDetectorImpl(
    context: Context,
    isActiveCallback: (Boolean) -> Unit
) : HeadsetDetector(context, isActiveCallback) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val changeStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            isActiveCallback(isHeadsetActive())
        }
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
    }

    override fun start() {
        val filter = IntentFilter(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)
        filter.addAction(AudioManager.ACTION_HEADSET_PLUG)
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
        context.registerReceiver(changeStateReceiver, filter)
    }

    override fun stop() {
        context.unregisterReceiver(changeStateReceiver)
    }
}
