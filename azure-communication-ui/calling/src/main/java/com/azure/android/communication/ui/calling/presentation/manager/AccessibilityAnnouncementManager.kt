// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.manager

import android.app.Activity
import android.content.Context
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import com.azure.android.communication.ui.calling.implementation.R
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.CameraDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.CaptionsStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import kotlinx.coroutines.flow.collect

// Manager to hook into accessibility and provide announcements
// To add a Hook, extend AccessibilityHook and add to the "Hooks" list at the bottom of this file
// These hooks make announcements based on Redux State changes
internal class AccessibilityAnnouncementManager(
    private val store: Store<ReduxState>,
    private val accessibilityHooks: List<AccessibilityHook>
) {
    private lateinit var lastState: ReduxState

    suspend fun start(activity: Activity) {
        lastState = store.getCurrentState()
        store.getStateFlow().collect { newState ->
            accessibilityHooks.forEach {
                if (it.shouldTrigger(lastState, newState)) {
                    val message = it.message(lastState, newState, activity)
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
            val event = AccessibilityEvent.obtain()
            event.text.add(message)
            event.eventType = AccessibilityEvent.TYPE_ANNOUNCEMENT
            manager.sendAccessibilityEvent(event)
        }
    }
}

// Accessibility Hook
// shouldTrigger -> detect if we should announce something
// message -> get the text tp read
internal abstract class AccessibilityHook {
    abstract fun shouldTrigger(lastState: ReduxState, newState: ReduxState): Boolean
    abstract fun message(lastState: ReduxState, newState: ReduxState, context: Context): String
}

internal class ParticipantAddedOrRemovedHook : AccessibilityHook() {
    private var callJoinTime = System.currentTimeMillis()
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
            result = context.getString(R.string.azure_communication_ui_calling_accessibility_user_added, added.first().displayName)
        } else if (removed.size == 1) {
            result = context.getString(R.string.azure_communication_ui_calling_accessibility_user_left, removed.first().displayName)
        }
        // if (newState.remoteParticipantState.participantMap.isEmpty()) {
        // TODO: Add "Meeting is now empty"
        // }

        return result
    }
}

internal class MeetingJoinedHook : AccessibilityHook() {
    override fun shouldTrigger(lastState: ReduxState, newState: ReduxState) =
        (lastState.callState.callingStatus != CallingStatus.CONNECTED && newState.callState.callingStatus == CallingStatus.CONNECTED)

    override fun message(lastState: ReduxState, newState: ReduxState, context: Context) =
        context.getString(R.string.azure_communication_ui_calling_accessibility_meeting_connected)
}

internal class SwitchCameraStatusHook : AccessibilityHook() {
    override fun shouldTrigger(lastState: ReduxState, newState: ReduxState): Boolean =
        (lastState.localParticipantState.cameraState.device != newState.localParticipantState.cameraState.device)

    override fun message(lastState: ReduxState, newState: ReduxState, context: Context): String {
        return when (newState.localParticipantState.cameraState.device) {
            CameraDeviceSelectionStatus.FRONT -> context.getString(R.string.azure_communication_ui_calling_switch_camera_button_front)
            CameraDeviceSelectionStatus.BACK -> context.getString(R.string.azure_communication_ui_calling_switch_camera_button_back)
            else -> ""
        }
    }
}

internal class MicStatusHook : AccessibilityHook() {
    override fun shouldTrigger(lastState: ReduxState, newState: ReduxState): Boolean =
        (lastState.localParticipantState.audioState.operation != newState.localParticipantState.audioState.operation)

    override fun message(lastState: ReduxState, newState: ReduxState, context: Context): String {
        return when (newState.localParticipantState.audioState.operation) {
            AudioOperationalStatus.ON -> context.getString(R.string.azure_communication_ui_calling_setup_view_button_mic_on)
            AudioOperationalStatus.OFF -> context.getString(R.string.azure_communication_ui_calling_setup_view_button_mic_off)
            AudioOperationalStatus.PENDING -> ""
        }
    }
}

internal class CameraStatusHook : AccessibilityHook() {
    override fun shouldTrigger(lastState: ReduxState, newState: ReduxState): Boolean =
        (lastState.localParticipantState.cameraState.operation != newState.localParticipantState.cameraState.operation)

    override fun message(lastState: ReduxState, newState: ReduxState, context: Context): String {
        return when (newState.localParticipantState.cameraState.operation) {
            CameraOperationalStatus.ON -> context.getString(R.string.azure_communication_ui_calling_setup_view_button_video_on)
            CameraOperationalStatus.OFF -> context.getString(R.string.azure_communication_ui_calling_setup_view_button_video_off)
            else -> ""
        }
    }
}

internal class CaptionsStatusHook : AccessibilityHook() {
    override fun shouldTrigger(lastState: ReduxState, newState: ReduxState) =
        (lastState.captionsState.status != newState.captionsState.status)

    override fun message(lastState: ReduxState, newState: ReduxState, context: Context) =
        when (newState.captionsState.status) {
            CaptionsStatus.STARTED -> context.getString(R.string.azure_communication_ui_calling_captions_is_on)
            CaptionsStatus.STOPPED -> context.getString(R.string.azure_communication_ui_calling_captions_is_off)
            else -> ""
        }
}
