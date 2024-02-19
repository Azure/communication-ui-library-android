// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.util

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import org.json.JSONObject
import java.util.UUID

class CallIdentifiersHelper {
    companion object {
        fun getGroupId() = UUID.randomUUID().toString()

        fun getACSToken(): String {
            val aadToken = TestFixture.aadToken

            return JSONObject(
                Fuel.get(TestFixture.tokenFunctionUrl).authentication().bearer(aadToken)
                    .responseString().component3().get(),
            ).getString("token")
        }
    }
}
