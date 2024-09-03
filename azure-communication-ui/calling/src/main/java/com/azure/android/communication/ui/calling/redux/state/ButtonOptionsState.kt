package com.azure.android.communication.ui.calling.redux.state

internal data class DefaultButtonState(
    val isEnabled: Boolean? = null,
    val isVisible: Boolean? = null,
)

internal data class ButtonOptionsState(
    val cameraButtonState: DefaultButtonState? = null,
)
