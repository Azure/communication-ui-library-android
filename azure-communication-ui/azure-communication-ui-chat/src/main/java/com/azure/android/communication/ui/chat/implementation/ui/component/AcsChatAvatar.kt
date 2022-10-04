package com.azure.android.communication.ui.chat.implementation.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.microsoft.fluentui.persona.AvatarSize
import com.microsoft.fluentui.persona.AvatarView

@Composable
internal fun AcsChatAvatar(name: String, color: Color? = null, avatarSize: AvatarSize = AvatarSize.LARGE, @DrawableRes image: Int = -1, isGrouped: Boolean = false) {
    AndroidView(factory = {
        val view = AvatarView(it)
        view.name = name
        view.avatarSize = avatarSize
        color?.apply {
            view.avatarBackgroundColor = toArgb()
        }

        view
    })
}

@Preview
@Composable
internal fun PreviewAcsChatAvatar() {
    Column {
        AcsChatAvatar(name = "Bradley Cooper")
        AcsChatAvatar(name = "Megan Fox")
        AcsChatAvatar(name = "Tom Cruise")
        AcsChatAvatar(name = "Tom Cruise", color = Color.DarkGray)
        AcsChatAvatar(name = "Matt Damon")
    }
}
