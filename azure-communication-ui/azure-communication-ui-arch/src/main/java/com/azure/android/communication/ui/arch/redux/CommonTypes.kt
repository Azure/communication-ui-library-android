// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.arch.redux

internal typealias Dispatch = (Any) -> Unit
internal typealias Middleware<State> = (store: Store<State>) -> (next: Dispatch) -> Dispatch
