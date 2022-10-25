// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.microsoft.fluentui.persona.AvatarGroupView
import com.microsoft.fluentui.persona.AvatarSize
import com.microsoft.fluentui.persona.IAvatar

@Composable
internal fun AvatarGroup(typingParticipantsDisplayNames: List<String>) {
    val avatarList = ArrayList<IAvatar>(typingParticipantsDisplayNames.size)
    typingParticipantsDisplayNames.forEach { displayName ->
        val data = AvatarData(displayName)
        avatarList.add(data)
    }
    AndroidView(factory = {
        AvatarGroupView(it).apply {
            avatarSize = AvatarSize.SMALL
            maxDisplayedAvatars = 2
            setAvatars(avatarList)
            contentDescription = "Typing participants: " + typingParticipantsDisplayNames.joinToString()
        }
    })
}

data class AvatarData(
    override var name: String,
    override var avatarContentDescriptionLabel: String = name,
    override var avatarBackgroundColor: Int? = null,
    override var avatarImageBitmap: Bitmap? = null,
    override var avatarImageDrawable: Drawable? = null,
    override var avatarImageResourceId: Int? = -1,
    override var avatarImageUri: Uri? = null,
    override var email: String = ""
) : IAvatar
