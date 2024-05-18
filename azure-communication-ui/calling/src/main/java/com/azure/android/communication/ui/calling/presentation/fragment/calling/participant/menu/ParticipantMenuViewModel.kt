// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.participant.menu

import com.azure.android.communication.ui.calling.models.ParticipantCapabilityType
import com.azure.android.communication.ui.calling.models.ParticipantRole
import com.azure.android.communication.ui.calling.presentation.manager.CapabilitiesManager
import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.ParticipantAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class ParticipantMenuViewModel(
    private val dispatch: (Action) -> Unit,
    private val capabilitiesManager: CapabilitiesManager,
) {
    private val displayMenuStateFlow = MutableStateFlow(false)
    private var userIdentifier: String? = null
    private val muteParticipantEnabledMutableFlow = MutableStateFlow(false)
    private val remoteParticipantEnabledMutableFlow = MutableStateFlow(false)

    val displayMenuFlow = displayMenuStateFlow.asStateFlow()
    var displayName: String? = null
    val muteParticipantEnabledFlow = muteParticipantEnabledMutableFlow.asStateFlow()


    fun init(capabilities: Set<ParticipantCapabilityType>, localParticipantRole: ParticipantRole?) {
        update(capabilities, localParticipantRole)
    }

    fun update(capabilities: Set<ParticipantCapabilityType>, localParticipantRole: ParticipantRole?) {
        remoteParticipantEnabledMutableFlow.value = capabilitiesManager.hasCapability(
            capabilities,
            localParticipantRole,
            ParticipantCapabilityType.REMOVE_PARTICIPANT,
            )
    }

    fun displayParticipantMenu(
        userIdentifier: String,
        displayName: String?
    ) {
        this.userIdentifier = userIdentifier
        this.displayName = displayName
        displayMenuStateFlow.value = true
    }

    fun close() {
        displayMenuStateFlow.value = false
    }

    fun muteParticipant() {
        close()
    }

    fun removeParticipant() {
        userIdentifier?.let {
            dispatch(ParticipantAction.Remove(it))
        }
        close()
    }
}