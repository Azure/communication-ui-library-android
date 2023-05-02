// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling

import android.app.Application
import android.content.Context
import com.azure.android.communication.ui.calling.data.CallHistoryRepositoryImpl
import com.azure.android.communication.ui.calling.di.DependencyInjectionContainer
import com.azure.android.communication.ui.calling.di.DependencyInjectionContainerImpl
import com.azure.android.communication.ui.calling.logger.DefaultLogger
import com.azure.android.communication.ui.calling.presentation.DependencyInjectionContainerHolder
import com.azure.android.communication.ui.calling.presentation.VideoStreamRendererFactory
import com.azure.android.communication.ui.calling.presentation.manager.DebugInfoManager
import com.azure.android.communication.ui.calling.presentation.manager.DebugInfoManagerImpl
import com.azure.android.communication.ui.calling.service.sdk.CallingSDK
import com.azure.android.communication.ui.calling.utilities.CoroutineContextProvider

internal fun createDebugInfoManager(context: Context): DebugInfoManager {
    return DebugInfoManagerImpl(CallHistoryRepositoryImpl(context, DefaultLogger()))
}

internal fun createDependencyInjectionContainer(
        callComposite: CallComposite,
        applicationContext: Context,
        customCallingSDK: CallingSDK?,
        customVideoStreamRendererFactory: VideoStreamRendererFactory?,
        customCoroutineContextProvider: CoroutineContextProvider?,
) : DependencyInjectionContainer {
    return DependencyInjectionContainerImpl(
            applicationContext,
            callComposite,
            customCallingSDK,
            customVideoStreamRendererFactory,
            customCoroutineContextProvider,
    )
}

internal fun CallComposite.getDIContainer() : DependencyInjectionContainer {
    return this.dependencyInjectionContainer
}

internal fun CallComposite.getInstanceId() : Int {
    return this.instanceId
}