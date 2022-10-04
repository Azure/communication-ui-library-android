package com.azure.android.communication.ui.chat.service.sdk

import android.util.Log
import com.azure.android.communication.chat.models.ChatMessageType
import com.azure.android.communication.ui.chat.CoroutineContextProvider
import com.azure.android.communication.ui.chat.models.ChatCompositeRemoteOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class ChatLauncherPolling(
    private val pollingTime: Long,
    private val remoteOptions: ChatCompositeRemoteOptions,
) {

    private lateinit var wrapper: ChatSDK

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
        wrapper = ChatSDKWrapperPolling(pollingTime, chatThreadData, chatEventsHandler)

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
            wrapper.getTypingIndicatorReceivedEventSharedFlow().collect {
                Log.d("chatpoc TypingIndicatorReceived ", it.toString())
            }
        }

        coroutineScope.launch {
            wrapper.getReadReceiptReceivedEventSharedFlow().collect {
                Log.d("chatpoc ReadReceiptReceived ", it.toString())
            }
        }

        coroutineScope.launch {
            wrapper.getChatMessageEditedEventSharedFlow().collect {
                Log.d("chatpoc Edited ", it.toString())
            }
        }

        coroutineScope.launch {
            wrapper.getChatMessageDeletedEventSharedFlow().collect {
                Log.d("chatpoc Deleted ", it.toString())
            }
        }

        coroutineScope.launch {
            wrapper.getParticipantsRemovedEventSharedFlow().collect {
                Log.d("chatpoc ParticipantsRemovedEvent ", it.toString())
            }
        }

        coroutineScope.launch {
            wrapper.getParticipantsAddedEventSharedFlow().collect {
                Log.d("chatpoc ParticipantsAddedEvent ", it.toString())
            }
        }

        coroutineScope.launch {
            wrapper.getChatThreadPropertiesSharedFlow().collect {
                Log.d("chatpoc ChatThreadPropertiesShared ", it.toString())
            }
        }

        coroutineScope.launch {
            wrapper.getChatThreadDeletedEventSharedFlow().collect {
                Log.d("chatpoc ChatThreadDeletedEvent ", it.toString())
            }
        }

        coroutineScope.launch {
            wrapper.getChatThreadCreatedEventSharedFlow().collect {
                Log.d("chatpoc ChatThreadCreatedEvent ", it.toString())
            }
        }

        coroutineScope.launch {
            wrapper.getChatMessageReceivedEventSharedFlow().collect {
                Log.d("chatpoc ChatMessageReceivedEvent ", it.toString())
            }
        }

        coroutineScope.launch {
            wrapper.getMessagesSharedFlow().collect {
                var m = ""
                it.forEach {
                    m += it.content ?: " null "
                    m += " "
                }

                Log.d("chatpoc getMessagesSharedFlow ", m)
            }
        }
    }

    fun getMessagesFirstPage() {
        wrapper.getMessagesFirstPage()
    }

    fun getMessagesNextPage() {
        wrapper.getMessagesFirstPage()
    }

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
