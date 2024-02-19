// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat

import android.content.Context
import com.azure.android.communication.ui.chat.configuration.ChatCompositeConfiguration
import com.azure.android.communication.ui.chat.configuration.ChatConfiguration
import com.azure.android.communication.ui.chat.error.ChatErrorHandler
import com.azure.android.communication.ui.chat.error.EventHandler
import com.azure.android.communication.ui.chat.locator.ServiceLocator
import com.azure.android.communication.ui.chat.logger.DefaultLogger
import com.azure.android.communication.ui.chat.logger.Logger
import com.azure.android.communication.ui.chat.models.ChatCompositeRemoteOptions
import com.azure.android.communication.ui.chat.presentation.manager.NetworkManager
import com.azure.android.communication.ui.chat.redux.AppStore
import com.azure.android.communication.ui.chat.redux.Dispatch
import com.azure.android.communication.ui.chat.redux.action.ChatAction
import com.azure.android.communication.ui.chat.redux.middleware.repository.MessageRepositoryMiddlewareImpl
import com.azure.android.communication.ui.chat.redux.middleware.sdk.ChatActionHandler
import com.azure.android.communication.ui.chat.redux.middleware.sdk.ChatMiddlewareImpl
import com.azure.android.communication.ui.chat.redux.middleware.sdk.ChatServiceListener
import com.azure.android.communication.ui.chat.redux.reducer.AccessibilityReducerImpl
import com.azure.android.communication.ui.chat.redux.reducer.AppStateReducer
import com.azure.android.communication.ui.chat.redux.reducer.ChatReducerImpl
import com.azure.android.communication.ui.chat.redux.reducer.ErrorReducerImpl
import com.azure.android.communication.ui.chat.redux.reducer.LifecycleReducerImpl
import com.azure.android.communication.ui.chat.redux.reducer.NavigationReducerImpl
import com.azure.android.communication.ui.chat.redux.reducer.NetworkReducerImpl
import com.azure.android.communication.ui.chat.redux.reducer.ParticipantsReducerImpl
import com.azure.android.communication.ui.chat.redux.reducer.Reducer
import com.azure.android.communication.ui.chat.redux.reducer.RepositoryReducerImpl
import com.azure.android.communication.ui.chat.redux.state.AppReduxState
import com.azure.android.communication.ui.chat.redux.state.ReduxState
import com.azure.android.communication.ui.chat.repository.MessageRepository
import com.azure.android.communication.ui.chat.service.ChatService
import com.azure.android.communication.ui.chat.service.sdk.ChatEventHandler
import com.azure.android.communication.ui.chat.service.sdk.ChatFetchNotificationHandler
import com.azure.android.communication.ui.chat.service.sdk.ChatSDKWrapper
import com.azure.android.communication.ui.chat.utilities.CoroutineContextProvider
import com.azure.android.communication.ui.chat.utilities.TestHelper
import com.azure.android.communication.ui.chat.utilities.announceForAccessibility
import com.jakewharton.threetenabp.AndroidThreeTen

