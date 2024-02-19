package com.azure.android.communication.ui.chat.preview

import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.models.RemoteParticipantInfoModel
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType
import com.azure.android.communication.ui.chat.service.sdk.wrapper.CommunicationIdentifier
import org.threeten.bp.OffsetDateTime

internal val MOCK_LOCAL_USER_ID = "Local"
internal val MOCK_MESSAGES
    get(): List<MessageInfoModel> {

        val userA_ID = CommunicationIdentifier.UnknownIdentifier("Peter")
        val userA_Display = "Peter Terry"

        val userB_ID = CommunicationIdentifier.UnknownIdentifier(MOCK_LOCAL_USER_ID)
        val userB_Display = "Local User"

        val userC_ID = CommunicationIdentifier.UnknownIdentifier("Carlos")
        val userC_Display = "Carlos Slattery"

        val userD_ID = CommunicationIdentifier.UnknownIdentifier("Johnnie")
        val userD_Display = "Johnnie McConnell"

        return listOf(
            MessageInfoModel(
                senderCommunicationIdentifier = userA_ID,
                senderDisplayName = userA_Display,
                content = "I'll message in 2 weeks",
                messageType = ChatMessageType.TEXT,
                id = "1",
                internalId = null,
                createdOn = OffsetDateTime.now().minusWeeks(2).minusMinutes(10),
            ),
            MessageInfoModel(
                senderCommunicationIdentifier = userA_ID,
                senderDisplayName = userA_Display,
                content = "Hey!!",
                messageType = ChatMessageType.TEXT,
                id = "2",
                internalId = null,
                createdOn = OffsetDateTime.now().minusDays(2).minusMinutes(10),
            ),
            MessageInfoModel(
                senderCommunicationIdentifier = userB_ID,
                senderDisplayName = userB_Display,
                content = "Hi Peter, thanks for following up with me",
                messageType = ChatMessageType.TEXT,
                id = "3",
                internalId = null,
                createdOn = OffsetDateTime.now().minusDays(1).minusMinutes(12),
            ),
            MessageInfoModel(
                senderCommunicationIdentifier = userB_ID,
                senderDisplayName = userB_Display,
                content = "I like to type",
                messageType = ChatMessageType.TEXT,
                id = "4",
                internalId = null,
                createdOn = OffsetDateTime.now().minusDays(1).minusMinutes(11),
            ),
            MessageInfoModel(
                senderCommunicationIdentifier = userB_ID,
                senderDisplayName = userB_Display,
                content = "a lot",
                messageType = ChatMessageType.TEXT,
                id = "5",
                internalId = null,
                createdOn = OffsetDateTime.now().minusDays(1).minusMinutes(10),
            ),
            MessageInfoModel(
                content = null,
                messageType = ChatMessageType.PARTICIPANT_ADDED,
                senderCommunicationIdentifier = userC_ID,
                participants =
                    listOf(
                        RemoteParticipantInfoModel(CommunicationIdentifier.UnknownIdentifier(""), userC_Display),
                        RemoteParticipantInfoModel(CommunicationIdentifier.UnknownIdentifier(""), userA_Display),
                    ),
                senderDisplayName = null,
                id = "6",
                internalId = null,
                createdOn = OffsetDateTime.now().minusDays(1).minusMinutes(10),
            ),
            MessageInfoModel(
                senderCommunicationIdentifier = userA_ID,
                senderDisplayName = userA_Display,
                content = "No Problem",
                messageType = ChatMessageType.TEXT,
                id = "7",
                internalId = null,
                createdOn = OffsetDateTime.now().minusMinutes(20),
            ),
            MessageInfoModel(
                content = null,
                messageType = ChatMessageType.PARTICIPANT_REMOVED,
                senderCommunicationIdentifier = userD_ID,
                senderDisplayName = null,
                participants = listOf(RemoteParticipantInfoModel(CommunicationIdentifier.UnknownIdentifier(""), userD_Display)),
                id = "8",
                internalId = null,
                createdOn = OffsetDateTime.now().minusMinutes(10),
            ),
            MessageInfoModel(
                senderCommunicationIdentifier = userA_ID,
                senderDisplayName = userA_Display,
                content = "Let's work through the feedback we received on our wednesday meeting",
                messageType = ChatMessageType.TEXT,
                id = "9",
                internalId = null,
                createdOn = OffsetDateTime.now().minusMinutes(5),
            ),
            MessageInfoModel(
                senderCommunicationIdentifier = userA_ID,
                senderDisplayName = userA_Display,
                content = "<B> Hey!! </B> Check this link <A href=\"https://www.microsoft.com\">microsoft</A>",
                messageType = ChatMessageType.HTML,
                id = "10",
                internalId = null,
                createdOn = OffsetDateTime.now(),
            ),
            MessageInfoModel(
                messageType = ChatMessageType.PARTICIPANT_REMOVED,
                isCurrentUser = true,
                id = "11",
                internalId = null,
                createdOn = OffsetDateTime.now(),
            ),
        )
    }
