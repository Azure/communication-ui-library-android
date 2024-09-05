package com.azure.android.communication.ui.calling.redux.action

internal sealed class ButtonViewDataAction : Action {
    class CallScreenCameraButtonIsEnabledUpdated(val isEnabled: Boolean) : ButtonViewDataAction()
    class CallScreenCameraButtonIsVisibleUpdated(val isVisible: Boolean) : ButtonViewDataAction()
    class CallScreenMicButtonIsEnabledUpdated(val isEnabled: Boolean) : ButtonViewDataAction()
    class CallScreenMicButtonIsVisibleUpdated(val isVisible: Boolean) : ButtonViewDataAction()

    class CallScreenAudioDeviceButtonIsEnabledUpdated(val isEnabled: Boolean) : ButtonViewDataAction()
    class CallScreenAudioDeviceButtonIsVisibleUpdated(val isVisible: Boolean) : ButtonViewDataAction()

    class CallScreenLiveCaptionsButtonIsEnabledUpdated(val isEnabled: Boolean) : ButtonViewDataAction()
    class CallScreenLiveCaptionsButtonIsVisibleUpdated(val isVisible: Boolean) : ButtonViewDataAction()

    class CallScreenLiveCaptionsToggleButtonIsEnabledUpdated(val isEnabled: Boolean) : ButtonViewDataAction()
    class CallScreenLiveCaptionsToggleButtonIsVisibleUpdated(val isVisible: Boolean) : ButtonViewDataAction()

    class CallScreenSpokenLanguageButtonIsEnabledUpdated(val isEnabled: Boolean) : ButtonViewDataAction()
    class CallScreenSpokenLanguageButtonIsVisibleUpdated(val isVisible: Boolean) : ButtonViewDataAction()

    class CallScreenCaptionsLanguageButtonIsEnabledUpdated(val isEnabled: Boolean) : ButtonViewDataAction()
    class CallScreenCaptionsLanguageButtonIsVisibleUpdated(val isVisible: Boolean) : ButtonViewDataAction()

    class CallScreenShareDiagnosticsButtonIsEnabledUpdated(val isEnabled: Boolean) : ButtonViewDataAction()
    class CallScreenShareDiagnosticsButtonIsVisibleUpdated(val isVisible: Boolean) : ButtonViewDataAction()

    class CallScreenReportIssueButtonIsEnabledUpdated(val isEnabled: Boolean) : ButtonViewDataAction()
    class CallScreenReportIssueButtonIsVisibleUpdated(val isVisible: Boolean) : ButtonViewDataAction()

    class CallScreenCustomButtonIsEnabledUpdated(val id: String, val isEnabled: Boolean) : ButtonViewDataAction()
    class CallScreenCustomButtonIsVisibleUpdated(val id: String, val isVisible: Boolean) : ButtonViewDataAction()
}
