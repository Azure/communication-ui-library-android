package com.azure.android.communication.ui

import com.azure.android.communication.ui.helper.MainCoroutineRule
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Rule

open class ACSBaseUnitTest {
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    fun runScopedTest(body: suspend TestScope.() -> Unit) = mainCoroutineRule.scope.runTest { body() }
}