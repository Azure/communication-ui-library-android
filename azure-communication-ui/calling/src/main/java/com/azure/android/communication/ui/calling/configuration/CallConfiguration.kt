// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.configuration

import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.ui.calling.DiagnosticConfig
import com.azure.android.communication.ui.calling.models.CallCompositePushNotificationInfo
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantRole
import java.util.UUID

internal enum class CallType {
    GROUP_CALL,
    TEAMS_MEETING,
    ROOMS_CALL,
    ONE_TO_N_CALL_OUTGOING,
    ONE_TO_N_CALL_INCOMING
}

internal data class CallConfiguration(
    val communicationTokenCredential: CommunicationTokenCredential,
    val displayName: String,
    val groupId: UUID?,
    val meetingLink: String?,
    val roomId: String?,
    val roomRoleHint: CallCompositeParticipantRole?,
    val callType: CallType,
    val participants: Collection<CommunicationIdentifier>? = null,
    val pushNotificationInfo: CallCompositePushNotificationInfo? = null,
    val disableInternalPushForIncomingCall: Boolean,
) {
    val diagnosticConfig = DiagnosticConfig()
}
