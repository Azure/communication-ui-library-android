// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.factories

import com.azure.android.communication.ui.configuration.LocalParticipantConfiguration
import com.azure.android.communication.ui.configuration.LocalizationProvider
import com.azure.android.communication.ui.presentation.fragment.calling.banner.BannerViewModel
import com.azure.android.communication.ui.presentation.fragment.calling.controlbar.ControlBarViewModel
import com.azure.android.communication.ui.presentation.fragment.calling.hangup.ConfirmLeaveOverlayViewModel
import com.azure.android.communication.ui.presentation.fragment.calling.header.InfoHeaderViewModel
import com.azure.android.communication.ui.presentation.fragment.calling.lobby.LobbyOverlayViewModel
import com.azure.android.communication.ui.presentation.fragment.calling.localuser.LocalParticipantViewModel
import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.ParticipantGridViewModel
import com.azure.android.communication.ui.presentation.fragment.calling.participantlist.ParticipantListViewModel
import com.azure.android.communication.ui.presentation.fragment.common.audiodevicelist.AudioDeviceListViewModel
import com.azure.android.communication.ui.redux.Store
import com.azure.android.communication.ui.redux.state.ReduxState

internal class CallingViewModelFactory(
    private val store: Store<ReduxState>,
    private val localizationProvider: LocalizationProvider,
    private val participantGridCellViewModelFactory: ParticipantGridCellViewModelFactory,
    private val localParticipantConfig: LocalParticipantConfiguration?,
) {

    private val participantGridViewModel by lazy {
        ParticipantGridViewModel(participantGridCellViewModelFactory, localizationProvider)
    }

    private val controlBarViewModel by lazy {
        ControlBarViewModel(
            store::dispatch,
            localizationProvider
        )
    }

    // %1 people
    private val floatingHeaderViewModel by lazy {
        InfoHeaderViewModel(localizationProvider)
    }

    private val audioDeviceListViewModel by lazy {
        AudioDeviceListViewModel(store::dispatch, localizationProvider)
    }

    private val confirmLeaveOverlayViewModel by lazy {
        ConfirmLeaveOverlayViewModel(store::dispatch, localizationProvider)
    }

    private val localParticipantViewModel by lazy {
        LocalParticipantViewModel(store::dispatch, localizationProvider, localParticipantConfig)
    }

    private val participantListViewModel by lazy {
        ParticipantListViewModel(localizationProvider)
    }

    private val bannerViewModel by lazy {
        BannerViewModel(localizationProvider)
    }

    private val lobbyOverlayViewModel by lazy {
        LobbyOverlayViewModel(localizationProvider)
    }

    fun provideParticipantGridViewModel(): ParticipantGridViewModel {
        return participantGridViewModel
    }

    fun provideControlBarViewModel(): ControlBarViewModel {
        return controlBarViewModel
    }

    fun provideFloatingHeaderViewModel(): InfoHeaderViewModel {
        return floatingHeaderViewModel
    }

    fun provideAudioDeviceListViewModel(): AudioDeviceListViewModel {
        return audioDeviceListViewModel
    }

    fun provideConfirmLeaveOverlayViewModel(): ConfirmLeaveOverlayViewModel {
        return confirmLeaveOverlayViewModel
    }

    fun provideLocalParticipantViewModel(): LocalParticipantViewModel {
        return localParticipantViewModel
    }

    fun provideParticipantListViewModel(): ParticipantListViewModel {
        return participantListViewModel
    }

    fun provideBannerViewModel(): BannerViewModel {
        return bannerViewModel
    }

    fun provideLobbyOverlayViewModel(): LobbyOverlayViewModel {
        return lobbyOverlayViewModel
    }
}
