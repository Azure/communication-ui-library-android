// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.helper

import kotlinx.coroutines.test.StandardTestDispatcher

internal class StandardTestContextProvider :
    BaseTestContextProvider(StandardTestDispatcher(name = "StandardTestContextProvider"))
