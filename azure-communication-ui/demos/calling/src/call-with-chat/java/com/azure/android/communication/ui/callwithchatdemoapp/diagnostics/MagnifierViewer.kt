// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callwithchatdemoapp.diagnostics

import android.app.Application
import com.microsoft.office.outlook.magnifierlib.Magnifier

class MagnifierViewer private constructor(
    private val context: Application,
) {
    companion object {
        private lateinit var magnifierViewer: MagnifierViewer

        fun getMagnifierViewer(context: Application): MagnifierViewer {
            if (!this::magnifierViewer.isInitialized) {
                magnifierViewer = MagnifierViewer(context)
            }
            return magnifierViewer
        }
    }

    fun show() {
        Magnifier.startMagnifierViewer(context, null)
    }

    fun hide() {
        Magnifier.stopMagnifierViewer()
    }
}
