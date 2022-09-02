// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callwithchat;

import com.azure.android.communication.ui.calling.CallComposite;
import com.azure.android.communication.ui.calling.CallCompositeBuilder;
//import com.azure.android.communication.ui.calling.models.CallCompositeCustomButtonViewData;
//import com.azure.android.communication.ui.calling.models.CallCompositeOverlayOptions;
//import com.azure.android.communication.ui.calling.models.CallCompositeCustomButtonType;
//import com.azure.android.communication.ui.chat.ChatComposite;
//import com.azure.android.communication.ui.chat.ChatCompositeBuilder;

import java.util.ArrayList;

public class CallWithChatCompositeBuilder {
    /**
     * Builds the {@link CallWithChatComposite}.
     *
     * @return {@link CallWithChatComposite}
     */
    public CallWithChatComposite build() {

//        final ChatCompositeBuilder chatCompositeBuilder = new ChatCompositeBuilder();
//        final ChatComposite chatComposite = chatCompositeBuilder.build();
//
//        final CallCompositeBuilder callCompositeBuilder = new CallCompositeBuilder();
//        final CallingIntegrationBridgeImpl callingIntegrationBridge =
//                new CallingIntegrationBridgeImpl(callCompositeBuilder);
//
//        // TODO: consider to move chatButton code
//        final CallCompositeCustomButtonViewData chatButton = new CallCompositeCustomButtonViewData(
//                CallCompositeCustomButtonType.CALL_SCREEN_INFO_HEADER,
//                R.drawable.azure_communication_ui_callwithchat_ic_fluent_chat_24_regular,
//                "Open Chat",
//                null);
//
//        chatComposite.addOnUnreadMessagesChangedEventHandler(eventArgs ->
//            chatButton.setBadgeNumber(eventArgs.getCount())
//        );
//
//        final ArrayList<CallCompositeCustomButtonViewData> customButtonStates =
//                new ArrayList<CallCompositeCustomButtonViewData>() { { add(chatButton); } };
//
//        final CallComposite callComposite = callCompositeBuilder
//                .customButtonViewData(customButtonStates)
//                .build();
//
//        chatButton.setOnClickEventHandler(eventArgs -> {
//            final CallCompositeOverlayOptions overlayOptions = new CallCompositeOverlayOptions();
//            callingIntegrationBridge.setOverlay(chatComposite::getCompositeUIView, overlayOptions);
//            callComposite.setOverlay(chatComposite::getCompositeUIView, overlayOptions);
//        });
//
//        return new CallWithChatComposite(callComposite, callingIntegrationBridge, chatComposite);
        return null;
    }
}
