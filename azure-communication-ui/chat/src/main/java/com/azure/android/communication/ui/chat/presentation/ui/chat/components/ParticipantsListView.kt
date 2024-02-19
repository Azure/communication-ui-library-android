/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.azure.android.communication.ui.chat.R
import com.azure.android.communication.ui.chat.models.RemoteParticipantInfoModel
import com.azure.android.communication.ui.chat.service.sdk.wrapper.CommunicationIdentifier

@Composable
internal fun ParticipantsListView(
    participants: List<RemoteParticipantInfoModel>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        ListSubHeaderView(
            text =
                stringResource(
                    id = R.string.azure_communication_ui_chat_in_this_chat_count,
                    participants.count(),
                ),
        )
        LazyColumn(modifier = modifier.fillMaxWidth()) {
            items(
                items = participants,
                key = { it.userIdentifier.id },
                itemContent = { it -> ParticipantView(participant = it) },
            )
        }
    }
}

@Preview
@Composable
internal fun PreviewParticipantsListView() {
    ParticipantsListView(
        participants =
            listOf(
                RemoteParticipantInfoModel(
                    userIdentifier = CommunicationIdentifier.UnknownIdentifier("7A13DD2C-B49F-4521-9364-975F12F6E333"),
                    "Bilbo Baggins",
                ),
                RemoteParticipantInfoModel(
                    userIdentifier = CommunicationIdentifier.UnknownIdentifier("931804B1-D72E-4E70-BFEA-7813C7761BD2"),
                    "Johnny Mnemonic",
                ),
                RemoteParticipantInfoModel(
                    userIdentifier = CommunicationIdentifier.UnknownIdentifier("152D5D76-3DDC-44BE-873F-A4575F8C91DF"),
                    "Sherlock Holmes",
                ),
                RemoteParticipantInfoModel(
                    userIdentifier = CommunicationIdentifier.UnknownIdentifier("85FF2697-2ABB-480E-ACCA-09EBE3D6F5EC"),
                    "Katniss Everdeen",
                ),
            ),
    )
}
