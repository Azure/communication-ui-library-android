package com.azure.android.communication.ui.redux.action

internal sealed class DisplayAction :
    Action {
    class IsConfirmLeaveOverlayDisplayed(val isConfirmLeaveOverlayDisplayed: Boolean) : DisplayAction()
}
