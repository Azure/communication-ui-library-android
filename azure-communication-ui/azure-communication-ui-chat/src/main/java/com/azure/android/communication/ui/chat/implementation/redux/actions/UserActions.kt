package com.azure.android.communication.ui.chat.implementation.redux.actions

internal sealed class UserActions {
    class IsTyping
    data class SendMessage(val message: String)
}
