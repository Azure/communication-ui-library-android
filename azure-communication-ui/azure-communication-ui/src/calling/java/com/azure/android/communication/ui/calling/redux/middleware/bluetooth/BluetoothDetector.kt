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

abstract class BluetoothDetector(val context: Context, val isActiveCallback: (Boolean, String) -> Unit) {
    abstract fun start()
    abstract fun stop()
}

class BluetoothDetectorImpl(
    context: Context,
    isActiveCallback: (Boolean, String) -> Unit
) : BluetoothDetector(context, isActiveCallback) {

    private val btManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val btAdapter = btManager.adapter
    private val serviceListener = object : BluetoothProfile.ServiceListener {
        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
            headsetProxy = proxy as BluetoothHeadset
            isActiveCallback(isBluetoothScoAvailable, bluetoothDeviceName)
            registerReceiver()
        }

        override fun onServiceDisconnected(profile: Int) {
            context.unregisterReceiver(changeStateReceiver)
            headsetProxy = null
        }
    }

    private val changeStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            isActiveCallback(isBluetoothScoAvailable, bluetoothDeviceName)
        }
    }

    private val isBluetoothScoAvailable
        get() = try {
            (headsetProxy?.connectedDevices?.size ?: 0) > 0
        } catch (exception: SecurityException) {
            false
        }


    private val bluetoothDeviceName
        @SuppressLint("MissingPermission")
        get() =
            try {
                (headsetProxy?.connectedDevices?.first()?.name) ?: ""
            } catch (exception: Exception) {
                ""
            }




    private var headsetProxy: BluetoothHeadset? = null



    override fun start() = openProfileProxy()

    override fun stop() {
        btAdapter?.run {
            headsetProxy?.run {
                closeProfileProxy(BluetoothProfile.HEADSET, this)
            }
            headsetProxy = null
        }
    }

    private fun openProfileProxy() {
        if (btAdapter?.isEnabled == true) btAdapter.run {
            getProfileProxy(context, serviceListener, BluetoothProfile.HEADSET)
        }
    }

    private fun registerReceiver() {
        val filter = IntentFilter(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
        context.registerReceiver(changeStateReceiver, filter)
    }
}

