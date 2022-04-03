// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.factories

import com.azure.android.communication.ui.configuration.LocalParticipantConfiguration
import com.azure.android.communication.ui.configuration.LocalizationProvider
import com.azure.android.communication.ui.presentation.fragment.common.audiodevicelist.AudioDeviceListViewModel
import com.azure.android.communication.ui.presentation.fragment.setup.components.ErrorInfoViewModel
import com.azure.android.communication.ui.presentation.fragment.setup.components.JoinCallButtonHolderViewModel
import com.azure.android.communication.ui.presentation.fragment.setup.components.PermissionWarningViewModel
import com.azure.android.communication.ui.presentation.fragment.setup.components.PreviewAreaViewModel
import com.azure.android.communication.ui.presentation.fragment.setup.components.SetupControlBarViewModel
import com.azure.android.communication.ui.presentation.fragment.setup.components.SetupGradientViewModel
import com.azure.android.communication.ui.presentation.fragment.setup.components.SetupParticipantAvatarViewModel
import com.azure.android.communication.ui.redux.Store
import com.azure.android.communication.ui.redux.state.ReduxState

internal class SetupViewModelFactory(
    private val store: Store<ReduxState>,
    private val localizationProvider: LocalizationProvider,
    private val localParticipantConfig: LocalParticipantConfiguration?
) {

    private val audioDeviceListViewModel by lazy {
        AudioDeviceListViewModel(store::dispatch, localizationProvider)
    }

    private val previewAreaViewModel by lazy {
        PreviewAreaViewModel(store::dispatch)
    }

    private val setupControlsViewModel by lazy {
        SetupControlBarViewModel(
            store::dispatch,
            localizationProvider
        )
    }

    private val warningsViewModel by lazy {
        PermissionWarningViewModel(
            store::dispatch,
            localizationProvider
        )
    }

    private val snackBarViewModel by lazy {
        ErrorInfoViewModel(localizationProvider)
    }

    private val setupGradientViewModel by lazy {
        SetupGradientViewModel()
    }

    private val participantAvatarViewModel by lazy {
        SetupParticipantAvatarViewModel(localParticipantConfig)
    }

    private val joinCallButtonHolderViewModel by lazy {
        JoinCallButtonHolderViewModel(
            store::dispatch,
            localizationProvider
        )
    }

    fun providePreviewAreaViewModel() = previewAreaViewModel

    fun provideAudioDeviceListViewModel() = audioDeviceListViewModel

    fun provideSetupControlsViewModel() = setupControlsViewModel

    fun provideWarningsViewModel() = warningsViewModel

    fun provideErrorInfoViewModel() = snackBarViewModel

    fun provideSetupGradientViewModel() = setupGradientViewModel

    fun provideParticipantAvatarViewModel() = participantAvatarViewModel

    fun provideJoinCallButtonHolderViewModel() = joinCallButtonHolderViewModel
}
