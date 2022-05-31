package com.azure.android.communication.ui.calling.error

import com.azure.android.communication.ui.calling.models.internal.ErrorCode
import com.azure.android.communication.ui.calling.models.EventCode

internal class CallStateError(
    val errorCode: ErrorCode,
    val eventCode: EventCode? = null
)
