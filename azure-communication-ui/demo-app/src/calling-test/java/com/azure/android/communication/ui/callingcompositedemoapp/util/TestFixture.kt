// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.util

import androidx.test.platform.app.InstrumentationRegistry
import com.azure.android.communication.ui.callingcompositedemoapp.BuildConfig

// Do not modify when testing on AppCenter
object TestFixture {
    val teamsUrl by lazy {
        InstrumentationRegistry.getArguments().getString("teamsUrl")
            ?: BuildConfig.TEAMS_MEETING_LINK
    }

    val tokenFunctionUrl by lazy {
        InstrumentationRegistry.getArguments().getString("tokenFunctionUrl")
            ?: BuildConfig.TOKEN_FUNCTION_URL
    }

    val aadToken by lazy {
        InstrumentationRegistry.getArguments().getString("aadToken")
            ?: BuildConfig.AAD_TOKEN
    }

    val expiredToken by lazy {
        InstrumentationRegistry.getArguments().getString("expiredToken")
            ?: BuildConfig.ACS_TOKEN_EXPIRED
    }

    val groupId by lazy {
        InstrumentationRegistry.getArguments().getString("groupId")
            ?: BuildConfig.GROUP_CALL_ID
    }
}
