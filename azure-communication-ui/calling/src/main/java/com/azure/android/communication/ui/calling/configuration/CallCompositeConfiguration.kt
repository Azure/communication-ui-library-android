// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.configuration

import android.content.Context
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.ui.calling.configuration.events.CallCompositeEventsHandler
import com.azure.android.communication.ui.calling.models.CallCompositeCallScreenOptions
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions
import com.azure.android.communication.ui.calling.models.CallCompositeLocalizationOptions
import com.azure.android.communication.ui.calling.models.CallCompositeSupportedScreenOrientation
import com.azure.android.communication.ui.calling.models.CallCompositeTelecomManagerOptions

internal class CallCompositeConfiguration {
    var themeConfig: Int? = null
    var localizationConfig: CallCompositeLocalizationOptions? = null
    var callCompositeEventsHandler = CallCompositeEventsHandler()
    var callConfig: CallConfiguration? = null
    var callCompositeLocalOptions: CallCompositeLocalOptions? = null
    val remoteParticipantsConfiguration: RemoteParticipantsConfiguration = RemoteParticipantsConfiguration()
    var enableMultitasking: Boolean = false
    var enableSystemPiPWhenMultitasking: Boolean = false
    var callScreenOrientation: CallCompositeSupportedScreenOrientation? = null
    var setupScreenOrientation: CallCompositeSupportedScreenOrientation? = null
    var callScreenOptions: CallCompositeCallScreenOptions? = null
    var telecomManagerOptions: CallCompositeTelecomManagerOptions? = null
    var applicationContext: Context? = null
    var displayName: String? = null
    var credential: CommunicationTokenCredential? = null
    var disableInternalPushForIncomingCall: Boolean = false
}
