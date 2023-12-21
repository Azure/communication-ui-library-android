// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service.sdk

import android.content.Context
import androidx.core.util.Consumer
import com.azure.android.communication.calling.CallAgent
import com.azure.android.communication.calling.CallAgentOptions
import com.azure.android.communication.calling.CallClient
import com.azure.android.communication.calling.CallClientOptions
import com.azure.android.communication.calling.IncomingCall
import com.azure.android.communication.calling.IncomingCallListener
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.ui.calling.DiagnosticConfig
import com.azure.android.communication.ui.calling.logger.Logger
import com.azure.android.communication.ui.calling.service.sdk.ext.setTags
import java9.util.concurrent.CompletableFuture

internal object CallingSDKInstanceManager {
    var callingSDKCallAgentWrapper: CallingSDKCallAgentWrapper? = null
}

internal class CallingSDKCallAgentWrapper(private val logger: Logger) {
    private var callClientInternal: CallClient? = null
    private var callAgentCompletableFuture: CompletableFuture<CallAgent>? = null
    private var callClientCompletableFuture: CompletableFuture<CallClient>? = null
    private var incomingCallListener: UIIncomingCallListener = UIIncomingCallListener()

    var incomingCallWrapper: IncomingCallWrapper? = null

    fun getIncomingCallListener(): UIIncomingCallListener {
        return incomingCallListener
    }

    fun registerPushNotification(
        context: Context,
        name: String,
        communicationTokenCredential: CommunicationTokenCredential,
        deviceRegistrationToken: String,
        onCompleteCallback: Consumer<Boolean>,
    ) {
        createCallAgent(context, name, communicationTokenCredential).get()
            ?.registerPushNotification(deviceRegistrationToken)?.whenComplete { _, exception ->
                if (exception != null) {
                    logger.error("registerPushNotification error " + exception.message)
                    onCompleteCallback.accept(false)
                    throw exception
                }
                logger.debug("registerPushNotification success")
                onCompleteCallback.accept(true)
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
                            callAgent.addOnIncomingCallListener(incomingCallListener)
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
        incomingCallWrapper?.dispose()
        incomingCallWrapper = null
        incomingCallListener.incomingCallEventListener = null
        callAgentCompletableFuture?.get()?.removeOnIncomingCallListener(incomingCallListener)
        callAgentCompletableFuture?.get()?.dispose()
        callAgentCompletableFuture = null
        callClientInternal = null
        callClientCompletableFuture = null
    }
}

internal class UIIncomingCallListener :
    IncomingCallListener {
    var incomingCallEventListener: IncomingCallEvent? = null

    override fun onIncomingCall(incomingCall: IncomingCall) {
        incomingCallEventListener?.onIncomingCall(incomingCall)
    }
}
