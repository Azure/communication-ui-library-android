// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.azure.android.communication.ui.chat.R
import com.azure.android.communication.ui.chat.presentation.components.microcomponents.ChatCompositeActionBarBackButton

@Composable
fun ChatCompsiteActionBar(
    onNavIconPressed: () -> Unit = { }
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
            actions = {
            }, title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(id = R.string.azure_communication_ui_chat_action_bar_title), style = MaterialTheme.typography.body1)
                    Text("2 Participants", style = MaterialTheme.typography.body2)
                }
            }, navigationIcon = {
                ChatCompositeActionBarBackButton(
                    contentDescription = "Back button",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable(onClick = onNavIconPressed)
                        .padding(19.dp)
                )
            }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
        )
    }
}

@Preview
@Composable
fun PreviewChatCompsiteActionBar() {
    Column() {
        ChatCompsiteActionBar(
            onNavIconPressed = {},
        )

        ChatCompsiteActionBar(
            onNavIconPressed = {},
        )
    }
}