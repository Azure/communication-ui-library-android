// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.middleware.listener

import com.azure.android.communication.ui.chat.redux.Dispatch
import com.azure.android.communication.ui.chat.redux.action.Action
import com.azure.android.communication.ui.chat.service.ChatService
import kotlin.reflect.KFunction1

internal class ChatServiceListener(
    private val chatService: ChatService,
) {
    // start listening the chatService here
    //
    // Args:
    //  - Dispatcher
    //  Function to Dispatch Actions after Events from the SDK
    fun startListening(dispatcher: KFunction1<Action, Unit>) {
        // TODO: Bind service listeners here
        // I.e,
        // csb = ChatServiceBindings(chatService dispatcher)
    }

    fun stopListening() {
        // Todo: Cleanup bindings here
        // csb.dispose
    }
}

// Example Placeholder of a Bindings class, to handle the Event Listeners
internal class ChatServiceBindings(val chatService: ChatService, val dispatcher: Dispatch) {
    init {
        // Bind methods
        // e.g. chatService.addOnEventListener(::onEvent)
    }

    // TODO: Implement event handler methods
    // Method to be bound (signature may vary based on Call Sdk)
    fun onEvent(sdkEvent : Any) {
        /*
            Logic for mapping EVENT -> Action
            I.e.
            when (sdkEvent) {
                is NewMessageEvent : dispatch(sdkEvent.convertToAction())
                is ParticipantsReceivedEvent: dispatch(sdkEvent.convertToAction())
            }
         */
    }

    // Error handler
    fun onError(exception : Throwable) {

    }

    // Clean up listeners
    fun dispose() {
        // Remove Listeners
        // E.g. chatService.remoteOnEventListener(::onEvent)
    }

}

/*
 Convertors:
 Add functions to convert from Event->Action here.

 fun toAction(newMessageEvent : NewMessageEvent) = ChatAction.newMessageAction(...)
*/
