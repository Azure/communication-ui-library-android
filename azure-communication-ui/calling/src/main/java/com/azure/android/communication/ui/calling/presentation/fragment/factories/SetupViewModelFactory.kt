// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.factories

import android.content.Context
import android.media.AudioManager
import com.azure.android.communication.ui.calling.presentation.DependencyInjectionContainerHolder
import com.azure.android.communication.ui.calling.presentation.fragment.common.audiodevicelist.AudioDeviceListViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.setup.components.JoinCallButtonHolderViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.setup.components.PreviewAreaViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.setup.components.SetupControlBarViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.setup.components.SetupGradientViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.setup.components.SetupParticipantAvatarViewModel
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.state.ReduxState

internal class SetupViewModelFactory(
    private val store: Store<ReduxState>,
    private val context: Context,
    private val instanceId: Int,
) : BaseViewModelFactory(store) {

    val audioDeviceListViewModel by lazy {
        AudioDeviceListViewModel(store::dispatch)
    }

    val previewAreaViewModel by lazy {
        PreviewAreaViewModel(store::dispatch)
    }

    val setupControlBarViewModel by lazy {
        SetupControlBarViewModel(store::dispatch)
    }

    val setupGradientViewModel by lazy {
        SetupGradientViewModel()
    }

    val participantAvatarViewModel by lazy {
        SetupParticipantAvatarViewModel()
    }

    val joinCallButtonHolderViewModel by lazy {
        JoinCallButtonHolderViewModel(
                store::dispatch,
                context.getSystemService(Context.AUDIO_SERVICE) as AudioManager,
                instanceId = instanceId,
        )
    }
}
