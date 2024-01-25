// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.utilities

import androidx.lifecycle.LifecycleCoroutineScope
import com.azure.android.communication.ui.calling.redux.Store
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

// Boilerplate that is repeated a lot
internal fun <T> Store<T>.collect(
    scope: LifecycleCoroutineScope,
    function: (T) -> Unit
) {
    scope.launch {
        this@collect.getStateFlow().collect {
            function(it)
        }
    }
}

// These come together quite often, lets work on them as a pair.
internal fun <T> Pair<LifecycleCoroutineScope, Store<T>>.collect(
    function: (T) -> Unit
) =
    second.collect(first, function)

// We also type launch way to much, this will let it be clean.
fun LifecycleCoroutineScope.launchAll(vararg blocks: suspend () -> Unit) {
    launch {
        blocks.forEach { block ->
            launch { block() }
        }
    }
}
