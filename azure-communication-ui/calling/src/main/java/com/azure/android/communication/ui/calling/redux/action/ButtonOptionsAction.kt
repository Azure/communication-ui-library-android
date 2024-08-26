package com.azure.android.communication.ui.calling.redux.action

internal sealed class ButtonOptionsAction : Action {
    class CallScreenCameraButtonIsEnabledUpdated(val isEnabled: Boolean) : ButtonOptionsAction()
    class CallScreenCameraButtonIsVisibleUpdated(val isVisible: Boolean) : ButtonOptionsAction()
}