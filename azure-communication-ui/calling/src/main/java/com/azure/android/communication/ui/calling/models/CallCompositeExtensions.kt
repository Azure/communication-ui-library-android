// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
/* <CUSTOM_CALL_HEADER> */
package com.azure.android.communication.ui.calling.models

import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.ui.calling.presentation.manager.CallScreenInfoHeaderManager

internal fun buildCallCompositeRemoteParticipantLeftEvent(
    identifiers: List<CommunicationIdentifier>,
): CallCompositeRemoteParticipantLeftEvent {
    return CallCompositeRemoteParticipantLeftEvent(
        identifiers
    )
}

internal fun CallCompositeCallScreenHeaderOptions.setManager(manager: CallScreenInfoHeaderManager) {
    this.callScreenInfoHeaderManager = manager
}
/* </CUSTOM_CALL_HEADER> */
