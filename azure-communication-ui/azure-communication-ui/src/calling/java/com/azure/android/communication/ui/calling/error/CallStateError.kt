package com.azure.android.communication.ui.calling.error

import com.azure.android.communication.ui.calling.configuration.events.CommunicationUIErrorCode
import com.azure.android.communication.ui.calling.configuration.events.CommunicationUIEventCode

internal class CallStateError(
    val communicationUIErrorCode: CommunicationUIErrorCode,
    val communicationUIEventCode: CommunicationUIEventCode = CommunicationUIEventCode.NOP
)
