// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service.sdk

import com.azure.android.communication.calling.CallAgent
import com.azure.android.communication.calling.CallAgentOptions
import com.azure.android.communication.calling.CallClient
import com.azure.android.communication.calling.CallClientOptions
import com.azure.android.communication.calling.TelecomManagerOptions
import com.azure.android.communication.ui.calling.DiagnosticConfig
import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.calling.logger.Logger
import com.azure.android.communication.ui.calling.models.CallCompositeTelecomManagerIntegrationMode
import com.azure.android.communication.ui.calling.service.sdk.ext.setTags
import java9.util.concurrent.CompletableFuture

internal class CallingSDKInitialization(
    private val logger: Logger,
    private val callCompositeConfiguration: CallCompositeConfiguration
) {
    private var callClientInternal: CallClient? = null
    private var callAgentCompletableFuture: CompletableFuture<CallAgent>? = null
    private var callClientCompletableFuture: CompletableFuture<CallClient>? = null

    fun registerPushNotification(
        deviceRegistrationToken: String
    ): java.util.concurrent.CompletableFuture<Void> {
        val completableFuture: java.util.concurrent.CompletableFuture<Void> =
            java.util.concurrent.CompletableFuture<Void>()
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

    fun unregisterPushNotification(): java.util.concurrent.CompletableFuture<Void> {
        val completableFuture: java.util.concurrent.CompletableFuture<Void> =
            java.util.concurrent.CompletableFuture<Void>()
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

    fun createCallAgent(): CompletableFuture<CallAgent> {
        if (callAgentCompletableFuture == null || callAgentCompletableFuture!!.isCompletedExceptionally) {
            callAgentCompletableFuture = CompletableFuture<CallAgent>()
            val options = CallAgentOptions().apply { displayName = callCompositeConfiguration.displayName }
            callCompositeConfiguration.telecomManagerOptions?.let {
                if (it.telecomManagerIntegrationMode == CallCompositeTelecomManagerIntegrationMode.USE_SDK_PROVIDED_TELECOM_MANAGER) {
                    options.telecomManagerOptions = TelecomManagerOptions(it.phoneAccountId)
                }
            }
            try {
                setupCall()?.whenComplete { callClient, callAgentError ->
                    if (callAgentError != null) {
                        throw callAgentError
                    }
                    val createCallAgentFutureCompletableFuture = callClient.createCallAgent(
                        callCompositeConfiguration.applicationContext,
                        callCompositeConfiguration.credential,
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
