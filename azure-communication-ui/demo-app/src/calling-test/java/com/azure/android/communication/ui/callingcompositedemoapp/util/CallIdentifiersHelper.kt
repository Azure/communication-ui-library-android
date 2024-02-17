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

            return "eyJhbGciOiJSUzI1NiIsImtpZCI6IjYwNUVCMzFEMzBBMjBEQkRBNTMxODU2MkM4QTM2RDFCMzIyMkE2MTkiLCJ4NXQiOiJZRjZ6SFRDaURiMmxNWVZpeUtOdEd6SWlwaGsiLCJ0eXAiOiJKV1QifQ.eyJza3lwZWlkIjoiYWNzOmI2YWFkYTFmLTBiMWQtNDdhYy04NjZmLTkxYWFlMDBhMWQwMV8wMDAwMDAxZS01NDNmLTZjNDYtNmEwYi0zNDNhMGQwMDkxZjAiLCJzY3AiOjE3OTIsImNzaSI6IjE3MDgxMjgwNjIiLCJleHAiOjE3MDgyMTQ0NjIsInJnbiI6ImFtZXIiLCJhY3NTY29wZSI6InZvaXAsY2hhdCIsInJlc291cmNlSWQiOiJiNmFhZGExZi0wYjFkLTQ3YWMtODY2Zi05MWFhZTAwYTFkMDEiLCJyZXNvdXJjZUxvY2F0aW9uIjoidW5pdGVkc3RhdGVzIiwiaWF0IjoxNzA4MTI4MDYyfQ.F81vJBNDsAwo5DCtfrSNPPIOjThawd3y497ifDd-vNDiYWowVNx5ojZfXbkxmj07mD5ej6jcbjERqpIIWVFyernsvv5IEgILqFa4LnT87dKFrJfylM5_GpypgLsOiUYlqb82UxFQsV-aFjCog1CuPtSAdcZDzQ-2ui1g01-qBEFv6FU0WW0LZ-R6Pg_Fjs1_isOE9QPDcWbHrG7104lEfCBTvICCnNi5oWL6L8T4w0gsmch-tR4qHLab9AeDIcZ7AEsui8plo6R_8CwtfrRcgFUGIbQAOA-aWOYHtITYJbiUusUEGYMF_8FkRQ9H1nTg8nhDk6BpAPSuCw2UFYROFQ"

            val aadToken = TestFixture.aadToken

            return JSONObject(
                Fuel.get(TestFixture.tokenFunctionUrl).authentication().bearer(aadToken)
                    .responseString().component3().get()
            ).getString("token")
        }
    }
}
