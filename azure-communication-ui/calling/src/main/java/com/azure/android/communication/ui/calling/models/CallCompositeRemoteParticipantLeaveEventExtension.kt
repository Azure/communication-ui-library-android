// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models

import com.azure.android.communication.common.CommunicationIdentifier

internal fun buildCallCompositeRemoteParticipantLeaveEvent(
    identifiers: List<CommunicationIdentifier>,
): CallCompositeRemoteParticipantLeaveEvent {
    return CallCompositeRemoteParticipantLeaveEvent(identifiers)
}
