// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.banner

import com.azure.android.communication.ui.calling.redux.state.CallingState
import com.azure.android.communication.ui.calling.redux.state.VisibilityState
import com.azure.android.communication.ui.calling.redux.state.VisibilityStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class BannerViewModel {

    private lateinit var bannerInfoTypeStateMutableFlow: MutableStateFlow<BannerInfoType>
    private lateinit var isOverlayDisplayedMutableFlow: MutableStateFlow<Boolean>
    private var shouldShowBannerStateMutableFlow = MutableStateFlow(false)
    private var recordingState: ComplianceState = ComplianceState.OFF
    private var transcriptionState: ComplianceState = ComplianceState.OFF
    private var _displayedBannerType: BannerInfoType = BannerInfoType.BLANK
    private var hideWhileInPip: Boolean = false

    val bannerInfoTypeStateFlow: StateFlow<BannerInfoType>
        get() = bannerInfoTypeStateMutableFlow
    val isOverlayDisplayedFlow: StateFlow<Boolean>
        get() = isOverlayDisplayedMutableFlow
    val shouldShowBannerStateFlow: StateFlow<Boolean>
        get() = shouldShowBannerStateMutableFlow

    var displayedBannerType: BannerInfoType
        get() = _displayedBannerType
        internal set(value) {
            _displayedBannerType = value
        }

    fun init(
        callingState: CallingState,
        isOverlayDisplayedOverGrid: Boolean,
    ) {
        bannerInfoTypeStateMutableFlow = MutableStateFlow(
            createBannerInfoType(
                callingState.isRecording,
                callingState.isTranscribing
            )
        )
        isOverlayDisplayedMutableFlow = MutableStateFlow(isOverlayDisplayedOverGrid)
    }

    fun update(
        callingState: CallingState,
        visibilityState: VisibilityState,
        isOverlayDisplayedOverGrid: Boolean,
    ) {

        if (hideWhileInPip && visibilityState.status == VisibilityStatus.VISIBLE) {
            shouldShowBannerStateMutableFlow.value = true
            hideWhileInPip = false
            return
        }

        val currentBannerInfoType = bannerInfoTypeStateFlow.value
        val newBannerInfoType =
            createBannerInfoType(callingState.isRecording, callingState.isTranscribing)

        if (newBannerInfoType != currentBannerInfoType) {
            bannerInfoTypeStateMutableFlow.value = newBannerInfoType
            shouldShowBannerStateMutableFlow.value = true
            hideWhileInPip = false
        }

        if (shouldShowBannerStateMutableFlow.value && visibilityState.status != VisibilityStatus.VISIBLE) {
            shouldShowBannerStateMutableFlow.value = false
            hideWhileInPip = true
            return
        }
        isOverlayDisplayedMutableFlow.value = isOverlayDisplayedOverGrid
    }

    fun setDisplayedBannerType(bannerInfoType: BannerInfoType) {
        _displayedBannerType = bannerInfoType
    }

    fun dismissBanner() {
        hideWhileInPip = false
        shouldShowBannerStateMutableFlow.value = false
        _displayedBannerType = BannerInfoType.BLANK
        resetStoppedStates()
    }

    private fun createBannerInfoType(
        isRecording: Boolean,
        isTranscribing: Boolean,
    ): BannerInfoType {
        recordingState = when (isRecording) {
            true -> ComplianceState.ON
            false -> {
                if (recordingState == ComplianceState.ON) {
                    ComplianceState.STOPPED
                } else {
                    recordingState
                }
            }
        }

        transcriptionState = when (isTranscribing) {
            true -> ComplianceState.ON
            false -> {
                if (transcriptionState == ComplianceState.ON) {
                    ComplianceState.STOPPED
                } else {
                    transcriptionState
                }
            }
        }

        if ((recordingState == ComplianceState.ON) &&
            (transcriptionState == ComplianceState.ON)
        ) {
            return BannerInfoType.RECORDING_AND_TRANSCRIPTION_STARTED
        } else if ((recordingState == ComplianceState.ON) &&
            (transcriptionState == ComplianceState.OFF)
        ) {
            return BannerInfoType.RECORDING_STARTED
        } else if ((recordingState == ComplianceState.ON) &&
            (transcriptionState == ComplianceState.STOPPED)
        ) {
            return BannerInfoType.TRANSCRIPTION_STOPPED_STILL_RECORDING
        } else if ((recordingState == ComplianceState.OFF) &&
            (transcriptionState == ComplianceState.ON)
        ) {
            return BannerInfoType.TRANSCRIPTION_STARTED
        } else if ((recordingState == ComplianceState.OFF) &&
            (transcriptionState == ComplianceState.OFF)
        ) {
            return BannerInfoType.BLANK
        } else if ((recordingState == ComplianceState.OFF) &&
            (transcriptionState == ComplianceState.STOPPED)
        ) {
            return BannerInfoType.TRANSCRIPTION_STOPPED
        } else if ((recordingState == ComplianceState.STOPPED) &&
            (transcriptionState == ComplianceState.ON)
        ) {
            return BannerInfoType.RECORDING_STOPPED_STILL_TRANSCRIBING
        } else if ((recordingState == ComplianceState.STOPPED) &&
            (transcriptionState == ComplianceState.OFF)
        ) {
            return BannerInfoType.RECORDING_STOPPED
        } else if ((recordingState == ComplianceState.STOPPED) &&
            (transcriptionState == ComplianceState.STOPPED)
        ) {
            resetStoppedStates()
            return BannerInfoType.RECORDING_AND_TRANSCRIPTION_STOPPED
        } else {
            return BannerInfoType.BLANK
        }
    }

    private fun resetStoppedStates() {
        if (recordingState == ComplianceState.STOPPED) {
            recordingState = ComplianceState.OFF
        }
        if (transcriptionState == ComplianceState.STOPPED) {
            transcriptionState = ComplianceState.OFF
        }
    }
}

internal enum class ComplianceState {
    ON,
    STOPPED,
    OFF
}

internal enum class BannerInfoType {
    RECORDING_AND_TRANSCRIPTION_STARTED,
    RECORDING_STARTED,
    TRANSCRIPTION_STOPPED_STILL_RECORDING,
    TRANSCRIPTION_STARTED,
    BLANK,
    TRANSCRIPTION_STOPPED,
    RECORDING_STOPPED_STILL_TRANSCRIBING,
    RECORDING_STOPPED,
    RECORDING_AND_TRANSCRIPTION_STOPPED
}
