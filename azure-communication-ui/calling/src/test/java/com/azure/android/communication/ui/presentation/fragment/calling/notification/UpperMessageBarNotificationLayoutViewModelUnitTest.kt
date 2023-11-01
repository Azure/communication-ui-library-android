// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.notification

import com.azure.android.communication.ui.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.models.MediaCallDiagnostic
import com.azure.android.communication.ui.calling.models.MediaCallDiagnosticModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.notification.UpperMessageBarNotificationLayoutViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.notification.UpperMessageBarNotificationViewModel
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.state.CallDiagnosticsState
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock

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

        val mockAppStore = mock<AppStore<ReduxState>> {}
        val upperMessageBarNotificationLayoutViewModel = UpperMessageBarNotificationLayoutViewModel(mockAppStore::dispatch)

        runScopedTest {
            // arrange add notification
            val resultAddUpperMessageBarNotificationLayoutModelMessageFlow = mutableListOf<UpperMessageBarNotificationViewModel>()
            var flowJob = launch {
                upperMessageBarNotificationLayoutViewModel.getNewUpperMessageBarNotificationFlow().toList(resultAddUpperMessageBarNotificationLayoutModelMessageFlow)
            }

            // act add notification
            upperMessageBarNotificationLayoutViewModel.update(testCallDiagnosticsState)

            // assert add notification
            Assert.assertEquals(false, resultAddUpperMessageBarNotificationLayoutModelMessageFlow.last().upperMessageBarNotificationModel.isEmpty())

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

        val mockAppStore = mock<AppStore<ReduxState>> {}
        val upperMessageBarNotificationLayoutViewModel = UpperMessageBarNotificationLayoutViewModel(mockAppStore::dispatch)

        runScopedTest {
            // arrange add notification
            val resultAddUpperMessageBarNotificationLayoutModelMessageFlow = mutableListOf<UpperMessageBarNotificationViewModel>()
            var flowJob = launch {
                upperMessageBarNotificationLayoutViewModel.getNewUpperMessageBarNotificationFlow().toList(resultAddUpperMessageBarNotificationLayoutModelMessageFlow)
            }

            // act add notification
            upperMessageBarNotificationLayoutViewModel.update(testCallDiagnosticsState)

            // assert add notification
            Assert.assertEquals(false, resultAddUpperMessageBarNotificationLayoutModelMessageFlow.last().upperMessageBarNotificationModel.isEmpty())

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

        val mockAppStore = mock<AppStore<ReduxState>> {}
        val upperMessageBarNotificationLayoutViewModel = UpperMessageBarNotificationLayoutViewModel(mockAppStore::dispatch)

        runScopedTest {
            // arrange add notification
            val resultAddUpperMessageBarNotificationLayoutModelMessageFlow = mutableListOf<UpperMessageBarNotificationViewModel>()
            var flowJob = launch {
                upperMessageBarNotificationLayoutViewModel.getNewUpperMessageBarNotificationFlow().toList(resultAddUpperMessageBarNotificationLayoutModelMessageFlow)
            }

            // act add notification
            upperMessageBarNotificationLayoutViewModel.update(testCallDiagnosticsState)

            // assert add notification
            Assert.assertEquals(false, resultAddUpperMessageBarNotificationLayoutModelMessageFlow.last().upperMessageBarNotificationModel.isEmpty())

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

        val mockAppStore = mock<AppStore<ReduxState>> {}
        val upperMessageBarNotificationLayoutViewModel = UpperMessageBarNotificationLayoutViewModel(mockAppStore::dispatch)

        runScopedTest {
            // arrange add notification
            val resultAddUpperMessageBarNotificationLayoutModelMessageFlow = mutableListOf<UpperMessageBarNotificationViewModel>()
            var flowJob = launch {
                upperMessageBarNotificationLayoutViewModel.getNewUpperMessageBarNotificationFlow().toList(resultAddUpperMessageBarNotificationLayoutModelMessageFlow)
            }

            // act add notification
            upperMessageBarNotificationLayoutViewModel.update(testCallDiagnosticsState)

            // assert add notification
            Assert.assertEquals(false, resultAddUpperMessageBarNotificationLayoutModelMessageFlow.last().upperMessageBarNotificationModel.isEmpty())

            flowJob.cancel()
        }
    }
}
