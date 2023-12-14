// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.util

import com.azure.android.communication.ui.callingcompositedemoapp.BuildConfig

// Do not modify when testing on AppCenter
object TestFixture {
    val teamsUrl by lazy {
        BuildConfig.TEAMS_MEETING_LINK
    }

    val tokenFunctionUrl by lazy {
        BuildConfig.TOKEN_FUNCTION_URL
    }

    val aadToken by lazy {
        BuildConfig.AAD_TOKEN
    }

    val expiredToken by lazy {
        BuildConfig.ACS_TOKEN_EXPIRED
    }

    val groupId by lazy {
        BuildConfig.GROUP_CALL_ID
    }
}
