package com.azure.android.communication.ui.callingcompositedemoapp

import com.github.kittinunf.fuel.httpGet
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.Callable

class UrlTokenFetcher(val tokenFunctionURL : String) : Callable<String> {
    override fun call(): String {
        val result = tokenFunctionURL.httpGet().responseString();
        val response = result.component2().responseMessage
        val cause = result.component3().component2()

        if (cause != null || response == null) {
            throw IOException("Unable to fetch token: ", cause)
        } else {
            return JSONObject(response).getString("token")
        }
    }
}