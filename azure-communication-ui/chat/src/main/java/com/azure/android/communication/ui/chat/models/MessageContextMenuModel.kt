/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.azure.android.communication.ui.chat.models

internal data class MessageContextMenuModel(
    val messageInfoModel: MessageInfoModel,
    val menuItems: List<MenuItemModel>,
)
