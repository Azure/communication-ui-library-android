// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling

import android.content.Context
import com.azure.android.communication.ui.calling.data.CallHistoryRepositoryImpl
import com.azure.android.communication.ui.calling.di.DependencyInjectionContainer
import com.azure.android.communication.ui.calling.di.DependencyInjectionContainerImpl
import com.azure.android.communication.ui.calling.presentation.manager.DebugInfoManager
import com.azure.android.communication.ui.calling.presentation.manager.DebugInfoManagerImpl
import com.azure.android.communication.ui.calling.utilities.TestHelper

internal fun createDependencyInjectionContainerImpl(
    context: Context,
    callComposite: CallComposite,
): DependencyInjectionContainer {
    return DependencyInjectionContainerImpl(
        context,
        callComposite,
        TestHelper.callingSDK,
        TestHelper.videoStreamRendererFactory,
        TestHelper.coroutineContextProvider,
    )
}

internal fun createDebugInfoManager(context: Context): DebugInfoManager {
    return DebugInfoManagerImpl(CallHistoryRepositoryImpl(context))
}
