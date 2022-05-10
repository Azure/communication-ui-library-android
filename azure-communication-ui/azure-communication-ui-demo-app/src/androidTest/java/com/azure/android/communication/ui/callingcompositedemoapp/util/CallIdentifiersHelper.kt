// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.util

import com.github.kittinunf.fuel.httpGet
import org.json.JSONObject
import java.util.UUID

class CallIdentifiersHelper {
    companion object {
        fun getUUID() = UUID.randomUUID().toString()
        fun getACSToken(): String = JSONObject(
            TestFixture.tokenFunctionUrl.httpGet().responseString().component3()
                .get()
        ).getString("token")
    }
}
