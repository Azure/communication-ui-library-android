package com.azure.android.communication.ui.calling.error

import com.azure.android.communication.ui.calling.models.CallCompositeErrorCode
import com.azure.android.communication.ui.calling.models.CallCompositeEventCode

internal class CallStateError(
    val callCompositeErrorCode: CallCompositeErrorCode,
    val callCompositeEventCode: CallCompositeEventCode? = null
)
