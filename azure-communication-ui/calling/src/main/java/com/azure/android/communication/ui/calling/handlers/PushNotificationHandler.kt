// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.handlers

import android.content.Context
import com.azure.android.communication.calling.CallAgent
import com.azure.android.communication.calling.CallAgentOptions
import com.azure.android.communication.calling.CallClient
import com.azure.android.communication.calling.CallClientOptions
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.ui.calling.DiagnosticConfig
import com.azure.android.communication.ui.calling.models.CallCompositePushNotificationOptions
import com.azure.android.communication.ui.calling.service.sdk.ext.setTags
import com.azure.android.communication.ui.calling.logger.DefaultLogger
import com.azure.android.communication.ui.calling.logger.Logger

internal class PushNotificationHandler {
    private val diagnosticConfig: DiagnosticConfig by lazy { DiagnosticConfig() }
    private val logger: Logger by lazy { DefaultLogger() }

    // if di container is not available, use this method to register push notification
    fun registerPushNotification(
        context: Context,
        pushNotificationOptions: CallCompositePushNotificationOptions
    ) {
        logger.info("registerPushNotification")
        val callAgent = createCallAgent(
            context,
            pushNotificationOptions.displayName,
            pushNotificationOptions.tokenCredential
        )
        callAgent?.registerPushNotification(pushNotificationOptions.deviceRegistrationToken)
            ?.whenComplete { _, exception ->
                if (exception != null) {
                    logger.error("registerPushNotification error " + exception.message)
                    throw exception
                }
                logger.debug("registerPushNotification success")
                callAgent?.dispose()
            }
    }

    private fun createCallAgent(context: Context, name: String, communicationTokenCredential: CommunicationTokenCredential): CallAgent? {
        val callClientOptions = CallClientOptions().also {
            it.setTags(diagnosticConfig.tags, logger)
        }
        val callClient = CallClient(callClientOptions)
        val options = CallAgentOptions().apply { displayName = name }
        return callClient.createCallAgent(
            context,
            communicationTokenCredential,
            options
        ).get()
    }
}
