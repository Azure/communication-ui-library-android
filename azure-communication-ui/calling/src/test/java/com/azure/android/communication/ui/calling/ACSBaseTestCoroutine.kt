// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling

import com.azure.android.communication.ui.calling.helper.MainCoroutineRule
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Rule

open class ACSBaseTestCoroutine {
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    fun runScopedTest(body: suspend TestScope.() -> Unit) = mainCoroutineRule.scope.runTest { body() }
}
