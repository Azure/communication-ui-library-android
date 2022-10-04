package com.azure.android.communication.ui.chat

import android.content.Context
import com.azure.android.communication.ui.arch.locator.ServiceLocator
import com.azure.android.communication.ui.arch.redux.GenericState
import com.azure.android.communication.ui.arch.redux.GenericStore
import com.azure.android.communication.ui.arch.redux.GenericStoreImpl
import com.azure.android.communication.ui.chat.configuration.ChatCompositeConfiguration
import com.azure.android.communication.ui.chat.implementation.redux.middleware.ChatActionMiddleware
import com.azure.android.communication.ui.chat.implementation.redux.reducers.AcsChatReducer
import com.azure.android.communication.ui.chat.implementation.redux.states.AcsChatState
import com.azure.android.communication.ui.chat.implementation.ui.UINotifier
import com.azure.android.communication.ui.chat.models.ChatCompositeLocalOptions
import com.azure.android.communication.ui.chat.models.ChatCompositeRemoteOptions
import com.azure.android.communication.ui.chat.models.ChatCompositeUnreadMessageChangedEvent
import com.azure.android.communication.ui.chat.service.sdk.ChatService

// Holder/Lifecycle for SDK, ServiceLocator and Redux
internal class ChatContainer(
    private val configuration: ChatCompositeConfiguration,
    private val instanceId: Int
) {
    var started = false
    val locator = ServiceLocator.getInstance(instanceId = instanceId)
    val onUnreadMessageChangedHandlers = mutableSetOf<ChatCompositeEventHandler<ChatCompositeUnreadMessageChangedEvent>>()

    fun start(
        context: Context,
        remoteOptions: ChatCompositeRemoteOptions,
        localOptions: ChatCompositeLocalOptions
    ) {
        if (!started) {
            started = true
            initializeServiceLocator(context, remoteOptions, localOptions)
            startChatService()
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
        // if forground mode
        // destroy UI
        // destroy service layer
        destroyActivity()
        locator.clear()
    }

    fun initUINotifier() {
        locator.addTypedBuilder { UINotifier() }
    }

    fun destroyActivity() {
        locator.locate<UINotifier>().notifyStop()
    }

    fun stopNotifications() {
        locator.locate<ChatService>().stop()
    }

    private fun initializeServiceLocator(
        context: Context,
        remoteOptions: ChatCompositeRemoteOptions,
        localOptions: ChatCompositeLocalOptions
    ) {

        locator.clear()
        locator.addTypedBuilder { localOptions }
        locator.addTypedBuilder { remoteOptions }

        locator.addTypedBuilder<GenericStore> {
            GenericStoreImpl(
                initialState = GenericState(
                    setOf(
                        AcsChatState(
                            localOptions.participantViewData?.displayName ?: "",
                            participants = listOf(),
                            messages = listOf(),
                        )
                    )
                ),
                reducer = AcsChatReducer(),
                middlewares = mutableListOf(ChatActionMiddleware(locator.locate()))
            )
        }

        locator.addTypedBuilder {
            ChatService(
                context,
                locator.locate(),
            ) {
                locator.locate<GenericStore>().dispatch(it)

                // testing
                onUnreadMessageChangedHandlers.forEach { handler ->
                    handler.handle(ChatCompositeUnreadMessageChangedEvent(3))
                }
            }
        }
    }

    private fun startChatService() {
        locator.locate<ChatService>().apply {
            start()
            requestParticipantList()
            requestMessagesFirstPage()
        }
    }
}
