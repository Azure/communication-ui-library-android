/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.azure.android.communication.ui.chat.models

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.azure.android.communication.ui.chat.redux.action.Action

internal data class MenuItemModel(
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    val action: Action? = null,
    var onClickAction: ((context: Context) -> Unit)? = null,
)
