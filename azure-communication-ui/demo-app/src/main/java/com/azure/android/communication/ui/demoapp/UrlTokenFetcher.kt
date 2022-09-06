// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.demoapp

import com.github.kittinunf.fuel.httpGet
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.Callable

// The Calling SDK Requires a callable that can return a string
// It is run in a Handler (non-ui thread) so this network call is blocking.
class UrlTokenFetcher(private val tokenFunctionURL: String) : Callable<String> {
    override fun call(): String {
        val result = tokenFunctionURL.httpGet().responseString()
        val response = result.component3().get()
        val cause = result.component3().component2()

        if (cause != null) {
            throw IOException("Unable to fetch token: ", cause)
        } else {
            return JSONObject(response).getString("token")
        }
    }
}
