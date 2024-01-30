// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling

import com.azure.android.communication.ui.calling.presentation.fragment.calling.CallingViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.banner.BannerViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.ControlBarViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.hangup.LeaveConfirmViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.header.InfoHeaderViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.localuser.LocalParticipantViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.participant.grid.ParticipantGridViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.participantlist.ParticipantListViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.common.audiodevicelist.AudioDeviceListViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.factories.CallingViewModelFactory
import com.azure.android.communication.ui.calling.redux.AppStore

import com.azure.android.communication.ui.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.models.ParticipantInfoModel
import com.azure.android.communication.ui.calling.models.ParticipantStatus
import com.azure.android.communication.ui.calling.models.StreamType
import com.azure.android.communication.ui.calling.models.VideoStreamModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.more.MoreCallOptionsListViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.hold.OnHoldOverlayViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.lobby.ConnectingLobbyOverlayViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.lobby.WaitingLobbyOverlayViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.notification.ToastNotificationViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.notification.UpperMessageBarNotificationLayoutViewModel
import com.azure.android.communication.ui.calling.presentation.manager.NetworkManager
import com.azure.android.communication.ui.calling.redux.state.AppReduxState
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.redux.state.LifecycleState
import com.azure.android.communication.ui.calling.redux.state.LifecycleStatus
import com.azure.android.communication.ui.calling.redux.state.CallingState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.LocalUserState
import com.azure.android.communication.ui.calling.redux.state.AudioState
import com.azure.android.communication.ui.calling.redux.state.OperationStatus
import com.azure.android.communication.ui.calling.redux.state.CameraState
import com.azure.android.communication.ui.calling.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.CameraDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.CameraTransmissionStatus
import com.azure.android.communication.ui.calling.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.BluetoothState
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.RemoteParticipantsState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
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
internal class CallingViewModelUnitTest : ACSBaseTestCoroutine() {

