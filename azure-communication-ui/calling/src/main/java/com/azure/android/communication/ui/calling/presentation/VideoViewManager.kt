// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.azure.android.communication.calling.CreateViewOptions
import com.azure.android.communication.calling.MediaStreamType
import com.azure.android.communication.calling.ScalingMode
import com.azure.android.communication.ui.calling.service.sdk.CallingSDK
import com.azure.android.communication.ui.calling.service.sdk.LocalVideoStream
import com.azure.android.communication.ui.calling.service.sdk.RemoteVideoStream
import com.azure.android.communication.ui.calling.service.sdk.VideoStreamRenderer
import com.azure.android.communication.ui.calling.service.sdk.VideoStreamRendererLocalWrapper
import com.azure.android.communication.ui.calling.service.sdk.VideoStreamRendererRemoteWrapper
import com.azure.android.communication.ui.calling.service.sdk.VideoStreamRendererView
import com.azure.android.communication.ui.calling.utilities.isAndroidTV

internal class VideoViewManager(
    private val callingSDKWrapper: CallingSDK,
    private val context: Context,
    private val videoStreamRendererFactory: VideoStreamRendererFactory,
) {
    private val remoteParticipantVideoRendererMap: HashMap<String, VideoRenderer> = HashMap()
    private val localParticipantVideoRendererMap: HashMap<String, VideoRenderer> = HashMap()
    private val isAndroidTV = isAndroidTV(context)

    private class VideoRenderer(
        var rendererView: VideoStreamRendererView?,
        var videoStreamRenderer: VideoStreamRenderer?,
        var isScreenShareView: Boolean,
    )

    fun updateScalingForRemoteStream() {
        val remoteParticipants = callingSDKWrapper.getRemoteParticipantsMap()
        // for TV, on new participant join, change first remote participant scaling from fit to crop
        if (isAndroidTV && remoteParticipants.size > 1 && remoteParticipantVideoRendererMap.size == 1) {
            if (!remoteParticipantVideoRendererMap.values.first().isScreenShareView) {
                remoteParticipantVideoRendererMap.values.first().rendererView?.let {
                    it.updateScalingMode(ScalingMode.CROP)
                }
            }
        }
    }

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

    fun removeRemoteParticipantVideoRenderer(userVideoStreams: List<Pair<String, String>>) {
        removeRemoteParticipantRenderer(userVideoStreams)
        val remoteParticipants = callingSDKWrapper.getRemoteParticipantsMap()

        // for TV, for last participant, change last remote participant scaling from crop to fit
        if (isAndroidTV && userVideoStreams.isNotEmpty() &&
            remoteParticipantVideoRendererMap.size == 1 &&
            remoteParticipants.size == 1
        ) {
            if (!remoteParticipantVideoRendererMap.values.first().isScreenShareView) {
                remoteParticipantVideoRendererMap.values.first().rendererView?.let {
                    it.updateScalingMode(ScalingMode.FIT)
                }
            }
        }
    }

    fun updateLocalVideoRenderer(videoStreamID: String?) {
        removeLocalParticipantRenderer(videoStreamID)
        if (videoStreamID != null) {
            if (!localParticipantVideoRendererMap.containsKey(videoStreamID)) {
                callingSDKWrapper.getLocalVideoStream().get()?.let { videoStream ->
                    val videoStreamRenderer =
                        videoStreamRendererFactory.getLocalParticipantVideoStreamRenderer(
                            videoStream,
                            context,
                        )
                    val rendererView = videoStreamRenderer.createView()
                    localParticipantVideoRendererMap[videoStreamID] =
                        VideoRenderer(rendererView, videoStreamRenderer, false)
                }
            }
        }
    }

    fun getLocalVideoRenderer(
        videoStreamID: String,
        scalingMode: ScalingMode,
    ): View? {
        var rendererView: VideoStreamRendererView? = null
        if (localParticipantVideoRendererMap.containsKey(videoStreamID)) {
            rendererView = localParticipantVideoRendererMap[videoStreamID]?.rendererView
        }

        rendererView?.updateScalingMode(scalingMode)

        detachFromParentView(rendererView?.getView())
        return rendererView?.getView()
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
                context,
            )
        ) {
            rendererView = remoteParticipantVideoRendererMap[uniqueID]?.rendererView
        }

        detachFromParentView(rendererView?.getView())

        return rendererView?.getView()
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
                val stream =
                    remoteParticipants[userID]?.videoStreams?.find { videoStream ->
                        videoStream.id.toString() == videoStreamID
                    }

                if (stream != null) {
                    val isScreenShare = stream.mediaStreamType == MediaStreamType.SCREEN_SHARING
                    val videoStreamRenderer =
                        videoStreamRendererFactory.getRemoteParticipantVideoStreamRenderer(
                            stream,
                            context,
                        )

                    val forceFitMode = isAndroidTV && !isScreenShare && remoteParticipants.size <= 1

                    val rendererView =
                        if (isScreenShare || forceFitMode) {
                            videoStreamRenderer.createView(
                                CreateViewOptions(
                                    ScalingMode.FIT,
                                ),
                            )
                        } else {
                            videoStreamRenderer.createView()
                        }

                    remoteParticipantVideoRendererMap[uniqueID] =
                        VideoRenderer(
                            rendererView,
                            videoStreamRenderer,
                            isScreenShare,
                        )
                    return true
                }
            }
        }

        return false
    }

    private fun removeLocalParticipantRenderer(videoStreamID: String?) {
        val removedLocalStreams =
            localParticipantVideoRendererMap.filter { (streamID, _) ->
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
        val removedRemoteStreams =
            remoteParticipantVideoRendererMap.filter { (streamID, _) ->
                streamID !in uniqueIDStreamList
            }

        removedRemoteStreams.values.forEach { videoStream ->
            destroyVideoRenderer(videoStream)
        }

        removedRemoteStreams.keys.forEach { streamID ->
            remoteParticipantVideoRendererMap.remove(streamID)
        }
    }

    private fun generateUniqueKey(
        userIdentifier: String,
        videoStreamId: String,
    ): String {
        return "$userIdentifier:$videoStreamId"
    }

    private fun destroyVideoRenderer(videoRenderer: VideoRenderer?) {
        if (videoRenderer != null) {
            detachFromParentView(videoRenderer.rendererView?.getView())
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

internal interface VideoStreamRendererFactory {
    fun getRemoteParticipantVideoStreamRenderer(
        stream: RemoteVideoStream,
        context: Context,
    ): VideoStreamRenderer

    fun getLocalParticipantVideoStreamRenderer(
        stream: LocalVideoStream,
        context: Context,
    ): VideoStreamRenderer
}

internal class VideoStreamRendererFactoryImpl : VideoStreamRendererFactory {
    override fun getRemoteParticipantVideoStreamRenderer(
        stream: RemoteVideoStream,
        context: Context,
    ) = VideoStreamRendererRemoteWrapper(
        stream.native as com.azure.android.communication.calling.RemoteVideoStream,
        context,
    )

    override fun getLocalParticipantVideoStreamRenderer(
        stream: LocalVideoStream,
        context: Context,
    ) = VideoStreamRendererLocalWrapper(
        stream.native as com.azure.android.communication.calling.LocalVideoStream,
        context,
    )
}
