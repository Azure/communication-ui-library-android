// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.banner

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.R
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class BannerView : ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var bannerText: TextView
    private lateinit var bannerCloseButton: ImageButton

    private lateinit var viewModel: BannerViewModel

    fun start(
        viewModel: BannerViewModel,
        viewLifecycleOwner: LifecycleOwner,
    ) {
        this.viewModel = viewModel

        // Start callbacks
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getBannerInfoTypeStateFlow().collect {
                updateNoticeBox(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getShouldShowBannerStateFlow().collect {
                visibility =
                    if (it) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
            }
        }
    }

    private fun updateNoticeBox(bannerInfoType: BannerInfoType) {
        if (bannerInfoType != BannerInfoType.BLANK) {
            bannerText.text = getBannerInfo(bannerInfoType)
            bannerText.setOnClickListener(getBannerClickDestination(bannerInfoType))
        }
    }

    private fun getBannerClickDestination(bannerInfoType: BannerInfoType): OnClickListener {
        return OnClickListener {
            bannerText.isEnabled = false
            val url = when (bannerInfoType) {
                BannerInfoType.RECORDING_AND_TRANSCRIPTION_STARTED,
                BannerInfoType.RECORDING_STARTED,
                BannerInfoType.TRANSCRIPTION_STARTED,
                BannerInfoType.RECORDING_STOPPED_STILL_TRANSCRIBING,
                BannerInfoType.TRANSCRIPTION_STOPPED_STILL_RECORDING,
                -> {
                    context.getString(R.string.azure_communication_ui_call_privacy_policy_url)
                }
                BannerInfoType.TRANSCRIPTION_STOPPED,
                BannerInfoType.RECORDING_STOPPED,
                BannerInfoType.RECORDING_AND_TRANSCRIPTION_STOPPED,
                -> {
                    context.getString(R.string.azure_communication_ui_call_learn_more_url)
                }
                else -> {
                    ""
                }
            }

            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            context.startActivity(intent)
            postDelayed(
                {
                    bannerText.isEnabled = true
                },
                400
            )
        }
    }

    private fun getBannerInfo(bannerInfoType: BannerInfoType): CharSequence {
        return when (bannerInfoType) {
            BannerInfoType.RECORDING_AND_TRANSCRIPTION_STARTED ->
                context.getText(R.string.azure_communication_ui_call_recording_and_transcribing_started)
            BannerInfoType.RECORDING_STARTED ->
                context.getText(R.string.azure_communication_ui_call_recording_started)
            BannerInfoType.TRANSCRIPTION_STOPPED_STILL_RECORDING ->
                context.getText(R.string.azure_communication_ui_call_transcription_stopped_still_recording)
            BannerInfoType.TRANSCRIPTION_STARTED ->
                context.getText(R.string.azure_communication_ui_call_transcription_started)
            BannerInfoType.TRANSCRIPTION_STOPPED ->
                context.getText(R.string.azure_communication_ui_call_transcription_stopped)
            BannerInfoType.RECORDING_STOPPED_STILL_TRANSCRIBING ->
                context.getText(R.string.azure_communication_ui_call_recording_stopped_still_transcribing)
            BannerInfoType.RECORDING_STOPPED ->
                context.getText(R.string.azure_communication_ui_call_recording_stopped)
            BannerInfoType.RECORDING_AND_TRANSCRIPTION_STOPPED ->
                context.getText(R.string.azure_communication_ui_call_recording_and_transcribing_stopped)
            else -> ""
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        bannerText = findViewById(R.id.azure_communication_ui_call_banner_text)
        bannerCloseButton = findViewById(R.id.azure_communication_ui_call_banner_close)

        bannerCloseButton.setOnClickListener {
            viewModel.dismissBanner()
        }
    }
}
