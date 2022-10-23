/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.azure.android.communication.ui.chat.models.RemoteParticipantInfoModel
import com.azure.android.communication.ui.chat.service.sdk.wrapper.CommunicationIdentifier
import com.microsoft.fluentui.persona.AvatarSize

@Composable
internal fun ParticipantView(participant: RemoteParticipantInfoModel) {
    Row(
        modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AvatarView(name = participant.displayName, avatarSize = AvatarSize.SMALL)
        BasicText(
            text = participant.displayName ?: "Uknown participant",
            modifier = Modifier.padding(start = 18.dp)
        )
    }
}

@Preview
@Composable
internal fun PreviewParticipant() {
    val userOne = RemoteParticipantInfoModel(
        userIdentifier = CommunicationIdentifier.UnknownIdentifier("7A13DD2C-B49F-4521-9364-975F12F6E333"),
        "John Smith"
    )
    ParticipantView(userOne)
}
