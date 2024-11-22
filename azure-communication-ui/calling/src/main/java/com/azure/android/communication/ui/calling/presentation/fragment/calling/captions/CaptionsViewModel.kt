// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.captions

import com.azure.android.communication.ui.calling.presentation.manager.CaptionsDataManager
import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.RttAction
import com.azure.android.communication.ui.calling.redux.state.CaptionsState
import com.azure.android.communication.ui.calling.redux.state.CaptionsStatus
import com.azure.android.communication.ui.calling.redux.state.DeviceConfigurationState
import com.azure.android.communication.ui.calling.redux.state.RttState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class CaptionsViewModel(
    private val dispatch: (Action) -> Unit,
    captionsDataManager: CaptionsDataManager,
) {
    private lateinit var isVisibleMutableFlow: MutableStateFlow<Boolean>
    private lateinit var isRttInputVisibleMutableFlow: MutableStateFlow<Boolean>
    private lateinit var captionsStartInProgressStateMutableFlow: MutableStateFlow<Boolean>
    private lateinit var softwareKeyboardStateMutableFlow: MutableStateFlow<Boolean>
    private lateinit var isMaximizedMutableFlow: MutableStateFlow<Boolean>

    val captionsAndRttData = captionsDataManager.captionsAndRttData
    val recordUpdatedAtPositionSharedFlow = captionsDataManager.recordUpdatedAtPositionSharedFlow
    val recordInsertedAtPositionSharedFlow = captionsDataManager.recordInsertedAtPositionSharedFlow
    val recordRemovedAtPositionSharedFlow = captionsDataManager.recordRemovedAtPositionSharedFlow

    val softwareKeyboardStateFlow: StateFlow<Boolean>
        get() = softwareKeyboardStateMutableFlow

    val isVisibleFlow: StateFlow<Boolean>
        get() = isVisibleMutableFlow
    val isMaximizedFlow: StateFlow<Boolean>
        get() = isMaximizedMutableFlow

    val isRttInputVisibleFlow: StateFlow<Boolean>
        get() = isRttInputVisibleMutableFlow
    val captionsStartProgressStateFlow: StateFlow<Boolean>
        get() = captionsStartInProgressStateMutableFlow

    fun update(
        captionsState: CaptionsState,
        rttState: RttState,
        isVisible: Boolean,
        deviceConfigurationState: DeviceConfigurationState,
    ) {
        isVisibleMutableFlow.value = isVisible
        isMaximizedMutableFlow.value = rttState.isMaximized
        isRttInputVisibleMutableFlow.value = rttState.isRttActive && rttState.isMaximized
        captionsStartInProgressStateMutableFlow.value = canShowCaptionsStartInProgressUI(captionsState)
        softwareKeyboardStateMutableFlow.value = deviceConfigurationState.isSoftwareKeyboardVisible
    }

    fun init(
        captionsState: CaptionsState,
        rttState: RttState,
        isVisible: Boolean,
        deviceConfigurationState: DeviceConfigurationState,
    ) {
        isVisibleMutableFlow = MutableStateFlow(isVisible)
        isMaximizedMutableFlow = MutableStateFlow(rttState.isMaximized)
        isRttInputVisibleMutableFlow = MutableStateFlow(rttState.isRttActive && rttState.isMaximized)
        captionsStartInProgressStateMutableFlow = MutableStateFlow(canShowCaptionsStartInProgressUI(captionsState))
        softwareKeyboardStateMutableFlow = MutableStateFlow(deviceConfigurationState.isSoftwareKeyboardVisible)
    }

    private fun canShowCaptionsStartInProgressUI(
        captionsState: CaptionsState
    ) = captionsState.status == CaptionsStatus.START_REQUESTED

    fun sendRttMessage(message: String, isFinal: Boolean) {
        dispatch(RttAction.SendRtt(message, isFinal))
    }

    fun maximizeCaptionsLayout() {
        dispatch(RttAction.UpdateMaximized(true))
    }

    fun minimizeCaptionsLayout() {
        dispatch(RttAction.UpdateMaximized(false))
    }
}
