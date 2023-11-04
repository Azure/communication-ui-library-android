// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service.sdk

import android.content.Context
import com.azure.android.communication.calling.CallAgent
import com.azure.android.communication.calling.CallAgentOptions
import com.azure.android.communication.calling.CallClient
import com.azure.android.communication.calling.CallClientOptions
import com.azure.android.communication.calling.IncomingCall
import com.azure.android.communication.calling.IncomingCallListener
import com.azure.android.communication.calling.PropertyChangedListener
import com.azure.android.communication.calling.PushNotificationInfo
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import com.azure.android.communication.ui.calling.DiagnosticConfig
import com.azure.android.communication.ui.calling.logger.DefaultLogger
import com.azure.android.communication.ui.calling.logger.Logger
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallEndEvent
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallEvent
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallInfo
import com.azure.android.communication.ui.calling.service.sdk.ext.setTags
import java9.util.concurrent.CompletableFuture

/*
* This class is a wrapper around the CallingSDKInitializationWrapper class. It is used to inject a custom CallingSDK
* We need to have callingSDK for handling push notifications without UI layer
*/
internal object CallingSDKInitializationWrapperInjectionHelper {
    var callingSDKInitializationWrapper: CallingSDKInitializationWrapper? = null
    var callingSDKCallAgentWrapper: CallingSDKCallAgentWrapper? = null
}

internal interface IncomingCallEvent {
    fun onIncomingCall(incomingCall: IncomingCall)
}

internal class CallingSDKCallAgentWrapper {
    private var callClientInternal: CallClient? = null
    private var callAgentCompletableFuture: CompletableFuture<CallAgent>? = null
    private var callClientCompletableFuture: CompletableFuture<CallClient>? = null
    private val logger: Logger by lazy { DefaultLogger() }

    fun registerPushNotification(
        context: Context,
        name: String,
        communicationTokenCredential: CommunicationTokenCredential,
        deviceRegistrationToken: String,
    ) {
        createCallAgent(context, name, communicationTokenCredential).get()
            ?.registerPushNotification(deviceRegistrationToken)?.whenComplete { _, exception ->
                if (exception != null) {
                    logger.error("registerPushNotification error " + exception.message)
                    throw exception
                }
                logger.debug("registerPushNotification success")
            }
    }

    fun setupCall(): CompletableFuture<CallClient>? {
        if (callClientCompletableFuture == null ||
            callClientCompletableFuture!!.isCompletedExceptionally
        ) {
            callClientCompletableFuture = CompletableFuture<CallClient>()
            if (callClientInternal == null) {
                val callClientOptions = CallClientOptions().also {
                    it.setTags(DiagnosticConfig().tags, logger)
                }
                callClientInternal = CallClient(callClientOptions)
                callClientCompletableFuture?.complete(callClientInternal)
            }
        }

        return callClientCompletableFuture
    }

    fun createCallAgent(
        context: Context,
        name: String,
        communicationTokenCredential: CommunicationTokenCredential,
    ): CompletableFuture<CallAgent> {
        if (callAgentCompletableFuture == null || callAgentCompletableFuture!!.isCompletedExceptionally) {
            callAgentCompletableFuture = CompletableFuture<CallAgent>()
            val options = CallAgentOptions().apply { displayName = name }
            try {
                setupCall()?.whenComplete { callClient, callAgentError ->
                    if (callAgentError != null) {
                        throw callAgentError
                    }
                    val createCallAgentFutureCompletableFuture = callClient.createCallAgent(
                        context,
                        communicationTokenCredential,
                        options
                    )
                    createCallAgentFutureCompletableFuture.whenComplete { callAgent: CallAgent, error: Throwable? ->
                        if (error != null) {
                            callAgentCompletableFuture!!.completeExceptionally(error)
                        } else {
                            callAgentCompletableFuture!!.complete(callAgent)
                        }
                    }
                }
            } catch (error: Throwable) {
                callAgentCompletableFuture!!.completeExceptionally(error)
            }
        }

        return callAgentCompletableFuture!!
    }

    fun dispose() {
        callAgentCompletableFuture?.get()?.dispose()
        callAgentCompletableFuture = null
        callClientInternal = null
        callClientCompletableFuture = null
    }
}

