// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

internal class CallCompositeActivityViewModelFactory(
    private val application: Application,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CallCompositeActivityViewModel(
            application,
        ) as T
    }
}
