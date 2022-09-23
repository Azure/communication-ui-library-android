// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat

import android.content.Context
import com.azure.android.communication.ui.chat.configuration.ChatCompositeConfiguration
import com.azure.android.communication.ui.chat.configuration.ChatConfiguration
import com.azure.android.communication.ui.chat.locator.ServiceLocator
import com.azure.android.communication.ui.chat.models.ChatCompositeLocalOptions
import com.azure.android.communication.ui.chat.models.ChatCompositeRemoteOptions
import com.azure.android.communication.ui.chat.models.ChatCompositeUnreadMessageChangedEvent
import com.azure.android.communication.ui.chat.redux.AppStore
import com.azure.android.communication.ui.chat.redux.middleware.ChatMiddlewareImpl
import com.azure.android.communication.ui.chat.redux.reducer.AppStateReducer
import com.azure.android.communication.ui.chat.redux.reducer.ChatReducerImpl
import com.azure.android.communication.ui.chat.redux.reducer.ErrorReducerImpl
import com.azure.android.communication.ui.chat.redux.reducer.LifecycleReducerImpl
import com.azure.android.communication.ui.chat.redux.reducer.NavigationReducerImpl
import com.azure.android.communication.ui.chat.redux.reducer.ParticipantsReducerImpl
import com.azure.android.communication.ui.chat.redux.reducer.Reducer
import com.azure.android.communication.ui.chat.redux.state.AppReduxState
import com.azure.android.communication.ui.chat.redux.state.ReduxState
import com.azure.android.communication.ui.chat.service.ChatService
import com.azure.android.communication.ui.chat.service.sdk.ChatSDKWrapper
import com.azure.android.communication.ui.chat.utilities.CoroutineContextProvider

internal class ChatContainer(
    private val configuration: ChatCompositeConfiguration,
) {
    var started = false
    private var locator: ServiceLocator? = null
    private val onUnreadMessageChangedHandlers =
        mutableSetOf<ChatCompositeEventHandler<ChatCompositeUnreadMessageChangedEvent>>()

    fun start(
        context: Context,
        remoteOptions: ChatCompositeRemoteOptions,
        localOptions: ChatCompositeLocalOptions?,
        instanceId: Int,
    ) {
        // currently only single instance is supported
        if (!started) {
            started = true
            configuration.chatConfig =
                ChatConfiguration(
                    endPointURL = remoteOptions.locator.endpointURL,
                    credential = remoteOptions.credential,
                    applicationID = "azure_communication_ui", // TODO: modify while working on diagnostics config < 24
                    sdkName = "com.azure.android:azure-communication-chat",
                    sdkVersion = "2.0.0",
                    threadId = remoteOptions.locator.chatThreadId,
                    senderDisplayName = remoteOptions.displayName
                )

            ChatCompositeConfiguration.putConfig(instanceId, configuration)

            locator = ServiceLocator.getInstance(instanceId = instanceId)
            locator?.let { serviceLocator ->
                localOptions?.let { localOptions ->
                    serviceLocator.addTypedBuilder { localOptions }
                }
                serviceLocator.addTypedBuilder { remoteOptions }

                serviceLocator.addTypedBuilder {
                    ChatSDKWrapper(
                        context = context,
                        instanceId = instanceId,
                    )
                }

                serviceLocator.addTypedBuilder {
                    ChatService(chatSDK = serviceLocator.locate<ChatSDKWrapper>())
                }

                serviceLocator.addTypedBuilder { CoroutineContextProvider() }

                serviceLocator.addTypedBuilder {
                    AppStore(
                        initialState = AppReduxState(),
                        reducer = AppStateReducer(
                            chatReducer = ChatReducerImpl(),
                            participantReducer = ParticipantsReducerImpl(),
                            lifecycleReducer = LifecycleReducerImpl(),
                            errorReducer = ErrorReducerImpl(),
                            navigationReducer = NavigationReducerImpl()
                        ) as Reducer<ReduxState>,
                        middlewares = mutableListOf(ChatMiddlewareImpl()),
                        dispatcher = (serviceLocator.locate() as CoroutineContextProvider).SingleThreaded
                    )
                }
            }
        }
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

    fun stop() {
        locator?.clear()
    }
}
