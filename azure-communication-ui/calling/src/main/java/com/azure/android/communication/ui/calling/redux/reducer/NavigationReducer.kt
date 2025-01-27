// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.NavigationAction
import com.azure.android.communication.ui.calling.redux.state.NavigationState
import com.azure.android.communication.ui.calling.redux.state.NavigationStatus

internal interface NavigationReducer : Reducer<NavigationState>

internal class NavigationReducerImpl : NavigationReducer {
    override fun reduce(state: NavigationState, action: Action): NavigationState {
        return when (action) {
            is NavigationAction.Exit -> {
                state.copy(navigationState = NavigationStatus.EXIT)
            }
            is NavigationAction.CallLaunched, is NavigationAction.CallLaunchWithoutSetup -> {
                state.copy(navigationState = NavigationStatus.IN_CALL)
            }
            is NavigationAction.SetupLaunched -> {
                state.copy(navigationState = NavigationStatus.SETUP)
            }
            is NavigationAction.HideSupportForm -> {
                state.copy(supportVisible = false)
            }
            is NavigationAction.ShowSupportForm -> {
                state.copy(supportVisible = true)
            }
            is NavigationAction.ShowCaptionsOptions -> {
                state.copy(showCaptionsToggleUI = true)
            }
            is NavigationAction.CloseCaptionsOptions -> {
                state.copy(showCaptionsToggleUI = false)
            }
            is NavigationAction.ShowSupportedSpokenLanguagesOptions -> {
                state.copy(showSupportedSpokenLanguagesSelection = true)
            }
            is NavigationAction.ShowSupportedCaptionLanguagesOptions -> {
                state.copy(showSupportedCaptionLanguagesSelections = true)
            }
            is NavigationAction.HideSupportedLanguagesOptions -> {
                state.copy(showSupportedSpokenLanguagesSelection = false, showSupportedCaptionLanguagesSelections = false)
            }
            is NavigationAction.ShowMoreMenu -> {
                state.copy(showMoreMenu = true)
            }
            is NavigationAction.CloseMoreMenu -> {
                state.copy(showMoreMenu = false)
            }
            else -> state
        }
    }
}
