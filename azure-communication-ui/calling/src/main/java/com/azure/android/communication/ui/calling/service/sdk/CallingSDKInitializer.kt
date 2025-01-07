// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service.sdk

import com.azure.android.communication.calling.CallAgent
import com.azure.android.communication.calling.CallAgentOptions
import com.azure.android.communication.calling.CallClient
import com.azure.android.communication.calling.CallClientOptions
import com.azure.android.communication.calling.IncomingCall
import com.azure.android.communication.calling.IncomingCallListener
import com.azure.android.communication.calling.PropertyChangedListener
import com.azure.android.communication.calling.PushNotificationInfo
import com.azure.android.communication.calling.TelecomManagerOptions
import com.azure.android.communication.ui.calling.CallCompositeException
import com.azure.android.communication.ui.calling.DiagnosticConfig
import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.calling.logger.Logger
import com.azure.android.communication.ui.calling.models.CallCompositePushNotification
import com.azure.android.communication.ui.calling.models.CallCompositeTelecomManagerIntegrationMode
import com.azure.android.communication.ui.calling.models.buildCallCompositeIncomingCallCancelledEvent
import com.azure.android.communication.ui.calling.models.buildCallCompositeIncomingCallEvent
import com.azure.android.communication.ui.calling.service.sdk.ext.setTags
import java.util.concurrent.CompletableFuture

