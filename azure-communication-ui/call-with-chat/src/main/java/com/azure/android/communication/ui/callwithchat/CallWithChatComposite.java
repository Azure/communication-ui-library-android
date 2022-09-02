// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callwithchat;

import android.content.Context;

import com.azure.android.communication.common.CommunicationIdentifier;
import com.azure.android.communication.ui.callwithchat.models.CallWithChatCompositeErrorCode;
import com.azure.android.communication.ui.callwithchat.models.CallWithChatCompositeErrorEvent;
import com.azure.android.communication.ui.callwithchat.models.CallWithChatCompositeLocalOptions;
import com.azure.android.communication.ui.callwithchat.models.CallWithChatCompositeParticipantViewData;
import com.azure.android.communication.ui.callwithchat.models.CallWithChatCompositeRemoteOptions;
import com.azure.android.communication.ui.callwithchat.models.CallWithChatCompositeEventHandler;
import com.azure.android.communication.ui.calling.CallComposite;
import com.azure.android.communication.ui.callwithchat.models.CallWithChatCompositeRemoteParticipantJoinedEvent;
import com.azure.android.communication.ui.callwithchat.models.CallWithChatCompositeSetParticipantViewDataResult;
import com.azure.android.communication.ui.chat.ChatComposite;

public final class CallWithChatComposite {

    private CallWithChatManager callWithChatManager;

    CallWithChatComposite(final CallComposite callComposite,
                          final ChatComposite chatComposite) {

        callWithChatManager = new CallWithChatManager(callComposite, chatComposite);
    }

    public void launch(final Context context,
                       final CallWithChatCompositeRemoteOptions remoteOptions) {
        launch(context, remoteOptions, null);
    }

    public void launch(final Context context,
                       final CallWithChatCompositeRemoteOptions remoteOptions,
                       final CallWithChatCompositeLocalOptions localOptions) {

        callWithChatManager.launch(context, remoteOptions, localOptions);
    }

    /**
     * Add {@link CallWithChatCompositeEventHandler}.
     *
     * <p> A callback for Call Composite Error Events.
     * See {@link CallWithChatCompositeErrorCode} for values.</p>
     * <pre>
     *
     * &#47;&#47; add error handler
     * callWithChatComposite.addOnErrorEventHandler&#40;event -> {
     *     &#47;&#47; Process error event
     *     System.out.println&#40;event.getCause&#40;&#41;&#41;;
     *     System.out.println&#40;event.getErrorCode&#40;&#41;&#41;;
     * }&#41;;
     *
     * </pre>
     *
     * @param errorHandler The {@link CallWithChatCompositeEventHandler}.
     */
    public void addOnErrorEventHandler(
            final CallWithChatCompositeEventHandler<CallWithChatCompositeErrorEvent> errorHandler) {
    }

    /**
     * Remove {@link CallWithChatCompositeEventHandler}.
     *
     * <p> A callback for Call Composite Error Events.
     * See {@link CallWithChatCompositeErrorEvent} for values.</p>
     *
     * @param errorHandler The {@link CallWithChatCompositeEventHandler}.
     */
    public void removeOnErrorEventHandler(
            final CallWithChatCompositeEventHandler<CallWithChatCompositeErrorEvent> errorHandler) {
    }

    /**
     * Add {@link CallWithChatCompositeEventHandler}.
     *
     * <pre>
     *
     * &#47;&#47; add remote participant joined handler
     * callWithChatComposite.addOnRemoteParticipantJoinedEventHandler&#40;event -> {
     *     &#47;&#47; Use call composite to set configurations for remote participant
     * }&#41;;
     *
     * </pre>
     *
     * @param eventHandler The {@link CallWithChatCompositeEventHandler}.
     */
    public void addOnRemoteParticipantJoinedEventHandler(
            final CallWithChatCompositeEventHandler<CallWithChatCompositeRemoteParticipantJoinedEvent> eventHandler) {
    }

    /**
     * Remove {@link CallWithChatCompositeEventHandler}.
     *
     * @param eventHandler The {@link CallWithChatCompositeEventHandler}.
     */
    public void removeOnRemoteParticipantJoinedEventHandler(
            final CallWithChatCompositeEventHandler<CallWithChatCompositeRemoteParticipantJoinedEvent> eventHandler) {
    }

    /**
     * Set {@link CallWithChatCompositeParticipantViewData}.
     *
     * <p>
     *     Used to set Participant View Data (E.g. Avatar and displayName) to be used on this device only.
     * </p>
     * <p>
     *     This should be called from {@link #addOnRemoteParticipantJoinedEventHandler(
     *     CallWithChatCompositeEventHandler)} to assign Participant View Data when a Participant
     *     joins the meeting if you'd like to modify the Participants view data.
     * </p>
     *
     * @param identifier  The {@link CommunicationIdentifier}.
     * @param participantViewData The {@link CallWithChatCompositeParticipantViewData}.
     * @return {@link CallWithChatCompositeSetParticipantViewDataResult}.
     */
    public CallWithChatCompositeSetParticipantViewDataResult setRemoteParticipantViewData(
            final CommunicationIdentifier identifier,
            final CallWithChatCompositeParticipantViewData participantViewData) {
        return null;
    }
}
