// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation

import android.content.Context
import android.content.Intent

internal class UIManager(
    private val parentContext: Context,
) {

    fun start() {
        val intent = Intent(parentContext, MainActivity::class.java)
        parentContext.startActivity(intent)
    }
}
