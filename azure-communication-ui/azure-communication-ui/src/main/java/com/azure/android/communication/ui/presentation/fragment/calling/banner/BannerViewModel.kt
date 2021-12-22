// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.banner

import com.azure.android.communication.ui.redux.state.CallingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class BannerViewModel {

    private lateinit var bannerInfoTypeStateFlow: MutableStateFlow<BannerInfoType>
    private var shouldShowBannerStateFlow = MutableStateFlow(false)

    private var recordingState: ComplianceState = ComplianceState.OFF
    private var transcriptionState: ComplianceState = ComplianceState.OFF

    fun getBannerInfoTypeStateFlow(): StateFlow<BannerInfoType> {
        return bannerInfoTypeStateFlow
    }

    fun getShouldShowBannerStateFlow(): StateFlow<Boolean> {
        return shouldShowBannerStateFlow
    }

    fun init(callingState: CallingState) {
        bannerInfoTypeStateFlow = MutableStateFlow(
            createBannerInfoType(callingState.isRecording, callingState.isTranscribing)
        )
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

    fun update(callingState: CallingState) {
        val currentBannerInfoType = bannerInfoTypeStateFlow.value
        val newBannerInfoType =
            createBannerInfoType(callingState.isRecording, callingState.isTranscribing)

        if (newBannerInfoType != currentBannerInfoType) {
            bannerInfoTypeStateFlow.value = newBannerInfoType
            shouldShowBannerStateFlow.value = true
        }
    }

    fun dismissBanner() {
        shouldShowBannerStateFlow.value = false
        resetStoppedStates()
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
