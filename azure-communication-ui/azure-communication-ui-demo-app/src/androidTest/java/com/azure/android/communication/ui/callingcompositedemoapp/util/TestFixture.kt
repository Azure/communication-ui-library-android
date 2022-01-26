// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.android.communication.ui.callingcompositedemoapp.util

import androidx.test.platform.app.InstrumentationRegistry

object TestFixture {
    val teamsUrl by lazy {
        InstrumentationRegistry.getArguments().getString("teamsUrl") ?: ""
    }

    val groupId by lazy {
        InstrumentationRegistry.getArguments().getString("groupId") ?: ""
    }

    val acsToken by lazy {
        InstrumentationRegistry.getArguments().getString("acsToken") ?: ""
    }
}
