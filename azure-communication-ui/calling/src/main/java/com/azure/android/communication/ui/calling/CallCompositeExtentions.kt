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

internal fun createDebugInfoManager(context: Context): DebugInfoManager {
    return DebugInfoManagerImpl(CallHistoryRepositoryImpl(context))
}
