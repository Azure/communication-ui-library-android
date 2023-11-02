// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.handlers

import android.content.Context
import com.azure.android.communication.calling.CallAgent
import com.azure.android.communication.calling.CallAgentOptions
import com.azure.android.communication.calling.CallClient
import com.azure.android.communication.calling.CallClientOptions
import com.azure.android.communication.calling.PushNotificationInfo
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.ui.calling.DiagnosticConfig
import com.azure.android.communication.ui.calling.configuration.events.CallCompositeIncomingCallListener
import com.azure.android.communication.ui.calling.models.CallCompositePushNotificationInfo
import com.azure.android.communication.ui.calling.models.CallCompositePushNotificationOptions
import com.azure.android.communication.ui.calling.service.sdk.CallingSDK
import com.azure.android.communication.ui.calling.service.sdk.ext.setTags
import com.azure.android.communication.ui.calling.logger.DefaultLogger
import com.azure.android.communication.ui.calling.logger.Logger
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteOptions
import java9.util.concurrent.CompletableFuture

internal class PushNotificationHandler(private val callingSDK: CallingSDK? = null) {

    private val diagnosticConfig: DiagnosticConfig by lazy { DiagnosticConfig() }
    private val logger: Logger by lazy { DefaultLogger() }

    private var callAgent: CallAgent? = null
    private var callClient: CallClient? = null

    fun registerPushNotificationAsync(context: Context,
                                      pushNotificationOptions: CallCompositePushNotificationOptions) {
        callingSDK?.registerPushNotificationTokenAsync(pushNotificationOptions.deviceRegistrationToken)
            ?: run {
                val result = CompletableFuture<Void>()
                callAgent = createCallAgent(
                    context,
                    pushNotificationOptions.displayName,
                    pushNotificationOptions.tokenCredential
                )
                callAgent?.registerPushNotification(pushNotificationOptions.deviceRegistrationToken)
                    ?.whenComplete { t, u ->
                        if (u != null) {
                            result.completeExceptionally(u)
                        } else {
                            result.complete(t)
                        }
                    }
                callAgent?.dispose()
                callClient?.dispose()
            }
    }

    fun handlePushNotificationAsync(
        context: Context,
        remoteOptions: CallCompositeRemoteOptions,
        pushNotificationInfo: CallCompositePushNotificationInfo,
        incomingCallListeners: MutableIterable<CallCompositeIncomingCallListener>
    ) {
        callingSDK?.handlePushNotificationAsync(PushNotificationInfo.fromMap(pushNotificationInfo.notificationInfo))
            ?:  run {
            val result = CompletableFuture<Void>()

                callAgent = createCallAgent(
                    context,
                    remoteOptions.displayName,
                    remoteOptions.credential
                )
                callAgent?.addOnIncomingCallListener {
                    for (incomingCallListener in incomingCallListeners) {
                        incomingCallListener.onIncomingCall(it.id)
                    }
                }
                callAgent?.handlePushNotification(PushNotificationInfo.fromMap(pushNotificationInfo.notificationInfo))
                ?.whenComplete { t, u ->
                    if (u != null) {
                        result.completeExceptionally(u)
                    } else {
                        result.complete(t)
                    }
                }
        }
    }

    private fun createCallAgent(context: Context, name: String, communicationTokenCredential: CommunicationTokenCredential): CallAgent? {
        val callClientOptions = CallClientOptions().also {
            it.setTags(diagnosticConfig.tags, logger)
        }
        callClient = CallClient(callClientOptions)
        val options = CallAgentOptions().apply { displayName = name }
        return callClient?.createCallAgent(
            context,
            communicationTokenCredential,
            options
        )?.get()
    }
}