// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat

import android.content.Context
import com.azure.android.communication.ui.chat.configuration.ChatCompositeConfiguration
import com.azure.android.communication.ui.chat.configuration.ChatConfiguration
import com.azure.android.communication.ui.chat.locator.ServiceLocator
import com.azure.android.communication.ui.chat.models.ChatCompositeLocalOptions
import com.azure.android.communication.ui.chat.models.ChatCompositeRemoteOptions
import com.azure.android.communication.ui.chat.presentation.manager.NetworkManager
import com.azure.android.communication.ui.chat.redux.AppStore
import com.azure.android.communication.ui.chat.redux.Dispatch
import com.azure.android.communication.ui.chat.redux.action.ChatAction
import com.azure.android.communication.ui.chat.redux.middleware.repository.RepositoryMiddlewareImpl
import com.azure.android.communication.ui.chat.redux.middleware.sdk.ChatActionHandler
import com.azure.android.communication.ui.chat.redux.middleware.sdk.ChatMiddlewareImpl
import com.azure.android.communication.ui.chat.redux.middleware.sdk.ChatServiceListener
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
import com.azure.android.communication.ui.chat.service.sdk.ChatSDKWrapper
import com.azure.android.communication.ui.chat.service.sdk.ChatEventHandler
import com.azure.android.communication.ui.chat.service.sdk.ChatFetchNotificationHandler
import com.azure.android.communication.ui.chat.utilities.CoroutineContextProvider

internal class ChatContainer(
    private val configuration: ChatCompositeConfiguration,
    private val instanceId: Int
) {
    companion object {
        lateinit var locator: ServiceLocator
    }

    private var started = false
    private var locator: ServiceLocator? = null

    fun start(
        context: Context,
        remoteOptions: ChatCompositeRemoteOptions,
        localOptions: ChatCompositeLocalOptions?,

    ) {
        // currently only single instance is supported
        if (!started) {
            started = true
            configuration.chatConfig =
                ChatConfiguration(
                    endPointURL = remoteOptions.locator.endpointURL,
                    identity = remoteOptions.identity,
                    credential = remoteOptions.credential,
                    applicationID = "azure_communication_ui", // TODO: modify while working on diagnostics config < 24
                    sdkName = "com.azure.android:azure-communication-chat",
                    sdkVersion = "2.0.0",
                    threadId = remoteOptions.locator.chatThreadId,
                    senderDisplayName = remoteOptions.displayName
                )

            locator = initializeServiceLocator(
                instanceId,
                localOptions,
                remoteOptions,
                context
            )
                .apply {
                    locate<Dispatch>()(ChatAction.StartChat())
                    locate<NetworkManager>().start(context)
                }
        }
    }

    private fun initializeServiceLocator(
        instanceId: Int,
        localOptions: ChatCompositeLocalOptions?,
        remoteOptions: ChatCompositeRemoteOptions,
        context: Context
    ) =
        ServiceLocator.getInstance(instanceId = instanceId).apply {
            addTypedBuilder { CoroutineContextProvider() }

            addTypedBuilder { MessageRepository() }

            addTypedBuilder { localOptions ?: ChatCompositeLocalOptions() }

            addTypedBuilder { remoteOptions }

            addTypedBuilder { ChatEventHandler() }

            addTypedBuilder { ChatFetchNotificationHandler(coroutineContextProvider = locate()) }

            addTypedBuilder {
                ChatService(
                    chatSDK = ChatSDKWrapper(
                        context = context,
                        chatConfig = configuration.chatConfig!!,
                        coroutineContextProvider = locate(),
                        chatEventHandler = locate(),
                        chatPullEventHandler = locate()
                    )
                )
            }

            addTypedBuilder {
                AppStore(
                    initialState = AppReduxState(
                        configuration.chatConfig!!.threadId,
                        configuration.chatConfig!!.identity,
                        configuration.chatConfig?.senderDisplayName
                    ),
                    reducer = AppStateReducer(
                        chatReducer = ChatReducerImpl(),
                        participantReducer = ParticipantsReducerImpl(),
                        lifecycleReducer = LifecycleReducerImpl(),
                        errorReducer = ErrorReducerImpl(),
                        navigationReducer = NavigationReducerImpl(),
                        repositoryReducer = RepositoryReducerImpl(),
                        networkReducer = NetworkReducerImpl()
                    ) as Reducer<ReduxState>,
                    middlewares = mutableListOf(
                        ChatMiddlewareImpl(
                            chatActionHandler = ChatActionHandler(
                                chatService = locate()
                            ),
                            chatServiceListener = ChatServiceListener(
                                chatService = locate(),
                                coroutineContextProvider = locate()
                            )
                        ),
                        RepositoryMiddlewareImpl(locate<MessageRepository>())
                    ),
                    dispatcher = (locate() as CoroutineContextProvider).SingleThreaded
                )
            }

            addTypedBuilder<Dispatch> { locate<AppStore<ReduxState>>()::dispatch }

            addTypedBuilder { NetworkManager(dispatch = locate()) }
        }

    fun stop() {
        locator?.locate<NetworkManager>()?.stop()
        locator?.clear()
    }
}
