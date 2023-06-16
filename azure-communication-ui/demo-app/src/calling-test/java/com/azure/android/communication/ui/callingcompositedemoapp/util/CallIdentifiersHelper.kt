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

            return "eyJhbGciOiJSUzI1NiIsImtpZCI6IjVFODQ4MjE0Qzc3MDczQUU1QzJCREU1Q0NENTQ0ODlEREYyQzRDODQiLCJ4NXQiOiJYb1NDRk1kd2M2NWNLOTVjelZSSW5kOHNUSVEiLCJ0eXAiOiJKV1QifQ.eyJza3lwZWlkIjoiYWNzOmI2YWFkYTFmLTBiMWQtNDdhYy04NjZmLTkxYWFlMDBhMWQwMV8wMDAwMDAxOS02NTNiLWQxYzItMjhmNC0zNDNhMGQwMGY1ZDEiLCJzY3AiOjE3OTIsImNzaSI6IjE2ODY5MzgyMDEiLCJleHAiOjE2ODcwMjQ2MDEsInJnbiI6ImFtZXIiLCJhY3NTY29wZSI6InZvaXAiLCJyZXNvdXJjZUlkIjoiYjZhYWRhMWYtMGIxZC00N2FjLTg2NmYtOTFhYWUwMGExZDAxIiwicmVzb3VyY2VMb2NhdGlvbiI6InVuaXRlZHN0YXRlcyIsImlhdCI6MTY4NjkzODIwMX0.nCNeJM5ZOAqEZfO3xUtELrM58JUUpoVsFd3f7GsZr167x_tdkFXmVwBmeu55gyJRT7wNaj2xZGbVXWFhpouDvKKFYsfc-nS1KjcaNATpfZ43CBY8DtfRz0hwWbTle_-seL5VxWV0NJjzgZssAWvydG1Svgx53_YHGX-IsAnxZlsi9N6ki4gDD78-xW5A5yx2PE6x0N7eOc2z3l20By9OqSdvSl7fyCMgLkCJU-AFyfF5EVni3OE364bdqOxoYex6e241hGDKr_vBSRs67zcrkxugazayiBx1QKTPzbxjhGjG0x49__-f30Xyg6eq25y-b8IqLupIO3TPgmQkXv1uOg"
        }
    }
}
