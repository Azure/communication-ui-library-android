// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.banner

import com.azure.android.communication.ui.calling.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.redux.state.CallingState
import com.azure.android.communication.ui.calling.redux.state.CallStatus
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
            CallStatus.CONNECTED,
            isRecording = false,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType = BannerInfoType.BLANK

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState)
            }

            // act
            bannerViewModel.update(testCallingState)

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
            CallStatus.CONNECTED,
            isRecording = false,
            isTranscribing = true,
        )
        val expectedBannerInfoType: BannerInfoType = BannerInfoType.TRANSCRIPTION_STARTED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState)
            }

            // act
            bannerViewModel.update(testCallingState)

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
            CallStatus.CONNECTED,
            isRecording = true,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType = BannerInfoType.RECORDING_STARTED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState)
            }

            // act
            bannerViewModel.update(testCallingState)

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
            CallStatus.CONNECTED,
            isRecording = true,
            isTranscribing = true,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.RECORDING_AND_TRANSCRIPTION_STARTED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallingStatus) {
                bannerViewModel.update(callingState)
            }

            // act
            bannerViewModel.update(testCallingState)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateOFFTranscriptionStateONAndIsRecordingFalseIsTranscribingFalse_then_emitCorrectBannerInfoType() {
        val setupCallStatus: List<CallingState> = listOf(
            CallingState(
                CallStatus.CONNECTED,
                isRecording = false,
                isTranscribing = true,
            )
        )
        val testCallingState = CallingState(
            CallStatus.CONNECTED,
            isRecording = false,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType = BannerInfoType.TRANSCRIPTION_STOPPED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallStatus) {
                bannerViewModel.update(callingState)
            }

            // act
            bannerViewModel.update(testCallingState)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateOFFTranscriptionStateONAndIsRecordingFalseIsTranscribingTrue_then_emitCorrectBannerInfoType() {
        val setupCallStatus: List<CallingState> = listOf(
            CallingState(
                CallStatus.CONNECTED,
                isRecording = false,
                isTranscribing = true,
            )
        )
        val testCallingState = CallingState(
            CallStatus.CONNECTED,
            isRecording = false,
            isTranscribing = true,
        )
        val expectedBannerInfoType: BannerInfoType = BannerInfoType.TRANSCRIPTION_STARTED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallStatus) {
                bannerViewModel.update(callingState)
            }

            // act
            bannerViewModel.update(testCallingState)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateOFFTranscriptionStateONAndIsRecordingTrueIsTranscribingFalse_then_emitCorrectBannerInfoType() {
        val setupCallStatus: List<CallingState> = listOf(
            CallingState(
                CallStatus.CONNECTED,
                isRecording = false,
                isTranscribing = true,
            )
        )
        val testCallingState = CallingState(
            CallStatus.CONNECTED,
            isRecording = true,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.TRANSCRIPTION_STOPPED_STILL_RECORDING

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallStatus) {
                bannerViewModel.update(callingState)
            }

            // act
            bannerViewModel.update(testCallingState)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateOFFTranscriptionStateONAndIsRecordingTrueIsTranscribingTrue_then_emitCorrectBannerInfoType() {
        val setupCallStatus: List<CallingState> = listOf(
            CallingState(
                CallStatus.CONNECTED,
                isRecording = false,
                isTranscribing = true,
            )
        )
        val testCallingState = CallingState(
            CallStatus.CONNECTED,
            isRecording = true,
            isTranscribing = true,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.RECORDING_AND_TRANSCRIPTION_STARTED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallStatus) {
                bannerViewModel.update(callingState)
            }

            // act
            bannerViewModel.update(testCallingState)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateOFFTranscriptionStateSTOPPEDAndIsRecordingFalseIsTranscribingFalse_then_emitCorrectBannerInfoType() {
        val setupCallStatus: List<CallingState> = listOf(
            CallingState(
                CallStatus.CONNECTED,
                isRecording = false,
                isTranscribing = true,
            ),
            CallingState(
                CallStatus.CONNECTED,
                isRecording = false,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallStatus.CONNECTED,
            isRecording = false,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType = BannerInfoType.TRANSCRIPTION_STOPPED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallStatus) {
                bannerViewModel.update(callingState)
            }

            // act
            bannerViewModel.update(testCallingState)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateOFFTranscriptionStateSTOPPEDAndIsRecordingFalseIsTranscribingTrue_then_emitCorrectBannerInfoType() {
        val setupCallStatus: List<CallingState> = listOf(
            CallingState(
                CallStatus.CONNECTED,
                isRecording = false,
                isTranscribing = true,
            ),
            CallingState(
                CallStatus.CONNECTED,
                isRecording = false,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallStatus.CONNECTED,
            isRecording = false,
            isTranscribing = true,
        )
        val expectedBannerInfoType: BannerInfoType = BannerInfoType.TRANSCRIPTION_STARTED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallStatus) {
                bannerViewModel.update(callingState)
            }

            // act
            bannerViewModel.update(testCallingState)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateOFFTranscriptionStateSTOPPEDAndIsRecordingTrueIsTranscribingFalse_then_emitCorrectBannerInfoType() {
        val setupCallStatus: List<CallingState> = listOf(
            CallingState(
                CallStatus.CONNECTED,
                isRecording = false,
                isTranscribing = true,
            ),
            CallingState(
                CallStatus.CONNECTED,
                isRecording = false,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallStatus.CONNECTED,
            isRecording = true,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.TRANSCRIPTION_STOPPED_STILL_RECORDING

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallStatus) {
                bannerViewModel.update(callingState)
            }

            // act
            bannerViewModel.update(testCallingState)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateOFFTranscriptionStateSTOPPEDAndIsRecordingTrueIsTranscribingTrue_then_emitCorrectBannerInfoType() {
        val setupCallStatus: List<CallingState> = listOf(
            CallingState(
                CallStatus.CONNECTED,
                isRecording = false,
                isTranscribing = true,
            ),
            CallingState(
                CallStatus.CONNECTED,
                isRecording = false,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallStatus.CONNECTED,
            isRecording = true,
            isTranscribing = true,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.RECORDING_AND_TRANSCRIPTION_STARTED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallStatus) {
                bannerViewModel.update(callingState)
            }

            // act
            bannerViewModel.update(testCallingState)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateONTranscriptionStateOFFAndIsRecordingFalseIsTranscribingFalse_then_emitCorrectBannerInfoType() {
        val setupCallStatus: List<CallingState> = listOf(
            CallingState(
                CallStatus.CONNECTED,
                isRecording = true,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallStatus.CONNECTED,
            isRecording = false,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType = BannerInfoType.RECORDING_STOPPED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallStatus) {
                bannerViewModel.update(callingState)
            }

            // act
            bannerViewModel.update(testCallingState)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateONTranscriptionStateOFFAndIsRecordingFalseIsTranscribingTrue_then_emitCorrectBannerInfoType() {
        val setupCallStatus: List<CallingState> = listOf(
            CallingState(
                CallStatus.CONNECTED,
                isRecording = true,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallStatus.CONNECTED,
            isRecording = false,
            isTranscribing = true,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.RECORDING_STOPPED_STILL_TRANSCRIBING

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallStatus) {
                bannerViewModel.update(callingState)
            }

            // act
            bannerViewModel.update(testCallingState)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateONTranscriptionStateOFFAndIsRecordingTrueIsTranscribingFalse_then_emitCorrectBannerInfoType() {
        val setupCallStatus: List<CallingState> = listOf(
            CallingState(
                CallStatus.CONNECTED,
                isRecording = true,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallStatus.CONNECTED,
            isRecording = true,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType = BannerInfoType.RECORDING_STARTED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallStatus) {
                bannerViewModel.update(callingState)
            }

            // act
            bannerViewModel.update(testCallingState)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateONTranscriptionStateOFFAndIsRecordingTrueIsTranscribingTrue_then_emitCorrectBannerInfoType() {
        val setupCallStatus: List<CallingState> = listOf(
            CallingState(
                CallStatus.CONNECTED,
                isRecording = true,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallStatus.CONNECTED,
            isRecording = true,
            isTranscribing = true,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.RECORDING_AND_TRANSCRIPTION_STARTED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallStatus) {
                bannerViewModel.update(callingState)
            }

            // act
            bannerViewModel.update(testCallingState)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateONTranscriptionStateONAndIsRecordingFalseIsTranscribingFalse_then_emitCorrectBannerInfoType() {
        val setupCallStatus: List<CallingState> = listOf(
            CallingState(
                CallStatus.CONNECTED,
                isRecording = true,
                isTranscribing = true,
            )
        )
        val testCallingState = CallingState(
            CallStatus.CONNECTED,
            isRecording = false,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.RECORDING_AND_TRANSCRIPTION_STOPPED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallStatus) {
                bannerViewModel.update(callingState)
            }

            // act
            bannerViewModel.update(testCallingState)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateONTranscriptionStateONAndIsRecordingFalseIsTranscribingTrue_then_emitCorrectBannerInfoType() {
        val setupCallStatus: List<CallingState> = listOf(
            CallingState(
                CallStatus.CONNECTED,
                isRecording = true,
                isTranscribing = true,
            )
        )
        val testCallingState = CallingState(
            CallStatus.CONNECTED,
            isRecording = false,
            isTranscribing = true,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.RECORDING_STOPPED_STILL_TRANSCRIBING

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallStatus) {
                bannerViewModel.update(callingState)
            }

            // act
            bannerViewModel.update(testCallingState)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateONTranscriptionStateONAndIsRecordingTrueIsTranscribingFalse_then_emitCorrectBannerInfoType() {
        val setupCallStatus: List<CallingState> = listOf(
            CallingState(
                CallStatus.CONNECTED,
                isRecording = true,
                isTranscribing = true,
            )
        )
        val testCallingState = CallingState(
            CallStatus.CONNECTED,
            isRecording = true,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.TRANSCRIPTION_STOPPED_STILL_RECORDING

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallStatus) {
                bannerViewModel.update(callingState)
            }

            // act
            bannerViewModel.update(testCallingState)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateONTranscriptionStateONAndIsRecordingTrueIsTranscribingTrue_then_emitCorrectBannerInfoType() {
        val setupCallStatus: List<CallingState> = listOf(
            CallingState(
                CallStatus.CONNECTED,
                isRecording = true,
                isTranscribing = true,
            )
        )
        val testCallingState = CallingState(
            CallStatus.CONNECTED,
            isRecording = true,
            isTranscribing = true,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.RECORDING_AND_TRANSCRIPTION_STARTED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallStatus) {
                bannerViewModel.update(callingState)
            }

            // act
            bannerViewModel.update(testCallingState)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateONTranscriptionStateSTOPPEDAndIsRecordingFalseIsTranscribingFalse_then_emitCorrectBannerInfoType() {
        val setupCallStatus: List<CallingState> = listOf(
            CallingState(
                CallStatus.CONNECTED,
                isRecording = true,
                isTranscribing = true,
            ),
            CallingState(
                CallStatus.CONNECTED,
                isRecording = true,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallStatus.CONNECTED,
            isRecording = false,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.RECORDING_AND_TRANSCRIPTION_STOPPED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallStatus) {
                bannerViewModel.update(callingState)
            }

            // act
            bannerViewModel.update(testCallingState)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateONTranscriptionStateSTOPPEDAndIsRecordingFalseIsTranscribingTrue_then_emitCorrectBannerInfoType() {
        val setupCallStatus: List<CallingState> = listOf(
            CallingState(
                CallStatus.CONNECTED,
                isRecording = true,
                isTranscribing = true,
            ),
            CallingState(
                CallStatus.CONNECTED,
                isRecording = true,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallStatus.CONNECTED,
            isRecording = false,
            isTranscribing = true,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.RECORDING_STOPPED_STILL_TRANSCRIBING

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallStatus) {
                bannerViewModel.update(callingState)
            }

            // act
            bannerViewModel.update(testCallingState)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateONTranscriptionStateSTOPPEDAndIsRecordingTrueIsTranscribingFalse_then_emitCorrectBannerInfoType() {
        val setupCallStatus: List<CallingState> = listOf(
            CallingState(
                CallStatus.CONNECTED,
                isRecording = true,
                isTranscribing = true,
            ),
            CallingState(
                CallStatus.CONNECTED,
                isRecording = true,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallStatus.CONNECTED,
            isRecording = true,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.TRANSCRIPTION_STOPPED_STILL_RECORDING

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallStatus) {
                bannerViewModel.update(callingState)
            }

            // act
            bannerViewModel.update(testCallingState)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateONTranscriptionStateSTOPPEDAndIsRecordingTrueIsTranscribingTrue_then_emitCorrectBannerInfoType() {
        val setupCallStatus: List<CallingState> = listOf(
            CallingState(
                CallStatus.CONNECTED,
                isRecording = true,
                isTranscribing = true,
            ),
            CallingState(
                CallStatus.CONNECTED,
                isRecording = true,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallStatus.CONNECTED,
            isRecording = true,
            isTranscribing = true,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.RECORDING_AND_TRANSCRIPTION_STARTED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallStatus) {
                bannerViewModel.update(callingState)
            }

            // act
            bannerViewModel.update(testCallingState)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateSTOPPEDTranscriptionStateOFFAndIsRecordingFalseIsTranscribingFalse_then_emitCorrectBannerInfoType() {
        val setupCallStatus: List<CallingState> = listOf(
            CallingState(
                CallStatus.CONNECTED,
                isRecording = true,
                isTranscribing = false,
            ),
            CallingState(
                CallStatus.CONNECTED,
                isRecording = false,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallStatus.CONNECTED,
            isRecording = false,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType = BannerInfoType.RECORDING_STOPPED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallStatus) {
                bannerViewModel.update(callingState)
            }

            // act
            bannerViewModel.update(testCallingState)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateSTOPPEDTranscriptionStateOFFAndIsRecordingFalseIsTranscribingTrue_then_emitCorrectBannerInfoType() {
        val setupCallStatus: List<CallingState> = listOf(
            CallingState(
                CallStatus.CONNECTED,
                isRecording = true,
                isTranscribing = false,
            ),
            CallingState(
                CallStatus.CONNECTED,
                isRecording = false,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallStatus.CONNECTED,
            isRecording = false,
            isTranscribing = true,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.RECORDING_STOPPED_STILL_TRANSCRIBING

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallStatus) {
                bannerViewModel.update(callingState)
            }

            // act
            bannerViewModel.update(testCallingState)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateSTOPPEDTranscriptionStateOFFAndIsRecordingTrueIsTranscribingFalse_then_emitCorrectBannerInfoType() {
        val setupCallStatus: List<CallingState> = listOf(
            CallingState(
                CallStatus.CONNECTED,
                isRecording = true,
                isTranscribing = false,
            ),
            CallingState(
                CallStatus.CONNECTED,
                isRecording = false,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallStatus.CONNECTED,
            isRecording = true,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType = BannerInfoType.RECORDING_STARTED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallStatus) {
                bannerViewModel.update(callingState)
            }

            // act
            bannerViewModel.update(testCallingState)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateSTOPPEDTranscriptionStateOFFAndIsRecordingTrueIsTranscribingTrue_then_emitCorrectBannerInfoType() {
        val setupCallStatus: List<CallingState> = listOf(
            CallingState(
                CallStatus.CONNECTED,
                isRecording = true,
                isTranscribing = false,
            ),
            CallingState(
                CallStatus.CONNECTED,
                isRecording = false,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallStatus.CONNECTED,
            isRecording = true,
            isTranscribing = true,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.RECORDING_AND_TRANSCRIPTION_STARTED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallStatus) {
                bannerViewModel.update(callingState)
            }

            // act
            bannerViewModel.update(testCallingState)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateSTOPPEDTranscriptionStateONAndIsRecordingFalseIsTranscribingFalse_then_emitCorrectBannerInfoType() {
        val setupCallStatus: List<CallingState> = listOf(
            CallingState(
                CallStatus.CONNECTED,
                isRecording = true,
                isTranscribing = false,
            ),
            CallingState(
                CallStatus.CONNECTED,
                isRecording = false,
                isTranscribing = true,
            )
        )
        val testCallingState = CallingState(
            CallStatus.CONNECTED,
            isRecording = false,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.RECORDING_AND_TRANSCRIPTION_STOPPED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallStatus) {
                bannerViewModel.update(callingState)
            }

            // act
            bannerViewModel.update(testCallingState)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateSTOPPEDTranscriptionStateONAndIsRecordingFalseIsTranscribingTrue_then_emitCorrectBannerInfoType() {
        val setupCallStatus: List<CallingState> = listOf(
            CallingState(
                CallStatus.CONNECTED,
                isRecording = true,
                isTranscribing = false,
            ),
            CallingState(
                CallStatus.CONNECTED,
                isRecording = false,
                isTranscribing = true,
            )
        )
        val testCallingState = CallingState(
            CallStatus.CONNECTED,
            isRecording = false,
            isTranscribing = true,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.RECORDING_STOPPED_STILL_TRANSCRIBING

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallStatus) {
                bannerViewModel.update(callingState)
            }

            // act
            bannerViewModel.update(testCallingState)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateSTOPPEDTranscriptionStateONAndIsRecordingTrueIsTranscribingFalse_then_emitCorrectBannerInfoType() {
        val setupCallStatus: List<CallingState> = listOf(
            CallingState(
                CallStatus.CONNECTED,
                isRecording = true,
                isTranscribing = false,
            ),
            CallingState(
                CallStatus.CONNECTED,
                isRecording = false,
                isTranscribing = true,
            )
        )
        val testCallingState = CallingState(
            CallStatus.CONNECTED,
            isRecording = true,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.TRANSCRIPTION_STOPPED_STILL_RECORDING

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallStatus) {
                bannerViewModel.update(callingState)
            }

            // act
            bannerViewModel.update(testCallingState)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateSTOPPEDTranscriptionStateONAndIsRecordingTrueIsTranscribingTrue_then_emitCorrectBannerInfoType() {
        val setupCallStatus: List<CallingState> = listOf(
            CallingState(
                CallStatus.CONNECTED,
                isRecording = true,
                isTranscribing = false,
            ),
            CallingState(
                CallStatus.CONNECTED,
                isRecording = false,
                isTranscribing = true,
            )
        )
        val testCallingState = CallingState(
            CallStatus.CONNECTED,
            isRecording = true,
            isTranscribing = true,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.RECORDING_AND_TRANSCRIPTION_STARTED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallStatus) {
                bannerViewModel.update(callingState)
            }

            // act
            bannerViewModel.update(testCallingState)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateSTOPPEDTranscriptionStateSTOPPEDAndIsRecordingFalseIsTranscribingFalse_then_emitCorrectBannerInfoType() {
        val setupCallStatus: List<CallingState> = listOf(
            CallingState(
                CallStatus.CONNECTED,
                isRecording = true,
                isTranscribing = true,
            ),
            CallingState(
                CallStatus.CONNECTED,
                isRecording = false,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallStatus.CONNECTED,
            isRecording = false,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.BLANK

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallStatus) {
                bannerViewModel.update(callingState)
            }

            // act
            bannerViewModel.update(testCallingState)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateSTOPPEDTranscriptionStateSTOPPEDAndIsRecordingFalseIsTranscribingTrue_then_emitCorrectBannerInfoType() {
        val setupCallStatus: List<CallingState> = listOf(
            CallingState(
                CallStatus.CONNECTED,
                isRecording = true,
                isTranscribing = true,
            ),
            CallingState(
                CallStatus.CONNECTED,
                isRecording = false,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallStatus.CONNECTED,
            isRecording = false,
            isTranscribing = true,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.TRANSCRIPTION_STARTED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallStatus) {
                bannerViewModel.update(callingState)
            }

            // act
            bannerViewModel.update(testCallingState)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateSTOPPEDTranscriptionStateSTOPPEDAndIsRecordingTrueIsTranscribingFalse_then_emitCorrectBannerInfoType() {
        val setupCallStatus: List<CallingState> = listOf(
            CallingState(
                CallStatus.CONNECTED,
                isRecording = true,
                isTranscribing = true,
            ),
            CallingState(
                CallStatus.CONNECTED,
                isRecording = false,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallStatus.CONNECTED,
            isRecording = true,
            isTranscribing = false,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.RECORDING_STARTED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallStatus) {
                bannerViewModel.update(callingState)
            }

            // act
            bannerViewModel.update(testCallingState)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun
    bannerBarViewModel_update_when_recordingStateSTOPPEDTranscriptionStateSTOPPEDAndIsRecordingTrueIsTranscribingTrue_then_emitCorrectBannerInfoType() {
        val setupCallStatus: List<CallingState> = listOf(
            CallingState(
                CallStatus.CONNECTED,
                isRecording = true,
                isTranscribing = true,
            ),
            CallingState(
                CallStatus.CONNECTED,
                isRecording = false,
                isTranscribing = false,
            )
        )
        val testCallingState = CallingState(
            CallStatus.CONNECTED,
            isRecording = true,
            isTranscribing = true,
        )
        val expectedBannerInfoType: BannerInfoType =
            BannerInfoType.RECORDING_AND_TRANSCRIPTION_STARTED

        runScopedTest {
            // arrange
            val initialCallingState = CallingState(CallStatus.CONNECTED)
            val bannerViewModel = BannerViewModel()
            bannerViewModel.init(initialCallingState)

            val resultBannerInfoTypeStateFlow = mutableListOf<BannerInfoType>()
            val flowJob = launch {
                bannerViewModel.bannerInfoTypeStateFlow.toList(resultBannerInfoTypeStateFlow)
            }

            for (callingState in setupCallStatus) {
                bannerViewModel.update(callingState)
            }

            // act
            bannerViewModel.update(testCallingState)

            // assert
            Assert.assertEquals(expectedBannerInfoType, resultBannerInfoTypeStateFlow.last())

            flowJob.cancel()
        }
    }
}
