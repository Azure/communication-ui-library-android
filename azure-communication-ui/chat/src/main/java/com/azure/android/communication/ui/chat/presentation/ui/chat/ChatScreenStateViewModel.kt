// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

internal class ChatScreenStateViewModel : ViewModel() {
    var messageInputTextState = mutableStateOf("")
}
