// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.factories

import com.azure.android.communication.ui.calling.configuration.CallType
import com.azure.android.communication.ui.calling.presentation.fragment.calling.banner.BannerViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.ControlBarViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.hangup.LeaveConfirmViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.header.InfoHeaderViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.hold.OnHoldOverlayViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.lobby.LobbyErrorHeaderViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.lobby.LobbyHeaderViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.lobby.WaitingLobbyOverlayViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.localuser.LocalParticipantViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.participant.grid.ParticipantGridViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.participantlist.ParticipantListViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.common.audiodevicelist.AudioDeviceListViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.more.MoreCallOptionsListViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.connecting.overlay.ConnectingOverlayViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.notification.ToastNotificationViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.notification.UpperMessageBarNotificationLayoutViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.participant.menu.ParticipantMenuViewModel
import com.azure.android.communication.ui.calling.presentation.manager.CapabilitiesManager
import com.azure.android.communication.ui.calling.presentation.manager.DebugInfoManager
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.state.ReduxState

internal class CallingViewModelFactory(
    private val store: Store<ReduxState>,
    private val participantGridCellViewModelFactory: ParticipantGridCellViewModelFactory,
    private val maxRemoteParticipants: Int,
    private val debugInfoManager: DebugInfoManager,
    private val capabilitiesManager: CapabilitiesManager,
    private val showSupportFormOption: Boolean = false,
    private val enableMultitasking: Boolean,
    private val isTelecomManagerEnabled: Boolean = false,
    private val callType: CallType? = null
) : BaseViewModelFactory(store) {

    val moreCallOptionsListViewModel by lazy {
        MoreCallOptionsListViewModel(
            debugInfoManager,
            showSupportFormOption = showSupportFormOption,
            store::dispatch
        )
    }

    val participantGridViewModel by lazy {
        ParticipantGridViewModel(participantGridCellViewModelFactory, maxRemoteParticipants)
    }

    val controlBarViewModel by lazy {
        ControlBarViewModel(
            store::dispatch,
            capabilitiesManager,
        )
    }

    val floatingHeaderViewModel by lazy {
        InfoHeaderViewModel(enableMultitasking)
    }

    val lobbyHeaderViewModel by lazy {
        LobbyHeaderViewModel()
    }

    val upperMessageBarNotificationLayoutViewModel by lazy {
        UpperMessageBarNotificationLayoutViewModel(store::dispatch)
    }

    val toastNotificationViewModel by lazy {
        ToastNotificationViewModel(store::dispatch)
    }

    val audioDeviceListViewModel by lazy {
        AudioDeviceListViewModel(store::dispatch)
    }

    val confirmLeaveOverlayViewModel by lazy {
        LeaveConfirmViewModel(store)
    }

    val localParticipantViewModel by lazy {
        LocalParticipantViewModel(
            store::dispatch,
        )
    }

    val participantListViewModel by lazy {
        ParticipantListViewModel(store::dispatch)
    }

    val participantMenuViewModel by lazy {
        ParticipantMenuViewModel(
            store::dispatch,
            capabilitiesManager,
        )
    }

    val bannerViewModel by lazy {
        BannerViewModel()
    }

    val waitingLobbyOverlayViewModel by lazy {
        WaitingLobbyOverlayViewModel()
    }

    val connectingOverlayViewModel by lazy {
        ConnectingOverlayViewModel(store::dispatch, isTelecomManagerEnabled, callType)
    }

    val onHoldOverlayViewModel by lazy {
        OnHoldOverlayViewModel { store.dispatch(it) }
    }

    val lobbyErrorHeaderViewModel by lazy { LobbyErrorHeaderViewModel(store::dispatch) }
}
