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
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallEndEvent
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallEvent
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallInfo

internal interface IncomingCallEvent {
    fun onIncomingCall(incomingCall: IncomingCall)
}

internal class IncomingCallWrapper(
    private val logger: Logger,
    private val callingSDKCallAgentWrapper: CallingSDKCallAgentWrapper,
    private val incomingCallEventHandlers: Iterable<CallCompositeEventHandler<CallCompositeIncomingCallEvent>>?,
    private val incomingCallEndEventHandlers: Iterable<CallCompositeEventHandler<CallCompositeIncomingCallEndEvent>>?
) : IncomingCallEvent {
    private var incomingCallInternal: IncomingCall? = null

    private val onIncomingCallEnded =
        PropertyChangedListener { _ ->
            val code = incomingCallInternal?.callEndReason?.code ?: -1
            val subCode = incomingCallInternal?.callEndReason?.subcode ?: -1
            dispose()
            incomingCallEndEventHandlers?.forEach {
                it.handle(
                    CallCompositeIncomingCallEndEvent(
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
            if (incomingCall == null) {
                return
            }
            incomingCallEventHandlers?.forEach {
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

    fun declineCall() {
        incomingCallInternal?.reject()?.get()
    }

    fun handlePushNotification(
        context: Context,
        displayName: String,
        communicationTokenCredential: CommunicationTokenCredential,
        pushNotificationInfo: Map<String, String>
    ) {
        callingSDKCallAgentWrapper.createCallAgent(
            context,
            displayName,
            communicationTokenCredential
        ).whenComplete { callAgent, callAgentError ->
            if (callAgentError != null) {
                throw callAgentError
            }
            val info = PushNotificationInfo.fromMap(pushNotificationInfo)
            callAgent.handlePushNotification(info)
        }
    }
}
