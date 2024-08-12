// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.configuration

import android.content.Context
import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.ui.calling.configuration.events.CallCompositeEventsHandler
import com.azure.android.communication.ui.calling.models.CallCompositeCapabilitiesChangedNotificationMode
/*  <DEFAULT_AUDIO_MODE:0>
import com.azure.android.communication.ui.calling.models.CallCompositeAudioSelectionMode
</DEFAULT_AUDIO_MODE:0> */
import com.azure.android.communication.ui.calling.models.CallCompositeCallScreenOptions
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions
import com.azure.android.communication.ui.calling.models.CallCompositeLocalizationOptions
import com.azure.android.communication.ui.calling.models.CallCompositeSetupScreenOptions
import com.azure.android.communication.ui.calling.models.CallCompositeSupportedScreenOrientation
import com.azure.android.communication.ui.calling.models.CallCompositeTelecomManagerOptions
import com.azure.android.communication.ui.calling.models.setManager
import com.azure.android.communication.ui.calling.presentation.manager.CallDurationManager

internal class CallCompositeConfiguration {
    /*  <DEFAULT_AUDIO_MODE:0>
    var audioSelectionMode: CallCompositeAudioSelectionMode? = null
    </DEFAULT_AUDIO_MODE:0> */
    var themeConfig: Int? = null
    var localizationConfig: CallCompositeLocalizationOptions? = null
    var callCompositeEventsHandler = CallCompositeEventsHandler()
    lateinit var callConfig: CallConfiguration
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
    var capabilitiesChangedNotificationMode: CallCompositeCapabilitiesChangedNotificationMode? = null
    var setupScreenOptions: CallCompositeSetupScreenOptions? = null
    var identifier: CommunicationIdentifier? = null

    fun setTimerConfig(timerConfig: CallDurationManager) {
        callScreenOptions?.headerOptions?.timer?.setManager(timerConfig)
        callScreenOptions?.headerOptions?.timer?.let {
            timerConfig.startDuration(it.startDuration)
        }
    }
}
