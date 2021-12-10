// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration

import com.azure.android.communication.ui.configuration.events.CallCompositeEventsHandler

internal class CallCompositeConfiguration {
    var themeConfig: ThemeConfiguration? = null
    var callCompositeEventsHandler = CallCompositeEventsHandler()
    var callConfig: CallConfiguration? = null
}
