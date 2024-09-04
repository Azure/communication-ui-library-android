package com.azure.android.communication.ui.calling.redux.action

internal sealed class ButtonViewDataAction : Action {
    class CallScreenCameraButtonIsEnabledUpdated(val isEnabled: Boolean) : ButtonViewDataAction()
    class CallScreenCameraButtonIsVisibleUpdated(val isVisible: Boolean) : ButtonViewDataAction()
}
