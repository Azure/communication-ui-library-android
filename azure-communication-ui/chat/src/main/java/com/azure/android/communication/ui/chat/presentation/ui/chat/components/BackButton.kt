// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
internal fun BackButton(
        contentDescription: String,
        modifier: Modifier = Modifier
) {

}

@Composable
@Preview(showBackground = true)
fun PreviewAcsChatBackButton() {
    BackButton(contentDescription = "Back Button")
}
