// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.azure.android.communication.ui.chat.R
import com.azure.android.communication.ui.chat.presentation.style.ChatCompositeTheme
import com.azure.android.communication.ui.chat.redux.action.Action

@Composable
internal fun ActionBarView(
    title: String,
    subTitle: String?,
    onTitleClicked: (() -> Unit)? = null,
    onBackButtonPressed: () -> Unit = { },
    postAction: (Action) -> Unit,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        val brushColor: Color = ChatCompositeTheme.colors.outlineColor

        TopAppBar(
            modifier =
                Modifier.drawWithContent {
                    drawContent()

                    clipRect {
                        val y = size.height
                        drawLine(
                            brush = SolidColor(brushColor),
                            cap = StrokeCap.Square,
                            start = Offset.Zero.copy(y = y),
                            end = Offset(x = size.width, y = y),
                        )
                    }
                },
            backgroundColor = ChatCompositeTheme.colors.background,
            title = {
                var modifier = Modifier.padding(end = 65.dp)
                if (onTitleClicked != null) {
                    modifier = modifier.clickable { onTitleClicked() }
                }
                Column(
                    modifier = modifier.then(Modifier.fillMaxWidth()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = title,
                        textAlign = TextAlign.Center,
                        style =
                            ChatCompositeTheme.typography.title
                                .copy(color = ChatCompositeTheme.colors.textColor),
                    )
                    if (subTitle != null) {
                        Text(
                            text = subTitle,
                            textAlign = TextAlign.Center,
                            style =
                                ChatCompositeTheme.typography.body
                                    .copy(color = ChatCompositeTheme.colors.textColor),
                        )
                    }
                }
            },
            navigationIcon = {
                BackButtonView(
                    contentDescription = "Back button",
                    onBackButtonPressed = onBackButtonPressed,
                )
            },
        )
    }
}

@Preview
@Composable
internal fun PreviewActionBarView() {
    ActionBarView(
        subTitle = stringResource(id = R.string.azure_communication_ui_chat_count_people, 4),
        title = "Topic",
        onTitleClicked = {},
    ) {}
}
