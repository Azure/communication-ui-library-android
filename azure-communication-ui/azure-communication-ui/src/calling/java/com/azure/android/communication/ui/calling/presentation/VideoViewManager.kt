// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation

import android.content.Context
import android.graphics.Matrix
import android.graphics.Point
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import com.azure.android.communication.calling.*
import com.azure.android.communication.ui.calling.service.sdk.CallingSDKWrapper
import java.util.*
import kotlin.collections.HashMap

internal class VideoViewManager(
    private val callingSDKWrapper: CallingSDKWrapper,
    private val context: Context,
) {
    private val remoteParticipantVideoRendererMap: HashMap<String, VideoRenderer> = HashMap()
    private val localParticipantVideoRendererMap: HashMap<String, VideoRenderer> = HashMap()

    private class VideoRenderer(
        var rendererView: VideoStreamRendererView?,
        var videoStreamRenderer: VideoStreamRenderer?,
        var videoStreamID: String,
        var isScreenShareView: Boolean,
    )

    fun getScreenShareVideoStreamRenderer(): VideoStreamRenderer? {
        remoteParticipantVideoRendererMap.values.forEach {
            if (it.isScreenShareView) {
                return it.videoStreamRenderer
            }
        }
        return null
    }

    fun destroy() {
        localParticipantVideoRendererMap.values.map { videoRenderer ->
            destroyVideoRenderer(videoRenderer)
        }
        remoteParticipantVideoRendererMap.values.map { videoRenderer ->
            destroyVideoRenderer(videoRenderer)
        }
        remoteParticipantVideoRendererMap.clear()
        localParticipantVideoRendererMap.clear()
    }

    fun removeRemoteParticipantVideoRenderer(
        userVideoStreams: List<Pair<String, String>>,
    ) {
        removeRemoteParticipantRenderer(userVideoStreams)
    }

    fun updateLocalVideoRenderer(videoStreamID: String?) {
        removeLocalParticipantRenderer(videoStreamID)
        if (videoStreamID != null) {
            if (!localParticipantVideoRendererMap.containsKey(videoStreamID)) {
                val videoStream = callingSDKWrapper.getLocalVideoStream().get()
                val videoStreamRenderer =
                    VideoStreamRenderer(videoStream, context)
                val rendererView = videoStreamRenderer.createView()
                localParticipantVideoRendererMap[videoStreamID] =
                    VideoRenderer(rendererView, videoStreamRenderer, videoStreamID, false)
            }
        }
    }

    fun getLocalVideoRenderer(videoStreamID: String): View? {
        var rendererView: VideoStreamRendererView? = null
        if (localParticipantVideoRendererMap.containsKey(videoStreamID)) {
            rendererView = localParticipantVideoRendererMap[videoStreamID]?.rendererView
        }
        detachFromParentView(rendererView)

        Timer().schedule(object : TimerTask() {
            override fun run() {
                ViewScaleHelper.scaleView(
                    rendererView?.get(0) as TextureView,
                    rendererView.width, rendererView.height,
                    1920,
                    -1080,
                    true, Point(0,0))
            }
        }, 5000)
        return rendererView
    }

    fun getRemoteVideoStreamRenderer(
        participantID: String,
        videoStreamId: String,
    ): View? {
        var rendererView: VideoStreamRendererView? = null
        val uniqueID = generateUniqueKey(participantID, videoStreamId)
        if (remoteParticipantVideoRendererMap.containsKey(uniqueID)) {
            rendererView = remoteParticipantVideoRendererMap[uniqueID]?.rendererView
        } else if (updateRemoteParticipantVideoRenderer(
                participantID,
                videoStreamId,
                context
            )
        ) {
            rendererView = remoteParticipantVideoRendererMap[uniqueID]?.rendererView
        }

        detachFromParentView(rendererView)

        return rendererView
    }

    private fun updateRemoteParticipantVideoRenderer(
        userID: String,
        videoStreamID: String,
        context: Context,
    ): Boolean {
        val remoteParticipants = callingSDKWrapper.getRemoteParticipantsMap()

        val uniqueID = generateUniqueKey(userID, videoStreamID)

        if (!remoteParticipantVideoRendererMap.containsKey(uniqueID)) {

            if (remoteParticipants.containsKey(userID) &&
                remoteParticipants[userID]?.videoStreams != null &&
                remoteParticipants[userID]?.videoStreams?.size!! > 0
            ) {
                var stream: RemoteVideoStream? = null
                remoteParticipants[userID]?.videoStreams?.forEach { videoStream ->
                    if (videoStream.id.toString() == videoStreamID) {
                        stream = videoStream
                    }
                }

                if (stream != null) {
                    val isScreenShare = stream!!.mediaStreamType == MediaStreamType.SCREEN_SHARING
                    val videoStreamRenderer = VideoStreamRenderer(stream, context)

                    val viewOption =
                        if (isScreenShare) CreateViewOptions(ScalingMode.FIT) else CreateViewOptions(
                            ScalingMode.CROP
                        )

                    val rendererView = videoStreamRenderer.createView(viewOption)
                    remoteParticipantVideoRendererMap[uniqueID] =
                        VideoRenderer(
                            rendererView,
                            videoStreamRenderer,
                            videoStreamID,
                            isScreenShare
                        )
                    return true
                }
            }
        }

        return false
    }

    private fun removeLocalParticipantRenderer(videoStreamID: String?) {
        val removedLocalStreams = localParticipantVideoRendererMap.filter { (streamID, _) ->
            streamID !== videoStreamID
        }

        removedLocalStreams.values.forEach { videoStream ->
            destroyVideoRenderer(videoStream)
        }

        removedLocalStreams.keys.forEach { streamID ->
            localParticipantVideoRendererMap.remove(streamID)
        }
    }

    private fun removeRemoteParticipantRenderer(userWithVideoStreamList: List<Pair<String, String>>) {
        var uniqueIDStreamList: List<String> = mutableListOf()
        userWithVideoStreamList.forEach { (userID, streamID) ->
            uniqueIDStreamList = uniqueIDStreamList.plus(generateUniqueKey(userID, streamID))
        }
        val removedRemoteStreams = remoteParticipantVideoRendererMap.filter { (streamID, _) ->
            streamID !in uniqueIDStreamList
        }

        removedRemoteStreams.values.forEach { videoStream ->
            destroyVideoRenderer(videoStream)
        }

        removedRemoteStreams.keys.forEach { streamID ->
            remoteParticipantVideoRendererMap.remove(streamID)
        }
    }

    private fun generateUniqueKey(userIdentifier: String, videoStreamId: String): String {
        return "$userIdentifier:$videoStreamId"
    }

    private fun destroyVideoRenderer(videoRenderer: VideoRenderer?) {
        if (videoRenderer != null) {
            detachFromParentView(videoRenderer.rendererView)
            videoRenderer.videoStreamRenderer?.dispose()
            videoRenderer.videoStreamRenderer = null
            videoRenderer.rendererView?.dispose()
            videoRenderer.rendererView = null
        }
    }

    private fun detachFromParentView(view: View?) {
        if (view != null && view.parent != null) {
            (view.parent as ViewGroup).removeView(view)
        }
    }
}

