// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling

import com.azure.android.communication.ui.helper.MainCoroutineRule
import com.azure.android.communication.ui.presentation.fragment.calling.banner.BannerViewModel
import com.azure.android.communication.ui.presentation.fragment.calling.controlbar.ControlBarViewModel
import com.azure.android.communication.ui.presentation.fragment.calling.hangup.ConfirmLeaveOverlayViewModel
import com.azure.android.communication.ui.presentation.fragment.calling.header.InfoHeaderViewModel
import com.azure.android.communication.ui.presentation.fragment.calling.lobby.LobbyOverlayViewModel
import com.azure.android.communication.ui.presentation.fragment.calling.localuser.LocalParticipantViewModel
import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.ParticipantGridViewModel
import com.azure.android.communication.ui.presentation.fragment.calling.participantlist.ParticipantListViewModel
import com.azure.android.communication.ui.presentation.fragment.common.audiodevicelist.AudioDeviceListViewModel
import com.azure.android.communication.ui.presentation.fragment.factories.CallingViewModelFactory
import com.azure.android.communication.ui.redux.AppStore
import com.azure.android.communication.ui.redux.state.CameraState
import com.azure.android.communication.ui.redux.state.AudioState
import com.azure.android.communication.ui.redux.state.BluetoothState
import com.azure.android.communication.ui.redux.state.CameraDeviceSelectionStatus
import com.azure.android.communication.ui.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.redux.state.AppReduxState
import com.azure.android.communication.ui.redux.state.ReduxState
import com.azure.android.communication.ui.redux.state.LocalUserState
import com.azure.android.communication.ui.redux.state.CallingStatus
import com.azure.android.communication.ui.redux.state.CallingState
import com.azure.android.communication.ui.redux.state.CameraTransmissionStatus
import com.azure.android.communication.ui.redux.state.LifecycleState
import com.azure.android.communication.ui.redux.state.LifecycleStatus
import com.azure.android.communication.ui.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.redux.state.AudioDeviceSelectionStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
internal class CallingViewModelUnitTest {
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    @ExperimentalCoroutinesApi
    fun callingViewModel_onStateChange_when_stateIsBackground_then_doesNotCallChildViewModels() {

        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val appState = AppReduxState("")
            appState.localParticipantState = getLocalUserState()
            val stateFlow = MutableStateFlow<ReduxState>(appState)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doAnswer { stateFlow }
                on { getCurrentState() } doAnswer { appState }
            }
            val mockParticipantGridViewModel = mock<ParticipantGridViewModel> {
            }

            val mockControlBarViewModel = mock<ControlBarViewModel> {
                on { update(any(), any(), any()) } doAnswer { }
            }

            val mockConfirmLeaveOverlayViewModel = mock<ConfirmLeaveOverlayViewModel> {}

            val mockLocalParticipantViewModel = mock<LocalParticipantViewModel> {
                on { update(any(), any(), any(), any(), any(), any()) } doAnswer { }
            }

            val mockFloatingHeaderViewModel = mock<InfoHeaderViewModel> {}

            val mockAudioDeviceListViewModel = mock<AudioDeviceListViewModel>()

            val mockParticipantListViewModel = mock<ParticipantListViewModel>()

            val mockBannerViewModel = mock<BannerViewModel>()

            val mockLobbyOverlayViewModel = mock<LobbyOverlayViewModel>()

            val mockCallingViewModelProvider = mock<CallingViewModelFactory> {
                on { provideParticipantGridViewModel() } doAnswer { mockParticipantGridViewModel }
                on { provideControlBarViewModel() } doAnswer { mockControlBarViewModel }
                on { provideConfirmLeaveOverlayViewModel() } doAnswer { mockConfirmLeaveOverlayViewModel }
                on { provideLocalParticipantViewModel() } doAnswer { mockLocalParticipantViewModel }
                on { provideFloatingHeaderViewModel() } doAnswer { mockFloatingHeaderViewModel }
                on { provideAudioDeviceListViewModel() } doAnswer { mockAudioDeviceListViewModel }
                on { provideParticipantListViewModel() } doAnswer { mockParticipantListViewModel }
                on { provideBannerViewModel() } doAnswer { mockBannerViewModel }
                on { provideLobbyOverlayViewModel() } doAnswer { mockLobbyOverlayViewModel }
            }

            val callingViewModel = CallingViewModel(
                mockAppStore,
                mockCallingViewModelProvider
            )

            val newBackgroundState = AppReduxState("")
            newBackgroundState.lifecycleState = LifecycleState(LifecycleStatus.BACKGROUND)
            newBackgroundState.localParticipantState = getLocalUserState()

            // act
            val flowJob = launch {
                callingViewModel.init(this)
            }
            stateFlow.emit(newBackgroundState)

            // assert
            verify(mockParticipantGridViewModel, times(0)).update(any(), any())
            verify(mockControlBarViewModel, times(1)).update(any(), any(), any())
            verify(mockLocalParticipantViewModel, times(1)).update(
                any(), any(), any(), any(), any(), any()
            )

