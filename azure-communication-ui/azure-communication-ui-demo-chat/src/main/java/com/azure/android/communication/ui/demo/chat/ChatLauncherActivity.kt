package com.azure.android.communication.ui.demo.chat

import android.os.Bundle
import android.util.LayoutDirection
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.common.CommunicationUserIdentifier
import com.azure.android.communication.ui.chat.ChatCompositeBuilder
import com.azure.android.communication.ui.chat.models.ChatCompositeJoinLocator
import com.azure.android.communication.ui.chat.models.ChatCompositeLocalOptions
import com.azure.android.communication.ui.chat.models.ChatCompositeLocalizationOptions
import com.azure.android.communication.ui.chat.models.ChatCompositeParticipantViewData
import com.azure.android.communication.ui.chat.models.ChatCompositeRemoteOptions
import com.azure.android.communication.ui.demo.chat.databinding.ActivityChatLauncherBinding
import java.util.Locale

class ChatLauncherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatLauncherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.launchAsViewButton.setOnClickListener(::onLaunchLiveView)
        binding.launchAsActivityButton.setOnClickListener(::onLaunchLiveActivity)
    }

    private val chatComposite by lazy {
        val chatCompositeBuilder = ChatCompositeBuilder()
        chatCompositeBuilder
            .localization(ChatCompositeLocalizationOptions(Locale.getDefault(), LayoutDirection.LOCALE))
            .build()
    }

    private fun onLaunchLiveActivity(view: View) {
        val threadId = ""
        val endpoint = ""
        val locator = ChatCompositeJoinLocator(endpoint, threadId)

        val tokenRefreshOptions = CommunicationTokenRefreshOptions({
            ""
        }, true)

        val credential = CommunicationTokenCredential(tokenRefreshOptions)
        val communicationUserId = ""

        val remoteOptions = ChatCompositeRemoteOptions(
            locator,
            CommunicationUserIdentifier(communicationUserId),
            credential
        )

        val localOptions = ChatCompositeLocalOptions(ChatCompositeParticipantViewData())

        chatComposite.launch(this, remoteOptions, localOptions)
    }

    private fun onLaunchLiveView(view: View) {
        binding.viewHolder.apply {
            removeAllViews()
            addView(chatComposite.getCompositeUIView(view.context))
        }
    }
}
