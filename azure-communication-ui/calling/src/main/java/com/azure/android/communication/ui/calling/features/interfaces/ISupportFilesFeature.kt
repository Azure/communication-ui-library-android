// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.features.interfaces

import android.content.Context
import com.azure.android.communication.calling.CallClient
import com.azure.android.communication.ui.calling.features.ACSFeature
import java.io.File

abstract class ISupportFilesFeature : ACSFeature {
    abstract fun getSupportFiles(client: CallClient, context: Context): List<File>
}
