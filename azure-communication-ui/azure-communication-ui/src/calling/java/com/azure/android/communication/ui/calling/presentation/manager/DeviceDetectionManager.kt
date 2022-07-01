// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.manager

import android.app.Activity
import android.os.Bundle
import com.azure.android.communication.ui.calling.utilities.audio.BluetoothDetector
import com.azure.android.communication.ui.calling.utilities.audio.HeadsetDetector

// Glues the Detector and Redux Together
// Listens for Bluetooth devices and dispatches info
// when they are detected
internal class DeviceDetectionManager(
    private val bluetoothDetector: BluetoothDetector,
    private val headsetDetector: HeadsetDetector,
    ) {


    // Hook for onCreate
    // Starts detecting devices
    fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            bluetoothDetector.start()
            headsetDetector.start()
        }
    }

    // Call when the Activity is finishing (i.e. call is done)
    fun onDestroy(activity: Activity) {
        if (activity.isFinishing) {
            bluetoothDetector.stop()
            headsetDetector.stop()
        }
    }

    fun manuallyDetectBluetooth() = bluetoothDetector.trigger()
}
