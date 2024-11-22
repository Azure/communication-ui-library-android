// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.captions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.azure.android.communication.ui.calling.implementation.R
import com.microsoft.fluentui.persona.AvatarView

internal class CaptionsRecyclerViewAdapter(
    private val captionsRttRecords: List<CaptionsRttRecord>
) : RecyclerView.Adapter<CaptionsRecyclerViewAdapter.CaptionsViewHolder>() {
    class CaptionsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageTextView: TextView =
            view.findViewById(R.id.azure_communication_ui_calling_caption_info_text)
        val avatarView: AvatarView =
            view.findViewById(R.id.azure_communication_ui_calling_caption_avatar_view)
        val displayNameTextView: TextView =
            view.findViewById(R.id.azure_communication_ui_calling_caption_display_name)

        val rttIndicator: View =
            view.findViewById(R.id.azure_communication_ui_calling_caption_rtt_indicator)
        val rttTypingIndicator: View =
            view.findViewById(R.id.azure_communication_ui_calling_caption_rtt_typing_indicator)
        val rttTypingIndicatorText: View =
            view.findViewById(R.id.azure_communication_ui_calling_caption_rtt_typing_indicator_text)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CaptionsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.azure_communication_ui_calling_captions_item_view, parent, false)
        return CaptionsViewHolder(view)
    }

    override fun getItemCount() = captionsRttRecords.size

    override fun onBindViewHolder(holder: CaptionsViewHolder, position: Int) {
        val captionsRttRecord = captionsRttRecords[position]
        holder.messageTextView.text = captionsRttRecord.displayText
        var speakerName = captionsRttRecord.displayName
        if (speakerName.isEmpty()) {
            speakerName =
                holder.messageTextView.context.getString(R.string.azure_communication_ui_calling_view_participant_drawer_unnamed)
        }
        if (holder.displayNameTextView.text != speakerName) {
            holder.displayNameTextView.text = speakerName
        }
        if (holder.avatarView.name != speakerName) {
            holder.avatarView.name = speakerName
        }
        val bitMap = captionsRttRecord.avatarBitmap

        if (bitMap == null) {
            holder.avatarView.avatarImageBitmap = null
        } else if (captionsRttRecord.avatarBitmap != holder.avatarView.avatarImageBitmap) {
            holder.avatarView.avatarImageBitmap = bitMap
        }

        val isRtt = captionsRttRecord.type == CaptionsRttType.RTT
        val isRttTyping = isRtt && !captionsRttRecord.isFinal
        holder.rttTypingIndicator.isVisible = isRttTyping
        holder.rttTypingIndicatorText.isVisible = isRttTyping

        holder.rttIndicator.isVisible = isRtt && !isRttTyping
    }
}
