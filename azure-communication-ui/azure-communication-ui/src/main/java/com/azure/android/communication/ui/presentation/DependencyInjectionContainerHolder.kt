// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation

import androidx.lifecycle.ViewModel
import com.azure.android.communication.ui.di.DependencyInjectionContainer

internal class DependencyInjectionContainerHolder : ViewModel() {
    var container: DependencyInjectionContainer? = null

    override fun onCleared() {
        super.onCleared()
        container = null
    }
}
