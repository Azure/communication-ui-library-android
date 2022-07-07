package com.azure.android.communication.ui.calling.presentation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.azure.android.communication.ui.calling.service.sdk.CallingSDK

internal class DependencyInjectionContainerHolderFactory(
    private val application: Application,
    private val callingSDK: CallingSDK?,
    private val videoStreamRendererFactory: VideoStreamRendererFactory?,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DependencyInjectionContainerHolder(
            application,
            callingSDK,
            videoStreamRendererFactory
        ) as T
    }
}
