// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.azure.android.communication.ui.chat.presentation.style.ChatCompositeTheme

internal data class ActionBarViewModel(val participantCount: Int, val topic: String)

@Composable
internal fun ActionBar(
        viewModel: ActionBarViewModel,
        onBackButtonPressed: () -> Unit = { }
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        TopAppBar(
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
                backgroundColor = Color.White,
                title = {
                    Column(
                            modifier = Modifier.padding(start = 90.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                                text = viewModel.topic,
                                textAlign = TextAlign.Center,
                                style = ChatCompositeTheme.typography.title
                        )
                        if (viewModel.participantCount == 1) {
                            Text(
                                    text = "${viewModel.participantCount} Participant",
                                    textAlign = TextAlign.Center,
                                    style = ChatCompositeTheme.typography.body
                            )
                        } else {
                            Text(
                                    text = "${viewModel.participantCount} Participants",
                                    textAlign = TextAlign.Center,
                                    style = ChatCompositeTheme.typography.body
                            )
                        }
                    }
                },
                navigationIcon = {
                    BackButton(
                            contentDescription = "Back button",
                            onBackButtonPressed = onBackButtonPressed
                    )
                }
        )
    }
}

@Preview
@Composable
internal fun PreviewActionBar() {
    ActionBar(
            viewModel = ActionBarViewModel(
                    participantCount = 4,
                    topic = "Topic"
            )
    ) {}
}
