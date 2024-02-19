// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.setup.components

import android.content.Context
import android.util.AttributeSet
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantViewData
import com.microsoft.fluentui.persona.AvatarView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class SetupParticipantAvatarView(context: Context, attrs: AttributeSet? = null) :
    AvatarView(context, attrs) {
    fun start(
        viewLifecycleOwner: LifecycleOwner,
        viewModel: SetupParticipantAvatarViewModel,
        participantViewData: CallCompositeParticipantViewData?,
    ) {
        name = viewModel.getDisplayName()
        participantViewData?.let {
            it.avatarBitmap?.let { image ->
                avatarImageBitmap = image
                adjustViewBounds = true
                scaleType = it.scaleType
            }
            it.displayName?.let { displayName ->
                name = displayName
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getShouldDisplayAvatarViewStateFlow().collect {
                visibility = if (it) VISIBLE else GONE
            }
        }
    }
}
