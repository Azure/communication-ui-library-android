package com.azure.android.communication.ui.calling.redux.state

internal data class DefaultButtonState(
    val isEnabled: Boolean? = null,
    val isVisible: Boolean? = null,
)

internal data class CustomButtonState(
    val id: String?,
    val isEnabled: Boolean?,
    val isVisible: Boolean?,
    val title: String? = null,
    val drawableId: Int,
)

internal data class ButtonState(
    val setupScreenCameraButtonState: DefaultButtonState? = null,
    val setupScreenMicButtonState: DefaultButtonState? = null,
    val setupScreenAudioDeviceButtonState: DefaultButtonState? = null,

    val callScreenCameraButtonState: DefaultButtonState? = null,
    val callScreenMicButtonState: DefaultButtonState? = null,
    val callScreenAudioDeviceButtonState: DefaultButtonState? = null,

    val liveCaptionsButton: DefaultButtonState? = null,
    val liveCaptionsToggleButton: DefaultButtonState? = null,
    val spokenLanguageButton: DefaultButtonState? = null,
    val captionsLanguageButton: DefaultButtonState? = null,
    val shareDiagnosticsButton: DefaultButtonState? = null,
    val reportIssueButton: DefaultButtonState? = null,

    val callScreenCustomButtonsState: List<CustomButtonState> = emptyList(),
)
