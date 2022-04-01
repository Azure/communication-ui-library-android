// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.manager

import android.app.Activity
import android.content.Context
import android.view.View
import com.azure.android.communication.ui.redux.Store
import com.azure.android.communication.ui.redux.state.ReduxState
import kotlinx.coroutines.flow.collect


internal class AccessibilityAnnouncementManager(
    private val store: Store<ReduxState>,
) {

    private var lastState: ReduxState = store.getCurrentState()

    suspend fun start(activity: Activity) {
        val rootView = activity.window.decorView.findViewById<View>(android.R.id.content)
        store.getStateFlow().collect { newState ->
            accessibilityHooks.forEach {
                if (it.shouldTrigger(lastState, newState)) {
                    val message = it.message(lastState, newState, rootView.context)
                    if (message.isNotBlank()) {
                        rootView.announceForAccessibility(message)
                    }
                }
            }
            lastState = newState
        }
    }
}

// Accessibility Hook
//
// shouldTrigger -> detect if we should announce something
// message -> get the text tp read
internal data class AccessibilityHook(
    val shouldTrigger : (lastState: ReduxState, newState: ReduxState) -> Boolean,
    val message : (lastState: ReduxState, newState: ReduxState, context: Context) -> String
)

// This hook checks if a participant is added or removed and announces that
internal val participantAddedOrRemovedHook = AccessibilityHook(
    shouldTrigger = { oldState, newState ->
        oldState.remoteParticipantState.participantMap.size != newState.remoteParticipantState.participantMap.size
    },
    message = { oldState, newState, _ ->
        val oldList = oldState.remoteParticipantState.participantMap.values
        val newList = newState.remoteParticipantState.participantMap.values

        val added = newList.filter { !oldList.contains(it) }
        val removed = oldList.filter { !newList.contains(it) }

        var result = ""
        if (added.size == 1) {
            result = "${added.first().displayName} joined the meeting"
        } else if (removed.size == 1) {
            result = "${removed.first().displayName} left the meeting"
        }
        result
    },
)

internal val accessibilityHooks = listOf(participantAddedOrRemovedHook)
