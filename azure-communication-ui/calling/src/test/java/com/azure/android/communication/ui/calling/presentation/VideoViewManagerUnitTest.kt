// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation

import android.content.Context
import android.widget.FrameLayout
import com.azure.android.communication.calling.MediaStreamType
import com.azure.android.communication.calling.ScalingMode
import com.azure.android.communication.ui.calling.presentation.VideoStreamRendererFactory
import com.azure.android.communication.ui.calling.presentation.VideoViewManager
import com.azure.android.communication.ui.calling.service.sdk.CallingSDKWrapper
import com.azure.android.communication.ui.calling.service.sdk.LocalVideoStream
import com.azure.android.communication.ui.calling.service.sdk.RemoteParticipant
import com.azure.android.communication.ui.calling.service.sdk.RemoteVideoStream
import com.azure.android.communication.ui.calling.service.sdk.VideoStreamRenderer
import com.azure.android.communication.ui.calling.service.sdk.VideoStreamRendererView
import java9.util.concurrent.CompletableFuture
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
internal class VideoViewManagerUnitTest {

    @Test
    fun videoViewManager_destroy_then_clearLocalAndRemoteParticipantVideoRendererMap() {
        // arrange
        val mockVideoStream = mock<RemoteVideoStream> {
            on { id } doAnswer { 111 }
            on { mediaStreamType } doAnswer { MediaStreamType.VIDEO }
        }
        val mockRemoteParticipant = mock<RemoteParticipant> {
            on { videoStreams } doAnswer { listOf(mockVideoStream) }
        }

        val remoteParticipantMap: MutableMap<String, RemoteParticipant> = mutableMapOf()
        remoteParticipantMap["user"] = mockRemoteParticipant

        val mockLocalVideoStream = mock<LocalVideoStream> { }

        val localVideoStreamCompletableFuture =
            CompletableFuture<LocalVideoStream>()
        localVideoStreamCompletableFuture.complete(mockLocalVideoStream)

        val mockCallingSDKWrapper = mock<CallingSDKWrapper> {
            on { getRemoteParticipantsMap() } doAnswer { remoteParticipantMap }
            on { getLocalVideoStream() } doAnswer { localVideoStreamCompletableFuture }
        }

        val mockUiModeManager = mock<android.app.UiModeManager> {
            on { currentModeType } doAnswer { android.content.res.Configuration.UI_MODE_TYPE_WATCH }
        }

        val mockContext = mock<Context> {
            on { getSystemService(Context.UI_MODE_SERVICE) } doAnswer { mockUiModeManager }
        }

        val mockLayout = mock<FrameLayout> {}

        val mockVideoStreamRendererView = mock<VideoStreamRendererView> {
            on { getView() } doAnswer { mockLayout }
        }

        val mockVideoStreamRenderer = mock<VideoStreamRenderer> {
            on { createView() } doAnswer { mockVideoStreamRendererView }
        }

        val mockVideoStreamRendererHelper = mock<VideoStreamRendererFactory> {
            on {
                getRemoteParticipantVideoStreamRenderer(
                    any(),
                    any()
                )
            } doAnswer { mockVideoStreamRenderer }
            on {
                getLocalParticipantVideoStreamRenderer(
                    any(),
                    any()
                )
            } doAnswer { mockVideoStreamRenderer }
        }

        val videoViewManager =
            VideoViewManager(mockCallingSDKWrapper, mockContext, mockVideoStreamRendererHelper)

        val remoteVideoView =
            videoViewManager.getRemoteVideoStreamRenderer("user", "111")
        videoViewManager.updateLocalVideoRenderer("345")
        val localVideoView = videoViewManager.getLocalVideoRenderer("345", ScalingMode.FIT)

        // act
        videoViewManager.destroy()
        remoteParticipantMap.clear()
        val remoteVideoViewAfterDelete =
            videoViewManager.getRemoteVideoStreamRenderer("user", "111")
        val localVideoViewAfterDelete = videoViewManager.getLocalVideoRenderer("345", ScalingMode.FIT)

        // assert
        Assert.assertNotNull(remoteVideoView)
        Assert.assertNotNull(localVideoView)
        Assert.assertEquals(remoteVideoViewAfterDelete, null)
        Assert.assertEquals(localVideoViewAfterDelete, null)
    }

