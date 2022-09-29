// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.chatviewcomponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt

data class AcsChatActionBarViewModel(val participantCount: Int, val topic: String)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatCompositeActionBar(
    viewModel: AcsChatActionBarViewModel,
    onBackButtonPressed: () -> Unit = { }
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        CenterAlignedTopAppBar(
            modifier = Modifier.drawWithContent {
                drawContent()
                clipRect {
                    val y = size.height
                    drawLine(
                        brush = SolidColor(Color("#E1E1E1".toColorInt())),
                        cap = StrokeCap.Square,
                        start = Offset.Zero.copy(y = y),
                        end = Offset(x = size.width, y = y)
                    )
                }
            },
            actions = {},
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = viewModel.topic,
                        style = MaterialTheme.typography.body1)
                    if (viewModel.participantCount == 1) {
                        Text("${viewModel.participantCount} Participant", style = MaterialTheme.typography.body2)
                    } else {
                        Text("${viewModel.participantCount} Participants", style = MaterialTheme.typography.body2)
                    }
                }
            },
            navigationIcon = {
                AcsChatBackButton(
                    contentDescription = "Back button",
                    onBackButtonPressed = onBackButtonPressed
                )
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
        )
    }
}

@Preview
@Composable
fun PreviewChatCompsiteActionBar() {
    Column() {
        ChatCompositeActionBar(
            viewModel = AcsChatActionBarViewModel(
                participantCount = 4,
                topic = "Topic"
            )
        ) {}

        ChatCompositeActionBar(
            viewModel = AcsChatActionBarViewModel(
                participantCount = 1,
                topic = "Title"
            )
        )  {}
    }
}