internal class CallingSDKInitializer(
    private val logger: Logger,
    private val callCompositeConfiguration: CallCompositeConfiguration
) {
    private var callClientInternal: CallClient? = null
    private var callAgentCompletableFuture: CompletableFuture<CallAgent>? = null
    private var callClientCompletableFuture: CompletableFuture<CallClient>? = null
    private val callCompositeIncomingCallListener = IncomingCallListener {
        onIncomingCall(it)
    }
    private val onIncomingCallEnded = PropertyChangedListener { _ ->
        onIncomingCallCancelled()
    }
    private var incomingCall: IncomingCall? = null

    fun getIncomingCall(): IncomingCall? {
        return incomingCall
    }

    private fun isAnyCallActive(): Boolean {
        return callAgentCompletableFuture?.get()?.calls?.isNotEmpty() ?: false
    }

    fun registerPushNotification(
        deviceRegistrationToken: String
    ): CompletableFuture<Void> {
        val completableFuture: CompletableFuture<Void> =
            CompletableFuture<Void>()
        createCallAgent().whenComplete { callAgent, callAgentError ->
            if (callAgentError != null) {
                completableFuture.completeExceptionally(callAgentError)
            }
            callAgent?.registerPushNotification(deviceRegistrationToken)?.whenComplete { result, exception ->
                if (exception != null) {
                    completableFuture.completeExceptionally(exception)
                }
                completableFuture.complete(result)
            }
        }
        return completableFuture
    }

    fun unregisterPushNotification(): CompletableFuture<Void> {
        val completableFuture: CompletableFuture<Void> =
            CompletableFuture<Void>()
        createCallAgent().whenComplete { callAgent, callAgentError ->
            if (callAgentError != null) {
                completableFuture.completeExceptionally(callAgentError)
            }
            callAgent?.unregisterPushNotification()?.whenComplete { result, exception ->
                if (exception != null) {
                    completableFuture.completeExceptionally(exception)
                }
                completableFuture.complete(result)
            }
        }
        return completableFuture
    }

    fun setupCallClient(): CompletableFuture<CallClient>? {
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

    fun createCallAgent(): CompletableFuture<CallAgent> {
        if (callAgentCompletableFuture == null || callAgentCompletableFuture!!.isCompletedExceptionally) {
            callAgentCompletableFuture = CompletableFuture<CallAgent>()
            val options = CallAgentOptions().apply { displayName = callCompositeConfiguration.displayName }
            callCompositeConfiguration.telecomManagerOptions?.let {
                if (it.telecomManagerIntegrationMode == CallCompositeTelecomManagerIntegrationMode.SDK_PROVIDED_TELECOM_MANAGER) {
                    options.telecomManagerOptions = TelecomManagerOptions(it.phoneAccountId)
                }
            }
            options.setDisableInternalPushForIncomingCall(callCompositeConfiguration.disableInternalPushForIncomingCall)

            try {
                setupCallClient()?.whenComplete { callClient, callAgentError ->
                    if (callAgentError != null) {
                        throw CallCompositeException("Failed to create call agent", callAgentError)
                    }
                    val createCallAgentFutureCompletableFuture = callClient.createCallAgent(
                        callCompositeConfiguration.applicationContext,
                        callCompositeConfiguration.credential,
                        options
                    )
                    logger.info("creating call agent")
                    createCallAgentFutureCompletableFuture.whenComplete { callAgent, error ->
                        if (error != null) {
                            callAgentCompletableFuture!!.completeExceptionally(error)
                        } else {
                            callAgentCompletableFuture!!.complete(callAgent)

                            callAgent.addOnIncomingCallListener(callCompositeIncomingCallListener)
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
        logger.info("dispose")
        incomingCall?.removeOnCallEndedListener(onIncomingCallEnded)
        incomingCall = null
        callAgentCompletableFuture?.get()?.removeOnIncomingCallListener(callCompositeIncomingCallListener)
        callAgentCompletableFuture?.get()?.dispose()
        callAgentCompletableFuture = null
        callClientInternal = null
        callClientCompletableFuture = null
    }

    fun rejectIncomingCall(callID: String): CompletableFuture<Void> {
        val completableFuture: CompletableFuture<Void> = CompletableFuture<Void>()
        if (incomingCall == null || incomingCall?.id != callID) {
            completableFuture.completeExceptionally(IllegalStateException("No incoming call to reject"))
        }
        incomingCall?.reject()?.whenComplete { result, exception ->
            if (exception != null) {
                completableFuture.completeExceptionally(exception)
            }
            incomingCall = null
            completableFuture.complete(result)
        }
        return completableFuture
    }

    fun handlePushNotification(pushNotification: CallCompositePushNotification): CompletableFuture<Void> {
        val completableFuture: CompletableFuture<Void> = CompletableFuture<Void>()
        if (pushNotification.notificationInfo == null) {
            completableFuture.completeExceptionally(IllegalArgumentException("Push notification info is null"))
        }
        if (isAnyCallActive()) {
            completableFuture.completeExceptionally(IllegalStateException("Currently UI is busy with an active call - only one call is supported"))
        }
        createCallAgent().whenComplete { callAgent, callAgentError ->
            if (callAgentError != null) {
                completableFuture.completeExceptionally(callAgentError)
            }
            val pushNotificationInfo = PushNotificationInfo.fromMap(pushNotification.notificationInfo)
            callAgent?.handlePushNotification(pushNotificationInfo)?.whenComplete { result, exception ->
                if (exception != null) {
                    completableFuture.completeExceptionally(exception)
                }
                completableFuture.complete(result)
            }
        }
        return completableFuture
    }

    private fun onIncomingCall(incomingCall: IncomingCall?) {
        if (isAnyCallActive()) {
            logger.info("Currently UI Library is busy with an active call - only one call is supported")
            return
        }
        logger.info("Incoming call received")
        try {
            incomingCall?.let {
                this.incomingCall = incomingCall
                it.addOnCallEndedListener(onIncomingCallEnded)
                notifyHandlerForIncomingCall(it)
            }
        } catch (e: Exception) {
            logger.error("Error while handling incoming call", e)
        }
    }

    private fun notifyHandlerForIncomingCall(incomingCall: IncomingCall) {
        try {
            callCompositeConfiguration.callCompositeEventsHandler.getOnIncomingCallHandlers().forEach {
                it.handle(
                    buildCallCompositeIncomingCallEvent(
                        incomingCall.id,
                        incomingCall.callerInfo.displayName,
                        incomingCall.callerInfo.identifier
                    )
                )
            }
        } catch (e: Exception) {
            logger.error("Error while notifying incoming call", e)
        }
    }

    private fun onIncomingCallCancelled() {
        logger.info("Incoming call cancelled")
        incomingCall?.let { incomingCall ->
            incomingCall.callEndReason?.let { callEndReason ->
                callCompositeConfiguration.callCompositeEventsHandler.getOnIncomingCallCancelledHandlers().forEach {
                    it.handle(
                        buildCallCompositeIncomingCallCancelledEvent(
                            callEndReason.code,
                            callEndReason.subcode,
                            incomingCall.id
                        )
                    )
                }
            }
        }
        incomingCall?.removeOnCallEndedListener(onIncomingCallEnded)
        incomingCall = null
    }
}
