// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.configuration

import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.ui.calling.DiagnosticConfig
/* <ROOMS_SUPPORT:0> */
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantRole
/* </ROOMS_SUPPORT:0> */
import java.util.UUID

internal enum class CallType {
    GROUP_CALL,
    TEAMS_MEETING,
    ONE_TO_N_OUTGOING,
    ONE_TO_ONE_INCOMING,
    /* <ROOMS_SUPPORT:3> */
    ROOMS_CALL,
    /* </ROOMS_SUPPORT:0> */
}

internal data class CallConfiguration(
    val groupId: UUID?,
    val meetingLink: String?,
    /* <MEETING_ID_LOCATOR> */
    val meetingId: String?,
    val meetingPasscode: String?,
    /* </MEETING_ID_LOCATOR> */
    /* <ROOMS_SUPPORT:5> */
    val roomId: String?,
    val roomRoleHint: CallCompositeParticipantRole?,
    /* </ROOMS_SUPPORT:4> */
    val callType: CallType,
    val participants: Collection<CommunicationIdentifier>? = null,
    val incomingCallId: String? = null,
) {
    val diagnosticConfig = DiagnosticConfig()
}
