// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.notification

import com.azure.android.communication.ui.calling.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.models.CallDiagnosticQuality
import com.azure.android.communication.ui.calling.models.MediaCallDiagnostic
import com.azure.android.communication.ui.calling.models.MediaCallDiagnosticModel
import com.azure.android.communication.ui.calling.models.NetworkCallDiagnostic
import com.azure.android.communication.ui.calling.models.NetworkCallDiagnosticModel
import com.azure.android.communication.ui.calling.models.NetworkQualityCallDiagnosticModel
import com.azure.android.communication.ui.calling.models.ToastNotificationModel
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
internal class ToastNotificationViewModelUnitTest : ACSBaseTestCoroutine() {
    @ExperimentalCoroutinesApi
    @Test
    fun toastNotificationViewModel_update_when_NetworkReceiveQualityBAD_then_emitToast() {
        val networkQualityCallDiagnosticModel =
            NetworkQualityCallDiagnosticModel(
                NetworkCallDiagnostic.NETWORK_RECEIVE_QUALITY,
                CallDiagnosticQuality.BAD,
            )
        val testCallDiagnosticsState = CallDiagnosticsState(networkQualityCallDiagnosticModel, null, null)

        runScopedTest {
            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val toastNotificationViewModel = ToastNotificationViewModel(mockAppStore::dispatch)

            val resultToastNotificationModelMessageFlow = mutableListOf<ToastNotificationModel>()
            val flowJob =
                launch {
                    toastNotificationViewModel.getToastNotificationModelFlow().toList(resultToastNotificationModelMessageFlow)
                }

            // act
            toastNotificationViewModel.update(testCallDiagnosticsState)

            // assert
            Assert.assertEquals(false, resultToastNotificationModelMessageFlow.last().isEmpty())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun toastNotificationViewModel_update_when_NetworkReceiveQualityPOOR_then_emitToast() {
        val networkQualityCallDiagnosticModel =
            NetworkQualityCallDiagnosticModel(
                NetworkCallDiagnostic.NETWORK_RECEIVE_QUALITY,
                CallDiagnosticQuality.POOR,
            )
        val testCallDiagnosticsState = CallDiagnosticsState(networkQualityCallDiagnosticModel, null, null)

        runScopedTest {
            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val toastNotificationViewModel = ToastNotificationViewModel(mockAppStore::dispatch)

            val resultToastNotificationModelMessageFlow = mutableListOf<ToastNotificationModel>()
            val flowJob =
                launch {
                    toastNotificationViewModel.getToastNotificationModelFlow().toList(resultToastNotificationModelMessageFlow)
                }

            // act
            toastNotificationViewModel.update(testCallDiagnosticsState)

            // assert
            Assert.assertEquals(false, resultToastNotificationModelMessageFlow.last().isEmpty())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun toastNotificationViewModel_update_when_NetworkReconnectionQualityBAD_then_emitToast() {
        val networkQualityCallDiagnosticModel =
            NetworkQualityCallDiagnosticModel(
                NetworkCallDiagnostic.NETWORK_RECONNECTION_QUALITY,
                CallDiagnosticQuality.BAD,
            )
        val testCallDiagnosticsState = CallDiagnosticsState(networkQualityCallDiagnosticModel, null, null)

        runScopedTest {
            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val toastNotificationViewModel = ToastNotificationViewModel(mockAppStore::dispatch)

            val resultToastNotificationModelMessageFlow = mutableListOf<ToastNotificationModel>()
            val flowJob =
                launch {
                    toastNotificationViewModel.getToastNotificationModelFlow().toList(resultToastNotificationModelMessageFlow)
                }

            // act
            toastNotificationViewModel.update(testCallDiagnosticsState)

            // assert
            Assert.assertEquals(false, resultToastNotificationModelMessageFlow.last().isEmpty())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun toastNotificationViewModel_update_when_NetworkReconnectionQualityPOOR_then_emitToast() {
        val networkQualityCallDiagnosticModel =
            NetworkQualityCallDiagnosticModel(
                NetworkCallDiagnostic.NETWORK_RECONNECTION_QUALITY,
                CallDiagnosticQuality.POOR,
            )
        val testCallDiagnosticsState = CallDiagnosticsState(networkQualityCallDiagnosticModel, null, null)

        runScopedTest {
            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val toastNotificationViewModel = ToastNotificationViewModel(mockAppStore::dispatch)

            val resultToastNotificationModelMessageFlow = mutableListOf<ToastNotificationModel>()
            val flowJob =
                launch {
                    toastNotificationViewModel.getToastNotificationModelFlow().toList(resultToastNotificationModelMessageFlow)
                }

            // act
            toastNotificationViewModel.update(testCallDiagnosticsState)

            // assert
            Assert.assertEquals(false, resultToastNotificationModelMessageFlow.last().isEmpty())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun toastNotificationViewModel_update_when_NetworkRelaysUnreachableTrue_then_emitToast() {
        val networkCallDiagnosticModel =
            NetworkCallDiagnosticModel(
                NetworkCallDiagnostic.NETWORK_RELAYS_UNREACHABLE,
                true,
            )
        val testCallDiagnosticsState = CallDiagnosticsState(null, networkCallDiagnosticModel, null)

        runScopedTest {
            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val toastNotificationViewModel = ToastNotificationViewModel(mockAppStore::dispatch)

            val resultToastNotificationModelMessageFlow = mutableListOf<ToastNotificationModel>()
            val flowJob =
                launch {
                    toastNotificationViewModel.getToastNotificationModelFlow().toList(resultToastNotificationModelMessageFlow)
                }

            // act
            toastNotificationViewModel.update(testCallDiagnosticsState)

            // assert
            Assert.assertEquals(false, resultToastNotificationModelMessageFlow.last().isEmpty())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun toastNotificationViewModel_update_when_MediaSpeakingWhileMicrophoneMutedTrue_then_emitToast() {
        val mediaCallDiagnosticModel =
            MediaCallDiagnosticModel(
                MediaCallDiagnostic.SPEAKING_WHILE_MICROPHONE_IS_MUTED,
                true,
            )
        val testCallDiagnosticsState = CallDiagnosticsState(null, null, mediaCallDiagnosticModel)

        runScopedTest {
            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val toastNotificationViewModel = ToastNotificationViewModel(mockAppStore::dispatch)

            val resultToastNotificationModelMessageFlow = mutableListOf<ToastNotificationModel>()
            val flowJob =
                launch {
                    toastNotificationViewModel.getToastNotificationModelFlow().toList(resultToastNotificationModelMessageFlow)
                }

            // act
            toastNotificationViewModel.update(testCallDiagnosticsState)

            // assert
            Assert.assertEquals(false, resultToastNotificationModelMessageFlow.last().isEmpty())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun toastNotificationViewModel_update_when_MediaCameraStartTimedOutTrue_then_emitToast() {
        val mediaCallDiagnosticModel =
            MediaCallDiagnosticModel(
                MediaCallDiagnostic.CAMERA_START_TIMED_OUT,
                true,
            )
        val testCallDiagnosticsState = CallDiagnosticsState(null, null, mediaCallDiagnosticModel)

        runScopedTest {
            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val toastNotificationViewModel = ToastNotificationViewModel(mockAppStore::dispatch)

            val resultToastNotificationModelMessageFlow = mutableListOf<ToastNotificationModel>()
            val flowJob =
                launch {
                    toastNotificationViewModel.getToastNotificationModelFlow().toList(resultToastNotificationModelMessageFlow)
                }

            // act
            toastNotificationViewModel.update(testCallDiagnosticsState)

            // assert
            Assert.assertEquals(false, resultToastNotificationModelMessageFlow.last().isEmpty())

            flowJob.cancel()
        }
    }
}
