// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.redux.middleware.repository

import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.redux.AppStore
import com.azure.android.communication.ui.chat.redux.action.ChatAction
import com.azure.android.communication.ui.chat.redux.state.ReduxState
import com.azure.android.communication.ui.chat.repository.MessageRepository
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType
import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.mock

internal class MessageRepositoryMiddlewareUnitTest {
    @Test
    fun messageRepositoryMiddleware_invoke_when_invokedWithAnyAction_then_invokeNext() {
        val message =
            MessageInfoModel(
                id = "1",
                content = "Message 1",
                messageType = ChatMessageType.TEXT,
            )

        // arrange
        val actionToDispatch = ChatAction.SendMessage(message)
        var nextReceivedAction: ChatAction? = null

        val mockMessageRepository = mock<MessageRepository> {}

        val messageRepositoryMiddlewareImplementation =
            MessageRepositoryMiddlewareImpl(
                mockMessageRepository,
            )

        val mockAppStore = mock<AppStore<ReduxState>> {}

        // act
        messageRepositoryMiddlewareImplementation.invoke(mockAppStore)(
            fun(action) {
                nextReceivedAction = action as ChatAction
            },
        )(actionToDispatch)

        // assert
        Assert.assertEquals(
            actionToDispatch,
            nextReceivedAction,
        )
    }
}
