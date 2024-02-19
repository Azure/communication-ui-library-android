// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.mocking

import android.content.Context
import android.view.View
import android.webkit.WebView
import android.widget.FrameLayout
import com.azure.android.communication.calling.CameraFacing
import com.azure.android.communication.calling.CreateViewOptions
import com.azure.android.communication.calling.MediaStreamType
import com.azure.android.communication.calling.ScalingMode
import com.azure.android.communication.ui.calling.presentation.VideoStreamRendererFactory
import com.azure.android.communication.ui.calling.service.sdk.LocalVideoStream
import com.azure.android.communication.ui.calling.service.sdk.RemoteVideoStream
import com.azure.android.communication.ui.calling.service.sdk.StreamSize
import com.azure.android.communication.ui.calling.service.sdk.VideoDeviceInfo
import com.azure.android.communication.ui.calling.service.sdk.VideoStreamRenderer
import com.azure.android.communication.ui.calling.service.sdk.VideoStreamRendererView
import com.google.android.apps.common.testing.accessibility.framework.replacements.LayoutParams

internal class TestVideoStreamRendererFactory(private val callEvents: CallEvents) :
    VideoStreamRendererFactory {
    override fun getRemoteParticipantVideoStreamRenderer(
        stream: RemoteVideoStream,
        context: Context,
    ): VideoStreamRenderer {
        // subscribe to events happening on the `stream`
        // `stream` itself doesn't support this, so we need an aux event provider
        return TestVideoStreamRendererLocalWrapper(context, callEvents, Stream.Remote(stream))
    }

    override fun getLocalParticipantVideoStreamRenderer(
        stream: LocalVideoStream,
        context: Context,
    ): VideoStreamRenderer {
        return TestVideoStreamRendererLocalWrapper(context, callEvents, Stream.Local(stream))
    }
}

internal enum class VideoType {
    REMOTE_VIDEO,
    REMOTE_SCREEN,
    LOCAL_VIDEO_FRONT,
    LOCAL_VIDEO_BACK,
    LOCAL_SCREEN_SHARE,
}

internal sealed class Stream {
    data class Local(val stream: LocalVideoStream) : Stream()

    data class Remote(val stream: RemoteVideoStream) : Stream()
}

internal class TestVideoStreamRendererLocalWrapper(
    private val context: Context,
    callEvents: CallEvents,
    stream: Stream,
) : VideoStreamRenderer, LocalStreamEventObserver {
    private val videoType: VideoType

    init {
        when (stream) {
            is Stream.Local -> {
                videoType =
                    when (stream.stream.source.cameraFacing) {
                        CameraFacing.FRONT -> VideoType.LOCAL_VIDEO_FRONT
                        CameraFacing.BACK -> VideoType.LOCAL_VIDEO_BACK
                        else -> TODO("Camera modes that aren't Front or Back not yet implemented")
                    }
                callEvents.localStreamObservers[stream.stream] = this
            }
            is Stream.Remote -> {
                videoType =
                    if (stream.stream.mediaStreamType == MediaStreamType.SCREEN_SHARING) {
                        VideoType.REMOTE_SCREEN
                    } else {
                        VideoType.REMOTE_VIDEO
                    }
            }
        }
    }

    private var view: TestVideoStreamRendererView? = null

    override fun createView(): VideoStreamRendererView {
        return createViewInternal()
    }

    override fun createView(options: CreateViewOptions): VideoStreamRendererView {
        return createViewInternal(options)
    }

    private fun createViewInternal(options: CreateViewOptions? = null): VideoStreamRendererView {
        val v =
            synchronized(this) {
                if (view != null) {
                    view!!
                } else {
                    TestVideoStreamRendererView(context, videoType).also { view = it }
                }
            }
        return v
    }

    override fun dispose() =
        synchronized(this) {
            view = null
        }

    override fun getStreamSize(): StreamSize? {
        return view?.let {
            StreamSize(it.getView().width, it.getView().height)
        }
    }

    override fun onSwitchSource(deviceInfo: VideoDeviceInfo) {
        view?.let {
            when (deviceInfo.cameraFacing) {
                CameraFacing.FRONT -> it.switchVideoType(VideoType.LOCAL_VIDEO_FRONT)
                CameraFacing.BACK -> it.switchVideoType(VideoType.LOCAL_VIDEO_BACK)
                else -> TODO("Not yet implemented")
            }
        }
    }
}

internal class TestVideoStreamRendererView(context: Context, videoType: VideoType) : VideoStreamRendererView {
    private val v =
        WebView(context).also {
            it.settings.allowFileAccess = true
            it.layoutParams = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            it.loadUrl(fileName(videoType))
        }

    override fun dispose() {}

    override fun getView(): View {
        return v
    }

    override fun updateScalingMode(scalingMode: ScalingMode) {
    }

    fun switchVideoType(newVideoType: VideoType) {
        v.loadUrl(fileName(newVideoType))
    }

    private fun fileName(videoType: VideoType): String {
        val f =
            when (videoType) {
                VideoType.REMOTE_VIDEO -> "remoteVideo.html"
                VideoType.REMOTE_SCREEN -> "remoteScreenShare.html"
                VideoType.LOCAL_VIDEO_BACK -> "localVideoBack.html"
                VideoType.LOCAL_VIDEO_FRONT -> "localVideoFront.html"
                VideoType.LOCAL_SCREEN_SHARE -> "localScreenShare.html"
            }
        return "file:///android_asset/videoStreams/$f"
    }
}
