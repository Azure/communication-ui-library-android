// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

/**
 * Shows a button that lets the user scroll to the bottom.
 */
@Composable
internal fun UnreadMessagesIndicatorView(
    unreadCount: Int,
    onClicked: () -> Unit
) {
}

@Preview
@Composable
internal fun PreviewUnreadMessagesIndicatorView() {
    UnreadMessagesIndicatorView(20) {}
}