internal class CallingSDKInitializationWrapper(
    private val callingSDKCallAgentWrapper: CallingSDKCallAgentWrapper,
    private val logger: Logger? = null,
) : IncomingCallEvent {
    private var incomingCallListener: UIIncomingCallListener? = null
    private var incomingCallInternal: IncomingCall? = null
    private var callAgent: CallAgent? = null
    private var onIncomingCallEventHandlers:
        MutableIterable<CallCompositeEventHandler<CallCompositeIncomingCallEvent>>? = null
    private var onIncomingCallEndEventHandlers:
        MutableIterable<CallCompositeEventHandler<CallCompositeIncomingCallEndEvent>>? = null

    private val onIncomingCallEnded =
        PropertyChangedListener { _ ->
            val code = incomingCallInternal?.callEndReason?.code ?: -1
            val subCode = incomingCallInternal?.callEndReason?.subcode ?: -1
            unsubscribeEvents()
            onIncomingCallEndEventHandlers?.forEach {
                it.handle(
                    CallCompositeIncomingCallEndEvent(
                        code,
                        subCode
                    )
                )
            }
        }

    val incomingCall: IncomingCall?
        get() {
            return incomingCallInternal
        }

    fun setupCall(): CompletableFuture<CallClient>? {
        return callingSDKCallAgentWrapper.setupCall()
    }

    fun onIncomingCallAccepted() {
        unsubscribeEvents()
    }

    fun createCallAgent(
        subscribeForIncomingCall: Boolean = false,
        context: Context,
        displayName: String,
        communicationTokenCredential: CommunicationTokenCredential,
        pushNotificationInfo: Map<String, String>? = null,
    ): CompletableFuture<CallAgent> {
        val callAgentFeature = callingSDKCallAgentWrapper.createCallAgent(
            context,
            displayName,
            communicationTokenCredential
        )

        callAgentFeature.whenComplete { callAgent, error ->
            if (error != null) {
                throw error
            }
            this.callAgent = callAgent
            if (subscribeForIncomingCall && pushNotificationInfo != null) {
                incomingCallListener = UIIncomingCallListener(this)
                callAgent.addOnIncomingCallListener(incomingCallListener)
                callAgent.handlePushNotification(PushNotificationInfo.fromMap(pushNotificationInfo))
            }
        }

        return callAgentFeature
    }

    private fun unsubscribeEvents() {
        incomingCallInternal?.removeOnCallEndedListener(onIncomingCallEnded)
        incomingCallInternal = null
        incomingCallListener?.let {
            callAgent?.removeOnIncomingCallListener(it)
        }
        incomingCallListener = null
    }

    fun dispose() {
        logger?.info("Disposing CallingSDKInitializationWrapper")
        unsubscribeEvents()
        callAgent = null
        callingSDKCallAgentWrapper.dispose()
    }

    fun setupIncomingCall(
        context: Context,
        displayName: String,
        communicationTokenCredential: CommunicationTokenCredential,
        pushNotificationInfo: Map<String, String>,
        onIncomingCallEventHandlers:
            MutableIterable<CallCompositeEventHandler<CallCompositeIncomingCallEvent>>,
        onIncomingCallEndEventHandlers:
            MutableIterable<CallCompositeEventHandler<CallCompositeIncomingCallEndEvent>>
    ) {
        this.onIncomingCallEventHandlers = onIncomingCallEventHandlers
        this.onIncomingCallEndEventHandlers = onIncomingCallEndEventHandlers

        setupCall()?.whenComplete { _, error ->
            if (error == null) {
                createCallAgent(
                    true,
                    context,
                    displayName,
                    communicationTokenCredential,
                    pushNotificationInfo
                )
            } else {
                throw error
            }
        }
    }

    fun declineCall() {
        incomingCallInternal?.reject()?.get()
    }

    class UIIncomingCallListener(
        private val incomingCallEvent: IncomingCallEvent,
    ) : IncomingCallListener {
        override fun onIncomingCall(incomingCall: IncomingCall) {
            incomingCallEvent.onIncomingCall(incomingCall)
        }
    }

    override fun onIncomingCall(incomingCall: IncomingCall) {
        logger?.info("Incoming call received")
        if (this.incomingCallInternal != null) {
            // only one call is supported in UI Library
            return
        }
        this.incomingCallInternal = incomingCall
        logger?.info("Incoming call received - notifying")

        incomingCallInternal?.addOnCallEndedListener(onIncomingCallEnded)

        onIncomingCallEventHandlers?.forEach {
            it.handle(
                CallCompositeIncomingCallEvent(
                    CallCompositeIncomingCallInfo(
                        incomingCall.id,
                        incomingCall.callerInfo.displayName,
                        incomingCall.callerInfo.identifier.rawId
                    )
                )
            )
        }
    }
}
