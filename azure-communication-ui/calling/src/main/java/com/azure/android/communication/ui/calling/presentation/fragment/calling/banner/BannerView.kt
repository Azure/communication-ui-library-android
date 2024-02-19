// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.banner

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.calling.implementation.R
import kotlinx.coroutines.launch

internal class BannerView : ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var bannerView: View
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
            viewModel.bannerInfoTypeStateFlow.collect {
                updateNoticeBox(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.shouldShowBannerStateFlow.collect {
                visibility =
                    if (it) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isOverlayDisplayedFlow.collect {
                if (it) {
                    ViewCompat.setImportantForAccessibility(bannerView, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS)
                } else {
                    ViewCompat.setImportantForAccessibility(bannerView, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES)
                }
            }
        }
    }

    private fun updateNoticeBox(bannerInfoType: BannerInfoType) {
        if (bannerInfoType != BannerInfoType.BLANK) {
            viewModel.setDisplayedBannerType(bannerInfoType)
            bannerText.text = getBannerInfo(bannerInfoType)
            bannerText.setOnClickListener(getBannerClickDestination(bannerInfoType))

            val textToAnnounce = "${context.getString(
                R.string.azure_communication_ui_calling_alert_title,
            )}: ${context.getString(
                R.string.azure_communication_ui_calling_view_button_close_button_full_accessibility_label,
            )}, ${bannerText.text} ${context.getString(R.string.azure_communication_ui_calling_view_link)}"
            announceForAccessibility(textToAnnounce)

            bannerCloseButton.contentDescription = "${getBannerTitle(bannerText.text)}: ${context.getString(R.string.azure_communication_ui_calling_view_button_close_button_accessibility_label)}"
        } else if (bannerText.text.isNullOrBlank() && viewModel.displayedBannerType != BannerInfoType.BLANK) {
            // Below code helps to display banner message on screen rotate. When recording and transcription being saved is displayed
            // and screen is rotated, blank banner is displayed.
            // We can not remove reset state in view model on stop as that cause incorrect message order
            bannerText.text = getBannerInfo(viewModel.displayedBannerType)
            bannerText.setOnClickListener(getBannerClickDestination(bannerInfoType))

            val textToAnnounce = "${context.getString(
                R.string.azure_communication_ui_calling_alert_title,
            )}: ${context.getString(
                R.string.azure_communication_ui_calling_view_button_close_button_full_accessibility_label,
            )}, ${bannerText.text} ${context.getString(R.string.azure_communication_ui_calling_view_link)}"
            announceForAccessibility(textToAnnounce)

            bannerCloseButton.contentDescription = "${getBannerTitle(bannerText.text)}: ${context.getString(R.string.azure_communication_ui_calling_view_button_close_button_accessibility_label)}"
        }
    }

    private fun getBannerClickDestination(bannerInfoType: BannerInfoType): OnClickListener {
        return OnClickListener {
            bannerText.isEnabled = false
            val url =
                when (bannerInfoType) {
                    BannerInfoType.RECORDING_AND_TRANSCRIPTION_STARTED,
                    BannerInfoType.RECORDING_STARTED,
                    BannerInfoType.TRANSCRIPTION_STARTED,
                    BannerInfoType.RECORDING_STOPPED_STILL_TRANSCRIBING,
                    BannerInfoType.TRANSCRIPTION_STOPPED_STILL_RECORDING,
                    -> {
                        context.getString(R.string.azure_communication_ui_calling_view_link_privacy_policy_url)
                    }
                    BannerInfoType.TRANSCRIPTION_STOPPED,
                    BannerInfoType.RECORDING_STOPPED,
                    BannerInfoType.RECORDING_AND_TRANSCRIPTION_STOPPED,
                    -> {
                        context.getString(R.string.azure_communication_ui_calling_view_link_learn_more_url)
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
                400,
            )
        }
    }

    private fun getBannerInfo(bannerInfoType: BannerInfoType): CharSequence {
        return when (bannerInfoType) {
            BannerInfoType.RECORDING_AND_TRANSCRIPTION_STARTED ->
                context.getText(R.string.azure_communication_ui_calling_view_banner_recording_and_transcribing_started)
            BannerInfoType.RECORDING_STARTED ->
                context.getText(R.string.azure_communication_ui_calling_view_banner_recording_started)
            BannerInfoType.TRANSCRIPTION_STOPPED_STILL_RECORDING ->
                context.getText(R.string.azure_communication_ui_calling_view_banner_transcription_stopped_still_recording)
            BannerInfoType.TRANSCRIPTION_STARTED ->
                context.getText(R.string.azure_communication_ui_calling_view_banner_transcription_started)
            BannerInfoType.TRANSCRIPTION_STOPPED ->
                context.getText(R.string.azure_communication_ui_calling_view_banner_transcription_stopped)
            BannerInfoType.RECORDING_STOPPED_STILL_TRANSCRIBING ->
                context.getText(R.string.azure_communication_ui_calling_view_banner_recording_stopped_still_transcribing)
            BannerInfoType.RECORDING_STOPPED ->
                context.getText(R.string.azure_communication_ui_calling_view_banner_recording_stopped)
            BannerInfoType.RECORDING_AND_TRANSCRIPTION_STOPPED ->
                context.getText(R.string.azure_communication_ui_calling_view_banner_recording_and_transcribing_stopped)
            else -> ""
        }
    }

    private fun getBannerTitle(bannerText: CharSequence): CharSequence {
        return bannerText
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        bannerView = findViewById(R.id.azure_communication_ui_call_banner)
        bannerText = findViewById(R.id.azure_communication_ui_call_banner_text)
        bannerCloseButton = findViewById(R.id.azure_communication_ui_call_banner_close)

        bannerCloseButton.setOnClickListener {
            viewModel.dismissBanner()
        }
    }
}
