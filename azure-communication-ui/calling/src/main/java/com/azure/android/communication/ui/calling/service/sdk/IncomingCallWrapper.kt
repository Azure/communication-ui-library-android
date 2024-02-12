// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service.sdk

import android.content.Context
import com.azure.android.communication.calling.IncomingCall
import com.azure.android.communication.calling.PropertyChangedListener
import com.azure.android.communication.calling.PushNotificationInfo
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import com.azure.android.communication.ui.calling.logger.Logger
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallEndedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallEvent
import java.util.concurrent.CompletableFuture

internal interface IncomingCallEvent {
    fun onIncomingCall(incomingCall: IncomingCall)
}

internal class IncomingCallWrapper(
    private val logger: Logger,
    private val callingSDKCallAgentWrapper: CallingSDKCallAgentWrapper,
    private val incomingCallEventHandlers: Iterable<CallCompositeEventHandler<CallCompositeIncomingCallEvent>>?,
    private val incomingCallEndEventHandlers: Iterable<CallCompositeEventHandler<CallCompositeIncomingCallEndedEvent>>?
) : IncomingCallEvent {
    private var incomingCallInternal: IncomingCall? = null

    private val onIncomingCallEnded =
        PropertyChangedListener { _ ->
            val code = incomingCallInternal?.callEndReason?.code ?: -1
            val subCode = incomingCallInternal?.callEndReason?.subcode ?: -1
            dispose()
            incomingCallEndEventHandlers?.forEach {
                it.handle(
                    CallCompositeIncomingCallEndedEvent(
                        code,
                        subCode
                    )
                )
            }
        }

    init {
        callingSDKCallAgentWrapper.getIncomingCallListener().incomingCallEventListener = this
    }

    override fun onIncomingCall(incomingCall: IncomingCall) {
        logger.info("Incoming call received")
        if (this.incomingCallInternal != null) {
            // only one call is supported in UI Library
            return
        }

        logger.info("Incoming call received - notifying")
        incomingCall.addOnCallEndedListener(onIncomingCallEnded)
        this.incomingCallInternal = incomingCall

        notifyHandlerForIncomingCall()
    }

    fun incomingCall(): IncomingCall? {
        return incomingCallInternal
    }

    fun dispose() {
        incomingCallInternal?.removeOnCallEndedListener(onIncomingCallEnded)
        incomingCallInternal = null
    }

    private fun notifyHandlerForIncomingCall() {
        incomingCallInternal?.let { incomingCall ->
            incomingCallEventHandlers?.forEach {
                it.handle(
                    CallCompositeIncomingCallEvent(
                        incomingCall.id,
                        incomingCall.callerInfo.displayName,
                        incomingCall.callerInfo.identifier.rawId
                    )
                )
            }
        }
    }

    fun declineCall() {
        incomingCallInternal?.reject()?.get()
    }

    fun handlePushNotification(
        context: Context,
        displayName: String,
        communicationTokenCredential: CommunicationTokenCredential,
        pushNotificationInfo: Map<String, String>,
        disableInternalPushForIncomingCall: Boolean
    ): CompletableFuture<Void> {
        val completableFuture: CompletableFuture<Void> = CompletableFuture<Void>()
        callingSDKCallAgentWrapper.createCallAgent(
            context,
            displayName,
            communicationTokenCredential,
            disableInternalPushForIncomingCall
        ).whenComplete { callAgent, callAgentError ->
            if (callAgentError != null) {
                completableFuture.completeExceptionally(callAgentError)
            }
            val info = PushNotificationInfo.fromMap(pushNotificationInfo)
            callAgent.handlePushNotification(info).whenComplete { result, handlePushError ->
                if (handlePushError != null) {
                    completableFuture.completeExceptionally(handlePushError)
                } else {
                    completableFuture.complete(result)
                }
            }
        }
        return completableFuture
    }
}