internal class ChatContainer(
    private val chatAdapter: ChatAdapter,
    private val configuration: ChatCompositeConfiguration,
    private val instanceId: Int,
) {
    companion object {
        lateinit var locator: ServiceLocator
    }

    private var started = false
    private var locator: ServiceLocator? = null

    fun start(
        context: Context,
        remoteOptions: ChatCompositeRemoteOptions,
    ) {
        // currently only single instance is supported
        if (!started) {
            AndroidThreeTen.init(context)
            started = true
            configuration.chatConfig =
                ChatConfiguration(
                    endpoint = remoteOptions.endpoint,
                    identity = remoteOptions.identity,
                    credential = remoteOptions.credential,
                    applicationID = DiagnosticConfig().tag,
                    sdkName = "com.azure.android:azure-communication-chat",
                    sdkVersion = "2.0.1",
                    threadId = remoteOptions.threadId,
                    senderDisplayName = remoteOptions.displayName,
                )

            locator =
                initializeServiceLocator(
                    instanceId,
                    remoteOptions,
                    context,
                )
                    .apply {
                        locate<Dispatch>()(ChatAction.StartChat())
                        locate<NetworkManager>().start(context)
                        locate<EventHandler>().start()
                        locate<ChatErrorHandler>().start()
                    }
        }
    }

    private fun initializeServiceLocator(
        instanceId: Int,
        remoteOptions: ChatCompositeRemoteOptions,
        context: Context,
    ) = ServiceLocator.getInstance(instanceId = instanceId).apply {
        addTypedBuilder { TestHelper.coroutineContextProvider ?: CoroutineContextProvider() }

        val messageRepository = MessageRepository.createSkipListBackedRepository()

        addTypedBuilder { chatAdapter }

        addTypedBuilder { messageRepository }

        addTypedBuilder { remoteOptions }

        addTypedBuilder { ChatEventHandler() }

        addTypedBuilder {
            ChatFetchNotificationHandler(
                coroutineContextProvider = locate(),
                localParticipantIdentifier = configuration.chatConfig?.identity ?: "",
            )
        }

        addTypedBuilder {
            ChatSDKWrapper(
                context = context,
                chatConfig = configuration.chatConfig!!,
                coroutineContextProvider = locate(),
                chatEventHandler = locate(),
                chatFetchNotificationHandler = locate(),
                logger = locate(),
            )
        }

        addTypedBuilder {
            ChatService(
                chatSDK = TestHelper.chatSDK ?: locate<ChatSDKWrapper>(),
            )
        }

        addTypedBuilder {
            ChatServiceListener(
                chatService = locate(),
                coroutineContextProvider = locate(),
            )
        }

        addTypedBuilder {
            AppStore(
                initialState =
                    AppReduxState(
                        configuration.chatConfig!!.threadId,
                        configuration.chatConfig!!.identity,
                        configuration.chatConfig?.senderDisplayName,
                    ),
                reducer =
                    AppStateReducer(
                        chatReducer = ChatReducerImpl(),
                        participantReducer = ParticipantsReducerImpl(),
                        lifecycleReducer = LifecycleReducerImpl(),
                        errorReducer = ErrorReducerImpl(),
                        navigationReducer = NavigationReducerImpl(),
                        repositoryReducer = RepositoryReducerImpl(),
                        networkReducer = NetworkReducerImpl(),
                        accessibilityReducer =
                            AccessibilityReducerImpl(context) {
                                announceForAccessibility(context, it)
                            },
                    ) as Reducer<ReduxState>,
                middlewares =
                    mutableListOf(
                        ChatMiddlewareImpl(
                            chatActionHandler =
                                ChatActionHandler(
                                    chatService = locate(),
                                ),
                            chatServiceListener = locate(),
                        ),
                        MessageRepositoryMiddlewareImpl(messageRepository),
                    ),
                dispatcher = (locate() as CoroutineContextProvider).SingleThreaded,
            )
        }

        addTypedBuilder<Dispatch> { locate<AppStore<ReduxState>>()::dispatch }

        addTypedBuilder { NetworkManager(dispatch = locate()) }

        addTypedBuilder {
            EventHandler(
                coroutineContextProvider = locate(),
                store = locate(),
                configuration = configuration,
            )
        }

        addTypedBuilder {
            ChatErrorHandler(
                coroutineContextProvider = locate(),
                store = locate(),
                configuration = configuration,
            )
        }
        addTypedBuilder<Logger> { DefaultLogger() }
    }

    fun stop() {
        locator?.locate<EventHandler>()?.stop()
        locator?.locate<ChatErrorHandler>()?.stop()
        locator?.locate<ChatSDKWrapper>()?.destroy()
        locator?.locate<ChatServiceListener>()?.unsubscribe()
        locator?.locate<AppStore<ReduxState>>()?.end()
        locator?.locate<NetworkManager>()?.stop()
        locator?.clear()
        locator = null
    }
}
