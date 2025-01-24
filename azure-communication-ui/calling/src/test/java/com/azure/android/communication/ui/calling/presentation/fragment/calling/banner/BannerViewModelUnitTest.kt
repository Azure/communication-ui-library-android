// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.banner

import com.azure.android.communication.ui.calling.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.redux.state.CallingState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.VisibilityState
import com.azure.android.communication.ui.calling.redux.state.VisibilityStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class BannerViewModelUnitTest : ACSBaseTestCoroutine() {

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateOFFTranscriptionStateOFFAndIsRecordingFalseIsTranscribingFalse_then_emitCorrectBannerInfoType() {
        val setupCallingStatus: List<CallingState> = emptyList()
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = false,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType = BannerInfoType.BLANK

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, false)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)
            }

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateOFFTranscriptionStateOFFAndIsRecordingFalseIsTranscribingTrue_then_emitCorrectBannerInfoType() {
        val setupCallingStatus: List<CallingState> = emptyList()
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = false,
            isTranscribing = true,
        )
        val expectedBannerInfoType: BannerInfoType = BannerInfoType.TRANSCRIPTION_STARTED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)
            }

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateOFFTranscriptionStateOFFAndIsRecordingTrueIsTranscribingFalse_then_emitCorrectBannerInfoType() {
        val setupCallingStatus: List<CallingState> = emptyList()
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = true,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType = BannerInfoType.RECORDING_STARTED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)
            }

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateOFFTranscriptionStateOFFAndIsRecordingTrueIsTranscribingTrue_then_emitCorrectBannerInfoType() {
        val setupCallingStatus: List<CallingState> = emptyList()
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = true,
            isTranscribing = true,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.RECORDING_AND_TRANSCRIPTION_STARTED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)
            }

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateOFFTranscriptionStateONAndIsRecordingFalseIsTranscribingFalse_then_emitCorrectBannerInfoType() {
        val setupCallingStatus: List<CallingState> = listOf(
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = false,
                isTranscribing = true,
            )
        )
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = false,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType = BannerInfoType.TRANSCRIPTION_STOPPED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)
            }

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateOFFTranscriptionStateONAndIsRecordingFalseIsTranscribingTrue_then_emitCorrectBannerInfoType() {
        val setupCallingStatus: List<CallingState> = listOf(
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = false,
                isTranscribing = true,
            )
        )
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = false,
            isTranscribing = true,
        )
        val expectedBannerInfoType: BannerInfoType = BannerInfoType.TRANSCRIPTION_STARTED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)
            }

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateOFFTranscriptionStateONAndIsRecordingTrueIsTranscribingFalse_then_emitCorrectBannerInfoType() {
        val setupCallingStatus: List<CallingState> = listOf(
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = false,
                isTranscribing = true,
            )
        )
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = true,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.TRANSCRIPTION_STOPPED_STILL_RECORDING

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)
            }

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateOFFTranscriptionStateONAndIsRecordingTrueIsTranscribingTrue_then_emitCorrectBannerInfoType() {
        val setupCallingStatus: List<CallingState> = listOf(
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = false,
                isTranscribing = true,
            )
        )
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = true,
            isTranscribing = true,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.RECORDING_AND_TRANSCRIPTION_STARTED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)
            }

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateOFFTranscriptionStateSTOPPEDAndIsRecordingFalseIsTranscribingFalse_then_emitCorrectBannerInfoType() {
        val setupCallingStatus: List<CallingState> = listOf(
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = false,
                isTranscribing = true,
            ),
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = false,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = false,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType = BannerInfoType.TRANSCRIPTION_STOPPED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)
            }

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateOFFTranscriptionStateSTOPPEDAndIsRecordingFalseIsTranscribingTrue_then_emitCorrectBannerInfoType() {
        val setupCallingStatus: List<CallingState> = listOf(
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = false,
                isTranscribing = true,
            ),
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = false,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = false,
            isTranscribing = true,
        )
        val expectedBannerInfoType: BannerInfoType = BannerInfoType.TRANSCRIPTION_STARTED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)
            }

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateOFFTranscriptionStateSTOPPEDAndIsRecordingTrueIsTranscribingFalse_then_emitCorrectBannerInfoType() {
        val setupCallingStatus: List<CallingState> = listOf(
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = false,
                isTranscribing = true,
            ),
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = false,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = true,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.TRANSCRIPTION_STOPPED_STILL_RECORDING

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)
            }

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateOFFTranscriptionStateSTOPPEDAndIsRecordingTrueIsTranscribingTrue_then_emitCorrectBannerInfoType() {
        val setupCallingStatus: List<CallingState> = listOf(
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = false,
                isTranscribing = true,
            ),
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = false,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = true,
            isTranscribing = true,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.RECORDING_AND_TRANSCRIPTION_STARTED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)
            }

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateONTranscriptionStateOFFAndIsRecordingFalseIsTranscribingFalse_then_emitCorrectBannerInfoType() {
        val setupCallingStatus: List<CallingState> = listOf(
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = true,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = false,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType = BannerInfoType.RECORDING_STOPPED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)
            }

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateONTranscriptionStateOFFAndIsRecordingFalseIsTranscribingTrue_then_emitCorrectBannerInfoType() {
        val setupCallingStatus: List<CallingState> = listOf(
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = true,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = false,
            isTranscribing = true,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.RECORDING_STOPPED_STILL_TRANSCRIBING

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)
            }

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateONTranscriptionStateOFFAndIsRecordingTrueIsTranscribingFalse_then_emitCorrectBannerInfoType() {
        val setupCallingStatus: List<CallingState> = listOf(
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = true,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = true,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType = BannerInfoType.RECORDING_STARTED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)
            }

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateONTranscriptionStateOFFAndIsRecordingTrueIsTranscribingTrue_then_emitCorrectBannerInfoType() {
        val setupCallingStatus: List<CallingState> = listOf(
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = true,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = true,
            isTranscribing = true,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.RECORDING_AND_TRANSCRIPTION_STARTED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)
            }

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateONTranscriptionStateONAndIsRecordingFalseIsTranscribingFalse_then_emitCorrectBannerInfoType() {
        val setupCallingStatus: List<CallingState> = listOf(
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = true,
                isTranscribing = true,
            )
        )
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = false,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.RECORDING_AND_TRANSCRIPTION_STOPPED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)
            }

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateONTranscriptionStateONAndIsRecordingFalseIsTranscribingTrue_then_emitCorrectBannerInfoType() {
        val setupCallingStatus: List<CallingState> = listOf(
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = true,
                isTranscribing = true,
            )
        )
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = false,
            isTranscribing = true,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.RECORDING_STOPPED_STILL_TRANSCRIBING

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)
            }

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateONTranscriptionStateONAndIsRecordingTrueIsTranscribingFalse_then_emitCorrectBannerInfoType() {
        val setupCallingStatus: List<CallingState> = listOf(
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = true,
                isTranscribing = true,
            )
        )
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = true,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.TRANSCRIPTION_STOPPED_STILL_RECORDING

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)
            }

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateONTranscriptionStateONAndIsRecordingTrueIsTranscribingTrue_then_emitCorrectBannerInfoType() {
        val setupCallingStatus: List<CallingState> = listOf(
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = true,
                isTranscribing = true,
            )
        )
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = true,
            isTranscribing = true,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.RECORDING_AND_TRANSCRIPTION_STARTED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)
            }

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateONTranscriptionStateSTOPPEDAndIsRecordingFalseIsTranscribingFalse_then_emitCorrectBannerInfoType() {
        val setupCallingStatus: List<CallingState> = listOf(
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = true,
                isTranscribing = true,
            ),
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = true,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = false,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.RECORDING_AND_TRANSCRIPTION_STOPPED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)
            }

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateONTranscriptionStateSTOPPEDAndIsRecordingFalseIsTranscribingTrue_then_emitCorrectBannerInfoType() {
        val setupCallingStatus: List<CallingState> = listOf(
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = true,
                isTranscribing = true,
            ),
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = true,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = false,
            isTranscribing = true,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.RECORDING_STOPPED_STILL_TRANSCRIBING

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)
            }

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateONTranscriptionStateSTOPPEDAndIsRecordingTrueIsTranscribingFalse_then_emitCorrectBannerInfoType() {
        val setupCallingStatus: List<CallingState> = listOf(
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = true,
                isTranscribing = true,
            ),
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = true,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = true,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.TRANSCRIPTION_STOPPED_STILL_RECORDING

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)
            }

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateONTranscriptionStateSTOPPEDAndIsRecordingTrueIsTranscribingTrue_then_emitCorrectBannerInfoType() {
        val setupCallingStatus: List<CallingState> = listOf(
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = true,
                isTranscribing = true,
            ),
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = true,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = true,
            isTranscribing = true,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.RECORDING_AND_TRANSCRIPTION_STARTED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)
            }

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateSTOPPEDTranscriptionStateOFFAndIsRecordingFalseIsTranscribingFalse_then_emitCorrectBannerInfoType() {
        val setupCallingStatus: List<CallingState> = listOf(
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = true,
                isTranscribing = false,
            ),
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = false,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = false,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType = BannerInfoType.RECORDING_STOPPED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)
            }

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateSTOPPEDTranscriptionStateOFFAndIsRecordingFalseIsTranscribingTrue_then_emitCorrectBannerInfoType() {
        val setupCallingStatus: List<CallingState> = listOf(
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = true,
                isTranscribing = false,
            ),
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = false,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = false,
            isTranscribing = true,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.RECORDING_STOPPED_STILL_TRANSCRIBING

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)
            }

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateSTOPPEDTranscriptionStateOFFAndIsRecordingTrueIsTranscribingFalse_then_emitCorrectBannerInfoType() {
        val setupCallingStatus: List<CallingState> = listOf(
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = true,
                isTranscribing = false,
            ),
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = false,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = true,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType = BannerInfoType.RECORDING_STARTED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)
            }

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateSTOPPEDTranscriptionStateOFFAndIsRecordingTrueIsTranscribingTrue_then_emitCorrectBannerInfoType() {
        val setupCallingStatus: List<CallingState> = listOf(
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = true,
                isTranscribing = false,
            ),
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = false,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = true,
            isTranscribing = true,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.RECORDING_AND_TRANSCRIPTION_STARTED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)
            }

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateSTOPPEDTranscriptionStateONAndIsRecordingFalseIsTranscribingFalse_then_emitCorrectBannerInfoType() {
        val setupCallingStatus: List<CallingState> = listOf(
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = true,
                isTranscribing = false,
            ),
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = false,
                isTranscribing = true,
            )
        )
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = false,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.RECORDING_AND_TRANSCRIPTION_STOPPED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)
            }

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateSTOPPEDTranscriptionStateONAndIsRecordingFalseIsTranscribingTrue_then_emitCorrectBannerInfoType() {
        val setupCallingStatus: List<CallingState> = listOf(
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = true,
                isTranscribing = false,
            ),
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = false,
                isTranscribing = true,
            )
        )
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = false,
            isTranscribing = true,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.RECORDING_STOPPED_STILL_TRANSCRIBING

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)
            }

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateSTOPPEDTranscriptionStateONAndIsRecordingTrueIsTranscribingFalse_then_emitCorrectBannerInfoType() {
        val setupCallingStatus: List<CallingState> = listOf(
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = true,
                isTranscribing = false,
            ),
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = false,
                isTranscribing = true,
            )
        )
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = true,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.TRANSCRIPTION_STOPPED_STILL_RECORDING

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)
            }

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateSTOPPEDTranscriptionStateONAndIsRecordingTrueIsTranscribingTrue_then_emitCorrectBannerInfoType() {
        val setupCallingStatus: List<CallingState> = listOf(
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = true,
                isTranscribing = false,
            ),
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = false,
                isTranscribing = true,
            )
        )
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = true,
            isTranscribing = true,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.RECORDING_AND_TRANSCRIPTION_STARTED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)
            }

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateSTOPPEDTranscriptionStateSTOPPEDAndIsRecordingFalseIsTranscribingFalse_then_emitCorrectBannerInfoType() {
        val setupCallingStatus: List<CallingState> = listOf(
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = true,
                isTranscribing = true,
            ),
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = false,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = false,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.BLANK

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)
            }

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateSTOPPEDTranscriptionStateSTOPPEDAndIsRecordingFalseIsTranscribingTrue_then_emitCorrectBannerInfoType() {
        val setupCallingStatus: List<CallingState> = listOf(
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = true,
                isTranscribing = true,
            ),
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = false,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = false,
            isTranscribing = true,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.TRANSCRIPTION_STARTED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)
            }

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateSTOPPEDTranscriptionStateSTOPPEDAndIsRecordingTrueIsTranscribingFalse_then_emitCorrectBannerInfoType() {
        val setupCallingStatus: List<CallingState> = listOf(
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = true,
                isTranscribing = true,
            ),
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = false,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = true,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.RECORDING_STARTED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)
            }

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateSTOPPEDTranscriptionStateSTOPPEDAndIsRecordingTrueIsTranscribingTrue_then_emitCorrectBannerInfoType() {
        val setupCallingStatus: List<CallingState> = listOf(
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = true,
                isTranscribing = true,
            ),
            CallingState(
                CallingStatus.CONNECTED,
                isRecording = false,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = true,
            isTranscribing = true,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.RECORDING_AND_TRANSCRIPTION_STARTED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)
            }

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun bannerBarViewModel_update_when_recordingStateOFFTranscriptionStateOFFAndIsRecordingFalseIsTranscribingFalse_then_emitCorrectBannerInfoType_VISIBLE() {
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = true,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType = BannerInfoType.RECORDING_STARTED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, bannerViewModel.bannerInfoTypeStateFlow.value)
            Assert.assertEquals(true, bannerViewModel.shouldShowBannerStateFlow.value)
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun bannerBarViewModel_update_when_recordingStateOFFTranscriptionStateOFFAndIsRecordingFalseIsTranscribingFalse_then_emitCorrectBannerInfoType_NOT_VISIBLE() {
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = true,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType = BannerInfoType.RECORDING_STARTED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val isOverlayDisplayedOverGrid = false
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.PIP_MODE_ENTERED), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(expectedBannerInfoType, bannerViewModel.bannerInfoTypeStateFlow.value)
            Assert.assertEquals(false, bannerViewModel.shouldShowBannerStateFlow.value)
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun bannerBarViewModel_update_when_recordingStateOFFTranscriptionStateOFFAndIsRecordingFalseIsTranscribingFalse_then_emitCorrectBannerInfoType_CHANGING_VISIBLE() {
        val testCallingState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = true,
            isTranscribing = false,
        )
        val isOverlayDisplayedOverGrid = false

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallingStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState, isOverlayDisplayedOverGrid)

            // act
            bannerViewModel.update(
                testCallingState,
                VisibilityState(VisibilityStatus.PIP_MODE_ENTERED),
                isOverlayDisplayedOverGrid,
            )

            // assert
            Assert.assertEquals(
                BannerInfoType.RECORDING_STARTED,
                bannerViewModel.bannerInfoTypeStateFlow.value
            )
            Assert.assertEquals(false, bannerViewModel.shouldShowBannerStateFlow.value)

            // act
            bannerViewModel.update(testCallingState, VisibilityState(VisibilityStatus.VISIBLE), isOverlayDisplayedOverGrid)

            // assert
            Assert.assertEquals(
                BannerInfoType.RECORDING_STARTED,
                bannerViewModel.bannerInfoTypeStateFlow.value
            )
            Assert.assertEquals(true, bannerViewModel.shouldShowBannerStateFlow.value)

            // act
            bannerViewModel.update(
                CallingState(
                    CallingStatus.CONNECTED,
                    isRecording = false,
                    isTranscribing = false,
                ),
                VisibilityState(VisibilityStatus.VISIBLE),
                isOverlayDisplayedOverGrid,
            )

            // assert
            Assert.assertEquals(
                BannerInfoType.RECORDING_STOPPED,
                bannerViewModel.bannerInfoTypeStateFlow.value
            )
            Assert.assertEquals(true, bannerViewModel.shouldShowBannerStateFlow.value)

            // act
            bannerViewModel.update(
                CallingState(
                    CallingStatus.CONNECTED,
                    isRecording = false,
                    isTranscribing = false,
                ),
                VisibilityState(VisibilityStatus.PIP_MODE_ENTERED),
                isOverlayDisplayedOverGrid,
            )

            // assert
            Assert.assertEquals(
                BannerInfoType.RECORDING_STOPPED,
                bannerViewModel.bannerInfoTypeStateFlow.value
            )
            Assert.assertEquals(false, bannerViewModel.shouldShowBannerStateFlow.value)

            // act
            bannerViewModel.update(
                CallingState(
                    CallingStatus.CONNECTED,
                    isRecording = true,
                    isTranscribing = true,
                ),
                VisibilityState(VisibilityStatus.PIP_MODE_ENTERED),
                isOverlayDisplayedOverGrid,
            )

            // assert
            Assert.assertEquals(
                BannerInfoType.RECORDING_AND_TRANSCRIPTION_STARTED,
                bannerViewModel.bannerInfoTypeStateFlow.value
            )
            Assert.assertEquals(false, bannerViewModel.shouldShowBannerStateFlow.value)

            // act
            bannerViewModel.update(
                CallingState(
                    CallingStatus.CONNECTED,
                    isRecording = true,
                    isTranscribing = true,
                ),
                VisibilityState(VisibilityStatus.VISIBLE),
                isOverlayDisplayedOverGrid,
            )

            // assert
            Assert.assertEquals(
                BannerInfoType.RECORDING_AND_TRANSCRIPTION_STARTED,
                bannerViewModel.bannerInfoTypeStateFlow.value
            )
            Assert.assertEquals(true, bannerViewModel.shouldShowBannerStateFlow.value)
        }
    }
}
