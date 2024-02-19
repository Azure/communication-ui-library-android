package com.azure.android.communication.ui.calling.error

import com.azure.android.communication.ui.calling.models.CallCompositeEventCode

internal class CallStateError(
    val errorCode: ErrorCode,
    val callCompositeEventCode: CallCompositeEventCode? = null,
)
