package com.azure.android.communication.ui.calling.error

import com.azure.android.communication.ui.calling.models.CommunicationUIErrorCode
import com.azure.android.communication.ui.calling.models.CommunicationUIEventCode

internal class CallStateError(
    val communicationUIErrorCode: CommunicationUIErrorCode,
    val communicationUIEventCode: CommunicationUIEventCode? = null
)