            flowJob.cancel()
        }
    }

    @Test
    @ExperimentalCoroutinesApi
    fun callingViewModel_onStateChange_when_stateIsNotBackground_then_callChildViewModels() {

        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val appState = AppReduxState("")
            appState.localParticipantState = getLocalUserState()
            val stateFlow = MutableStateFlow<ReduxState>(appState)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doAnswer { stateFlow }
                on { getCurrentState() } doAnswer { appState }
            }
            val mockParticipantGridViewModel = mock<ParticipantGridViewModel> {
            }

            val mockControlBarViewModel = mock<ControlBarViewModel> {
                on { update(any(), any(), any()) } doAnswer { }
            }

            val mockConfirmLeaveOverlayViewModel = mock<ConfirmLeaveOverlayViewModel> {}

            val mockLocalParticipantViewModel = mock<LocalParticipantViewModel> {
                on { update(any(), any(), any(), any(), any(), any()) } doAnswer { }
            }

            val mockFloatingHeaderViewModel = mock<InfoHeaderViewModel> {}

            val mockAudioDeviceListViewModel = mock<AudioDeviceListViewModel>()

            val mockParticipantListViewModel = mock<ParticipantListViewModel>()

            val mockBannerViewModel = mock<BannerViewModel>()

            val mockLobbyOverlayViewModel = mock<LobbyOverlayViewModel>()

            val mockCallingViewModelProvider = mock<CallingViewModelFactory> {
                on { provideParticipantGridViewModel() } doAnswer { mockParticipantGridViewModel }
                on { provideControlBarViewModel() } doAnswer { mockControlBarViewModel }
                on { provideConfirmLeaveOverlayViewModel() } doAnswer { mockConfirmLeaveOverlayViewModel }
                on { provideLocalParticipantViewModel() } doAnswer { mockLocalParticipantViewModel }
                on { provideFloatingHeaderViewModel() } doAnswer { mockFloatingHeaderViewModel }
                on { provideAudioDeviceListViewModel() } doAnswer { mockAudioDeviceListViewModel }
                on { provideParticipantListViewModel() } doAnswer { mockParticipantListViewModel }
                on { provideBannerViewModel() } doAnswer { mockBannerViewModel }
                on { provideLobbyOverlayViewModel() } doAnswer { mockLobbyOverlayViewModel }
            }

            val callingViewModel = CallingViewModel(
                mockAppStore,
                mockCallingViewModelProvider
            )

            val newForegroundState = AppReduxState("")
            newForegroundState.lifecycleState = LifecycleState(LifecycleStatus.FOREGROUND)
            newForegroundState.localParticipantState = getLocalUserState()

            // act
            val flowJob = launch {
                callingViewModel.init(this)
            }
            stateFlow.emit(newForegroundState)

            // assert
            verify(mockParticipantGridViewModel, times(0)).update(any(), any())
            verify(mockControlBarViewModel, times(2)).update(any(), any(), any())
            verify(mockLocalParticipantViewModel, times(2)).update(
                any(), any(), any(), any(), any(), any()
            )

            flowJob.cancel()
        }
    }

    @Test
    @ExperimentalCoroutinesApi
    fun callingViewModel_onStateChange_when_callStateConnected_then_callingChildViewModelsAreUpdated() {

        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val appState = AppReduxState("")
            appState.localParticipantState = getLocalUserState()
            val stateFlow = MutableStateFlow<ReduxState>(appState)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doAnswer { stateFlow }
                on { getCurrentState() } doAnswer { appState }
            }
            val mockParticipantGridViewModel = mock<ParticipantGridViewModel> {
                on { update(any(), any()) } doAnswer { }
            }

            val mockControlBarViewModel = mock<ControlBarViewModel> {
                on { update(any(), any(), any()) } doAnswer { }
            }

            val mockConfirmLeaveOverlayViewModel = mock<ConfirmLeaveOverlayViewModel> {}

            val mockLocalParticipantViewModel = mock<LocalParticipantViewModel> {
                on { update(any(), any(), any(), any(), any(), any()) } doAnswer { }
            }

            val mockFloatingHeaderViewModel = mock<InfoHeaderViewModel> {}

            val mockAudioDeviceListViewModel = mock<AudioDeviceListViewModel>()

            val mockParticipantListViewModel = mock<ParticipantListViewModel>()

            val mockBannerViewModel = mock<BannerViewModel>()
            val mockLobbyOverlayViewModel = mock<LobbyOverlayViewModel>()

            val mockCallingViewModelProvider = mock<CallingViewModelFactory> {
                on { provideParticipantGridViewModel() } doAnswer { mockParticipantGridViewModel }
                on { provideControlBarViewModel() } doAnswer { mockControlBarViewModel }
                on { provideConfirmLeaveOverlayViewModel() } doAnswer { mockConfirmLeaveOverlayViewModel }
                on { provideLocalParticipantViewModel() } doAnswer { mockLocalParticipantViewModel }
                on { provideFloatingHeaderViewModel() } doAnswer { mockFloatingHeaderViewModel }
                on { provideAudioDeviceListViewModel() } doAnswer { mockAudioDeviceListViewModel }
                on { provideParticipantListViewModel() } doAnswer { mockParticipantListViewModel }
                on { provideBannerViewModel() } doAnswer { mockBannerViewModel }
                on { provideLobbyOverlayViewModel() } doAnswer { mockLobbyOverlayViewModel }
            }

            val callingViewModel = CallingViewModel(
                mockAppStore,
                mockCallingViewModelProvider
            )

            val storeState = AppReduxState("")
            storeState.lifecycleState = LifecycleState(LifecycleStatus.FOREGROUND)
            storeState.localParticipantState = getLocalUserState()
            storeState.callState = CallingState(
                CallingStatus.CONNECTED,
                isRecording = false,
                isTranscribing = false
            )

            // act
            val flowJob = launch {
                callingViewModel.init(this)
            }
            stateFlow.emit(storeState)

            // assert
            verify(mockParticipantGridViewModel, times(1)).update(any(), any())
            verify(mockFloatingHeaderViewModel, times(1)).update(any())
            verify(mockParticipantListViewModel, times(1)).update(any(), any())
            verify(mockBannerViewModel, times(1)).update(any())
            verify(mockControlBarViewModel, times(2)).update(any(), any(), any())
            verify(mockLocalParticipantViewModel, times(2)).update(
                any(), any(), any(), any(), any(), any()
            )

            flowJob.cancel()
        }
    }

    @Test
    @ExperimentalCoroutinesApi
    fun callingViewModel_onStateChange_when_callStateNotConnected_then_callingChildViewModelsAreNotUpdated() {

        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val appState = AppReduxState("")
            appState.localParticipantState = getLocalUserState()
            val stateFlow = MutableStateFlow<ReduxState>(appState)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doAnswer { stateFlow }
                on { getCurrentState() } doAnswer { appState }
            }
            val mockParticipantGridViewModel = mock<ParticipantGridViewModel> {
            }

            val mockControlBarViewModel = mock<ControlBarViewModel> {
                on { update(any(), any(), any()) } doAnswer { }
            }

            val mockConfirmLeaveOverlayViewModel = mock<ConfirmLeaveOverlayViewModel> {}

            val mockLocalParticipantViewModel = mock<LocalParticipantViewModel> {
                on { update(any(), any(), any(), any(), any(), any()) } doAnswer { }
            }

            val mockFloatingHeaderViewModel = mock<InfoHeaderViewModel> {}

            val mockAudioDeviceListViewModel = mock<AudioDeviceListViewModel>()

            val mockParticipantListViewModel = mock<ParticipantListViewModel>()

            val mockBannerViewModel = mock<BannerViewModel>()
            val mockLobbyOverlayViewModel = mock<LobbyOverlayViewModel>()

            val mockCallingViewModelProvider = mock<CallingViewModelFactory> {
                on { provideParticipantGridViewModel() } doAnswer { mockParticipantGridViewModel }
                on { provideControlBarViewModel() } doAnswer { mockControlBarViewModel }
                on { provideConfirmLeaveOverlayViewModel() } doAnswer { mockConfirmLeaveOverlayViewModel }
                on { provideLocalParticipantViewModel() } doAnswer { mockLocalParticipantViewModel }
                on { provideFloatingHeaderViewModel() } doAnswer { mockFloatingHeaderViewModel }
                on { provideAudioDeviceListViewModel() } doAnswer { mockAudioDeviceListViewModel }
                on { provideParticipantListViewModel() } doAnswer { mockParticipantListViewModel }
                on { provideBannerViewModel() } doAnswer { mockBannerViewModel }
                on { provideLobbyOverlayViewModel() } doAnswer { mockLobbyOverlayViewModel }
            }

            val callingViewModel = CallingViewModel(
                mockAppStore,
                mockCallingViewModelProvider
            )

            val newForegroundState = AppReduxState("")
            newForegroundState.lifecycleState = LifecycleState(LifecycleStatus.FOREGROUND)
            newForegroundState.localParticipantState = getLocalUserState()

            // act
            val flowJob = launch {
                callingViewModel.init(this)
            }
            stateFlow.emit(newForegroundState)

            // assert
            verify(mockParticipantGridViewModel, times(0)).update(any(), any())
            verify(mockFloatingHeaderViewModel, times(0)).update(any())
            verify(mockParticipantListViewModel, times(0)).update(any(), any())
            verify(mockBannerViewModel, times(0)).update(any())
            verify(mockControlBarViewModel, times(2)).update(any(), any(), any())
            verify(mockLocalParticipantViewModel, times(2)).update(
                any(), any(), any(), any(), any(), any()
            )

            flowJob.cancel()
        }
    }

    private fun getLocalUserState() = LocalUserState(
        CameraState(
            CameraOperationalStatus.OFF,
            CameraDeviceSelectionStatus.FRONT,
            CameraTransmissionStatus.LOCAL
        ),
        AudioState(
            AudioOperationalStatus.OFF,
            AudioDeviceSelectionStatus.SPEAKER_SELECTED,
            BluetoothState(available = false, deviceName = "bluetooth")
        ),
        "test",
        "test"
    )
}
