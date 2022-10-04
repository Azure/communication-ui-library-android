package com.azure.android.communication.ui.chat.implementation.ui

// sample class to demo notification to UI from Contoso
// just a demo code for POC (Actual design will be different)

internal interface StopNotification {
    fun stop()
}

internal class UINotifier {

    private var stopNotifier: StopNotification? = null

    fun registerForStop(stopNotifier: StopNotification) {
        this.stopNotifier = stopNotifier
    }

    fun notifyStop() {
        stopNotifier?.stop()
    }
}
