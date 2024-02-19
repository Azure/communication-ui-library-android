// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.service.sdk

import com.azure.android.communication.chat.ChatThreadClient
import com.azure.android.communication.chat.models.ChatEventType
import com.azure.android.communication.chat.models.ChatMessageType
import com.azure.android.communication.chat.models.ListChatMessagesOptions
import com.azure.android.communication.ui.chat.models.ChatEventModel
import com.azure.android.communication.ui.chat.models.ChatThreadInfoModel
import com.azure.android.communication.ui.chat.models.RemoteParticipantInfoModel
import com.azure.android.communication.ui.chat.models.RemoteParticipantsInfoModel
import com.azure.android.communication.ui.chat.models.into
import com.azure.android.communication.ui.chat.service.sdk.wrapper.into
import com.azure.android.communication.ui.chat.utilities.CoroutineContextProvider
import com.azure.android.core.util.RequestContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.OffsetDateTime
import java.util.concurrent.Executors

internal class ChatFetchNotificationHandler(coroutineContextProvider: CoroutineContextProvider, val localParticipantIdentifier: String) {
    private val coroutineScope = CoroutineScope((coroutineContextProvider.Default))
    private val singleThreadedContext = Executors.newSingleThreadExecutor()

    private lateinit var eventSubscriber: (ChatEventModel) -> Unit
    private lateinit var chatThreadClient: ChatThreadClient

    private val listChatMessagesOptions =
        ListChatMessagesOptions().apply {
            maxPageSize = ChatSDKWrapper.PAGE_MESSAGES_SIZE
        }

    fun fetchMessages(from: OffsetDateTime?) {
        coroutineScope.launch {
            withContext(singleThreadedContext.asCoroutineDispatcher()) {
                fetchMessagesList(from)
            }
        }
    }

    fun start(
        chatThreadClient: ChatThreadClient,
        eventSubscriber: (ChatEventModel) -> Unit,
    ) {
        this.chatThreadClient = chatThreadClient
        this.eventSubscriber = eventSubscriber
    }

    fun stop() {
        singleThreadedContext.shutdown()
        coroutineScope.cancel()
    }

    private fun fetchMessagesList(from: OffsetDateTime?) {
        try {
            listChatMessagesOptions.startTime = from

            val messages =
                chatThreadClient.listMessages(listChatMessagesOptions, RequestContext.NONE)

            messages.byPage().forEach { pages ->
                pages.elements.forEach { message ->

                    if (message.type == ChatMessageType.PARTICIPANT_ADDED) {
                        val model =
                            RemoteParticipantsInfoModel(
                                participants =
                                    message.content.participants.map {
                                        RemoteParticipantInfoModel(
                                            userIdentifier = it.communicationIdentifier.into(),
                                            displayName = it.displayName,
                                            isLocalUser = it.communicationIdentifier.into().id == this.localParticipantIdentifier,
                                        )
                                    },
                            )
                        val infoModel =
                            ChatEventModel(
                                eventType = ChatEventType.PARTICIPANTS_ADDED.into(),
                                infoModel = model,
                                eventReceivedOffsetDateTime = null,
                            )
                        eventSubscriber(infoModel)
                    }

                    if (message.type == ChatMessageType.PARTICIPANT_REMOVED) {
                        val model =
                            RemoteParticipantsInfoModel(
                                participants =
                                    message.content.participants.map {
                                        RemoteParticipantInfoModel(
                                            userIdentifier = it.communicationIdentifier.into(),
                                            displayName = it.displayName,
                                            isLocalUser = it.communicationIdentifier.into().id == this.localParticipantIdentifier,
                                        )
                                    },
                            )
                        val infoModel =
                            ChatEventModel(
                                eventType = ChatEventType.PARTICIPANTS_REMOVED.into(),
                                infoModel = model,
                                eventReceivedOffsetDateTime = null,
                            )
                        eventSubscriber(infoModel)
                    }

                    if (message.type == ChatMessageType.HTML || message.type == ChatMessageType.TEXT) {
                        if (message.deletedOn != null) {
                            val infoModel =
                                ChatEventModel(
                                    eventType = ChatEventType.CHAT_MESSAGE_DELETED.into(),
                                    infoModel = message.into(localParticipantIdentifier),
                                    eventReceivedOffsetDateTime = null,
                                )
                            eventSubscriber(infoModel)
                        }

                        if (message.editedOn != null) {
                            val infoModel =
                                ChatEventModel(
                                    eventType = ChatEventType.CHAT_MESSAGE_EDITED.into(),
                                    infoModel = message.into(localParticipantIdentifier),
                                    eventReceivedOffsetDateTime = null,
                                )
                            eventSubscriber(infoModel)
                        }

                        // new message
                        if (message.deletedOn == null && message.editedOn == null) {
                            val infoModel =
                                ChatEventModel(
                                    eventType = ChatEventType.CHAT_MESSAGE_RECEIVED.into(),
                                    infoModel = message.into(localParticipantIdentifier),
                                    eventReceivedOffsetDateTime = null,
                                )
                            eventSubscriber(infoModel)
                        }
                    }

                    if (message.type == ChatMessageType.TOPIC_UPDATED) {
                        val model =
                            ChatThreadInfoModel(
                                receivedOn = message.editedOn ?: message.createdOn,
                                topic = message.content.topic,
                            )
                        val infoModel =
                            ChatEventModel(
                                eventType = ChatEventType.CHAT_THREAD_PROPERTIES_UPDATED.into(),
                                infoModel = model,
                                eventReceivedOffsetDateTime = null,
                            )
                        eventSubscriber(infoModel)
                    }
                }
            }
        } catch (ex: Exception) {
            // TODO: notify sdk wrapper about error in future
        }
    }
}
