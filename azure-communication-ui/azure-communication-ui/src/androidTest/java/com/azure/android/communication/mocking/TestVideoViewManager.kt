// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.mocking

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.azure.android.communication.calling.VideoStreamRenderer
import com.azure.android.communication.ui.calling.presentation.VideoViewManager

internal class TestVideoViewManager(private val context: Context) : VideoViewManager {
    override fun getScreenShareVideoStreamRenderer(): VideoStreamRenderer? {
        return null
    }

    override fun destroy() {
    }

    override fun removeRemoteParticipantVideoRenderer(userVideoStreams: List<Pair<String, String>>) {
    }

    override fun updateLocalVideoRenderer(videoStreamID: String?) {
    }

    override fun getLocalVideoRenderer(videoStreamID: String): View {
        return FrameLayout(context)
    }

    override fun getRemoteVideoStreamRenderer(participantID: String, videoStreamId: String): View? {
        if (participantID == "user2") {
            return FrameLayout(context)
        }
        return null
    }
}
