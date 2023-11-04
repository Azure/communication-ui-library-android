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
import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import com.azure.android.communication.ui.calling.CallCompositeException
import com.azure.android.communication.ui.calling.configuration.CallConfiguration
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
}

internal interface IncomingCallEvent {
    fun onIncomingCall(incomingCall: IncomingCall)
}

internal class CallingSDKInitializationWrapper(
    private val callConfigInjected: CallConfiguration?,
    private val logger: Logger? = null,
    private val onIncomingCallEventHandlers:
        MutableIterable<CallCompositeEventHandler<CallCompositeIncomingCallEvent>>? = null,
    private val onIncomingCallEndEventHandlers:
        MutableIterable<CallCompositeEventHandler<CallCompositeIncomingCallEndEvent>>? = null
) : IncomingCallEvent {
    private var callClientInternal: CallClient? = null
    private var callAgentCompletableFuture: CompletableFuture<CallAgent>? = null
    private var incomingCallListener: UIIncomingCallListener? = null
    private var callClientCompletableFuture: CompletableFuture<Void>? = null
    private var incomingCallInternal: IncomingCall? = null
    private val onIncomingCallEnded =
        PropertyChangedListener { _ ->
            val code = incomingCallInternal?.callEndReason?.code ?: -1
            val subCode = incomingCallInternal?.callEndReason?.subcode ?: -1
            dispose()
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

    val callConfig: CallConfiguration
        get() {
            try {
                return callConfigInjected!!
            } catch (ex: Exception) {
                throw CallCompositeException(
                    "Call configurations are not set",
                    IllegalStateException()
                )
            }
        }

    val callClient: CallClient
        get() {
            try {
                return callClientInternal!!
            } catch (ex: Exception) {
                throw CallCompositeException("Call is not started", IllegalStateException())
            }
        }

    fun setupCall(): CompletableFuture<Void>? {
        if (callClientCompletableFuture == null ||
            callClientCompletableFuture!!.isCompletedExceptionally
        ) {
            callClientCompletableFuture = CompletableFuture<Void>()
            if (callClientInternal == null) {
                val callClientOptions = CallClientOptions().also {
                    it.setTags(callConfig.diagnosticConfig.tags, logger)
                }
                callClientInternal = CallClient(callClientOptions)
                callClientCompletableFuture?.complete(null)
            }
        }

        return callClientCompletableFuture
    }

    fun createCallAgent(subscribeForIncomingCall: Boolean = false, context: Context): CompletableFuture<CallAgent> {
        if (callAgentCompletableFuture == null || callAgentCompletableFuture!!.isCompletedExceptionally) {
            callAgentCompletableFuture = CompletableFuture<CallAgent>()
            val options = CallAgentOptions().apply { displayName = callConfig.displayName }
            try {
                val createCallAgentFutureCompletableFuture = callClientInternal!!.createCallAgent(
                    context,
                    callConfig.communicationTokenCredential,
                    options
                )
                createCallAgentFutureCompletableFuture.whenComplete { callAgent: CallAgent, error: Throwable? ->
                    if (error != null) {
                        callAgentCompletableFuture!!.completeExceptionally(error)
                    } else {
                        if (subscribeForIncomingCall) {
                            incomingCallListener = UIIncomingCallListener(this)
                            callAgent.addOnIncomingCallListener(incomingCallListener)
                            callAgent.handlePushNotification(PushNotificationInfo.fromMap(callConfig.pushNotificationInfo!!.notificationInfo))
                        }
                        callAgentCompletableFuture!!.complete(callAgent)
                    }
                }
            } catch (error: Throwable) {
                callAgentCompletableFuture!!.completeExceptionally(error)
            }
        }

        return callAgentCompletableFuture!!
    }

    fun dispose() {
        logger?.info("Disposing CallingSDKInitializationWrapper")
        incomingCallInternal?.let {
            it.removeOnCallEndedListener(onIncomingCallEnded)
        }
        incomingCallListener?.let {
            callAgentCompletableFuture?.get()?.removeOnIncomingCallListener(it)
        }
        callAgentCompletableFuture?.get()?.dispose()
        callClientInternal = null
        callAgentCompletableFuture = null
        incomingCallInternal = null
        CallingSDKInitializationWrapperInjectionHelper.callingSDKInitializationWrapper = null
    }

    fun setupIncomingCall(
        context: Context,
    ) {
        if (onIncomingCallEventHandlers == null) {
            throw CallCompositeException(
                "onIncomingCallEventHandlers is null",
                IllegalArgumentException()
            )
        }

        setupCall()?.whenComplete { _, error ->
            if (error == null) {
                createCallAgent(true, context)
            } else {
                throw error
            }
        }
    }

    fun declineCall() {
        incomingCallInternal?.reject()
    }

    class UIIncomingCallListener(
        private val incomingCallEvent: IncomingCallEvent
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
