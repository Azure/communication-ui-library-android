package com.azure.android.communication.ui.chat.service.sdk

import android.content.Context
import android.util.Log
import com.azure.android.communication.chat.models.ChatMessageType
import com.azure.android.communication.ui.chat.CoroutineContextProvider
import com.azure.android.communication.ui.chat.implementation.ChatServiceConfigurationImpl
import com.azure.android.communication.ui.chat.models.ChatCompositeRemoteOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class ChatService(
    private val context: Context,
    private val remoteOptions: ChatCompositeRemoteOptions,
    private val onEvent: (Any) -> Unit
) {
    private lateinit var wrapper: ChatSDK

    fun stop() {
        if (::wrapper.isInitialized) {
            wrapper.removeChatThreadCreatedEventHandler()
            wrapper.removeTypingIndicatorReceivedEventHandler()
            wrapper.removeReadReceiptReceivedEventHandler()
            wrapper.removeParticipantRemovedEventHandler()
            wrapper.removeParticipantAddedEventHandler()
            wrapper.removeMessageReceivedEventHandler()
            wrapper.removeMessageEditedEventHandler()
            wrapper.removeMessageDeletedEventHandler()
            wrapper.removeChatThreadPropertiesUpdatedEventHandler()
            wrapper.removeChatThreadDeletedEventHandler()
        }
    }

    fun start() {
        val chatThreadData = ChatThreadData(
            remoteOptions.displayName,
            remoteOptions.communicationIdentifier,
            remoteOptions.credential,
            remoteOptions.locator.chatThreadId,
            remoteOptions.locator.endpoint,
            remoteOptions.applicationID,
            remoteOptions.sdkName,
            remoteOptions.sdkVersion
        )

        val chatEventsHandler = ChatEventsHandler(CoroutineContextProvider())
        val chatEventsFactory = ChatEventsFactory(chatEventsHandler)

        wrapper = if (ChatServiceConfigurationImpl.usePolling) {
            ChatSDKWrapperPolling(4000, chatThreadData, chatEventsHandler)
        } else {
            ChatSDKWrapper(context, chatThreadData, chatEventsFactory, chatEventsHandler)
        }

        wrapper.createChatClient()
        wrapper.createChatThreadClient()

        wrapper.startRealTimeNotifications()

        wrapper.addChatThreadCreatedEventHandler()
        wrapper.addChatThreadDeletedEventHandler()
        wrapper.addMessageDeletedEventHandler()
        wrapper.addMessageEditedEventHandler()
        wrapper.addTypingIndicatorReceivedEventHandler()
        wrapper.addReadReceiptReceivedEventHandler()
        wrapper.addParticipantRemovedEventHandler()
        wrapper.addParticipantAddedEventHandler()
        wrapper.addMessageReceivedEventHandler()
        wrapper.addChatThreadPropertiesUpdatedEventHandler()
        val coroutineScope = CoroutineScope((CoroutineContextProvider().Default))

        coroutineScope.launch {
            wrapper.getParticipantsRetrievedEventSharedFlow().collect {
                onEvent(it)
                Log.d("chatpoc ParticipantsRetrievedEventSharedFlow()", it.toString())
            }
        }
        coroutineScope.launch {
            wrapper.getTypingIndicatorReceivedEventSharedFlow().collect {
                onEvent(it)
                Log.d("chatpoc TypingIndicatorReceived ", it.toString())
            }
        }

        coroutineScope.launch {
            wrapper.getReadReceiptReceivedEventSharedFlow().collect {
                onEvent(it)
                Log.d("chatpoc ReadReceiptReceived ", it.toString())
            }
        }

        coroutineScope.launch {
            wrapper.getParticipantsRemovedEventSharedFlow().collect {
                onEvent(it)
                Log.d("chatpoc ParticipantsRemovedEvent ", it.toString())
            }
        }

        coroutineScope.launch {
            wrapper.getParticipantsAddedEventSharedFlow().collect {
                onEvent(it)
                Log.d("chatpoc ParticipantsAddedEvent ", it.toString())
            }
        }

        coroutineScope.launch {
            wrapper.getChatMessageEditedEventSharedFlow().collect {
                onEvent(it)
                Log.d("chatpoc edited ", it.toString())
            }
        }

        coroutineScope.launch {
            wrapper.getChatThreadPropertiesSharedFlow().collect {
                onEvent(it)
                Log.d("chatpoc ChatThreadPropertiesShared ", it.toString())
            }
        }

        coroutineScope.launch {
            wrapper.getChatThreadDeletedEventSharedFlow().collect {
                onEvent(it)
                Log.d("chatpoc ChatThreadDeletedEvent ", it.toString())
            }
        }

        coroutineScope.launch {
            wrapper.getChatThreadCreatedEventSharedFlow().collect {
                onEvent(it)
                Log.d("chatpoc ChatThreadCreatedEvent ", it.toString())
            }
        }

        coroutineScope.launch {
            wrapper.getChatMessageReceivedEventSharedFlow().collect {
                onEvent(it)
                Log.d("chatpoc ChatMessageReceivedEvent ", it.toString())
            }
        }

        coroutineScope.launch {
            wrapper.getMessagesSharedFlow().collect {
                onEvent(it)
                var m = ""
                it.forEach {
                    m += it.content ?: " null "
                    m += " "
                }

                Log.d("chatpoc getMessagesSharedFlow ", m)
            }
        }
    }

    fun requestMessagesFirstPage() {
        wrapper.getMessagesFirstPage()
    }

    fun requestMessagesNextPage() {
        wrapper.getMessagesFirstPage()
    }

    fun getMessagesNextPage() {
        wrapper.fetchMessagesNextPage()
    }

    fun requestParticipantList() = wrapper.requestListOfParticipants()

    fun sendTypingIndicator() {
        wrapper.sendTypingIndicator().handle { response, throwable ->
            val result = response
            val error = throwable
        }
    }

    fun sendMessage(type: ChatMessageType, content: String) {
        wrapper.sendMessage(type, content).handle { chatMessageResult, throwable ->
            val result = chatMessageResult
            val error = throwable
        }
    }

    fun sendReadReceipt(id: String) {
        wrapper.sendReadReceipt(id).handle { response, throwable ->
        }
    }

    fun editMessage(id: String, content: String) {
        wrapper.editMessage(id, content).handle { response, throwable ->
        }
    }

    fun deleteMessage(id: String) {
        wrapper.deleteMessage(id).handle { response, throwable ->
        }
    }

    fun removeSelfFromChat() {
        wrapper.removeSelfFromChat().handle { response, throwable ->
        }
    }
}