    @Test
    fun videoViewManager_updateLocalVideoRenderer_when_calledWithValidID_then_insertLocalVideoViewInMap() {
        // arrange
        val mockLocalVideoStream = mock<LocalVideoStream> { }

        val localVideoStreamCompletableFuture =
            CompletableFuture<LocalVideoStream>()
        localVideoStreamCompletableFuture.complete(mockLocalVideoStream)

        val mockCallingSDKWrapper = mock<CallingSDKWrapper> {
            on { getLocalVideoStream() } doAnswer { localVideoStreamCompletableFuture }
        }

        val mockUiModeManager = mock<android.app.UiModeManager> {
            on { currentModeType } doAnswer { android.content.res.Configuration.UI_MODE_TYPE_WATCH }
        }

        val mockContext = mock<Context> {
            on { getSystemService(Context.UI_MODE_SERVICE) } doAnswer { mockUiModeManager }
        }

        val mockLayout = mock<FrameLayout> {}

        val mockVideoStreamRendererView = mock<VideoStreamRendererView> {
            on { getView() } doAnswer { mockLayout }
        }

        val mockVideoStreamRenderer = mock<VideoStreamRenderer> {
            on { createView() } doAnswer { mockVideoStreamRendererView }
        }

        val mockVideoStreamRendererHelper = mock<VideoStreamRendererFactory> {
            on {
                getLocalParticipantVideoStreamRenderer(
                    any(),
                    any()
                )
            } doAnswer { mockVideoStreamRenderer }
        }

        val videoViewManager =
            VideoViewManager(mockCallingSDKWrapper, mockContext, mockVideoStreamRendererHelper)

        // act
        videoViewManager.updateLocalVideoRenderer("345")
        val localVideoView = videoViewManager.getLocalVideoRenderer("345", ScalingMode.FIT)

        // assert
        Assert.assertEquals(localVideoView, mockLayout)
    }

    @Test
    fun videoViewManager_getRemoteVideoStreamRenderer_when_calledWithValidIDs_then_returnView() {
        // arrange
        val mockVideoStream = mock<RemoteVideoStream> {
            on { id } doAnswer { 111 }
            on { mediaStreamType } doAnswer { MediaStreamType.VIDEO }
        }
        val mockRemoteParticipant = mock<RemoteParticipant> {
            on { videoStreams } doAnswer { listOf(mockVideoStream) }
        }

        val remoteParticipantMap: MutableMap<String, RemoteParticipant> = mutableMapOf()
        remoteParticipantMap["user"] = mockRemoteParticipant

        val mockCallingSDKWrapper = mock<CallingSDKWrapper> {
            on { getRemoteParticipantsMap() } doAnswer { remoteParticipantMap }
        }

        val mockUiModeManager = mock<android.app.UiModeManager> {
            on { currentModeType } doAnswer { android.content.res.Configuration.UI_MODE_TYPE_WATCH }
        }

        val mockContext = mock<Context> {
            on { getSystemService(Context.UI_MODE_SERVICE) } doAnswer { mockUiModeManager }
        }

        val mockLayout = mock<FrameLayout> {}

        val mockVideoStreamRendererView = mock<VideoStreamRendererView> {
            on { getView() } doAnswer { mockLayout }
        }

        val mockVideoStreamRenderer = mock<VideoStreamRenderer> {
            on { createView() } doAnswer { mockVideoStreamRendererView }
        }

        val mockVideoStreamRendererHelper = mock<VideoStreamRendererFactory> {
            on {
                getRemoteParticipantVideoStreamRenderer(
                    any(),
                    any()
                )
            } doAnswer { mockVideoStreamRenderer }
        }

        val videoViewManager =
            VideoViewManager(mockCallingSDKWrapper, mockContext, mockVideoStreamRendererHelper)

        // act
        val remoteVideoView =
            videoViewManager.getRemoteVideoStreamRenderer("user", "111")

        // assert
        Assert.assertEquals(remoteVideoView, mockLayout)
    }

    @Test
    fun videoViewManager_getRemoteVideoStreamRenderer_when_calledWithInValidIDs_then_returnNullView() {
        // arrange
        val mockRemoteParticipant = mock<RemoteParticipant> {}

        val remoteParticipantMap: MutableMap<String, RemoteParticipant> = mutableMapOf()
        remoteParticipantMap["user"] = mockRemoteParticipant

        val mockCallingSDKWrapper = mock<CallingSDKWrapper> {
            on { getRemoteParticipantsMap() } doAnswer { remoteParticipantMap }
        }

        val mockUiModeManager = mock<android.app.UiModeManager> {
            on { currentModeType } doAnswer { android.content.res.Configuration.UI_MODE_TYPE_WATCH }
        }

        val mockContext = mock<Context> {
            on { getSystemService(Context.UI_MODE_SERVICE) } doAnswer { mockUiModeManager }
        }

        val mockVideoStreamRendererHelper = mock<VideoStreamRendererFactory> {}

        val videoViewManager =
            VideoViewManager(mockCallingSDKWrapper, mockContext, mockVideoStreamRendererHelper)

        // act
        val remoteVideoView =
            videoViewManager.getRemoteVideoStreamRenderer("user1", "111")

        // assert
        Assert.assertEquals(remoteVideoView, null)
    }
}
