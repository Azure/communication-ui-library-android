// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment

import com.azure.android.communication.ui.presentation.fragment.calling.CallingViewModel
import com.azure.android.communication.ui.presentation.fragment.setup.SetupViewModel

internal data class ViewModelFactory(
    val callViewModel: CallingViewModel,
    val setupViewModel: SetupViewModel,
)
