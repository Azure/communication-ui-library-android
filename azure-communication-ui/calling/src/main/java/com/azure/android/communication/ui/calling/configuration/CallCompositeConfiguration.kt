// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.configuration

import com.azure.android.communication.ui.calling.configuration.events.CallCompositeEventsHandler
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions
import com.azure.android.communication.ui.calling.models.CallCompositeLocalizationOptions

internal class CallCompositeConfiguration {
    var themeConfig: Int? = null
    var localizationConfig: CallCompositeLocalizationOptions? = null
    var callCompositeEventsHandler = CallCompositeEventsHandler()
    var callConfig: CallConfiguration? = null
    var callCompositeLocalOptions: CallCompositeLocalOptions? = null
    val remoteParticipantsConfiguration: RemoteParticipantsConfiguration = RemoteParticipantsConfiguration()
    var enableMultitasking: Boolean = false
    var enableSystemPiPWhenMultitasking: Boolean = false
}
