// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.arch.redux

typealias Dispatch = (Any) -> Unit
typealias Middleware = (store: GenericStore) -> (next: Dispatch) -> Dispatch