    @Test
    @ExperimentalCoroutinesApi
    fun callingViewModel_onStateChange_when_stateIsBackground_then_doesNotCallChildViewModels() {

        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false)
            appState.localParticipantState = getLocalUserState()
            val stateFlow = MutableStateFlow<ReduxState>(appState)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doAnswer { stateFlow }
                on { getCurrentState() } doAnswer { appState }
            }
            val mockParticipantGridViewModel = mock<ParticipantGridViewModel> {
            }

            val mockControlBarViewModel = mock<ControlBarViewModel> {
                on { update(any(), any(), any(), any(), any(),) } doAnswer { }
            }

            val mockConfirmLeaveOverlayViewModel = mock<LeaveConfirmViewModel> {}

            val mockLocalParticipantViewModel = mock<LocalParticipantViewModel> {
                on { update(any(), any(), any(), any(), any(), any(), any(), any()) } doAnswer { }
            }

            val mockFloatingHeaderViewModel = mock<InfoHeaderViewModel> {}

            val mockAudioDeviceListViewModel = mock<AudioDeviceListViewModel>()

            val mockParticipantListViewModel = mock<ParticipantListViewModel>()

            val mockBannerViewModel = mock<BannerViewModel>()

            val mockWaitingLobbyOverlayViewModel = mock<WaitingLobbyOverlayViewModel>()
            val mockConnectingLobbyOverlayViewModel = mock<ConnectingLobbyOverlayViewModel>()

            val mockOnHoldOverlayViewModel = mock<OnHoldOverlayViewModel>()
            val mockMoreCallOptionsListViewModel = mock<MoreCallOptionsListViewModel>()

            val mockToastNotificationViewModel = mock<ToastNotificationViewModel>()
            val mockUpperMessageBarNotificationLayoutViewModel = mock<UpperMessageBarNotificationLayoutViewModel>()

            val mockNetworkManager = mock<NetworkManager>()

            val mockCallingViewModelProvider = mock<CallingViewModelFactory> {
                on { participantGridViewModel } doAnswer { mockParticipantGridViewModel }
                on { controlBarViewModel } doAnswer { mockControlBarViewModel }
                on { confirmLeaveOverlayViewModel } doAnswer { mockConfirmLeaveOverlayViewModel }
                on { localParticipantViewModel } doAnswer { mockLocalParticipantViewModel }
                on { floatingHeaderViewModel } doAnswer { mockFloatingHeaderViewModel }
                on { audioDeviceListViewModel } doAnswer { mockAudioDeviceListViewModel }
                on { participantListViewModel } doAnswer { mockParticipantListViewModel }
                on { bannerViewModel } doAnswer { mockBannerViewModel }
                on { waitingLobbyOverlayViewModel } doAnswer { mockWaitingLobbyOverlayViewModel }
                on { connectingLobbyOverlayViewModel } doAnswer { mockConnectingLobbyOverlayViewModel }
                on { onHoldOverlayViewModel } doAnswer { mockOnHoldOverlayViewModel }
                on { toastNotificationViewModel } doAnswer { mockToastNotificationViewModel }
                on { upperMessageBarNotificationLayoutViewModel } doAnswer { mockUpperMessageBarNotificationLayoutViewModel }
            }

            val callingViewModel = CallingViewModel(
                mockAppStore,
                mockCallingViewModelProvider,
                mockNetworkManager,
                false,
            )

            val newBackgroundState = AppReduxState("", false, false)
            newBackgroundState.lifecycleState = LifecycleState(LifecycleStatus.BACKGROUND)
            newBackgroundState.localParticipantState = getLocalUserState()

            // act
            val flowJob = launch {
                callingViewModel.init(this)
            }
            stateFlow.emit(newBackgroundState)

            // assert
            verify(mockParticipantGridViewModel, times(0)).update(any(), any(), any(), any(), any())
            verify(mockControlBarViewModel, times(1)).update(
                any(),
                any(),
                any(),
                any(),
                any(),
            )
            verify(mockLocalParticipantViewModel, times(1)).update(
                any(), any(), any(), any(), any(), any(), any(), any()
            )

            flowJob.cancel()
        }
    }

    @Test
    @ExperimentalCoroutinesApi
    fun callingViewModel_onStateChange_when_stateIsNotBackground_then_callChildViewModels() {

        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false)
            appState.localParticipantState = getLocalUserState()
            val stateFlow = MutableStateFlow<ReduxState>(appState)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doAnswer { stateFlow }
                on { getCurrentState() } doAnswer { appState }
            }
            val mockParticipantGridViewModel = mock<ParticipantGridViewModel> {
            }

            val mockControlBarViewModel = mock<ControlBarViewModel> {
                on { update(any(), any(), any(), any(), any(),) } doAnswer { }
            }

            val mockConfirmLeaveOverlayViewModel = mock<LeaveConfirmViewModel> {}

            val mockLocalParticipantViewModel = mock<LocalParticipantViewModel> {
                on { update(any(), any(), any(), any(), any(), any(), any(), any()) } doAnswer { }
            }

            val mockFloatingHeaderViewModel = mock<InfoHeaderViewModel> {}

            val mockAudioDeviceListViewModel = mock<AudioDeviceListViewModel>()

            val mockParticipantListViewModel = mock<ParticipantListViewModel>()

            val mockBannerViewModel = mock<BannerViewModel>()

            val mockWaitingLobbyOverlayViewModel = mock<WaitingLobbyOverlayViewModel>()
            val mockConnectingLobbyOverlayViewModel = mock<ConnectingLobbyOverlayViewModel>()
            val mockOnHoldOverlayViewModel = mock<OnHoldOverlayViewModel>()
            val mockMoreCallOptionsListViewModel = mock<MoreCallOptionsListViewModel>()
            val mockNetworkManager = mock<NetworkManager>()
            val mockToastNotificationViewModel = mock<ToastNotificationViewModel>()
            val mockUpperMessageBarNotificationLayoutViewModel = mock<UpperMessageBarNotificationLayoutViewModel>()

            val mockCallingViewModelProvider = mock<CallingViewModelFactory> {
                on { participantGridViewModel } doAnswer { mockParticipantGridViewModel }
                on { controlBarViewModel } doAnswer { mockControlBarViewModel }
                on { confirmLeaveOverlayViewModel } doAnswer { mockConfirmLeaveOverlayViewModel }
                on { localParticipantViewModel } doAnswer { mockLocalParticipantViewModel }
                on { floatingHeaderViewModel } doAnswer { mockFloatingHeaderViewModel }
                on { audioDeviceListViewModel } doAnswer { mockAudioDeviceListViewModel }
                on { participantListViewModel } doAnswer { mockParticipantListViewModel }
                on { bannerViewModel } doAnswer { mockBannerViewModel }
                on { waitingLobbyOverlayViewModel } doAnswer { mockWaitingLobbyOverlayViewModel }
                on { connectingLobbyOverlayViewModel } doAnswer { mockConnectingLobbyOverlayViewModel }
                on { onHoldOverlayViewModel } doAnswer { mockOnHoldOverlayViewModel }
                on { moreCallOptionsListViewModel } doAnswer { mockMoreCallOptionsListViewModel }
                on { toastNotificationViewModel } doAnswer { mockToastNotificationViewModel }
                on { upperMessageBarNotificationLayoutViewModel } doAnswer { mockUpperMessageBarNotificationLayoutViewModel }
            }

            val callingViewModel = CallingViewModel(
                mockAppStore,
                mockCallingViewModelProvider,
                mockNetworkManager,
                false,
            )

            val newForegroundState = AppReduxState("", false, false,)
            newForegroundState.lifecycleState = LifecycleState(LifecycleStatus.FOREGROUND)
            newForegroundState.localParticipantState = getLocalUserState()

            // act
            val flowJob = launch {
                callingViewModel.init(this)
            }
            stateFlow.emit(newForegroundState)

            // assert
            verify(mockParticipantGridViewModel, times(0)).update(any(), any(), any(), any(), any())
            verify(mockControlBarViewModel, times(2)).update(
                any(),
                any(),
                any(),
                any(),
                any(),
            )
            verify(mockLocalParticipantViewModel, times(2)).update(
                any(), any(), any(), any(), any(), any(), any(), any()
            )

            flowJob.cancel()
        }
    }

    @Test
    @ExperimentalCoroutinesApi
    fun callingViewModel_onStateChange_when_callStateConnected_then_callingChildViewModelsAreUpdated() {

        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false)
            appState.localParticipantState = getLocalUserState()
            val stateFlow = MutableStateFlow<ReduxState>(appState)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doAnswer { stateFlow }
                on { getCurrentState() } doAnswer { appState }
            }
            val mockParticipantGridViewModel = mock<ParticipantGridViewModel> {
                on { update(any(), any(), any(), any(), any()) } doAnswer { }
            }

            val mockControlBarViewModel = mock<ControlBarViewModel> {
                on { update(any(), any(), any(), any(), any(),) } doAnswer { }
            }

            val mockConfirmLeaveOverlayViewModel = mock<LeaveConfirmViewModel> {}

            val mockLocalParticipantViewModel = mock<LocalParticipantViewModel> {
                on { update(any(), any(), any(), any(), any(), any(), any(), any()) } doAnswer { }
            }

            val mockFloatingHeaderViewModel = mock<InfoHeaderViewModel> {}

            val mockAudioDeviceListViewModel = mock<AudioDeviceListViewModel>()

            val mockParticipantListViewModel = mock<ParticipantListViewModel>()

            val mockBannerViewModel = mock<BannerViewModel>()
            val mockWaitingLobbyOverlayViewModel = mock<WaitingLobbyOverlayViewModel>()
            val mockConnectingLobbyOverlayViewModel = mock<ConnectingLobbyOverlayViewModel>()
            val mockOnHoldOverlayViewModel = mock<OnHoldOverlayViewModel>()
            val mockMoreCallOptionsListViewModel = mock<MoreCallOptionsListViewModel>()
            val mockNetworkManager = mock<NetworkManager>()
            val mockToastNotificationViewModel = mock<ToastNotificationViewModel>()
            val mockUpperMessageBarNotificationLayoutViewModel = mock<UpperMessageBarNotificationLayoutViewModel>()

            val mockCallingViewModelProvider = mock<CallingViewModelFactory> {
                on { participantGridViewModel } doAnswer { mockParticipantGridViewModel }
                on { controlBarViewModel } doAnswer { mockControlBarViewModel }
                on { confirmLeaveOverlayViewModel } doAnswer { mockConfirmLeaveOverlayViewModel }
                on { localParticipantViewModel } doAnswer { mockLocalParticipantViewModel }
                on { floatingHeaderViewModel } doAnswer { mockFloatingHeaderViewModel }
                on { audioDeviceListViewModel } doAnswer { mockAudioDeviceListViewModel }
                on { participantListViewModel } doAnswer { mockParticipantListViewModel }
                on { bannerViewModel } doAnswer { mockBannerViewModel }
                on { waitingLobbyOverlayViewModel } doAnswer { mockWaitingLobbyOverlayViewModel }
                on { connectingLobbyOverlayViewModel } doAnswer { mockConnectingLobbyOverlayViewModel }
                on { onHoldOverlayViewModel } doAnswer { mockOnHoldOverlayViewModel }
                on { moreCallOptionsListViewModel } doAnswer { mockMoreCallOptionsListViewModel }
                on { toastNotificationViewModel } doAnswer { mockToastNotificationViewModel }
                on { upperMessageBarNotificationLayoutViewModel } doAnswer { mockUpperMessageBarNotificationLayoutViewModel }
            }

            val callingViewModel = CallingViewModel(
                mockAppStore,
                mockCallingViewModelProvider,
                mockNetworkManager,
                false,
            )

            val storeState = AppReduxState("", false, false)
            storeState.lifecycleState = LifecycleState(LifecycleStatus.FOREGROUND)
            storeState.localParticipantState = getLocalUserState()
            storeState.callState = CallingState(
                CallingStatus.CONNECTED,
                OperationStatus.NONE,
                isRecording = false,
                isTranscribing = false
            )

            // act
            val flowJob = launch {
                callingViewModel.init(this)
            }
            stateFlow.emit(storeState)

            // assert
            verify(mockParticipantGridViewModel, times(1)).update(any(), any(), any(), any(), any())
            verify(mockFloatingHeaderViewModel, times(1)).update(any())
            verify(mockParticipantListViewModel, times(1)).update(any(), any())
            verify(mockBannerViewModel, times(1)).update(any())
            verify(mockControlBarViewModel, times(2)).update(
                any(),
                any(),
                any(),
                any(),
                any(),
            )
            verify(mockLocalParticipantViewModel, times(2)).update(
                any(), any(), any(), any(), any(), any(), any(), any()
            )

            flowJob.cancel()
        }
    }

    @Test
    @ExperimentalCoroutinesApi
    fun callingViewModel_onStateChange_when_callStateNotConnected_then_callingChildViewModelsAreNotUpdated() {

        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false)
            appState.localParticipantState = getLocalUserState()
            val stateFlow = MutableStateFlow<ReduxState>(appState)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doAnswer { stateFlow }
                on { getCurrentState() } doAnswer { appState }
            }
            val mockParticipantGridViewModel = mock<ParticipantGridViewModel> {
            }

            val mockControlBarViewModel = mock<ControlBarViewModel> {
                on { update(any(), any(), any(), any(), any(),) } doAnswer { }
            }

            val mockConfirmLeaveOverlayViewModel = mock<LeaveConfirmViewModel> {}

            val mockLocalParticipantViewModel = mock<LocalParticipantViewModel> {
                on { update(any(), any(), any(), any(), any(), any(), any(), any()) } doAnswer { }
            }

            val mockFloatingHeaderViewModel = mock<InfoHeaderViewModel> {}

            val mockAudioDeviceListViewModel = mock<AudioDeviceListViewModel>()

            val mockParticipantListViewModel = mock<ParticipantListViewModel>()

            val mockBannerViewModel = mock<BannerViewModel>()
            val mockWaitingLobbyOverlayViewModel = mock<WaitingLobbyOverlayViewModel>()
            val mockConnectingLobbyOverlayViewModel = mock<ConnectingLobbyOverlayViewModel>()
            val mockOnHoldOverlayViewModel = mock<OnHoldOverlayViewModel>()
            val mockMoreCallOptionsListViewModel = mock<MoreCallOptionsListViewModel>()
            val mockNetworkManager = mock<NetworkManager>()
            val mockToastNotificationViewModel = mock<ToastNotificationViewModel>()
            val mockUpperMessageBarNotificationLayoutViewModel = mock<UpperMessageBarNotificationLayoutViewModel>()

            val mockCallingViewModelProvider = mock<CallingViewModelFactory> {
                on { participantGridViewModel } doAnswer { mockParticipantGridViewModel }
                on { controlBarViewModel } doAnswer { mockControlBarViewModel }
                on { confirmLeaveOverlayViewModel } doAnswer { mockConfirmLeaveOverlayViewModel }
                on { localParticipantViewModel } doAnswer { mockLocalParticipantViewModel }
                on { floatingHeaderViewModel } doAnswer { mockFloatingHeaderViewModel }
                on { audioDeviceListViewModel } doAnswer { mockAudioDeviceListViewModel }
                on { participantListViewModel } doAnswer { mockParticipantListViewModel }
                on { bannerViewModel } doAnswer { mockBannerViewModel }
                on { waitingLobbyOverlayViewModel } doAnswer { mockWaitingLobbyOverlayViewModel }
                on { connectingLobbyOverlayViewModel } doAnswer { mockConnectingLobbyOverlayViewModel }
                on { onHoldOverlayViewModel } doAnswer { mockOnHoldOverlayViewModel }
                on { moreCallOptionsListViewModel } doAnswer { mockMoreCallOptionsListViewModel }
                on { toastNotificationViewModel } doAnswer { mockToastNotificationViewModel }
                on { upperMessageBarNotificationLayoutViewModel } doAnswer { mockUpperMessageBarNotificationLayoutViewModel }
            }

            val callingViewModel = CallingViewModel(
                mockAppStore,
                mockCallingViewModelProvider,
                mockNetworkManager,
                false,
            )

            val newForegroundState = AppReduxState("", false, false)
            newForegroundState.lifecycleState = LifecycleState(LifecycleStatus.FOREGROUND)
            newForegroundState.localParticipantState = getLocalUserState()

            // act
            val flowJob = launch {
                callingViewModel.init(this)
            }
            stateFlow.emit(newForegroundState)

            // assert
            verify(mockParticipantGridViewModel, times(0)).update(any(), any(), any(), any(), any())
            verify(mockFloatingHeaderViewModel, times(0)).update(any())
            verify(mockParticipantListViewModel, times(0)).update(any(), any())
            verify(mockBannerViewModel, times(0)).update(any())
            verify(mockControlBarViewModel, times(2)).update(
                any(),
                any(),
                any(),
                any(),
                any(),
            )
            verify(mockLocalParticipantViewModel, times(2)).update(
                any(), any(), any(), any(), any(), any(), any(), any()
            )

            flowJob.cancel()
        }
    }

    @Test
    @ExperimentalCoroutinesApi
    fun callingViewModel_onParticipantListChange_then_showAllUsersIfNoLobbyUserExists() {

        runScopedTest {
            // one lobby participant and two connected participants
            val expectedParticipantCountOnGridView = 3
            val expectedParticipantCountOnParticipantList = 3
            val expectedParticipantCountOnFloatingHeader = 3

            val participantInfoModel1 = getParticipantInfoModel(
                "user one",
                "user1",
                isMuted = true,
                isSpeaking = true,
                cameraVideoStreamModel = VideoStreamModel("video_stream_2", StreamType.VIDEO),
                modifiedTimestamp = 111,
                speakingTimestamp = 222
            )
            val participantInfoModel2 = getParticipantInfoModel(
                "user two",
                "user2",
                isMuted = true,
                isSpeaking = true,
                cameraVideoStreamModel = VideoStreamModel("video_stream_2", StreamType.VIDEO),
                modifiedTimestamp = 111,
                speakingTimestamp = 222
            )
            val participantInfoModel3 = getParticipantInfoModel(
                "user three",
                "user3",
                isMuted = true,
                isSpeaking = true,
                cameraVideoStreamModel = VideoStreamModel("video_stream_2", StreamType.VIDEO),
                modifiedTimestamp = 111,
                speakingTimestamp = 222,
                participantStatus = ParticipantStatus.CONNECTED
            )
            val participantMap: Map<String, ParticipantInfoModel> = mapOf(
                "p1" to participantInfoModel1,
                "p2" to participantInfoModel2,
                "p3" to participantInfoModel3
            )

            callViewOptionsTests(
                participantMap,
                expectedParticipantCountOnGridView,
                expectedParticipantCountOnFloatingHeader,
                expectedParticipantCountOnParticipantList
            )
        }
    }

    @Test
    @ExperimentalCoroutinesApi
    fun callingViewModel_onParticipantListChange_then_hideLobbyParticipantsOnGridAndParticipantList() {

        runScopedTest {
            // one lobby participant and two connected participants
            val expectedParticipantCountOnGridView = 2
            val expectedParticipantCountOnParticipantList = 2
            val expectedParticipantCountOnFloatingHeader = 2

            val participantInfoModel1 = getParticipantInfoModel(
                "user one",
                "user1",
                isMuted = true,
                isSpeaking = true,
                cameraVideoStreamModel = VideoStreamModel("video_stream_2", StreamType.VIDEO),
                modifiedTimestamp = 111,
                speakingTimestamp = 222
            )
            val participantInfoModel2 = getParticipantInfoModel(
                "user two",
                "user2",
                isMuted = true,
                isSpeaking = true,
                cameraVideoStreamModel = VideoStreamModel("video_stream_2", StreamType.VIDEO),
                modifiedTimestamp = 111,
                speakingTimestamp = 222
            )
            val participantInfoModel3 = getParticipantInfoModel(
                "user three",
                "user3",
                isMuted = true,
                isSpeaking = true,
                cameraVideoStreamModel = VideoStreamModel("video_stream_2", StreamType.VIDEO),
                modifiedTimestamp = 111,
                speakingTimestamp = 222,
                participantStatus = ParticipantStatus.IN_LOBBY
            )
            val participantMap: Map<String, ParticipantInfoModel> = mapOf(
                "p1" to participantInfoModel1,
                "p2" to participantInfoModel2,
                "p3" to participantInfoModel3
            )

            callViewOptionsTests(
                participantMap,
                expectedParticipantCountOnGridView,
                expectedParticipantCountOnFloatingHeader,
                expectedParticipantCountOnParticipantList
            )
        }
    }

    private suspend fun TestScope.callViewOptionsTests(
        participantMap: Map<String, ParticipantInfoModel>,
        expectedParticipantCountOnGridView: Int,
        expectedParticipantCountOnFloatingHeader: Int,
        expectedParticipantCountOnParticipantList: Int
    ) {
        // arrange
        val appState = AppReduxState("", false, false)
        appState.localParticipantState = getLocalUserState()

        val timestamp: Number = System.currentTimeMillis()

        val stateFlow = MutableStateFlow<ReduxState>(appState)
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getStateFlow() } doAnswer { stateFlow }
            on { getCurrentState() } doAnswer { appState }
        }

        val mockFloatingHeaderViewModel = mock<InfoHeaderViewModel> {}
        val mockParticipantListViewModel = mock<ParticipantListViewModel>()
        val mockParticipantGridViewModel = mock<ParticipantGridViewModel> {}

        val mockControlBarViewModel = mock<ControlBarViewModel> {
            on { update(any(), any(), any(), any(), any()) } doAnswer { }
        }
        val mockConfirmLeaveOverlayViewModel = mock<LeaveConfirmViewModel> {}
        val mockLocalParticipantViewModel = mock<LocalParticipantViewModel> {
            on { update(any(), any(), any(), any(), any(), any(), any(), any()) } doAnswer { }
        }
        val mockAudioDeviceListViewModel = mock<AudioDeviceListViewModel>()
        val mockBannerViewModel = mock<BannerViewModel>()
        val mockWaitingLobbyOverlayViewModel = mock<WaitingLobbyOverlayViewModel>()
        val mockConnectingLobbyOverlayViewModel = mock<ConnectingLobbyOverlayViewModel>()
        val mockOnHoldOverlayViewModel = mock<OnHoldOverlayViewModel>()
        val mockMoreCallOptionsListViewModel = mock<MoreCallOptionsListViewModel>()
        val mockNetworkManager = mock<NetworkManager>()
        val mockToastNotificationViewModel = mock<ToastNotificationViewModel>()
        val mockUpperMessageBarNotificationLayoutViewModel = mock<UpperMessageBarNotificationLayoutViewModel>()

        val mockCallingViewModelProvider = mock<CallingViewModelFactory> {
            on { participantGridViewModel } doAnswer { mockParticipantGridViewModel }
            on { controlBarViewModel } doAnswer { mockControlBarViewModel }
            on { confirmLeaveOverlayViewModel } doAnswer { mockConfirmLeaveOverlayViewModel }
            on { localParticipantViewModel } doAnswer { mockLocalParticipantViewModel }
            on { floatingHeaderViewModel } doAnswer { mockFloatingHeaderViewModel }
            on { audioDeviceListViewModel } doAnswer { mockAudioDeviceListViewModel }
            on { participantListViewModel } doAnswer { mockParticipantListViewModel }
            on { bannerViewModel } doAnswer { mockBannerViewModel }
            on { waitingLobbyOverlayViewModel } doAnswer { mockWaitingLobbyOverlayViewModel }
            on { connectingLobbyOverlayViewModel } doAnswer { mockConnectingLobbyOverlayViewModel }
            on { onHoldOverlayViewModel } doAnswer { mockOnHoldOverlayViewModel }
            on { moreCallOptionsListViewModel } doAnswer { mockMoreCallOptionsListViewModel }
            on { toastNotificationViewModel } doAnswer { mockToastNotificationViewModel }
            on { upperMessageBarNotificationLayoutViewModel } doAnswer { mockUpperMessageBarNotificationLayoutViewModel }
        }

        val callingViewModel = CallingViewModel(
            mockAppStore,
            mockCallingViewModelProvider,
            mockNetworkManager,
            false
        )

        val newState = AppReduxState("", false, false)
        newState.lifecycleState = LifecycleState(LifecycleStatus.FOREGROUND)
        newState.localParticipantState = getLocalUserState()
        newState.callState = CallingState(
            CallingStatus.CONNECTED,
            OperationStatus.NONE,
            isRecording = false,
            isTranscribing = false
        )
        newState.remoteParticipantState = RemoteParticipantsState(
            participantMap,
            timestamp,
            listOf(),
            0
        )

        // act
        val flowJob = launch {
            callingViewModel.init(this)
        }
        stateFlow.emit(newState)

        // assert
        verify(mockParticipantGridViewModel, times(1)).update(
            any(),
            argThat { map -> map.size == expectedParticipantCountOnGridView },
            any(),
            any(),
            any(),
        )
        verify(mockFloatingHeaderViewModel, times(1)).update(
            expectedParticipantCountOnFloatingHeader
        )
        verify(
            mockParticipantListViewModel,
            times(1)
        ).update(argThat { map -> map.size == expectedParticipantCountOnParticipantList }, any())
        verify(mockBannerViewModel, times(1)).update(any())
        verify(mockControlBarViewModel, times(2)).update(
            any(),
            any(),
            any(),
            any(),
            any(),
        )
        verify(mockLocalParticipantViewModel, times(2)).update(
            any(), any(), any(), any(), any(), any(), any(), any(),
        )

        flowJob.cancel()
    }

    private fun getParticipantInfoModel(
        displayName: String,
        userIdentifier: String,
        isMuted: Boolean,
        isSpeaking: Boolean,
        screenShareVideoStreamModel: VideoStreamModel? = null,
        cameraVideoStreamModel: VideoStreamModel? = null,
        modifiedTimestamp: Number,
        speakingTimestamp: Number,
        participantStatus: ParticipantStatus = ParticipantStatus.CONNECTED
    ) = ParticipantInfoModel(
        displayName,
        userIdentifier,
        isMuted,
        isSpeaking,
        participantStatus,
        screenShareVideoStreamModel,
        cameraVideoStreamModel,
        modifiedTimestamp,
    )

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
