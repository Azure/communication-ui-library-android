// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.banner

import com.azure.android.communication.ui.calling.redux.state.CallingState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.VisibilityState
import com.azure.android.communication.ui.calling.redux.state.VisibilityStatus
import kotlinx.coroutines.flow.MutableStateFlow

internal class BannerViewModel {

    private var _bannerInfoTypeStateFlow: MutableStateFlow<BannerInfoType> =
        MutableStateFlow(BannerInfoType.BLANK)
    var bannerInfoTypeStateFlow = _bannerInfoTypeStateFlow

    private var _isOverlayDisplayedFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var isOverlayDisplayedFlow = _isOverlayDisplayedFlow

    private var _shouldShowBannerStateFlow = MutableStateFlow(false)
    var shouldShowBannerStateFlow = _shouldShowBannerStateFlow

    private var recordingState: ComplianceState = ComplianceState.OFF
    private var transcriptionState: ComplianceState = ComplianceState.OFF

    private var _displayedBannerType: BannerInfoType = BannerInfoType.BLANK
    private var hideWhileInPip: Boolean = false

    var displayedBannerType: BannerInfoType
        get() = _displayedBannerType
        internal set(value) {
            _displayedBannerType = value
        }

    fun init(callingState: CallingState) {
        bannerInfoTypeStateFlow = MutableStateFlow(
            createBannerInfoType(
                callingState.isRecording,
                callingState.isTranscribing
            )
        )
        _isOverlayDisplayedFlow = MutableStateFlow(isOverlayDisplayed(callingState.callingStatus))
    }

    fun updateIsOverlayDisplayed(callingStatus: CallingStatus) {
        _isOverlayDisplayedFlow.value = isOverlayDisplayed(callingStatus)
    }

    fun update(
        callingState: CallingState,
        visibilityState: VisibilityState,
    ) {

        if (hideWhileInPip && visibilityState.status == VisibilityStatus.VISIBLE) {
            _shouldShowBannerStateFlow.value = true
            hideWhileInPip = false
            return
        }

        val currentBannerInfoType = bannerInfoTypeStateFlow.value
        val newBannerInfoType =
            createBannerInfoType(callingState.isRecording, callingState.isTranscribing)

        if (newBannerInfoType != currentBannerInfoType) {
            bannerInfoTypeStateFlow.value = newBannerInfoType
            _shouldShowBannerStateFlow.value = true
            hideWhileInPip = false
        }

        if (_shouldShowBannerStateFlow.value && visibilityState.status != VisibilityStatus.VISIBLE) {
            _shouldShowBannerStateFlow.value = false
            hideWhileInPip = true
            return
        }
    }

    fun setDisplayedBannerType(bannerInfoType: BannerInfoType) {
        _displayedBannerType = bannerInfoType
    }

    fun dismissBanner() {
        hideWhileInPip = false
        _shouldShowBannerStateFlow.value = false
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

    private fun isOverlayDisplayed(callingStatus: CallingStatus) =
        callingStatus == CallingStatus.IN_LOBBY || callingStatus == CallingStatus.LOCAL_HOLD
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
