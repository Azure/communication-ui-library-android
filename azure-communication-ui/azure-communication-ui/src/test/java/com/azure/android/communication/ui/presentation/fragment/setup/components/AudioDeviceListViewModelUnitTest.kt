// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.setup.components

import com.azure.android.communication.ui.helper.MainCoroutineRule
import com.azure.android.communication.ui.presentation.fragment.common.audiodevicelist.AudioDeviceListViewModel
import com.azure.android.communication.ui.redux.AppStore
import com.azure.android.communication.ui.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.redux.state.ReduxState
import com.azure.android.communication.ui.redux.state.AudioState
import com.azure.android.communication.ui.redux.state.BluetoothState
import com.azure.android.communication.ui.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.redux.state.AudioOperationalStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
internal class AudioDeviceListViewModelUnitTest {
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun audioDeviceListViewModel_switchAudioDevice_then_dispatchAudioDeviceChangeRequested() {
        // Arrange
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { dispatch(any()) } doAnswer { }
        }
        val audioDeviceListViewModel = AudioDeviceListViewModel(mockAppStore::dispatch)
        val requestedAudioDevice = AudioDeviceSelectionStatus.SPEAKER_REQUESTED

        // Act
        audioDeviceListViewModel.switchAudioDevice(requestedAudioDevice)

        // Assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LocalParticipantAction.AudioDeviceChangeRequested
            }
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun audioDeviceListViewModel_displayAudioDeviceSelectionMenu_then_notifyDisplayAudioDeviceSelectionMenuStateFlow() {
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val mockAppStore = mock<AppStore<ReduxState>>()
            val audioDeviceListViewModel = AudioDeviceListViewModel(mockAppStore::dispatch)

            audioDeviceListViewModel.init(
                AudioState(
                    AudioOperationalStatus.ON,
                    AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                    BluetoothState(available = false, deviceName = "bluetooth")
                )
            )

            val emitResultFromDisplayAudioDeviceSelectionMenuStateFlow = mutableListOf<Boolean>()

            val emitResultFromDisplayAudioDeviceSelectionMenuStateFlowJob = launch {
                audioDeviceListViewModel.displayAudioDeviceSelectionMenuStateFlow
                    .toList(emitResultFromDisplayAudioDeviceSelectionMenuStateFlow)
            }

            // act
            audioDeviceListViewModel.displayAudioDeviceSelectionMenu()
            audioDeviceListViewModel.closeAudioDeviceSelectionMenu()

            // assert
            Assert.assertEquals(
                3,
                emitResultFromDisplayAudioDeviceSelectionMenuStateFlow.size
            )

            Assert.assertEquals(
                false,
                emitResultFromDisplayAudioDeviceSelectionMenuStateFlow[0]
            )

            Assert.assertEquals(
                true,
                emitResultFromDisplayAudioDeviceSelectionMenuStateFlow[1]
            )

            Assert.assertEquals(
                false,
                emitResultFromDisplayAudioDeviceSelectionMenuStateFlow[2]
            )

            emitResultFromDisplayAudioDeviceSelectionMenuStateFlowJob.cancel()
        }
    }
}
