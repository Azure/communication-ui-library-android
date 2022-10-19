package com.azure.android.communication.ui.chat.preview

import com.azure.android.communication.ui.chat.models.MessageInfoModel
import com.azure.android.communication.ui.chat.service.sdk.wrapper.ChatMessageType
import com.azure.android.communication.ui.chat.service.sdk.wrapper.CommunicationIdentifier
import org.threeten.bp.OffsetDateTime

internal val MOCK_LOCAL_USER_ID = "Local"
internal val MOCK_MESSAGES get(): List<MessageInfoModel> {
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
            content = "Hey!!",
            messageType = ChatMessageType.TEXT,
            id = null,
            internalId = null,
            createdOn = OffsetDateTime.parse("2007-12-23T10:15:30+01:00")
        ),

        MessageInfoModel(
            senderCommunicationIdentifier = userB_ID,
            senderDisplayName = userB_Display,
            content = "Hi Peter, thanks for following up with me",
            messageType = ChatMessageType.TEXT,
            id = null,
            internalId = null,
            createdOn = OffsetDateTime.parse("2007-12-23T10:15:30+01:00")
        ),

        MessageInfoModel(
            content = null,
            messageType = ChatMessageType.PARTICIPANT_ADDED,
            senderCommunicationIdentifier = userC_ID,
            senderDisplayName = userC_Display,
            id = null,
            internalId = null
        ),
        MessageInfoModel(
            senderCommunicationIdentifier = userA_ID,
            senderDisplayName = userA_Display,
            content = "No Problem",
            messageType = ChatMessageType.TEXT,
            id = null,
            internalId = null,
            createdOn = OffsetDateTime.parse("2007-12-23T10:15:30+01:00")
        ),
        MessageInfoModel(
            content = null,
            messageType = ChatMessageType.PARTICIPANT_REMOVED,
            senderCommunicationIdentifier = userD_ID,
            senderDisplayName = userD_Display,
            id = null,
            internalId = null
        ),
        MessageInfoModel(
            senderCommunicationIdentifier = userA_ID,
            senderDisplayName = userA_Display,
            content = "Let's work through the feedback we received on our wednesday meeting",
            messageType = ChatMessageType.TEXT,
            id = null,
            internalId = null,
            createdOn = OffsetDateTime.parse("2007-12-23T10:15:30+01:00")
        ),

        MessageInfoModel(
            senderCommunicationIdentifier = userA_ID,
            senderDisplayName = userA_Display,
            content = "<B> Hey!! </B> Check this link <A href=\"https://www.microsoft.com\">microsoft</A>",
            messageType = ChatMessageType.HTML,
            id = null,
            internalId = null,
            createdOn = OffsetDateTime.parse("2007-12-23T10:15:30+01:00")
        ),

    )
}
