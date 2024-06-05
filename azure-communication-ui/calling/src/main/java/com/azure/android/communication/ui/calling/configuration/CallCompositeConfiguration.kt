// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.configuration

import com.azure.android.communication.ui.calling.configuration.events.CallCompositeEventsHandler
import com.azure.android.communication.ui.calling.models.CallCompositeCapabilitiesChangedNotificationMode
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions
import com.azure.android.communication.ui.calling.models.CallCompositeLocalizationOptions
import com.azure.android.communication.ui.calling.models.CallCompositeSetupScreenOptions
import com.azure.android.communication.ui.calling.models.CallCompositeSupportedScreenOrientation

internal class CallCompositeConfiguration(
    val themeConfig: Int?,
    val localizationConfig: CallCompositeLocalizationOptions?,
    val callScreenOrientation: CallCompositeSupportedScreenOrientation?,
    val setupScreenOrientation: CallCompositeSupportedScreenOrientation?,
    val enableMultitasking: Boolean,
    val enableSystemPiPWhenMultitasking: Boolean,
    /* <ROOMS_SUPPORT> */
    val capabilitiesChangedNotificationMode: CallCompositeCapabilitiesChangedNotificationMode?,
    val setupScreenOptions: CallCompositeSetupScreenOptions?,
    /* </ROOMS_SUPPORT> */
) {
    var callCompositeLocalOptions: CallCompositeLocalOptions? = null
    val callCompositeEventsHandler = CallCompositeEventsHandler()
    lateinit var callConfig: CallConfiguration
    val remoteParticipantsConfiguration: RemoteParticipantsConfiguration = RemoteParticipantsConfiguration()
}
