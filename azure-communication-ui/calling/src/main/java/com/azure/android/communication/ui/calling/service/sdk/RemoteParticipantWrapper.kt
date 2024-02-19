// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service.sdk

import com.azure.android.communication.calling.ParticipantState
import com.azure.android.communication.calling.PropertyChangedListener
import com.azure.android.communication.calling.RemoteVideoStreamsUpdatedListener
import com.azure.android.communication.calling.RemoteParticipant as NativeRemoteParticipant

internal class RemoteParticipantWrapper(private val inner: NativeRemoteParticipant) : RemoteParticipant {
    override val identifier: CommunicationIdentifier
        get() {
            return inner.identifier.into()
        }

    override val displayName: String
        get() {
            return inner.displayName
        }

    override val isSpeaking: Boolean
        get() {
            return inner.isSpeaking
        }

    override val isMuted: Boolean
        get() {
            return inner.isMuted
        }

    override val state: ParticipantState
        get() {
            return inner.state
        }

    override val videoStreams: List<RemoteVideoStream>
        get() {
            return inner.videoStreams.map { it.into() }
        }

    override fun addOnVideoStreamsUpdatedListener(listener: RemoteVideoStreamsUpdatedListener?) {
        inner.addOnVideoStreamsUpdatedListener(listener)
    }

    override fun addOnIsMutedChangedListener(listener: PropertyChangedListener?) {
        inner.addOnIsMutedChangedListener(listener)
    }

    override fun addOnIsSpeakingChangedListener(listener: PropertyChangedListener?) {
        inner.addOnIsSpeakingChangedListener(listener)
    }

    override fun addOnStateChangedListener(listener: PropertyChangedListener?) {
        inner.addOnStateChangedListener(listener)
    }

    override fun removeOnVideoStreamsUpdatedListener(listener: RemoteVideoStreamsUpdatedListener?) {
        inner.removeOnVideoStreamsUpdatedListener(listener)
    }

    override fun removeOnIsMutedChangedListener(listener: PropertyChangedListener?) {
        inner.removeOnIsMutedChangedListener(listener)
    }

    override fun removeOnIsSpeakingChangedListener(listener: PropertyChangedListener?) {
        inner.removeOnIsSpeakingChangedListener(listener)
    }

    override fun removeOnStateChangedListener(listener: PropertyChangedListener?) {
        inner.removeOnStateChangedListener(listener)
    }
}
