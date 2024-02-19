// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.azure.android.communication.ui.chat.R
import com.azure.android.communication.ui.chat.models.EMPTY_MESSAGE_INFO_MODEL
import com.azure.android.communication.ui.chat.models.MenuItemModel
import com.azure.android.communication.ui.chat.models.MessageContextMenuModel
import com.azure.android.communication.ui.chat.preview.MOCK_MESSAGES
import com.azure.android.communication.ui.chat.redux.Dispatch
import com.azure.android.communication.ui.chat.redux.action.ChatAction
import com.jakewharton.threetenabp.AndroidThreeTen
import com.microsoft.fluentui.tokenized.drawer.Drawer
import com.microsoft.fluentui.tokenized.drawer.rememberDrawerState
import com.microsoft.fluentui.tokenized.listitem.ListItem
import com.microsoft.fluentui.tokenized.listitem.TextIcons
import kotlinx.coroutines.launch

@Composable
internal fun messageContextMenu(
    menu: MessageContextMenuModel,
    dispatch: Dispatch,
) {
    val lastSelectedItem = remember { mutableStateOf(EMPTY_MESSAGE_INFO_MODEL) }
    var drawerState = rememberDrawerState()
    val coroutineScope = rememberCoroutineScope()
    val currentItem = menu.messageInfoModel

    // When the selected item changes, show/hide menu
    // When EMPTY_MESSAGE_INFO_MODEL hide()
    // Otherwise show()
    if (currentItem.normalizedID != lastSelectedItem.value.normalizedID) {
        LaunchedEffect(currentItem) {
            coroutineScope.launch {
                if (currentItem == EMPTY_MESSAGE_INFO_MODEL) {
                    drawerState.close()
                } else {
                    drawerState.open()
                }
            }
        }
    }

    lastSelectedItem.value = currentItem

    Drawer(
        drawerState = drawerState,
        scrimVisible = true,
        expandable = false,
        drawerContent = {
            Column {
                menu.menuItems.map { item ->
                    val context = LocalContext.current
                    ListItem.Item(
                        text = stringResource(id = item.title),
                        primaryTextLeadingIcons =
                            TextIcons({
                                Icon(
                                    painter = painterResource(id = item.icon),
                                    contentDescription = null,
                                )
                            }),
                        onClick = {
                            dispatch(ChatAction.HideMessageContextMenu())
                            if (item.onClickAction != null) {
                                item.onClickAction?.invoke(context)
                            } else if (item.action != null) {
                                dispatch(item.action)
                            }
                        },
                    )
                }
            }
        },
    )
}

@Composable
@Preview
internal fun MessageContextMenuPreview() {
    // Preview Doesn't work
    // When setting initial drawerState to Open, it is still not visible in preview
    // May be fluent UI bug
    AndroidThreeTen.init(LocalContext.current)
    Box(
        Modifier
            .width(300.dp)
            .height(300.dp)
            .background(color = androidx.compose.ui.graphics.Color.Blue),
    ) {
        messageContextMenu(
            menu =
                MessageContextMenuModel(
                    messageInfoModel = MOCK_MESSAGES[0],
                    menuItems =
                        listOf(
                            MenuItemModel(
                                title = R.string.azure_communication_ui_chat_copy,
                                icon = R.drawable.azure_communication_ui_chat_ic_fluent_copy_20_regular,
                                onClickAction = { },
                            ),
                        ),
                ),
            dispatch = {},
        )
    }
}
