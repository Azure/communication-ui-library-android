// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.microsoft.fluentui.persona.AvatarSize
import com.microsoft.fluentui.persona.AvatarView

@Composable
internal fun AvatarView(name: String?, color: Color? = null, avatarSize: AvatarSize = AvatarSize.LARGE, @DrawableRes image: Int = -1, isGrouped: Boolean = false) {
    AndroidView(factory = {
        val view = AvatarView(it)
        view.name = name ?: ""
        view.avatarSize = avatarSize
        color?.apply {
            view.avatarBackgroundColor = toArgb()
        }

        view
    })
}

@Preview
@Composable
internal fun PreviewAvatarView() {
    AvatarView(name = "John Doe")
}
