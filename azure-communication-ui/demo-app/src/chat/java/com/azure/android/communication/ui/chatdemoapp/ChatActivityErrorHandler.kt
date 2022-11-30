package com.azure.android.communication.ui.chatdemoapp

import com.azure.android.communication.ui.chat.ChatCompositeEventHandler
import com.azure.android.communication.ui.chat.models.ChatCompositeErrorEvent
import java.lang.ref.WeakReference

class ChatActivityErrorHandler(
    chatLauncherActivity: ChatLauncherActivity,
) : ChatCompositeEventHandler<ChatCompositeErrorEvent> {

    private val activityWr: WeakReference<ChatLauncherActivity> = WeakReference(chatLauncherActivity)

    override fun handle(it: ChatCompositeErrorEvent) {
        println("================= application is logging exception =================")
        println(it.cause)
        println(it.errorCode)
        activityWr.get()?.showAlert("${it.cause}")
        println("====================================================================")
    }
}
