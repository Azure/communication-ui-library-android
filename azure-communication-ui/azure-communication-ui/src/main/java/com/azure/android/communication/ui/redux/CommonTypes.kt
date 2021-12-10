// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.redux

import com.azure.android.communication.ui.redux.action.Action

internal typealias Dispatch = (Action) -> Unit
internal typealias Middleware<State> = (store: Store<State>) -> (next: Dispatch) -> Dispatch
