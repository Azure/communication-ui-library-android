// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.arch.redux

interface Reducer<S> {
    fun reduce(state: S, action: Any): S
}
