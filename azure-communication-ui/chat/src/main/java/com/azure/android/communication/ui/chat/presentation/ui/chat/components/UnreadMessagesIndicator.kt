// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

/**
 * Shows a button that lets the user scroll to the bottom.
 */
@Composable
fun UnreadMessagesIndicator(
        unreadCount: Int,
        onClicked: () -> Unit
) {


}

@Preview
@Composable
fun UnreadMessagesIndicatorPreview() {
    UnreadMessagesIndicator(20) {}
}
