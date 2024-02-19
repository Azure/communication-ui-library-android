/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import android.view.View
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.microsoft.fluentui.persona.AvatarView

@Composable
internal fun ListItemView(
    title: String,
    customView: View? = null,
) {
    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { context ->
            com.microsoft.fluentui.listitem.ListItemView(context).apply {
                this.title = title
                this.customView = customView
            }
        },
    )
}

@Preview
@Composable
internal fun PreviewListItemView() {
    val displayName = "Aaron Buxton"
    val avatarView = com.microsoft.fluentui.persona.AvatarView(LocalContext.current)
    avatarView.name = displayName
    ListItemView(title = displayName, customView = avatarView)
}
