// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
/* <CUSTOM_CALL_HEADER> */
package com.azure.android.communication.ui.calling.models

import android.content.Context
import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.ui.calling.presentation.manager.CallScreenInfoHeader

internal fun buildCallCompositeRemoteParticipantLeftEvent(
    identifiers: List<CommunicationIdentifier>,
): CallCompositeRemoteParticipantLeftEvent {
    return CallCompositeRemoteParticipantLeftEvent(
        identifiers
    )
}

internal fun CallCompositeCallScreenHeaderOptions.setManager(manager: CallScreenInfoHeader) {
    this.callScreenInfoHeader = manager
}
/* </CUSTOM_CALL_HEADER> */

internal fun createButtonClickEvent(
    context: Context,
    buttonOptions: CallCompositeButtonOptions,
): CallCompositeButtonClickEvent {
    return CallCompositeButtonClickEvent(context, buttonOptions)
}

internal fun createCustomButtonClickEvent(
    context: Context,
    buttonOptions: CallCompositeCustomButtonOptions,
): CallCompositeCustomButtonClickEvent {
    return CallCompositeCustomButtonClickEvent(context, buttonOptions)
}
