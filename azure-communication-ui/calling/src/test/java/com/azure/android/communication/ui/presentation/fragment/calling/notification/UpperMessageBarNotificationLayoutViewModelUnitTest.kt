// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.notification

import com.azure.android.communication.ui.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.models.MediaCallDiagnostic
import com.azure.android.communication.ui.calling.models.MediaCallDiagnosticModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.notification.UpperMessageBarNotificationLayoutViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.notification.UpperMessageBarNotificationModel
import com.azure.android.communication.ui.calling.redux.state.CallDiagnosticsState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class UpperMessageBarNotificationLayoutViewModelUnitTest : ACSBaseTestCoroutine() {

    @ExperimentalCoroutinesApi
    @Test
    fun upperMessageBarNotificationLayoutViewModel_update_when_NoSpeakerDevicesAvailableTrue_then_addUpperMessageBarNotification_and_dismiss() {
        // add the notification state
        var mediaCallDiagnosticModel = MediaCallDiagnosticModel(
            MediaCallDiagnostic.NO_SPEAKER_DEVICES_AVAILABLE, true
        )
        var testCallDiagnosticsState = CallDiagnosticsState(null, null, mediaCallDiagnosticModel)

        // dismiss the notification state
        var dismissMediaCallDiagnosticModel = MediaCallDiagnosticModel(
            MediaCallDiagnostic.NO_SPEAKER_DEVICES_AVAILABLE, false
        )
        var dismissTestCallDiagnosticsState = CallDiagnosticsState(null, null, dismissMediaCallDiagnosticModel)

        val upperMessageBarNotificationLayoutViewModel = UpperMessageBarNotificationLayoutViewModel()

        runScopedTest {
            // arrange add notification
            val resultAddUpperMessageBarNotificationLayoutModelMessageFlow = mutableListOf<UpperMessageBarNotificationModel>()
            var flowJob = launch {
                upperMessageBarNotificationLayoutViewModel.getNewUpperMessageBarNotificationFlow().toList(resultAddUpperMessageBarNotificationLayoutModelMessageFlow)
            }

            // act add notification
            upperMessageBarNotificationLayoutViewModel.update(testCallDiagnosticsState)

            // assert add notification
            Assert.assertEquals(false, resultAddUpperMessageBarNotificationLayoutModelMessageFlow.last().isEmpty())

            flowJob.cancel()

            // arrange dismiss notification
            val resultDismissUpperMessageBarNotificationLayoutModelMessageFlow = mutableListOf<UpperMessageBarNotificationModel>()
            flowJob = launch {
                upperMessageBarNotificationLayoutViewModel.getDismissUpperMessageBarNotificationFlow().toList(resultDismissUpperMessageBarNotificationLayoutModelMessageFlow)
            }

            // act dismiss notification
            upperMessageBarNotificationLayoutViewModel.update(dismissTestCallDiagnosticsState)

            // assert dismiss notification
            Assert.assertEquals(false, resultDismissUpperMessageBarNotificationLayoutModelMessageFlow.last().isEmpty())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun upperMessageBarNotificationLayoutViewModel_update_when_NoMicrophoneDevicesAvailableTrue_then_addUpperMessageBarNotification_and_dismiss() {
        // add the notification state
        var mediaCallDiagnosticModel = MediaCallDiagnosticModel(
            MediaCallDiagnostic.NO_MICROPHONE_DEVICES_AVAILABLE, true
        )
        var testCallDiagnosticsState = CallDiagnosticsState(null, null, mediaCallDiagnosticModel)

        // dismiss the notification state
        var dismissMediaCallDiagnosticModel = MediaCallDiagnosticModel(
            MediaCallDiagnostic.NO_MICROPHONE_DEVICES_AVAILABLE, false
        )
        var dismissTestCallDiagnosticsState = CallDiagnosticsState(null, null, dismissMediaCallDiagnosticModel)

        val upperMessageBarNotificationLayoutViewModel = UpperMessageBarNotificationLayoutViewModel()

        runScopedTest {
            // arrange add notification
            val resultAddUpperMessageBarNotificationLayoutModelMessageFlow = mutableListOf<UpperMessageBarNotificationModel>()
            var flowJob = launch {
                upperMessageBarNotificationLayoutViewModel.getNewUpperMessageBarNotificationFlow().toList(resultAddUpperMessageBarNotificationLayoutModelMessageFlow)
            }

            // act add notification
            upperMessageBarNotificationLayoutViewModel.update(testCallDiagnosticsState)

            // assert add notification
            Assert.assertEquals(false, resultAddUpperMessageBarNotificationLayoutModelMessageFlow.last().isEmpty())

            flowJob.cancel()

            // arrange dismiss notification
            val resultDismissUpperMessageBarNotificationLayoutModelMessageFlow = mutableListOf<UpperMessageBarNotificationModel>()
            flowJob = launch {
                upperMessageBarNotificationLayoutViewModel.getDismissUpperMessageBarNotificationFlow().toList(resultDismissUpperMessageBarNotificationLayoutModelMessageFlow)
            }

            // act dismiss notification
            upperMessageBarNotificationLayoutViewModel.update(dismissTestCallDiagnosticsState)

            // assert dismiss notification
            Assert.assertEquals(false, resultDismissUpperMessageBarNotificationLayoutModelMessageFlow.last().isEmpty())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun upperMessageBarNotificationLayoutViewModel_update_when_MicrophoneNotFunctioningTrue_then_addUpperMessageBarNotification_and_dismiss() {
        // add the notification state
        var mediaCallDiagnosticModel = MediaCallDiagnosticModel(
            MediaCallDiagnostic.MICROPHONE_NOT_FUNCTIONING, true
        )
        var testCallDiagnosticsState = CallDiagnosticsState(null, null, mediaCallDiagnosticModel)

        // dismiss the notification state
        var dismissMediaCallDiagnosticModel = MediaCallDiagnosticModel(
            MediaCallDiagnostic.MICROPHONE_NOT_FUNCTIONING, false
        )
        var dismissTestCallDiagnosticsState = CallDiagnosticsState(null, null, dismissMediaCallDiagnosticModel)

        val upperMessageBarNotificationLayoutViewModel = UpperMessageBarNotificationLayoutViewModel()

        runScopedTest {
            // arrange add notification
            val resultAddUpperMessageBarNotificationLayoutModelMessageFlow = mutableListOf<UpperMessageBarNotificationModel>()
            var flowJob = launch {
                upperMessageBarNotificationLayoutViewModel.getNewUpperMessageBarNotificationFlow().toList(resultAddUpperMessageBarNotificationLayoutModelMessageFlow)
            }

            // act add notification
            upperMessageBarNotificationLayoutViewModel.update(testCallDiagnosticsState)

            // assert add notification
            Assert.assertEquals(false, resultAddUpperMessageBarNotificationLayoutModelMessageFlow.last().isEmpty())

            flowJob.cancel()

            // arrange dismiss notification
            val resultDismissUpperMessageBarNotificationLayoutModelMessageFlow = mutableListOf<UpperMessageBarNotificationModel>()
            flowJob = launch {
                upperMessageBarNotificationLayoutViewModel.getDismissUpperMessageBarNotificationFlow().toList(resultDismissUpperMessageBarNotificationLayoutModelMessageFlow)
            }

            // act dismiss notification
            upperMessageBarNotificationLayoutViewModel.update(dismissTestCallDiagnosticsState)

            // assert dismiss notification
            Assert.assertEquals(false, resultDismissUpperMessageBarNotificationLayoutModelMessageFlow.last().isEmpty())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun upperMessageBarNotificationLayoutViewModel_update_when_SpeakerNotFunctioningTrue_then_addUpperMessageBarNotification_and_dismiss() {
        // add the notification state
        var mediaCallDiagnosticModel = MediaCallDiagnosticModel(
            MediaCallDiagnostic.SPEAKER_NOT_FUNCTIONING, true
        )
        var testCallDiagnosticsState = CallDiagnosticsState(null, null, mediaCallDiagnosticModel)

        // dismiss the notification state
        var dismissMediaCallDiagnosticModel = MediaCallDiagnosticModel(
            MediaCallDiagnostic.SPEAKER_NOT_FUNCTIONING, false
        )
        var dismissTestCallDiagnosticsState = CallDiagnosticsState(null, null, dismissMediaCallDiagnosticModel)

        val upperMessageBarNotificationLayoutViewModel = UpperMessageBarNotificationLayoutViewModel()

        runScopedTest {
            // arrange add notification
            val resultAddUpperMessageBarNotificationLayoutModelMessageFlow = mutableListOf<UpperMessageBarNotificationModel>()
            var flowJob = launch {
                upperMessageBarNotificationLayoutViewModel.getNewUpperMessageBarNotificationFlow().toList(resultAddUpperMessageBarNotificationLayoutModelMessageFlow)
            }

            // act add notification
            upperMessageBarNotificationLayoutViewModel.update(testCallDiagnosticsState)

            // assert add notification
            Assert.assertEquals(false, resultAddUpperMessageBarNotificationLayoutModelMessageFlow.last().isEmpty())

            flowJob.cancel()

            // arrange dismiss notification
            val resultDismissUpperMessageBarNotificationLayoutModelMessageFlow = mutableListOf<UpperMessageBarNotificationModel>()
            flowJob = launch {
                upperMessageBarNotificationLayoutViewModel.getDismissUpperMessageBarNotificationFlow().toList(resultDismissUpperMessageBarNotificationLayoutModelMessageFlow)
            }

            // act dismiss notification
            upperMessageBarNotificationLayoutViewModel.update(dismissTestCallDiagnosticsState)

            // assert dismiss notification
            Assert.assertEquals(false, resultDismissUpperMessageBarNotificationLayoutModelMessageFlow.last().isEmpty())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun upperMessageBarNotificationLayoutViewModel_update_when_SpeakerMutedTrue_then_addUpperMessageBarNotification_and_dismiss() {
        // add the notification state
        var mediaCallDiagnosticModel = MediaCallDiagnosticModel(
            MediaCallDiagnostic.SPEAKER_MUTED, true
        )
        var testCallDiagnosticsState = CallDiagnosticsState(null, null, mediaCallDiagnosticModel)

        // dismiss the notification state
        var dismissMediaCallDiagnosticModel = MediaCallDiagnosticModel(
            MediaCallDiagnostic.SPEAKER_MUTED, false
        )
        var dismissTestCallDiagnosticsState = CallDiagnosticsState(null, null, dismissMediaCallDiagnosticModel)

        val upperMessageBarNotificationLayoutViewModel = UpperMessageBarNotificationLayoutViewModel()

        runScopedTest {
            // arrange add notification
            val resultAddUpperMessageBarNotificationLayoutModelMessageFlow = mutableListOf<UpperMessageBarNotificationModel>()
            var flowJob = launch {
                upperMessageBarNotificationLayoutViewModel.getNewUpperMessageBarNotificationFlow().toList(resultAddUpperMessageBarNotificationLayoutModelMessageFlow)
            }

            // act add notification
            upperMessageBarNotificationLayoutViewModel.update(testCallDiagnosticsState)

            // assert add notification
            Assert.assertEquals(false, resultAddUpperMessageBarNotificationLayoutModelMessageFlow.last().isEmpty())

            flowJob.cancel()

            // arrange dismiss notification
            val resultDismissUpperMessageBarNotificationLayoutModelMessageFlow = mutableListOf<UpperMessageBarNotificationModel>()
            flowJob = launch {
                upperMessageBarNotificationLayoutViewModel.getDismissUpperMessageBarNotificationFlow().toList(resultDismissUpperMessageBarNotificationLayoutModelMessageFlow)
            }

            // act dismiss notification
            upperMessageBarNotificationLayoutViewModel.update(dismissTestCallDiagnosticsState)

            // assert dismiss notification
            Assert.assertEquals(false, resultDismissUpperMessageBarNotificationLayoutModelMessageFlow.last().isEmpty())

            flowJob.cancel()
        }
    }
}
