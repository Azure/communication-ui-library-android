// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.demoapp

import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.common.CommunicationUserIdentifier
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException

internal class AuthServiceRemote(private val authUrl: String) : AuthService {
    companion object {
        private var token: String? = null
        private var communicationIdentifier: CommunicationIdentifier? = null
    }

    override val currentUserToken: String
        get() {
            return token!!
        }

    override val currentUserCommunicationIdentifier: CommunicationIdentifier
        get() {
            return communicationIdentifier!!
        }

    override fun tokenRefresher(): String {
        val result = authUrl.httpPost(listOf(Pair("token", token))).responseString()
        val response = result.component3().get()
        val cause = result.component3().component2()

        if (cause != null) {
            throw IOException("Unable to fetch token: ", cause)
        }

        token = JSONObject(response).getString("token")
        val acsId = JSONObject(response).getString("token")

        communicationIdentifier = CommunicationUserIdentifier(acsId)

        return token!!
    }

    override suspend fun ensureAuthInfo() {
        withContext(Dispatchers.IO) {
            if (token == null || communicationIdentifier == null) {
                val result = authUrl.httpGet().responseString()
                val response = result.component3().get()
                val cause = result.component3().component2()

                if (cause != null) {
                    throw IOException("Unable to fetch token: ", cause)
                }

                token = JSONObject(response).getString("token")
                val acsId =
                    JSONObject(response).getJSONObject("user").getString("communicationUserId")

                communicationIdentifier = CommunicationUserIdentifier(acsId)
            }
        }
    }
}
