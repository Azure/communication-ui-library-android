// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling

import com.azure.android.communication.ui.calling.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.configuration.CallType
import com.azure.android.communication.ui.calling.models.CallCompositeAudioVideoMode
import com.azure.android.communication.ui.calling.models.CallCompositeCallScreenControlBarOptions
import com.azure.android.communication.ui.calling.models.CallCompositeCallScreenOptions
import com.azure.android.communication.ui.calling.models.CallCompositeLeaveCallConfirmationMode
import com.azure.android.communication.ui.calling.models.ParticipantCapabilityType
import com.azure.android.communication.ui.calling.models.ParticipantInfoModel
import com.azure.android.communication.ui.calling.models.ParticipantRole
import com.azure.android.communication.ui.calling.models.ParticipantStatus
import com.azure.android.communication.ui.calling.models.StreamType
import com.azure.android.communication.ui.calling.models.VideoStreamModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.banner.BannerViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.captions.CaptionsViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.connecting.overlay.ConnectingOverlayViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.ControlBarViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.captions.CaptionsLanguageSelectionListViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.captions.CaptionsListViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.more.MoreCallOptionsListViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.hangup.LeaveConfirmViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.header.InfoHeaderViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.hold.OnHoldOverlayViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.lobby.LobbyErrorHeaderViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.lobby.LobbyHeaderViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.lobby.WaitingLobbyOverlayViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.localuser.LocalParticipantViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.notification.ToastNotificationViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.notification.UpperMessageBarNotificationLayoutViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.participant.grid.ParticipantGridViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.participant.menu.ParticipantMenuViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.participantlist.ParticipantListViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.common.audiodevicelist.AudioDeviceListViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.factories.CallingViewModelFactory
import com.azure.android.communication.ui.calling.presentation.manager.CapabilitiesManager
import com.azure.android.communication.ui.calling.presentation.manager.NetworkManager
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.action.NavigationAction
import com.azure.android.communication.ui.calling.redux.state.AppReduxState
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.AudioState
import com.azure.android.communication.ui.calling.redux.state.BluetoothState
import com.azure.android.communication.ui.calling.redux.state.CallingState
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.CameraDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.CameraState
import com.azure.android.communication.ui.calling.redux.state.CameraTransmissionStatus
import com.azure.android.communication.ui.calling.redux.state.LifecycleState
import com.azure.android.communication.ui.calling.redux.state.LifecycleStatus
import com.azure.android.communication.ui.calling.redux.state.LocalUserState
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.redux.state.RemoteParticipantsState
import com.azure.android.communication.ui.calling.redux.state.VisibilityStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
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
            val appState = AppReduxState("", false, false, false)
            appState.localParticipantState = getLocalUserState()
            val stateFlow = MutableStateFlow<ReduxState>(appState)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doAnswer { stateFlow }
                on { getCurrentState() } doAnswer { appState }
            }
            val mockParticipantGridViewModel = mock<ParticipantGridViewModel> {
            }

            val mockControlBarViewModel = mock<ControlBarViewModel> {
                on { update(any(), any(), any(), any(), any(), any(), any(), any(),) } doAnswer { }
            }

            val mockConfirmLeaveOverlayViewModel = mock<LeaveConfirmViewModel> {}

            val mockLocalParticipantViewModel = mock<LocalParticipantViewModel> {
                on { update(any(), any(), any(), any(), any(), any(), any(), any(), any()) } doAnswer { }
            }

            val mockFloatingHeaderViewModel = mock<InfoHeaderViewModel> {}

            val mockAudioDeviceListViewModel = mock<AudioDeviceListViewModel>()

            val mockParticipantListViewModel = mock<ParticipantListViewModel>()

            val mockBannerViewModel = mock<BannerViewModel>()

            val mockWaitingLobbyOverlayViewModel = mock<WaitingLobbyOverlayViewModel>()
            val mockConnectingOverlayViewModel = mock<ConnectingOverlayViewModel>()

            val mockOnHoldOverlayViewModel = mock<OnHoldOverlayViewModel>()
            val mockMoreCallOptionsListViewModel = mock<MoreCallOptionsListViewModel>()

            val mockToastNotificationViewModel = mock<ToastNotificationViewModel>()
            val mockUpperMessageBarNotificationLayoutViewModel = mock<UpperMessageBarNotificationLayoutViewModel>()

            val mockNetworkManager = mock<NetworkManager>()
            val mockLobbyHeaderViewModel = mock<LobbyHeaderViewModel>()
            val mockLobbyErrorHeaderViewModel = mock<LobbyErrorHeaderViewModel>()
            val mockParticipantMenuViewModel = mock<ParticipantMenuViewModel>()
            val mockCaptionsListViewModel = mock<CaptionsListViewModel>()
            val mockCaptionsLanguageSelectionListViewModel = mock<CaptionsLanguageSelectionListViewModel>()
            val mockCaptionsViewModel = mock<CaptionsViewModel>()

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
                on { connectingOverlayViewModel } doAnswer { mockConnectingOverlayViewModel }
                on { onHoldOverlayViewModel } doAnswer { mockOnHoldOverlayViewModel }
                on { moreCallOptionsListViewModel } doAnswer { mockMoreCallOptionsListViewModel }
                on { lobbyHeaderViewModel } doAnswer { mockLobbyHeaderViewModel }
                on { lobbyErrorHeaderViewModel } doAnswer { mockLobbyErrorHeaderViewModel }
                on { toastNotificationViewModel } doAnswer { mockToastNotificationViewModel }
                on { upperMessageBarNotificationLayoutViewModel } doAnswer { mockUpperMessageBarNotificationLayoutViewModel }
                on { participantMenuViewModel } doAnswer { mockParticipantMenuViewModel }
                on { captionsListViewModel } doAnswer { mockCaptionsListViewModel }
                on { captionsLanguageSelectionListViewModel } doAnswer { mockCaptionsLanguageSelectionListViewModel }
                on { captionsViewModel } doAnswer { mockCaptionsViewModel }
            }

            val callingViewModel = CallingViewModel(
                mockAppStore,
                mockCallingViewModelProvider,
                mockNetworkManager,
                callScreenOptions = CallCompositeCallScreenOptions().setControlBarOptions(
                    CallCompositeCallScreenControlBarOptions().setLeaveCallConfirmation(
                        CallCompositeLeaveCallConfirmationMode.ALWAYS_DISABLED
                    )
                ),
                false,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                capabilitiesManager = CapabilitiesManager(CallType.GROUP_CALL)
            )

            val newBackgroundState = AppReduxState(
                "",
                false,
                false,
                false,
            )
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
                any(),
                any(),
                any(),
            )
            verify(mockLocalParticipantViewModel, times(1)).update(
                any(), any(), any(), any(), any(), any(), any(), any(), any()
            )

            flowJob.cancel()
        }
    }

    @Test
    @ExperimentalCoroutinesApi
    fun callingViewModel_onStateChange_when_stateIsNotBackground_then_callChildViewModels() {

        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false, false)
            appState.localParticipantState = getLocalUserState()
            val stateFlow = MutableStateFlow<ReduxState>(appState)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doAnswer { stateFlow }
                on { getCurrentState() } doAnswer { appState }
            }
            val mockParticipantGridViewModel = mock<ParticipantGridViewModel> {
            }

            val mockControlBarViewModel = mock<ControlBarViewModel> {
                on { update(any(), any(), any(), any(), any(), any(), any(), any(),) } doAnswer { }
            }

            val mockConfirmLeaveOverlayViewModel = mock<LeaveConfirmViewModel> {}

            val mockLocalParticipantViewModel = mock<LocalParticipantViewModel> {
                on { update(any(), any(), any(), any(), any(), any(), any(), any(), any()) } doAnswer { }
            }

            val mockFloatingHeaderViewModel = mock<InfoHeaderViewModel> {}

            val mockAudioDeviceListViewModel = mock<AudioDeviceListViewModel>()

            val mockParticipantListViewModel = mock<ParticipantListViewModel>()

            val mockBannerViewModel = mock<BannerViewModel>()

            val mockWaitingLobbyOverlayViewModel = mock<WaitingLobbyOverlayViewModel>()
            val mockConnectingOverlayViewModel = mock<ConnectingOverlayViewModel>()
            val mockOnHoldOverlayViewModel = mock<OnHoldOverlayViewModel>()
            val mockMoreCallOptionsListViewModel = mock<MoreCallOptionsListViewModel>()
            val mockNetworkManager = mock<NetworkManager>()
            val mockToastNotificationViewModel = mock<ToastNotificationViewModel>()
            val mockUpperMessageBarNotificationLayoutViewModel = mock<UpperMessageBarNotificationLayoutViewModel>()

            val mockLobbyHeaderViewModel = mock<LobbyHeaderViewModel>()
            val mockLobbyErrorHeaderViewModel = mock<LobbyErrorHeaderViewModel>()
            val mockParticipantMenuViewModel = mock<ParticipantMenuViewModel>()
            val mockCaptionsListViewModel = mock<CaptionsListViewModel>()
            val mockCaptionsLanguageSelectionListViewModel = mock<CaptionsLanguageSelectionListViewModel>()
            val mockCaptionsViewModel = mock<CaptionsViewModel>()

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
                on { connectingOverlayViewModel } doAnswer { mockConnectingOverlayViewModel }
                on { onHoldOverlayViewModel } doAnswer { mockOnHoldOverlayViewModel }
                on { moreCallOptionsListViewModel } doAnswer { mockMoreCallOptionsListViewModel }
                on { lobbyHeaderViewModel } doAnswer { mockLobbyHeaderViewModel }
                on { lobbyErrorHeaderViewModel } doAnswer { mockLobbyErrorHeaderViewModel }
                on { toastNotificationViewModel } doAnswer { mockToastNotificationViewModel }
                on { upperMessageBarNotificationLayoutViewModel } doAnswer { mockUpperMessageBarNotificationLayoutViewModel }
                on { participantMenuViewModel } doAnswer { mockParticipantMenuViewModel }
                on { captionsListViewModel } doAnswer { mockCaptionsListViewModel }
                on { captionsLanguageSelectionListViewModel } doAnswer { mockCaptionsLanguageSelectionListViewModel }
                on { captionsViewModel } doAnswer { mockCaptionsViewModel }
            }

            val callingViewModel = CallingViewModel(
                mockAppStore,
                mockCallingViewModelProvider,
                mockNetworkManager,
                callScreenOptions = CallCompositeCallScreenOptions().setControlBarOptions(
                    CallCompositeCallScreenControlBarOptions().setLeaveCallConfirmation(
                        CallCompositeLeaveCallConfirmationMode.ALWAYS_DISABLED
                    )
                ),
                false,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                capabilitiesManager = CapabilitiesManager(CallType.GROUP_CALL)
            )

            val newForegroundState = AppReduxState(
                "",
                false,
                false,
                false,
            )
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
                any(),
                any(),
                any(),
            )
            verify(mockLocalParticipantViewModel, times(2)).update(
                any(), any(), any(), any(), any(), any(), any(), any(), any()
            )

            flowJob.cancel()
        }
    }

    @Test
    @ExperimentalCoroutinesApi
    fun callingViewModel_onStateChange_when_callStateConnectingAndCallTypeOutgoing_then_callingChildViewModelsAreUpdated() {

        runScopedTest {
            // arrange
            val callState = CallingStatus.CONNECTING
            gridUpdateTestWithCallState(callState)
        }
    }

    @Test
    @ExperimentalCoroutinesApi
    fun callingViewModel_onStateChange_when_callStateRingingAndCallTypeOutgoing_then_callingChildViewModelsAreUpdated() {

        runScopedTest {
            // arrange
            val callState = CallingStatus.RINGING
            gridUpdateTestWithCallState(callState)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun gridUpdateTestWithCallState(callState: CallingStatus) {
        coroutineScope {
            val appState = AppReduxState("", false, false, false)
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
                on { update(any(), any(), any(), any(), any(), any(), any(), any(),) } doAnswer { }
            }
            val mockConfirmLeaveOverlayViewModel = mock<LeaveConfirmViewModel> {}
            val mockLocalParticipantViewModel = mock<LocalParticipantViewModel> {
                on {
                    update(
                        any(),
                        any(),
                        any(),
                        any(),
                        any(),
                        any(),
                        any(),
                        any(),
                        any()
                    )
                } doAnswer { }
            }
            val mockFloatingHeaderViewModel = mock<InfoHeaderViewModel> {}
            val mockAudioDeviceListViewModel = mock<AudioDeviceListViewModel>()
            val mockParticipantListViewModel = mock<ParticipantListViewModel>()
            val mockBannerViewModel = mock<BannerViewModel>()
            val mockWaitingLobbyOverlayViewModel = mock<WaitingLobbyOverlayViewModel>()
            val mockConnectingOverlayViewModel = mock<ConnectingOverlayViewModel>()
            val mockOnHoldOverlayViewModel = mock<OnHoldOverlayViewModel>()
            val mockMoreCallOptionsListViewModel = mock<MoreCallOptionsListViewModel>()
            val mockNetworkManager = mock<NetworkManager>()
            val mockToastNotificationViewModel = mock<ToastNotificationViewModel>()
            val mockUpperMessageBarNotificationLayoutViewModel =
                mock<UpperMessageBarNotificationLayoutViewModel>()
            val mockLobbyHeaderViewModel = mock<LobbyHeaderViewModel>()
            val mockLobbyErrorHeaderViewModel = mock<LobbyErrorHeaderViewModel>()
            val mockParticipantMenuViewModel = mock<ParticipantMenuViewModel>()
            val mockCaptionsLanguageSelectionListViewModel = mock<CaptionsLanguageSelectionListViewModel>()
            val mockCaptionsListViewModel = mock<CaptionsListViewModel>()
            val mockCaptionsViewModel = mock<CaptionsViewModel>()

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
                on { connectingOverlayViewModel } doAnswer { mockConnectingOverlayViewModel }
                on { onHoldOverlayViewModel } doAnswer { mockOnHoldOverlayViewModel }
                on { moreCallOptionsListViewModel } doAnswer { mockMoreCallOptionsListViewModel }
                on { lobbyHeaderViewModel } doAnswer { mockLobbyHeaderViewModel }
                on { lobbyErrorHeaderViewModel } doAnswer { mockLobbyErrorHeaderViewModel }
                on { toastNotificationViewModel } doAnswer { mockToastNotificationViewModel }
                on { upperMessageBarNotificationLayoutViewModel } doAnswer { mockUpperMessageBarNotificationLayoutViewModel }
                on { participantMenuViewModel } doAnswer { mockParticipantMenuViewModel }
                on { captionsListViewModel } doAnswer { mockCaptionsListViewModel }
                on { captionsLanguageSelectionListViewModel } doAnswer { mockCaptionsLanguageSelectionListViewModel }
                on { captionsViewModel } doAnswer { mockCaptionsViewModel }
            }
            val callingViewModel = CallingViewModel(
                mockAppStore,
                mockCallingViewModelProvider,
                mockNetworkManager,
                callScreenOptions = CallCompositeCallScreenOptions().setControlBarOptions(
                    CallCompositeCallScreenControlBarOptions().setLeaveCallConfirmation(
                        CallCompositeLeaveCallConfirmationMode.ALWAYS_DISABLED
                    )
                ),
                false,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                CallType.ONE_TO_N_OUTGOING,
                capabilitiesManager = CapabilitiesManager(CallType.GROUP_CALL)
            )
            val storeState = AppReduxState("", false, false, false)
            storeState.lifecycleState = LifecycleState(LifecycleStatus.FOREGROUND)
            storeState.localParticipantState = getLocalUserState()
            storeState.callState = CallingState(
                callState,
                isRecording = false,
                isTranscribing = false
            )
            val flowJob = launch {
                callingViewModel.init(this)
            }
            stateFlow.emit(storeState)
            verify(mockParticipantGridViewModel, times(1)).update(any(), any(), any(), any(), any())
            verify(mockFloatingHeaderViewModel, times(1)).update(
                any(),
                any()
            )
            verify(mockParticipantListViewModel, times(1)).update(any(), any(), any(), any(), any())
            verify(mockBannerViewModel, times(1)).update(any(), any())
            verify(mockControlBarViewModel, times(2)).update(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
            )
            verify(mockLocalParticipantViewModel, times(2)).update(
                any(), any(), any(), any(), any(), any(), any(), any(), any()
            )
            flowJob.cancel()
        }
    }

    @Test
    @ExperimentalCoroutinesApi
    fun callingViewModel_onStateChange_when_callStateConnected_then_callingChildViewModelsAreUpdated() {

        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false, false)
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
                on { update(any(), any(), any(), any(), any(), any(), any(), any(),) } doAnswer { }
            }

            val mockConfirmLeaveOverlayViewModel = mock<LeaveConfirmViewModel> {}

            val mockLocalParticipantViewModel = mock<LocalParticipantViewModel> {
                on { update(any(), any(), any(), any(), any(), any(), any(), any(), any()) } doAnswer { }
            }

            val mockFloatingHeaderViewModel = mock<InfoHeaderViewModel> {}

            val mockAudioDeviceListViewModel = mock<AudioDeviceListViewModel>()

            val mockParticipantListViewModel = mock<ParticipantListViewModel>()

            val mockBannerViewModel = mock<BannerViewModel>()
            val mockWaitingLobbyOverlayViewModel = mock<WaitingLobbyOverlayViewModel>()
            val mockConnectingOverlayViewModel = mock<ConnectingOverlayViewModel>()
            val mockOnHoldOverlayViewModel = mock<OnHoldOverlayViewModel>()
            val mockMoreCallOptionsListViewModel = mock<MoreCallOptionsListViewModel>()
            val mockNetworkManager = mock<NetworkManager>()
            val mockToastNotificationViewModel = mock<ToastNotificationViewModel>()
            val mockUpperMessageBarNotificationLayoutViewModel = mock<UpperMessageBarNotificationLayoutViewModel>()

            val mockLobbyHeaderViewModel = mock<LobbyHeaderViewModel>()
            val mockParticipantMenuViewModel = mock<ParticipantMenuViewModel>()
            val mockCaptionsLanguageSelectionListViewModel = mock<CaptionsLanguageSelectionListViewModel>()
            val mockCaptionsListViewModel = mock<CaptionsListViewModel>()

            val mockLobbyErrorHeaderViewModel = mock<LobbyErrorHeaderViewModel>()
            val mockCaptionsViewModel = mock<CaptionsViewModel>()

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
                on { connectingOverlayViewModel } doAnswer { mockConnectingOverlayViewModel }
                on { onHoldOverlayViewModel } doAnswer { mockOnHoldOverlayViewModel }
                on { moreCallOptionsListViewModel } doAnswer { mockMoreCallOptionsListViewModel }
                on { lobbyHeaderViewModel } doAnswer { mockLobbyHeaderViewModel }
                on { lobbyErrorHeaderViewModel } doAnswer { mockLobbyErrorHeaderViewModel }
                on { toastNotificationViewModel } doAnswer { mockToastNotificationViewModel }
                on { upperMessageBarNotificationLayoutViewModel } doAnswer { mockUpperMessageBarNotificationLayoutViewModel }
                on { participantMenuViewModel } doAnswer { mockParticipantMenuViewModel }
                on { captionsListViewModel } doAnswer { mockCaptionsListViewModel }
                on { captionsLanguageSelectionListViewModel } doAnswer { mockCaptionsLanguageSelectionListViewModel }
                on { captionsViewModel } doAnswer { mockCaptionsViewModel }
            }

            val callingViewModel = CallingViewModel(
                mockAppStore,
                mockCallingViewModelProvider,
                mockNetworkManager,
                callScreenOptions = CallCompositeCallScreenOptions().setControlBarOptions(
                    CallCompositeCallScreenControlBarOptions().setLeaveCallConfirmation(
                        CallCompositeLeaveCallConfirmationMode.ALWAYS_DISABLED
                    )
                ),
                false,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                capabilitiesManager = CapabilitiesManager(CallType.GROUP_CALL)
            )

            val storeState = AppReduxState("", false, false, false)
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
            verify(mockParticipantGridViewModel, times(1)).update(any(), any(), any(), any(), any())
            verify(mockFloatingHeaderViewModel, times(1)).update(
                any(),
                any()
            )
            verify(mockParticipantListViewModel, times(1)).update(any(), any(), any(), any(), any())
            verify(mockBannerViewModel, times(1)).update(any(), any())
            verify(mockControlBarViewModel, times(2)).update(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
            )
            verify(mockLocalParticipantViewModel, times(2)).update(
                any(), any(), any(), any(), any(), any(), any(), any(), any()
            )

            flowJob.cancel()
        }
    }

    @Test
    @ExperimentalCoroutinesApi
    fun callingViewModel_onStateChange_when_callStateNotConnected_then_callingChildViewModelsAreNotUpdated() {

        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false, false)
            appState.localParticipantState = getLocalUserState()
            val stateFlow = MutableStateFlow<ReduxState>(appState)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doAnswer { stateFlow }
                on { getCurrentState() } doAnswer { appState }
            }
            val mockParticipantGridViewModel = mock<ParticipantGridViewModel> {
            }

            val mockControlBarViewModel = mock<ControlBarViewModel> {
                on { update(any(), any(), any(), any(), any(), any(), any(), any(),) } doAnswer { }
            }

            val mockConfirmLeaveOverlayViewModel = mock<LeaveConfirmViewModel> {}

            val mockLocalParticipantViewModel = mock<LocalParticipantViewModel> {
                on { update(any(), any(), any(), any(), any(), any(), any(), any(), any()) } doAnswer { }
            }

            val mockFloatingHeaderViewModel = mock<InfoHeaderViewModel> {}

            val mockAudioDeviceListViewModel = mock<AudioDeviceListViewModel>()

            val mockParticipantListViewModel = mock<ParticipantListViewModel>()

            val mockBannerViewModel = mock<BannerViewModel>()
            val mockWaitingLobbyOverlayViewModel = mock<WaitingLobbyOverlayViewModel>()
            val mockConnectingOverlayViewModel = mock<ConnectingOverlayViewModel>()
            val mockOnHoldOverlayViewModel = mock<OnHoldOverlayViewModel>()
            val mockMoreCallOptionsListViewModel = mock<MoreCallOptionsListViewModel>()
            val mockNetworkManager = mock<NetworkManager>()
            val mockToastNotificationViewModel = mock<ToastNotificationViewModel>()
            val mockUpperMessageBarNotificationLayoutViewModel = mock<UpperMessageBarNotificationLayoutViewModel>()

            val mockLobbyHeaderViewModel = mock<LobbyHeaderViewModel>()
            val mockParticipantMenuViewModel = mock<ParticipantMenuViewModel>()
            val mockCaptionsListViewModel = mock<CaptionsListViewModel>()
            val mockCaptionsLanguageSelectionListViewModel = mock<CaptionsLanguageSelectionListViewModel>()
            val mockLobbyErrorHeaderViewModel = mock<LobbyErrorHeaderViewModel>()
            val mockCaptionsViewModel = mock<CaptionsViewModel>()

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
                on { connectingOverlayViewModel } doAnswer { mockConnectingOverlayViewModel }
                on { onHoldOverlayViewModel } doAnswer { mockOnHoldOverlayViewModel }
                on { moreCallOptionsListViewModel } doAnswer { mockMoreCallOptionsListViewModel }
                on { lobbyHeaderViewModel } doAnswer { mockLobbyHeaderViewModel }
                on { lobbyErrorHeaderViewModel } doAnswer { mockLobbyErrorHeaderViewModel }
                on { toastNotificationViewModel } doAnswer { mockToastNotificationViewModel }
                on { upperMessageBarNotificationLayoutViewModel } doAnswer { mockUpperMessageBarNotificationLayoutViewModel }
                on { participantMenuViewModel } doAnswer { mockParticipantMenuViewModel }
                on { captionsListViewModel } doAnswer { mockCaptionsListViewModel }
                on { captionsLanguageSelectionListViewModel } doAnswer { mockCaptionsLanguageSelectionListViewModel }
                on { captionsViewModel } doAnswer { mockCaptionsViewModel }
            }

            val callingViewModel = CallingViewModel(
                mockAppStore,
                mockCallingViewModelProvider,
                mockNetworkManager,
                callScreenOptions = CallCompositeCallScreenOptions().setControlBarOptions(
                    CallCompositeCallScreenControlBarOptions().setLeaveCallConfirmation(
                        CallCompositeLeaveCallConfirmationMode.ALWAYS_DISABLED
                    )
                ),
                false,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                capabilitiesManager = CapabilitiesManager(CallType.GROUP_CALL)
            )

            val newForegroundState = AppReduxState(
                "",
                false,
                false,
                false,
            )
            newForegroundState.lifecycleState = LifecycleState(LifecycleStatus.FOREGROUND)
            newForegroundState.localParticipantState = getLocalUserState()

            // act
            val flowJob = launch {
                callingViewModel.init(this)
            }
            stateFlow.emit(newForegroundState)

            // assert
            verify(mockParticipantGridViewModel, times(0)).update(any(), any(), any(), any(), any())
            verify(mockFloatingHeaderViewModel, times(0)).update(
                any(),
                any()
            )
            verify(mockParticipantListViewModel, times(0)).update(any(), any(), any(), any(), any())
            verify(mockBannerViewModel, times(0)).update(any(), any())
            verify(mockControlBarViewModel, times(2)).update(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
            )
            verify(mockLocalParticipantViewModel, times(2)).update(
                any(), any(), any(), any(), any(), any(), any(), any(), any()
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
    fun callingViewModel_onParticipantListChange_then_hideLobbyParticipantsOnGridAndShowOnParticipantList() {

        runScopedTest {
            // one lobby participant and two connected participants
            val expectedParticipantCountOnGridView = 2
            val expectedParticipantCountOnParticipantList = 3
            val expectedParticipantCountOnFloatingHeader = 2

            val participantInfoModel1 = getParticipantInfoModel(
                "user one",
                "user1",
                isMuted = true,
                isSpeaking = true,
                cameraVideoStreamModel = VideoStreamModel("video_stream_2", StreamType.VIDEO),
                modifiedTimestamp = 111,
                speakingTimestamp = 222,
                participantStatus = ParticipantStatus.CONNECTED
            )
            val participantInfoModel2 = getParticipantInfoModel(
                "user two",
                "user2",
                isMuted = true,
                isSpeaking = true,
                cameraVideoStreamModel = VideoStreamModel("video_stream_2", StreamType.VIDEO),
                modifiedTimestamp = 111,
                speakingTimestamp = 222,
                participantStatus = ParticipantStatus.CONNECTED
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

    @Test
    @ExperimentalCoroutinesApi
    fun callingViewModel_onParticipantListChange_then_hideLobbyParticipantsOnGridAndParticipantList_ifCallStateIsNotConnected() {
        runScopedTest {
            // one lobby participant and two connected participants
            val participantInfoModel1 = getParticipantInfoModel(
                "user one",
                "user1",
                isMuted = true,
                isSpeaking = true,
                cameraVideoStreamModel = VideoStreamModel("video_stream_2", StreamType.VIDEO),
                modifiedTimestamp = 111,
                speakingTimestamp = 222,
                participantStatus = ParticipantStatus.CONNECTED
            )
            val participantInfoModel2 = getParticipantInfoModel(
                "user two",
                "user2",
                isMuted = true,
                isSpeaking = true,
                cameraVideoStreamModel = VideoStreamModel("video_stream_2", StreamType.VIDEO),
                modifiedTimestamp = 111,
                speakingTimestamp = 222,
                participantStatus = ParticipantStatus.CONNECTED
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

            val appState = AppReduxState("", false, false, false)
            appState.localParticipantState = getLocalUserState()

            val timestamp: Number = System.currentTimeMillis()

            val stateFlow = MutableStateFlow<ReduxState>(appState)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doAnswer { stateFlow }
                on { getCurrentState() } doAnswer { appState }
            }

            val mockFloatingHeaderViewModel = mock<InfoHeaderViewModel> {}
            val mockParticipantGridViewModel = mock<ParticipantGridViewModel> {}

            val mockControlBarViewModel = mock<ControlBarViewModel> {}
            val mockConfirmLeaveOverlayViewModel = mock<LeaveConfirmViewModel> {}
            val mockLobbyHeaderViewModel = mock<LobbyHeaderViewModel> {}
            val mockLobbyErrorHeaderViewModel = mock<LobbyErrorHeaderViewModel> {}
            val mockParticipantListViewModel = mock<ParticipantListViewModel> {}
            val mockAudioDeviceListViewModel = mock<AudioDeviceListViewModel>()
            val mockBannerViewModel = mock<BannerViewModel>()
            val mockWaitingLobbyOverlayViewModel = mock<WaitingLobbyOverlayViewModel>()
            val mockConnectingOverlayViewModel = mock<ConnectingOverlayViewModel>()
            val mockOnHoldOverlayViewModel = mock<OnHoldOverlayViewModel>()
            val mockMoreCallOptionsListViewModel = mock<MoreCallOptionsListViewModel>()
            val mockNetworkManager = mock<NetworkManager>()
            val mockLocalParticipantViewModel = mock<LocalParticipantViewModel> { }
            val mockParticipantMenuViewModel = mock<ParticipantMenuViewModel>()
            val mockCaptionsListViewModel = mock<CaptionsListViewModel>()
            val mockCaptionsLanguageSelectionListViewModel = mock<CaptionsLanguageSelectionListViewModel>()
            val mockCaptionsViewModel = mock<CaptionsViewModel>()

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
                on { connectingOverlayViewModel } doAnswer { mockConnectingOverlayViewModel }
                on { onHoldOverlayViewModel } doAnswer { mockOnHoldOverlayViewModel }
                on { moreCallOptionsListViewModel } doAnswer { mockMoreCallOptionsListViewModel }
                on { lobbyHeaderViewModel } doAnswer { mockLobbyHeaderViewModel }
                on { lobbyErrorHeaderViewModel } doAnswer { mockLobbyErrorHeaderViewModel }
                on { participantMenuViewModel } doAnswer { mockParticipantMenuViewModel }
                on { captionsListViewModel } doAnswer { mockCaptionsListViewModel }
                on { captionsLanguageSelectionListViewModel } doAnswer { mockCaptionsLanguageSelectionListViewModel }
                on { captionsViewModel } doAnswer { mockCaptionsViewModel }
            }

            val callingViewModel = CallingViewModel(
                mockAppStore,
                mockCallingViewModelProvider,
                mockNetworkManager,
                callScreenOptions = CallCompositeCallScreenOptions().setControlBarOptions(
                    CallCompositeCallScreenControlBarOptions().setLeaveCallConfirmation(
                        CallCompositeLeaveCallConfirmationMode.ALWAYS_ENABLED
                    )
                ),
                false,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                capabilitiesManager = CapabilitiesManager(CallType.GROUP_CALL)
            )

            val newState = AppReduxState("", false, false, false)
            newState.lifecycleState = LifecycleState(LifecycleStatus.FOREGROUND)
            newState.localParticipantState = getLocalUserState()
            newState.callState = CallingState(
                CallingStatus.IN_LOBBY,
                isRecording = false,
                isTranscribing = false
            )
            newState.remoteParticipantState = RemoteParticipantsState(
                participantMap,
                timestamp,
                listOf(),
                0,
                lobbyErrorCode = null,
                totalParticipantCount = 0,
            )

            // act
            val flowJob = launch {
                callingViewModel.init(this)
            }
            stateFlow.emit(newState)

            // assert
            verify(
                mockParticipantListViewModel,
                times(0)
            ).update(any(), any(), any(), any(), any())
            verify(
                mockLobbyHeaderViewModel,
                times(0)
            ).update(any(), any(), any())

            verify(
                mockLobbyErrorHeaderViewModel,
                times(0)
            ).update(any(), any(), any())

            verify(
                mockParticipantListViewModel,
                times(1)
            ).init(argThat { map -> map.isEmpty() }, argThat { status -> status == newState.localParticipantState }, argThat { value -> value == true }, any(), any())
            verify(
                mockLobbyHeaderViewModel,
                times(1)
            ).init(argThat { status -> status == CallingStatus.NONE }, argThat { map -> map.isEmpty() }, argThat { value -> value == true })

            verify(
                mockLobbyErrorHeaderViewModel,
                times(1)
            ).init(argThat { status -> status == CallingStatus.NONE }, argThat { value -> value == null }, argThat { value -> value == true })

            flowJob.cancel()
        }
    }

    @Test
    @ExperimentalCoroutinesApi
    fun callingViewModel_onParticipantListChange_then_hideLobbyParticipantsOnGridAndParticipantList_ifRoleIsUninitialized() {
        runScopedTest {
            testForParticipantRoleLobbyVisibility(emptySet(), false)
        }
    }

    @Test
    @ExperimentalCoroutinesApi
    fun callingViewModel_onParticipantListChange_then_hideLobbyParticipantsOnGridAndParticipantList_ifRoleIsAttendee() {
        runScopedTest {
            val capabilities = setOf(ParticipantCapabilityType.TURN_VIDEO_ON, ParticipantCapabilityType.UNMUTE_MICROPHONE)
            testForParticipantRoleLobbyVisibility(capabilities, false)
        }
    }

    @Test
    @ExperimentalCoroutinesApi
    fun callingViewModel_onParticipantListChange_then_hideLobbyParticipantsOnGridAndParticipantList_ifRoleIsConsumer() {
        runScopedTest {
            testForParticipantRoleLobbyVisibility(emptySet(), false)
        }
    }

    @Test
    @ExperimentalCoroutinesApi
    fun callingViewModel_onParticipantListChange_then_showLobbyParticipantsOnGridAndParticipantList_ifRoleIsPresenter() {
        runScopedTest {
            val capabilities = setOf(
                ParticipantCapabilityType.TURN_VIDEO_ON,
                ParticipantCapabilityType.UNMUTE_MICROPHONE,
                ParticipantCapabilityType.MANAGE_LOBBY,
                ParticipantCapabilityType.SHARE_SCREEN
            )
            testForParticipantRoleLobbyVisibility(capabilities, true)
        }
    }

    @Test
    @ExperimentalCoroutinesApi
    fun callingViewModel_onParticipantListChange_then_showLobbyParticipantsOnGridAndParticipantList_ifCapableManageLobby() {
        runScopedTest {
            val capabilities = setOf(
                ParticipantCapabilityType.TURN_VIDEO_ON,
                ParticipantCapabilityType.UNMUTE_MICROPHONE,
                ParticipantCapabilityType.MANAGE_LOBBY,
                ParticipantCapabilityType.SHARE_SCREEN
            )
            testForParticipantRoleLobbyVisibility(capabilities, true)
        }
    }

    private suspend fun TestScope.testForParticipantRoleLobbyVisibility(
        capabilities: Set<ParticipantCapabilityType>,
        showLobby: Boolean
    ) {
        // one lobby participant and two connected participants
        val participantInfoModel1 = getParticipantInfoModel(
            "user one",
            "user1",
            isMuted = true,
            isSpeaking = true,
            cameraVideoStreamModel = VideoStreamModel("video_stream_2", StreamType.VIDEO),
            modifiedTimestamp = 111,
            speakingTimestamp = 222,
            participantStatus = ParticipantStatus.CONNECTED
        )
        val participantInfoModel2 = getParticipantInfoModel(
            "user two",
            "user2",
            isMuted = true,
            isSpeaking = true,
            cameraVideoStreamModel = VideoStreamModel("video_stream_2", StreamType.VIDEO),
            modifiedTimestamp = 111,
            speakingTimestamp = 222,
            participantStatus = ParticipantStatus.CONNECTED
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

        val appState = AppReduxState("", false, false, false)
        appState.localParticipantState = getLocalUserState(capabilities = capabilities)

        val timestamp: Number = System.currentTimeMillis()

        val stateFlow = MutableStateFlow<ReduxState>(appState)
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getStateFlow() } doAnswer { stateFlow }
            on { getCurrentState() } doAnswer { appState }
        }

        val mockFloatingHeaderViewModel = mock<InfoHeaderViewModel> {}
        val mockParticipantGridViewModel = mock<ParticipantGridViewModel> {}

        val mockControlBarViewModel = mock<ControlBarViewModel> {}
        val mockConfirmLeaveOverlayViewModel = mock<LeaveConfirmViewModel> {}
        val mockLobbyHeaderViewModel = mock<LobbyHeaderViewModel> {}
        val mockLobbyErrorHeaderViewModel = mock<LobbyErrorHeaderViewModel> {}
        val mockParticipantListViewModel = mock<ParticipantListViewModel> {}
        val mockAudioDeviceListViewModel = mock<AudioDeviceListViewModel>()
        val mockBannerViewModel = mock<BannerViewModel>()
        val mockWaitingLobbyOverlayViewModel = mock<WaitingLobbyOverlayViewModel>()
        val mockConnectingOverlayViewModel = mock<ConnectingOverlayViewModel>()
        val mockOnHoldOverlayViewModel = mock<OnHoldOverlayViewModel>()
        val mockMoreCallOptionsListViewModel = mock<MoreCallOptionsListViewModel>()
        val mockNetworkManager = mock<NetworkManager>()
        val mockLocalParticipantViewModel = mock<LocalParticipantViewModel> { }
        val mockToastNotificationViewModel = mock<ToastNotificationViewModel>()
        val mockUpperMessageBarNotificationLayoutViewModel = mock<UpperMessageBarNotificationLayoutViewModel>()
        val mockParticipantMenuViewModel = mock<ParticipantMenuViewModel>()
        val mockCaptionsListViewModel = mock<CaptionsListViewModel>()
        val mockCaptionsLanguageSelectionListViewModel = mock<CaptionsLanguageSelectionListViewModel>()
        val mockCaptionsViewModel = mock<CaptionsViewModel>()

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
            on { connectingOverlayViewModel } doAnswer { mockConnectingOverlayViewModel }
            on { onHoldOverlayViewModel } doAnswer { mockOnHoldOverlayViewModel }
            on { moreCallOptionsListViewModel } doAnswer { mockMoreCallOptionsListViewModel }
            on { lobbyHeaderViewModel } doAnswer { mockLobbyHeaderViewModel }
            on { lobbyErrorHeaderViewModel } doAnswer { mockLobbyErrorHeaderViewModel }
            on { toastNotificationViewModel } doAnswer { mockToastNotificationViewModel }
            on { upperMessageBarNotificationLayoutViewModel } doAnswer { mockUpperMessageBarNotificationLayoutViewModel }
            on { participantMenuViewModel } doAnswer { mockParticipantMenuViewModel }
            on { captionsListViewModel } doAnswer { mockCaptionsListViewModel }
            on { captionsLanguageSelectionListViewModel } doAnswer { mockCaptionsLanguageSelectionListViewModel }
            on { captionsViewModel } doAnswer { mockCaptionsViewModel }
        }

        val callingViewModel = CallingViewModel(
            mockAppStore,
            mockCallingViewModelProvider,
            mockNetworkManager,
            callScreenOptions = CallCompositeCallScreenOptions().setControlBarOptions(
                CallCompositeCallScreenControlBarOptions().setLeaveCallConfirmation(
                    CallCompositeLeaveCallConfirmationMode.ALWAYS_ENABLED
                )
            ),
            false,
            CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
            capabilitiesManager = CapabilitiesManager(CallType.TEAMS_MEETING)
        )

        val newState = AppReduxState("", false, false, false)
        newState.lifecycleState = LifecycleState(LifecycleStatus.FOREGROUND)
        newState.localParticipantState = getLocalUserState(capabilities = capabilities)
        newState.callState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = false,
            isTranscribing = false
        )
        newState.remoteParticipantState = RemoteParticipantsState(
            participantMap,
            timestamp,
            listOf(),
            0,
            lobbyErrorCode = null,
            totalParticipantCount = participantMap.count(),
        )

        // act
        val flowJob = launch {
            callingViewModel.init(this)
        }
        stateFlow.emit(newState)

        // assert
        verify(
            mockParticipantListViewModel,
            times(1)
        ).update(
            argThat { map -> map.size == 3 },
            argThat { status -> status == newState.localParticipantState },
            argThat { value -> value.status == VisibilityStatus.VISIBLE },
            argThat { value -> value == showLobby },
            argThat { value -> value == 2 }
        )
        verify(
            mockLobbyHeaderViewModel,
            times(1)
        ).update(
            argThat { status -> status == CallingStatus.CONNECTED },
            argThat { map -> if (!showLobby) map.isEmpty() else map.size == 1 },
            argThat { value -> value == showLobby }
        )

        verify(
            mockLobbyErrorHeaderViewModel,
            times(1)
        ).update(
            argThat { status -> status == CallingStatus.CONNECTED },
            argThat { value -> value == null },
            argThat { value -> value == showLobby }
        )

        verify(
            mockParticipantListViewModel,
            times(1)
        ).init(
            argThat { map -> map.isEmpty() },
            argThat { status -> status == newState.localParticipantState },
            argThat { value -> value == showLobby },
            any(),
            any(),
        )
        verify(
            mockLobbyHeaderViewModel,
            times(1)
        ).init(
            argThat { status -> status == CallingStatus.NONE },
            argThat { map -> map.isEmpty() },
            argThat { value -> value == showLobby }
        )

        verify(
            mockLobbyErrorHeaderViewModel,
            times(1)
        ).init(
            argThat { status -> status == CallingStatus.NONE },
            argThat { value -> value == null },
            argThat { value -> value == showLobby }
        )

        flowJob.cancel()
    }

    private suspend fun TestScope.callViewOptionsTests(
        participantMap: Map<String, ParticipantInfoModel>,
        expectedParticipantCountOnGridView: Int,
        expectedParticipantCountOnFloatingHeader: Int,
        expectedParticipantCountOnParticipantList: Int
    ) {
        // arrange
        val appState = AppReduxState("", false, false, false)
        appState.localParticipantState = getLocalUserState()

        val timestamp: Number = System.currentTimeMillis()

        val stateFlow = MutableStateFlow<ReduxState>(appState)
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getStateFlow() } doAnswer { stateFlow }
            on { getCurrentState() } doAnswer { appState }
        }
        val lobbyParticipantCount = expectedParticipantCountOnParticipantList - expectedParticipantCountOnGridView

        val mockFloatingHeaderViewModel = mock<InfoHeaderViewModel> {}
        val mockParticipantListViewModel = mock<ParticipantListViewModel>()
        val mockParticipantGridViewModel = mock<ParticipantGridViewModel> {}

        val mockControlBarViewModel = mock<ControlBarViewModel> {
            on { update(any(), any(), any(), any(), any(), any(), any(), any(),) } doAnswer { }
        }
        val mockConfirmLeaveOverlayViewModel = mock<LeaveConfirmViewModel> {}
        val mockLocalParticipantViewModel = mock<LocalParticipantViewModel> {
            on { update(any(), any(), any(), any(), any(), any(), any(), any(), any()) } doAnswer { }
        }
        val mockLobbyHeaderViewModel = mock<LobbyHeaderViewModel> {
            on { update(any(), any(), any()) } doAnswer { }
        }
        val mockLobbyErrorHeaderViewModel = mock<LobbyErrorHeaderViewModel> {
            on { update(CallingStatus.CONNECTED, null, true) } doAnswer { }
        }
        val mockAudioDeviceListViewModel = mock<AudioDeviceListViewModel>()
        val mockBannerViewModel = mock<BannerViewModel>()
        val mockWaitingLobbyOverlayViewModel = mock<WaitingLobbyOverlayViewModel>()
        val mockConnectingOverlayViewModel = mock<ConnectingOverlayViewModel>()
        val mockOnHoldOverlayViewModel = mock<OnHoldOverlayViewModel>()
        val mockMoreCallOptionsListViewModel = mock<MoreCallOptionsListViewModel>()
        val mockNetworkManager = mock<NetworkManager>()
        val mockToastNotificationViewModel = mock<ToastNotificationViewModel>()
        val mockUpperMessageBarNotificationLayoutViewModel = mock<UpperMessageBarNotificationLayoutViewModel>()
        val mockParticipantMenuViewModel = mock<ParticipantMenuViewModel>()
        val mockCaptionsListViewModel = mock<CaptionsListViewModel>()
        val mockCaptionsLanguageSelectionListViewModel = mock<CaptionsLanguageSelectionListViewModel>()
        val mockCaptionsViewModel = mock<CaptionsViewModel>()

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
            on { connectingOverlayViewModel } doAnswer { mockConnectingOverlayViewModel }
            on { onHoldOverlayViewModel } doAnswer { mockOnHoldOverlayViewModel }
            on { moreCallOptionsListViewModel } doAnswer { mockMoreCallOptionsListViewModel }
            on { lobbyHeaderViewModel } doAnswer { mockLobbyHeaderViewModel }
            on { lobbyErrorHeaderViewModel } doAnswer { mockLobbyErrorHeaderViewModel }
            on { toastNotificationViewModel } doAnswer { mockToastNotificationViewModel }
            on { upperMessageBarNotificationLayoutViewModel } doAnswer { mockUpperMessageBarNotificationLayoutViewModel }
            on { participantMenuViewModel } doAnswer { mockParticipantMenuViewModel }
            on { captionsListViewModel } doAnswer { mockCaptionsListViewModel }
            on { captionsLanguageSelectionListViewModel } doAnswer { mockCaptionsLanguageSelectionListViewModel }
            on { captionsViewModel } doAnswer { mockCaptionsViewModel }
        }

        val callingViewModel = CallingViewModel(
            mockAppStore,
            mockCallingViewModelProvider,
            mockNetworkManager,
            callScreenOptions = CallCompositeCallScreenOptions().setControlBarOptions(
                CallCompositeCallScreenControlBarOptions().setLeaveCallConfirmation(
                    CallCompositeLeaveCallConfirmationMode.ALWAYS_DISABLED
                )
            ),
            false,
            CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
            capabilitiesManager = CapabilitiesManager(CallType.GROUP_CALL)
        )

        val newState = AppReduxState("", false, false, false)
        newState.lifecycleState = LifecycleState(LifecycleStatus.FOREGROUND)
        newState.localParticipantState = getLocalUserState()
        newState.callState = CallingState(
            CallingStatus.CONNECTED,
            isRecording = false,
            isTranscribing = false
        )
        newState.remoteParticipantState = RemoteParticipantsState(
            participantMap,
            timestamp,
            listOf(),
            0,
            lobbyErrorCode = null,
            totalParticipantCount = participantMap.size,
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
            argThat { count -> count == expectedParticipantCountOnFloatingHeader },
            any()
        )
        verify(
            mockParticipantListViewModel,
            times(1)
        ).update(argThat { map -> map.size == expectedParticipantCountOnParticipantList }, any(), any(), any(), any(),)
        verify(
            mockLobbyHeaderViewModel,
            times(1)
        ).update(argThat { status -> status == CallingStatus.CONNECTED }, argThat { map -> map.size == lobbyParticipantCount }, argThat { value -> value == true })

        verify(mockBannerViewModel, times(1)).update(any(), any())
        verify(mockControlBarViewModel, times(2)).update(
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
        )
        verify(mockLocalParticipantViewModel, times(2)).update(
            any(), any(), any(), any(), any(), any(), any(), any(), any()
        )
        verify(
            mockLobbyErrorHeaderViewModel,
            times(1)
        ).update(CallingStatus.CONNECTED, null, true)

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
        false, isSpeaking,
        participantStatus,
        screenShareVideoStreamModel,
        cameraVideoStreamModel,
        modifiedTimestamp,
    )

    private fun getLocalUserState(
        localParticipantRole: ParticipantRole = ParticipantRole.PRESENTER,
        capabilities: Set<ParticipantCapabilityType> = setOf(ParticipantCapabilityType.TURN_VIDEO_ON, ParticipantCapabilityType.UNMUTE_MICROPHONE)
    ) = LocalUserState(
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
        "test",
        localParticipantRole = localParticipantRole,
        capabilities = capabilities,
    )

    @Test
    @ExperimentalCoroutinesApi
    fun callingViewModel_displayLeaveCallDialogOn_doNotCallExitAction() {

        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false, false)
            appState.localParticipantState = getLocalUserState()
            val stateFlow = MutableStateFlow<ReduxState>(appState)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doAnswer { stateFlow }
                on { getCurrentState() } doAnswer { appState }
            }
            val mockParticipantGridViewModel = mock<ParticipantGridViewModel> {
            }

            val mockControlBarViewModel = mock<ControlBarViewModel> {
                on { update(any(), any(), any(), any(), any(), any(), any(), any(),) } doAnswer { }
            }

            val mockConfirmLeaveOverlayViewModel = mock<LeaveConfirmViewModel> {}

            val mockLocalParticipantViewModel = mock<LocalParticipantViewModel> {
                on { update(any(), any(), any(), any(), any(), any(), any(), any(), any()) } doAnswer { }
            }

            val mockFloatingHeaderViewModel = mock<InfoHeaderViewModel> {}

            val mockAudioDeviceListViewModel = mock<AudioDeviceListViewModel>()

            val mockParticipantListViewModel = mock<ParticipantListViewModel>()

            val mockBannerViewModel = mock<BannerViewModel>()

            val mockWaitingLobbyOverlayViewModel = mock<WaitingLobbyOverlayViewModel>()
            val mockConnectingOverlayViewModel = mock<ConnectingOverlayViewModel>()

            val mockOnHoldOverlayViewModel = mock<OnHoldOverlayViewModel>()
            val mockMoreCallOptionsListViewModel = mock<MoreCallOptionsListViewModel>()

            val mockToastNotificationViewModel = mock<ToastNotificationViewModel>()
            val mockUpperMessageBarNotificationLayoutViewModel = mock<UpperMessageBarNotificationLayoutViewModel>()

            val mockNetworkManager = mock<NetworkManager>()
            val mockLobbyHeaderViewModel = mock<LobbyHeaderViewModel>()
            val mockLobbyErrorHeaderViewModel = mock<LobbyErrorHeaderViewModel>()
            val mockParticipantMenuViewModel = mock<ParticipantMenuViewModel>()
            val mockCaptionsListViewModel = mock<CaptionsListViewModel>()
            val mockCaptionsLanguageSelectionListViewModel = mock<CaptionsLanguageSelectionListViewModel>()
            val mockCaptionsViewModel = mock<CaptionsViewModel>()

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
                on { connectingOverlayViewModel } doAnswer { mockConnectingOverlayViewModel }
                on { onHoldOverlayViewModel } doAnswer { mockOnHoldOverlayViewModel }
                on { moreCallOptionsListViewModel } doAnswer { mockMoreCallOptionsListViewModel }
                on { lobbyHeaderViewModel } doAnswer { mockLobbyHeaderViewModel }
                on { lobbyErrorHeaderViewModel } doAnswer { mockLobbyErrorHeaderViewModel }
                on { toastNotificationViewModel } doAnswer { mockToastNotificationViewModel }
                on { upperMessageBarNotificationLayoutViewModel } doAnswer { mockUpperMessageBarNotificationLayoutViewModel }
                on { participantMenuViewModel } doAnswer { mockParticipantMenuViewModel }
                on { captionsListViewModel } doAnswer { mockCaptionsListViewModel }
                on { captionsLanguageSelectionListViewModel } doAnswer { mockCaptionsLanguageSelectionListViewModel }
                on { captionsViewModel } doAnswer { mockCaptionsViewModel }
            }

            val callingViewModel = CallingViewModel(
                mockAppStore,
                mockCallingViewModelProvider,
                mockNetworkManager,
                callScreenOptions = CallCompositeCallScreenOptions().setControlBarOptions(
                    CallCompositeCallScreenControlBarOptions().setLeaveCallConfirmation(
                        CallCompositeLeaveCallConfirmationMode.ALWAYS_ENABLED
                    )
                ),
                false,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                capabilitiesManager = CapabilitiesManager(CallType.GROUP_CALL)
            )

            // act
            val flowJob = launch {
                callingViewModel.init(this)
            }
            callingViewModel.requestCallEnd()

            // assert
            verify(mockConfirmLeaveOverlayViewModel, times(1)).requestExitConfirmation()
            verify(mockAppStore, times(0)).dispatch(
                argThat { action ->
                    action is NavigationAction.Exit
                }
            )
            flowJob.cancel()
        }
    }

    @Test
    @ExperimentalCoroutinesApi
    fun callingViewModel_displayLeaveCallDialogOff_callExitAction() {

        runScopedTest {
            // arrange
            val appState = AppReduxState("", false, false, false)
            appState.localParticipantState = getLocalUserState()
            val stateFlow = MutableStateFlow<ReduxState>(appState)
            val mockAppStore = mock<AppStore<ReduxState>> {
                on { getStateFlow() } doAnswer { stateFlow }
                on { getCurrentState() } doAnswer { appState }
            }
            val mockParticipantGridViewModel = mock<ParticipantGridViewModel> {
            }

            val mockControlBarViewModel = mock<ControlBarViewModel> {
                on { update(any(), any(), any(), any(), any(), any(), any(), any(),) } doAnswer { }
            }

            val mockConfirmLeaveOverlayViewModel = mock<LeaveConfirmViewModel> {}

            val mockLocalParticipantViewModel = mock<LocalParticipantViewModel> {
                on { update(any(), any(), any(), any(), any(), any(), any(), any(), any()) } doAnswer { }
            }

            val mockFloatingHeaderViewModel = mock<InfoHeaderViewModel> {}

            val mockAudioDeviceListViewModel = mock<AudioDeviceListViewModel>()

            val mockParticipantListViewModel = mock<ParticipantListViewModel>()

            val mockBannerViewModel = mock<BannerViewModel>()

            val mockWaitingLobbyOverlayViewModel = mock<WaitingLobbyOverlayViewModel>()
            val mockConnectingOverlayViewModel = mock<ConnectingOverlayViewModel>()

            val mockOnHoldOverlayViewModel = mock<OnHoldOverlayViewModel>()
            val mockMoreCallOptionsListViewModel = mock<MoreCallOptionsListViewModel>()

            val mockToastNotificationViewModel = mock<ToastNotificationViewModel>()
            val mockUpperMessageBarNotificationLayoutViewModel = mock<UpperMessageBarNotificationLayoutViewModel>()

            val mockNetworkManager = mock<NetworkManager>()
            val mockLobbyHeaderViewModel = mock<LobbyHeaderViewModel>()
            val mockLobbyErrorHeaderViewModel = mock<LobbyErrorHeaderViewModel>()
            val mockParticipantMenuViewModel = mock<ParticipantMenuViewModel>()
            val mockCaptionsListViewModel = mock<CaptionsListViewModel>()
            val mockCaptionsLanguageSelectionListViewModel = mock<CaptionsLanguageSelectionListViewModel>()
            val mockCaptionsViewModel = mock<CaptionsViewModel>()

            val mockCallingViewModelProvider = mock<CallingViewModelFactory> {
                on { participantGridViewModel } doAnswer { mockParticipantGridViewModel }
                on { controlBarViewModel } doAnswer { mockControlBarViewModel }
                on { confirmLeaveOverlayViewModel } doAnswer { LeaveConfirmViewModel(mockAppStore) }
                on { localParticipantViewModel } doAnswer { mockLocalParticipantViewModel }
                on { floatingHeaderViewModel } doAnswer { mockFloatingHeaderViewModel }
                on { audioDeviceListViewModel } doAnswer { mockAudioDeviceListViewModel }
                on { participantListViewModel } doAnswer { mockParticipantListViewModel }
                on { bannerViewModel } doAnswer { mockBannerViewModel }
                on { waitingLobbyOverlayViewModel } doAnswer { mockWaitingLobbyOverlayViewModel }
                on { connectingOverlayViewModel } doAnswer { mockConnectingOverlayViewModel }
                on { onHoldOverlayViewModel } doAnswer { mockOnHoldOverlayViewModel }
                on { moreCallOptionsListViewModel } doAnswer { mockMoreCallOptionsListViewModel }
                on { lobbyHeaderViewModel } doAnswer { mockLobbyHeaderViewModel }
                on { lobbyErrorHeaderViewModel } doAnswer { mockLobbyErrorHeaderViewModel }
                on { toastNotificationViewModel } doAnswer { mockToastNotificationViewModel }
                on { upperMessageBarNotificationLayoutViewModel } doAnswer { mockUpperMessageBarNotificationLayoutViewModel }
                on { participantMenuViewModel } doAnswer { mockParticipantMenuViewModel }
                on { captionsListViewModel } doAnswer { mockCaptionsListViewModel }
                on { captionsLanguageSelectionListViewModel } doAnswer { mockCaptionsLanguageSelectionListViewModel }
                on { captionsViewModel } doAnswer { mockCaptionsViewModel }
            }

            val callingViewModel = CallingViewModel(
                mockAppStore,
                mockCallingViewModelProvider,
                mockNetworkManager,
                callScreenOptions = CallCompositeCallScreenOptions().setControlBarOptions(
                    CallCompositeCallScreenControlBarOptions().setLeaveCallConfirmation(
                        CallCompositeLeaveCallConfirmationMode.ALWAYS_DISABLED
                    )
                ),
                false,
                CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
                capabilitiesManager = CapabilitiesManager(CallType.GROUP_CALL)
            )

            // act
            val flowJob = launch {
                callingViewModel.init(this)
            }
            callingViewModel.requestCallEnd()

            // assert
            verify(mockConfirmLeaveOverlayViewModel, times(0)).requestExitConfirmation()
            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is CallingAction.CallEndRequested
                }
            )
            flowJob.cancel()
        }
    }
}
