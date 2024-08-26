// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
/* <CUSTOM_CALL_HEADER> */
package com.azure.android.communication.ui.calling.models

import android.content.Context
import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.ui.calling.CallCompositeEventHandler

internal fun buildCallCompositeRemoteParticipantLeftEvent(
    identifiers: List<CommunicationIdentifier>,
): CallCompositeRemoteParticipantLeftEvent {
    return CallCompositeRemoteParticipantLeftEvent(
        identifiers
    )
}

internal fun CallCompositeCallScreenHeaderOptions.setSubtitleChangedEventHandler(handler: CallCompositeEventHandler<String?>) {
    this.subtitleChangedEventHandler = handler
}
internal fun CallCompositeCallScreenHeaderOptions.setTitleChangedEventHandler(handler: CallCompositeEventHandler<String?>) {
    this.titleChangedEventHandler = handler
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

internal fun CallCompositeCustomButtonOptions.setEnabledChangedEventHandler(handler: CallCompositeEventHandler<Boolean>) {
    this.enabledChangedEventHandler = handler
}

internal fun CallCompositeCustomButtonOptions.setVisibleChangedEventHandler(handler: CallCompositeEventHandler<Boolean>) {
    this.visibleChangedEventHandler = handler
}

internal fun CallCompositeButtonOptions.setEnabledChangedEventHandler(handler: CallCompositeEventHandler<Boolean>) {
    this.enabledChangedEventHandler = handler
}

internal fun CallCompositeButtonOptions.setVisibleChangedEventHandler(handler: CallCompositeEventHandler<Boolean>) {
    this.visibleChangedEventHandler = handler
}