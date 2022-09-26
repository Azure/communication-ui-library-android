// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat

import android.content.Context
import com.azure.android.communication.ui.chat.configuration.ChatConfiguration
import com.azure.android.communication.ui.chat.configuration.RemoteParticipantsConfiguration
import com.azure.android.communication.ui.chat.locator.ServiceLocator
import com.azure.android.communication.ui.chat.models.ChatCompositeLocalOptions
import com.azure.android.communication.ui.chat.models.ChatCompositeLocalizationOptions
import com.azure.android.communication.ui.chat.models.ChatCompositeRemoteOptions
import com.azure.android.communication.ui.chat.models.ChatCompositeUnreadMessageChangedEvent
import com.azure.android.communication.ui.chat.redux.AppStore
import com.azure.android.communication.ui.chat.redux.action.LifecycleAction
import com.azure.android.communication.ui.chat.redux.middleware.ChatServiceMiddleware
import com.azure.android.communication.ui.chat.redux.reducer.AppStateReducer
import com.azure.android.communication.ui.chat.redux.reducer.ChatReducerImpl
import com.azure.android.communication.ui.chat.redux.reducer.ErrorReducerImpl
import com.azure.android.communication.ui.chat.redux.reducer.LifecycleReducerImpl
import com.azure.android.communication.ui.chat.redux.reducer.NavigationReducerImpl
import com.azure.android.communication.ui.chat.redux.reducer.ParticipantsReducerImpl
import com.azure.android.communication.ui.chat.redux.state.AppReduxState
import com.azure.android.communication.ui.chat.redux.state.ReduxState
import com.azure.android.communication.ui.chat.service.ChatService
import com.azure.android.communication.ui.chat.service.sdk.ChatSDKWrapper
import com.azure.android.communication.ui.chat.utilities.CoroutineContextProvider

internal class ChatContainer(private val instanceId: Int,
                             private val localization : ChatCompositeLocalizationOptions
) {
    var started = false
    private var locator: ServiceLocator? = null
    private val onUnreadMessageChangedHandlers =
        mutableSetOf<ChatCompositeEventHandler<ChatCompositeUnreadMessageChangedEvent>>()

    fun start(
        context: Context,
        remoteOptions: ChatCompositeRemoteOptions,
        localOptions: ChatCompositeLocalOptions?,
    ) {
        // currently only single instance is supported
        if (!started) {
            started = true
            locator = initializeDependencies(
                instanceId = instanceId,
                remoteOptions = remoteOptions,
                localOptions = localOptions,
                context = context
            ).apply {
                // Wake up the store
                locate<AppStore<ReduxState>>().dispatch(LifecycleAction.Wakeup())
            }
        }
    }

    fun stop() {
        locator?.clear()
    }

    fun addOnViewClosedEventHandler(handler: ChatCompositeEventHandler<Any>) {
    }

    fun removeOnViewClosedEventHandler(handler: ChatCompositeEventHandler<Any>) {
    }

    fun addOnUnreadMessagesChangedEventHandler(handler: ChatCompositeEventHandler<ChatCompositeUnreadMessageChangedEvent>) {
        onUnreadMessageChangedHandlers.add(handler)
    }

    fun removeOnUnreadMessagesChangedEventHandler(handler: ChatCompositeEventHandler<ChatCompositeUnreadMessageChangedEvent>) {
        onUnreadMessageChangedHandlers.remove(handler)
    }

    val remoteParticipantsConfiguration get() = RemoteParticipantsConfiguration()

    companion object {
        // Initialize a Instance ID of the Service Locator
        internal fun initializeDependencies(
            instanceId: Int,
            remoteOptions: ChatCompositeRemoteOptions,
            localOptions: ChatCompositeLocalOptions?,
            context: Context
        ) = ServiceLocator.getInstance(instanceId = instanceId).apply {
                clear()

            val contextProvider =   CoroutineContextProvider()

            // ChatConfiguration
                addTypedBuilder {
                    ChatConfiguration(
                        endPointURL = remoteOptions.locator.endpointURL,
                        credential = remoteOptions.credential,
                        applicationID = "azure_communication_ui", // TODO: modify while working on diagnostics config < 24
                        sdkName = "com.azure.android:azure-communication-chat",
                        sdkVersion = "2.0.0",
                        threadId = remoteOptions.locator.chatThreadId,
                        senderDisplayName = remoteOptions.displayName
                    )
                }

                // Local Options
                if (localOptions != null) {
                    addTypedBuilder { localOptions }
                } else {
                    addTypedBuilder { ChatCompositeLocalOptions() }
                }

                // Remote Options
                addTypedBuilder { remoteOptions }

                // Chat Service
                addTypedBuilder {
                    ChatService(
                        chatSDK = ChatSDKWrapper(
                            context = context,
                            chatConfig = locate(),
                        )
                    )
                }


                addTypedBuilder {
                    AppStore(
                        initialState = AppReduxState(),
                        reducer = AppStateReducer(
                            chatReducer = ChatReducerImpl(),
                            participantReducer = ParticipantsReducerImpl(),
                            lifecycleReducer = LifecycleReducerImpl(),
                            errorReducer = ErrorReducerImpl(),
                            navigationReducer = NavigationReducerImpl()
                        ),
                        middlewares = mutableListOf(
                            ChatServiceMiddleware(
                                chatService = locate()
                            )
                        ),
                        dispatcher = contextProvider.SingleThreaded
                    )
                }
            }
    }
}
