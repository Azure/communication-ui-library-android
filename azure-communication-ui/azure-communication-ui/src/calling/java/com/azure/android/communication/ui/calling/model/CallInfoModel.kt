package com.azure.android.communication.ui.calling.model

import com.azure.android.communication.ui.calling.error.CallStateError
import com.azure.android.communication.ui.calling.redux.state.CallingStatus

internal class CallInfoModel(val callingStatus: CallingStatus, val callStateError: CallStateError?)
