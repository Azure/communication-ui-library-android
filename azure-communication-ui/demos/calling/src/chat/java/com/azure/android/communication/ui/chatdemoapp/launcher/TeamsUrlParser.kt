// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chatdemoapp.launcher

import java.net.URLDecoder

internal class TeamsUrlParser {
    companion object {
        fun getThreadId(teamsMeetingLink: String): String {
            var threadId =
                teamsMeetingLink.replace("https://teams.microsoft.com/l/meetup-join/", "")

            val splitText = threadId.split("/")

            if (splitText.isEmpty()) {
                throw Error("Could not get chat thread from teams link")
            }

            threadId = splitText[0]

            return URLDecoder.decode(threadId, "UTF-8")
        }
    }
}
