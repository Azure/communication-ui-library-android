// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.captions

import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.ui.calling.presentation.manager.AvatarViewManager
import com.azure.android.communication.ui.calling.presentation.manager.CaptionsDataManager
import com.azure.android.communication.ui.calling.redux.state.CaptionsState
import com.azure.android.communication.ui.calling.redux.state.CaptionsStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class CaptionsViewModel {
    private lateinit var isVisibleMutableFlow: MutableStateFlow<Boolean>
    private lateinit var captionsStartInProgressStateMutableFlow: MutableStateFlow<Boolean>

    private val captionsData = mutableListOf<CaptionsRttEntryModel>()
    private val onLastCaptionsDataUpdatedMutableStateFlow = MutableStateFlow<CaptionsRttEntryModel?>(null)
    private val onNewCaptionsDataAddedMutableStateFlow = MutableStateFlow<CaptionsRttEntryModel?>(null)

    val captionsAndRttDataCache: List<CaptionsRttEntryModel> = captionsData
    val onLastCaptionsDataUpdatedStateFlow: StateFlow<CaptionsRttEntryModel?> = onLastCaptionsDataUpdatedMutableStateFlow
    val onNewCaptionsDataAddedStateFlow: StateFlow<CaptionsRttEntryModel?> = onNewCaptionsDataAddedMutableStateFlow

    val isVisibleFlow: StateFlow<Boolean>
        get() = isVisibleMutableFlow
    val captionsStartProgressStateFlow: StateFlow<Boolean>
        get() = captionsStartInProgressStateMutableFlow

    fun update(
        captionsState: CaptionsState,
        isVisible: Boolean,
    ) {
        isVisibleMutableFlow.value = isVisible
        captionsStartInProgressStateMutableFlow.value = canShowCaptionsStartInProgressUI(captionsState)
    }

    fun init(
        coroutineScope: CoroutineScope,
        captionsState: CaptionsState,
        isVisible: Boolean,
        captionsDataManager: CaptionsDataManager,
        localParticipantIdentifier: CommunicationIdentifier?,
        avatarViewManager: AvatarViewManager,
    ) {
        isVisibleMutableFlow = MutableStateFlow(isVisible)
        captionsStartInProgressStateMutableFlow = MutableStateFlow(canShowCaptionsStartInProgressUI(captionsState))

        captionsDataManager.captionsDataCache.let { data ->
            captionsData.addAll(data.map { it.into(avatarViewManager, localParticipantIdentifier) })
        }

        captionsDataManager.resetFlows()

        coroutineScope.launch {
            captionsDataManager.getOnLastCaptionsDataUpdatedStateFlow().collect { data ->
                data?.let {
                    onLastCaptionsDataUpdatedMutableStateFlow.value = it.into(avatarViewManager, localParticipantIdentifier)
                }
            }
        }
        coroutineScope.launch {
            captionsDataManager.getOnNewCaptionsDataAddedStateFlow().collect { data ->
                data?.let {
                    onNewCaptionsDataAddedMutableStateFlow.value = it.into(avatarViewManager, localParticipantIdentifier)
                }
            }
        }
    }

    private fun canShowCaptionsStartInProgressUI(
        captionsState: CaptionsState
    ) = captionsState.status == CaptionsStatus.START_REQUESTED
}
