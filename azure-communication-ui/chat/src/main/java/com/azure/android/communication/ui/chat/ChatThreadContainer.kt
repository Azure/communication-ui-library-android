// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat

import android.content.Context
import com.azure.android.communication.ui.chat.configuration.ChatCompositeConfiguration
import com.azure.android.communication.ui.chat.configuration.ChatConfiguration
import com.azure.android.communication.ui.chat.models.ChatCompositeLocalOptions
import com.azure.android.communication.ui.chat.models.ChatCompositeRemoteOptions
import com.azure.android.communication.ui.chat.presentation.manager.NetworkManager
import com.azure.android.communication.ui.chat.redux.AppStore
import com.azure.android.communication.ui.chat.redux.Dispatch
import com.azure.android.communication.ui.chat.redux.action.ChatAction
import com.azure.android.communication.ui.chat.redux.middleware.repository.MessageRepositoryMiddlewareImpl
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
import com.azure.android.communication.ui.chat.utilities.TestHelper
import com.jakewharton.threetenabp.AndroidThreeTen

internal class ChatThreadContainer(
    private val configuration: ChatCompositeConfiguration,
) {
    internal lateinit var appStore: AppStore<ReduxState>
    internal lateinit var remoteOptions: ChatCompositeRemoteOptions
    internal lateinit var messageRepository: MessageRepository
    internal lateinit var coroutineContextProvider: CoroutineContextProvider
    internal lateinit var dispatch : Dispatch

    private var started = false
    private lateinit var localOptions: ChatCompositeLocalOptions
    private lateinit var networkManager : NetworkManager
    private lateinit var chatService: ChatService
    private lateinit var chatFetchNotificationHandler: ChatFetchNotificationHandler
    private lateinit var chatEventHandler: ChatEventHandler

    fun start(
        context: Context,
        remoteOptions: ChatCompositeRemoteOptions,
        localOptions: ChatCompositeLocalOptions?,

    ) {
        // currently only single instance is supported
        if (!started) {
            AndroidThreeTen.init(context)
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

            initialize(
                localOptions,
                remoteOptions,
                context
            )
            dispatch(ChatAction.StartChat())
            networkManager.start(context)

        }
    }

    private fun initialize(

        localOptions: ChatCompositeLocalOptions?,
        remoteOptions: ChatCompositeRemoteOptions,
        context: Context,
    )
        {
            this.coroutineContextProvider = TestHelper.coroutineContextProvider ?: CoroutineContextProvider()
            this.messageRepository = MessageRepository.createSkipListBackedRepository()
            this.localOptions = localOptions ?: ChatCompositeLocalOptions()
            this.remoteOptions = remoteOptions
            this.chatEventHandler = ChatEventHandler()
            this.chatFetchNotificationHandler = ChatFetchNotificationHandler(coroutineContextProvider = coroutineContextProvider)

            this.chatService =
                ChatService(
                    chatSDK = TestHelper.chatSDK ?: ChatSDKWrapper(
                        context = context,
                        chatConfig = configuration.chatConfig!!,
                        coroutineContextProvider = coroutineContextProvider,
                        chatEventHandler = chatEventHandler,
                        chatFetchNotificationHandler = chatFetchNotificationHandler
                    )
                )


            this.appStore =
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
                                chatService = chatService
                            ),
                            chatServiceListener = ChatServiceListener(
                                chatService = chatService,
                                coroutineContextProvider = coroutineContextProvider
                            )
                        ),
                        MessageRepositoryMiddlewareImpl(messageRepository)
                    ),
                    dispatcher = coroutineContextProvider.SingleThreaded
                )


            this.dispatch = appStore::dispatch
            this.networkManager = NetworkManager(appStore::dispatch)
        }

    fun stop() {
        networkManager.stop()
    }
}
