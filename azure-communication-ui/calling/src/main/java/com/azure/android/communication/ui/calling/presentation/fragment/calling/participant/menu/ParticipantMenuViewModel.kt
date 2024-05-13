// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.participant.menu

import android.content.Context
import com.azure.android.communication.ui.calling.redux.action.Action
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class ParticipantMenuViewModel(
    private val dispatch: (Action) -> Unit,
) {
    private val displayMenuStateFlow = MutableStateFlow(false)

    var displayMenuFlow = displayMenuStateFlow.asStateFlow()

    fun init() {

    }

    fun displayParticipantMenuCallback(
        userIdentifier: String,
        displayName: String?
    ) {

    }
}