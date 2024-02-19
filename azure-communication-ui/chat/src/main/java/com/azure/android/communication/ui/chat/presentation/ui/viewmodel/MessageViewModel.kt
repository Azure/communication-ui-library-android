// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.viewmodel

import android.content.Context
import com.azure.android.communication.ui.chat.R
import com.azure.android.communication.ui.chat.models.EMPTY_MESSAGE_INFO_MODEL
import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.models.MessageSendStatus
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType
import com.azure.android.communication.ui.chat.utilities.findMessageIdxById
import com.azure.android.core.rest.annotation.Immutable
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

private val timeFormatShort = DateTimeFormatter.ofPattern("EEEE")
private val timeFormatLong = DateTimeFormatter.ofPattern("EEEE MMMM dd")

@Immutable
internal class MessageViewModel(
    val message: MessageInfoModel,
    val showUsername: Boolean,
    val showTime: Boolean,
    val dateHeaderText: String?,
    val isLocalUser: Boolean,
    val messageStatus: MessageSendStatus?,
    val showSentStatusIcon: Boolean,
    val showReadReceipt: Boolean,
    val isHiddenUser: Boolean,
    val includeDebugInfo: Boolean,
) {
    val isVisible get() = message.deletedOn == null && !isHiddenUser
}

internal fun List<MessageInfoModel>.toViewModelList(
    context: Context,
    localUserIdentifier: String,
    latestLocalUserMessageId: Long? = null,
    lastMessageIdReadByRemoteParticipants: Long = 0L,
    hiddenParticipant: Set<String>,
    includeDebugInfo: Boolean = false,
) = InfoModelToViewModelAdapter(
    context,
    this,
    localUserIdentifier,
    latestLocalUserMessageId,
    lastMessageIdReadByRemoteParticipants,
    hiddenParticipant,
    includeDebugInfo,
) as List<MessageViewModel>

private class InfoModelToViewModelAdapter(
    private val context: Context,
    private val messages: List<MessageInfoModel>,
    private val localUserIdentifier: String,
    private val latestLocalUserMessageId: Long?,
    private val lastMessageIdReadByRemoteParticipants: Long,
    private val hiddenParticipant: Set<String>,
    private val includeDebugInfo: Boolean = false,
) :
    List<MessageViewModel> {
    override fun get(index: Int): MessageViewModel {
        // Generate Message View Model here
        val lastMessage =
            try {
                messages[index - 1]
            } catch (e: IndexOutOfBoundsException) {
                EMPTY_MESSAGE_INFO_MODEL
            }
        val thisMessage = messages[index]
        val isLocalUser =
            thisMessage.senderCommunicationIdentifier?.id == localUserIdentifier || thisMessage.isCurrentUser
        val showReadReceipt =
            thisMessage.sendStatus == MessageSendStatus.SENT && lastMessageIdReadByRemoteParticipants != 0L &&
                lastMessageIdReadByRemoteParticipants == thisMessage.normalizedID

        return MessageViewModel(
            thisMessage,
            includeDebugInfo = includeDebugInfo,
            showUsername =
                !isLocalUser &&
                    (lastMessage.senderCommunicationIdentifier?.id ?: "")
                    != (thisMessage.senderCommunicationIdentifier?.id ?: ""),
            showTime =
                (
                    (lastMessage.senderCommunicationIdentifier?.id ?: "")
                        != (thisMessage.senderCommunicationIdentifier?.id ?: "") &&
                        !thisMessage.isCurrentUser
                ) ||
                    (thisMessage.isCurrentUser && !lastMessage.isCurrentUser),
            dateHeaderText =
                buildDateHeader(
                    lastMessage.createdOn!!,
                    thisMessage.createdOn ?: OffsetDateTime.now(),
                ),
            isLocalUser = isLocalUser,
            messageStatus = thisMessage.sendStatus,
            showReadReceipt = showReadReceipt,
            showSentStatusIcon = shouldShowMessageStatusIcon(thisMessage, showReadReceipt),
            isHiddenUser =
                messages[index].messageType == ChatMessageType.PARTICIPANT_ADDED &&
                    messages[index].participants.size == 1 &&
                    hiddenParticipant.contains(messages[index].participants.first().userIdentifier.id),
        )
    }

    private fun buildDateHeader(
        lastMessageDate: OffsetDateTime,
        thisMessageDate: OffsetDateTime,
    ): String? {
        val thisMessageDateZoned = thisMessageDate.atZoneSameInstant(ZoneId.systemDefault())
        val today =
            OffsetDateTime.now().withHour(0).withMinute(0)
                .withSecond(0)
                .withNano(0).atZoneSameInstant(ZoneId.systemDefault())

        val yesterday = today.minusDays(1)
        val weekAgo = today.minusWeeks(1)

        if (lastMessageDate.dayOfYear != thisMessageDate.dayOfYear) {
            if (thisMessageDateZoned.isAfter(today)) {
                return context.getString(R.string.azure_communication_ui_chat_message_today)
            } else if (thisMessageDateZoned.isAfter(yesterday)) {
                return context.getString(R.string.azure_communication_ui_chat_message_yesterday)
            } else if (thisMessageDateZoned.isAfter(weekAgo)) {
                return thisMessageDate.format(timeFormatShort)
            }
            return thisMessageDate.format(timeFormatLong)
        } else {
            return null
        }
    }

    // Rest of List Implementation
    override val size = messages.size

    override fun contains(element: MessageViewModel) = messages.contains(element.message)

    override fun containsAll(elements: Collection<MessageViewModel>) = messages.containsAll(elements.map { it.message })

    override fun indexOf(element: MessageViewModel) = messages.findMessageIdxById(element.message.normalizedID)

    override fun isEmpty() = messages.isEmpty()

    override fun iterator(): Iterator<MessageViewModel> {
        // Not Implemented
        TODO("Not Implemented, probably not needed")
    }

    override fun lastIndexOf(element: MessageViewModel) = messages.lastIndexOf(element.message)

    override fun listIterator(): ListIterator<MessageViewModel> {
        // Not Implemented
        TODO("Not Implemented, probably not needed")
    }

    override fun listIterator(index: Int): ListIterator<MessageViewModel> {
        // Not Implemented
        TODO("Not Implemented, probably not needed")
    }

    override fun subList(
        fromIndex: Int,
        toIndex: Int,
    ): List<MessageViewModel> {
        // Not Implemented
        TODO("Not Implemented, probably not needed")
    }

    private fun shouldShowMessageStatusIcon(
        message: MessageInfoModel,
        showReadReceipt: Boolean,
    ): Boolean {
        return !showReadReceipt && (message.sendStatus == MessageSendStatus.FAILED || latestLocalUserMessageId == message.normalizedID)
    }
}
