// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.configuration

import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.ui.calling.DiagnosticConfig
/* <ROOMS_SUPPORT:0> */
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantRole
/* </ROOMS_SUPPORT:0> */
import java.util.UUID

internal enum class CallType {
    GROUP_CALL,
    TEAMS_MEETING,
    ROOMS_CALL,
}

internal data class CallConfiguration(
    val communicationTokenCredential: CommunicationTokenCredential,
    val displayName: String,
    val groupId: UUID?,
    val meetingLink: String?,
    /* <ROOMS_SUPPORT:5> */
    val roomId: String?,
    val roomRoleHint: CallCompositeParticipantRole?,
    /* </ROOMS_SUPPORT:4> */
    val callType: CallType,
) {
    val diagnosticConfig = DiagnosticConfig()
}
