package com.azure.android.communication.ui.chat.implementation.redux.middleware

import com.azure.android.communication.chat.models.ChatMessageType
import com.azure.android.communication.ui.arch.redux.Dispatch
import com.azure.android.communication.ui.arch.redux.GenericStore
import com.azure.android.communication.ui.arch.redux.Middleware
import com.azure.android.communication.ui.chat.implementation.redux.actions.UserActions
import com.azure.android.communication.ui.chat.service.sdk.ChatService

internal class ChatActionMiddleware(private val chatService: ChatService) : Middleware {
    override fun invoke(store: GenericStore): (Dispatch) -> Dispatch = { next: Dispatch ->
        { action: Any ->
            when (action) {
                is UserActions.IsTyping -> {
                    chatService.sendTypingIndicator()
                    // We can ignore, no need to reduce state
                }
                is UserActions.SendMessage -> {
                    chatService.sendMessage(
                        type = ChatMessageType.TEXT,
                        content = action.message
                    )
                }
                else -> next(action)
            }
        }
    }
}
