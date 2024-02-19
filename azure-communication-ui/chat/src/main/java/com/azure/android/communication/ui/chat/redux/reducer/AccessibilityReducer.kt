package com.azure.android.communication.ui.chat.redux.reducer

import android.content.Context
import com.azure.android.communication.ui.chat.redux.action.Action
import com.azure.android.communication.ui.chat.redux.action.ChatAction
import com.azure.android.communication.ui.chat.redux.state.ReduxState

internal interface AccessibilityReducer : Reducer<ReduxState>

// Takes actions at the end of Reducing, and announces things about Actions that have been taken.
internal class AccessibilityReducerImpl(val context: Context, val notifyUser: (String) -> Unit) : AccessibilityReducer {
    override fun reduce(
        state: ReduxState,
        action: Action,
    ): ReduxState {
        when (action) {
            is ChatAction.MessageEdited ->
                notifyUser(
                    context.getString(
                        com.azure.android.communication.ui.chat.R.string.azure_communication_ui_chat_accessibility_edited_message,
                        action.message.senderDisplayName,
                        action.message.content,
                    ),
                )
            is ChatAction.MessageDeleted ->
                notifyUser(
                    context.getString(
                        com.azure.android.communication.ui.chat.R.string.azure_communication_ui_chat_accessibility_deleted_message,
                        action.message.senderDisplayName,
                    ),
                )
            is ChatAction.MessageReceived ->
                notifyUser(
                    context.getString(
                        com.azure.android.communication.ui.chat.R.string.azure_communication_ui_chat_accessibility_new_message,
                        action.message.senderDisplayName,
                        action.message.content,
                    ),
                )
        }
        return state
    }
}
