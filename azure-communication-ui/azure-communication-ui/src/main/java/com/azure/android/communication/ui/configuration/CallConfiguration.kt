// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration

import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.ui.DiagnosticConfig
import java.util.UUID

internal enum class CallType {
    GROUP_CALL,
    TEAMS_MEETING,
}

internal data class CallConfiguration(
    val communicationTokenCredential: CommunicationTokenCredential,
    val displayName: String,
    val groupId: UUID?,
    val meetingLink: String?,
    val callType: CallType,
) {
    val diagnosticConfig = DiagnosticConfig()
}