internal object ViewScaleHelper {
    fun scaleView(
        view: TextureView?,
        viewWidth: Int,
        viewHeight: Int,
        videoWidth: Int,
        videoHeight: Int,
        scaleToFit: Boolean,
        offset: Point
    ) {
        if (view != null && view.isAvailable) {
            val scaleWidth = viewWidth.toFloat() / videoWidth.toFloat()
            val scaleHeight = viewHeight.toFloat() / videoHeight.toFloat()
            val matrix = view.getTransform(null as Matrix?)
            val scale = if (scaleToFit) Math.min(scaleWidth, scaleHeight) else Math.max(
                scaleWidth,
                scaleHeight
            )
            val sw = (scale * videoWidth.toFloat()).toInt()
            val sh = (scale * videoHeight.toFloat()).toInt()
            val scaleX = sw.toFloat() / viewWidth.toFloat()
            val scaleY = sh.toFloat() / viewHeight.toFloat()
            matrix.setScale(scaleX, scaleY)
            val translateX: Float
            val translateY: Float
            if (scaleToFit) {
                translateX = (viewWidth - sw).toFloat() / 2.0f
                translateY = (viewHeight - sh).toFloat() / 2.0f
                matrix.postTranslate(translateX, translateY)
            } else {
                translateX = (viewWidth - sw).toFloat() / 2.0f + offset.x.toFloat()
                translateY = (viewHeight - sh).toFloat() / 2.0f + offset.y.toFloat()
                val translateX =
                    clampValue(translateX, (viewWidth - sw).toFloat(), 0.0f)
                val translateY =
                    clampValue(translateY, (viewHeight - sh).toFloat(), 0.0f)
                matrix.postTranslate(translateX, translateY)
                offset.x = (offset.x.toFloat() + (translateX - translateX)).toInt()
                offset.y = (offset.y.toFloat() + (translateY - translateY)).toInt()
            }
            view.setTransform(matrix)
        }
    }

    private fun clampValue(value: Float, min: Float, max: Float): Float {
        return Math.min(Math.max(value, min), max)
    }
}