// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.azure.android.communication.ui.calling.service.sdk.CallingSDK
import com.azure.android.communication.ui.calling.utilities.CoroutineContextProvider

internal class DependencyInjectionContainerHolderFactory(
    private val application: Application,
    private val callingSDK: CallingSDK?,
    private val videoStreamRendererFactory: VideoStreamRendererFactory?,
    private val coroutineContextProvider: CoroutineContextProvider?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DependencyInjectionContainerHolder(
            application,
            callingSDK,
            videoStreamRendererFactory,
            coroutineContextProvider
        ) as T
    }
}
