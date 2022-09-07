// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callwithchat

internal class TeamsUrlParser {
    companion object {
        fun getThreadId(teamsMeetingLink: String): String {
            var threadId = teamsMeetingLink.replace("https://teams.microsoft.com/l/meetup-join/", "")

            // TODO:
//        threadId = decodeURIComponent(threadId);

            val splitted = threadId.split("/")

            if (splitted.count() < 1) {
                throw Error("Could not get chat thread from teams link")
            }

            threadId = splitted[0]

            return threadId
        }
    }
}
