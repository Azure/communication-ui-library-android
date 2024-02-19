/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.azure.android.communication.ui.chat.R
import com.azure.android.communication.ui.chat.models.RemoteParticipantInfoModel
import com.azure.android.communication.ui.chat.service.sdk.wrapper.CommunicationIdentifier
import com.microsoft.fluentui.persona.AvatarView

@Composable
internal fun ParticipantView(participant: RemoteParticipantInfoModel) {
    val participantName =
        participant.displayName
            ?: stringResource(id = R.string.azure_communication_ui_chat_unnamed_participant)
    val avatarView = AvatarView(LocalContext.current).apply { name = participantName }

    ListItemView(title = participantName, avatarView)
}

@Preview
@Composable
internal fun PreviewParticipant() {
    val userOne =
        RemoteParticipantInfoModel(
            userIdentifier = CommunicationIdentifier.UnknownIdentifier("7A13DD2C-B49F-4521-9364-975F12F6E333"),
            "John Smith",
        )
    ParticipantView(userOne)
}
