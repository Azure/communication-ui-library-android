// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation

import android.content.Context
import android.content.Intent

internal class UIManager {

    companion object {
        /// Starts the Call Composite Activity
        fun start(context : Context, id: Int) {
            Intent(context, MainActivity::class.java).run {
                putExtra(MainActivity.KEY_INSTANCE_ID, id)
                context.startActivity(this)
            }
        }
    }
}
