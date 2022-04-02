// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.manager

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.widget.Toast
import com.azure.android.communication.ui.redux.Store
import com.azure.android.communication.ui.redux.state.CallingStatus
import com.azure.android.communication.ui.redux.state.ReduxState
import com.google.android.material.snackbar.Snackbar
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
                        announce(activity, message)

                    }
                }
            }
            lastState = newState
        }
    }

    private fun announce(activity: Activity, message: String) {

        val manager = activity.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        if (manager.isEnabled) {
            val toast = Toast.makeText(activity, message, Toast.LENGTH_SHORT)
            // We move this toast way off the screen so it's not visible
            toast.setGravity(Gravity.TOP or Gravity.AXIS_CLIP, -10000,-10000)
            toast.show()
        }

    }
}

// Accessibility Hook
//
// shouldTrigger -> detect if we should announce something
// message -> get the text tp read
internal abstract class AccessibilityHook {
    abstract fun shouldTrigger(lastState: ReduxState, newState: ReduxState): Boolean
    abstract fun message(lastState: ReduxState, newState: ReduxState, context: Context): String
}


internal class ParticipantAddedOrRemovedHook : AccessibilityHook() {
    var callJoinTime = System.currentTimeMillis()
    override fun shouldTrigger(lastState: ReduxState, newState: ReduxState): Boolean {
        if (lastState.callState.callingStatus != CallingStatus.CONNECTED && newState.callState.callingStatus == CallingStatus.CONNECTED) {
            callJoinTime = System.currentTimeMillis()
            return false
        }
        val shouldRun = lastState.remoteParticipantState.participantMap.size != newState.remoteParticipantState.participantMap.size

        if (shouldRun && (System.currentTimeMillis() - callJoinTime) > 1000) {
            return true
        }

        return false
    }

    override fun message(lastState: ReduxState, newState: ReduxState, context: Context): String {
        val oldList = lastState.remoteParticipantState.participantMap.values
        val newList = newState.remoteParticipantState.participantMap.values

        val added = newList.filter { !oldList.contains(it) }
        val removed = oldList.filter { !newList.contains(it) }

        var result = ""
        if (added.size == 1) {
            result = "${added.first().displayName} joined the meeting"
        } else if (removed.size == 1) {
            result = "${removed.first().displayName} left the meeting"
        }
        return result
    }
}


internal class MeetingJoinedHook : AccessibilityHook() {
    override fun shouldTrigger(lastState: ReduxState, newState: ReduxState): Boolean {
        return (lastState.callState.callingStatus != CallingStatus.CONNECTED && newState.callState.callingStatus == CallingStatus.CONNECTED)
    }

    override fun message(lastState: ReduxState, newState: ReduxState, context: Context): String {
        return "Meeting Joined"
    }
}

internal val accessibilityHooks = listOf(
    MeetingJoinedHook(),
    ParticipantAddedOrRemovedHook(),

)
