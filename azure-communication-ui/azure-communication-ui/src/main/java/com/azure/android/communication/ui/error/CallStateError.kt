package com.azure.android.communication.ui.error

import com.azure.android.communication.ui.configuration.events.CommunicationUIErrorCode
import com.azure.android.communication.ui.configuration.events.CommunicationUIEventCode

internal class CallStateError(
    val communicationUIErrorCode: CommunicationUIErrorCode,
    val communicationUIEventCode: CommunicationUIEventCode = CommunicationUIEventCode.NOP
)
