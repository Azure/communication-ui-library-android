// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.configuration

import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.ui.calling.DiagnosticConfig
import java.util.UUID

internal enum class CallType {
    GROUP_CALL,
    TEAMS_MEETING,
    ONE_TO_N_OUTGOING,
    ONE_TO_ONE_INCOMING,
    ROOMS_CALL,
}

internal data class CallConfiguration(
    val groupId: UUID?,
    val meetingLink: String?,
    val meetingId: String?,
    val meetingPasscode: String?,
    val roomId: String?,
    val callType: CallType,
    val participants: Collection<CommunicationIdentifier>? = null,
    val incomingCallId: String? = null,
) {
    val diagnosticConfig = DiagnosticConfig()
}
