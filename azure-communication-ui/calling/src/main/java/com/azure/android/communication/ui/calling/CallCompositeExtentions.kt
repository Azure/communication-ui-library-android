// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling

import android.content.Context
import com.azure.android.communication.ui.calling.data.CallHistoryRepositoryImpl
import com.azure.android.communication.ui.calling.logger.DefaultLogger
import com.azure.android.communication.ui.calling.presentation.manager.DebugInfoManager
import com.azure.android.communication.ui.calling.presentation.manager.DebugInfoManagerImpl
import java.io.File

internal fun CallComposite.getDiContainer() =
    CallComposite.diContainer

internal fun CallComposite.onExit() {
    CallComposite.diContainer = null
}
internal fun createDebugInfoManager(
    context: Context,
    getLogFiles: () -> List<File>
): DebugInfoManager =
    DebugInfoManagerImpl(CallHistoryRepositoryImpl(context, DefaultLogger()), getLogFiles)
