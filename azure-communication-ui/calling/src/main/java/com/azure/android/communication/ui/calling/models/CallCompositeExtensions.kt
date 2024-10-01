// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.android.communication.ui.calling.models

import android.content.Context
import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import java.util.Date

internal fun buildCallCompositeRemoteParticipantLeftEvent(
    identifiers: List<CommunicationIdentifier>,
): CallCompositeRemoteParticipantLeftEvent {
    return CallCompositeRemoteParticipantLeftEvent(
        identifiers
    )
}

internal fun createCallCompositeCallStartTimeEvent(
    startTime: Date,
): CallCompositeCallStartTimeEvent {
    return CallCompositeCallStartTimeEvent(startTime)
}

internal fun CallCompositeCallScreenHeaderViewData.setSubtitleChangedEventHandler(handler: CallCompositeEventHandler<String?>) {
    this.subtitleChangedEventHandler = handler
}
internal fun CallCompositeCallScreenHeaderViewData.setTitleChangedEventHandler(handler: CallCompositeEventHandler<String?>) {
    this.titleChangedEventHandler = handler
}

internal fun createButtonClickEvent(
    context: Context,
    buttonOptions: CallCompositeButtonViewData,
): CallCompositeButtonClickEvent {
    return CallCompositeButtonClickEvent(context, buttonOptions)
}

internal fun createCustomButtonClickEvent(
    context: Context,
    buttonOptions: CallCompositeCustomButtonViewData,
): CallCompositeCustomButtonClickEvent {
    return CallCompositeCustomButtonClickEvent(context, buttonOptions)
}

internal fun CallCompositeCustomButtonViewData.setEnabledChangedEventHandler(handler: CallCompositeEventHandler<Boolean>) {
    this.enabledChangedEventHandler = handler
}

internal fun CallCompositeCustomButtonViewData.setVisibleChangedEventHandler(handler: CallCompositeEventHandler<Boolean>) {
    this.visibleChangedEventHandler = handler
}

internal fun CallCompositeButtonViewData.setEnabledChangedEventHandler(handler: CallCompositeEventHandler<Boolean>) {
    this.enabledChangedEventHandler = handler
}

internal fun CallCompositeButtonViewData.setVisibleChangedEventHandler(handler: CallCompositeEventHandler<Boolean>) {
    this.visibleChangedEventHandler = handler
}
