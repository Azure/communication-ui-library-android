package com.azure.android.communication.ui.model

import com.azure.android.communication.ui.error.CallStateError
import com.azure.android.communication.ui.redux.state.CallingStatus

internal class CallInfoModel(val callingStatus: CallingStatus, val callStateError: CallStateError?)
