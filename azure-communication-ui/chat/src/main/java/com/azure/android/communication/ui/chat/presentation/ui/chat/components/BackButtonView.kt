// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.azure.android.communication.ui.chat.R
import com.azure.android.communication.ui.chat.presentation.style.ChatCompositeTheme

@Composable
internal fun BackButtonView(
    contentDescription: String,
    onBackButtonPressed: () -> Unit = { },
) {
    val semantics =
        Modifier.semantics {
            this.contentDescription = contentDescription
            this.role = Role.Image
        }
    val painter =
        painterResource(id = R.drawable.azure_communication_ui_chat_ic_fluent_arrow_left_20_filled)

    Image(
        painter = painter,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        colorFilter = ColorFilter.tint(ChatCompositeTheme.colors.content),
        modifier =
            Modifier
                .padding(19.dp)
                .size(24.dp, 24.dp)
                .clip(CircleShape)
                .clickable { onBackButtonPressed() }
                .then(semantics),
    )
}

@Composable
@Preview(showBackground = true)
internal fun PreviewBackButtonView() {
    BackButtonView(contentDescription = "Back Button")
}
