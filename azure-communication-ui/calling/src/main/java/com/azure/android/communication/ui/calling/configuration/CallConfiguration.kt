// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.configuration

import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.ui.calling.DiagnosticConfig
import com.azure.android.communication.ui.calling.models.CallCompositePushNotificationInfo
import java.util.UUID

internal enum class CallType {
    GROUP_CALL,
    TEAMS_MEETING,
    ONE_TO_N_CALL
}

internal data class CallConfiguration(
    val communicationTokenCredential: CommunicationTokenCredential,
    val displayName: String,
    val groupId: UUID?,
    val meetingLink: String?,
    val callType: CallType,
    val participants: List<String>? = null,
    val pushNotificationInfo: CallCompositePushNotificationInfo? = null
) {
    val diagnosticConfig = DiagnosticConfig()
}
