// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat

import androidx.test.rule.GrantPermissionRule
import com.azure.android.communication.ui.chat.configuration.ChatConfiguration
import com.azure.android.communication.ui.chat.mocking.TestChatSDK
import com.azure.android.communication.ui.chat.mocking.TestContextProvider
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.After
import org.junit.Rule

/**
 * Basic functionality required for our UI tests: UI elements, permissions, dependency injection.
 */
internal open class BaseUiTest {
    
    @Rule
    @JvmField
    var grantPermissionRule: GrantPermissionRule
    
    private val basePermissionList = arrayOf(
        "android.permission.ACCESS_NETWORK_STATE"
    )
    
    init {
        grantPermissionRule = GrantPermissionRule.grant(*basePermissionList)
    }
    
    lateinit var chatSDK: TestChatSDK
    lateinit var chatConfiguration: ChatConfiguration
    
    // Can't be @Before due to requiring a specific test scheduler.
    fun injectDependencies(scheduler: TestCoroutineScheduler) {
        val coroutineContextProvider = TestContextProvider(UnconfinedTestDispatcher(scheduler))
        chatSDK = TestChatSDK(chatConfiguration, coroutineContextProvider)
        
        TestHelper.chatSDK = chatSDK
        TestHelper.coroutineContextProvider = coroutineContextProvider
    }
    
    @After
    fun teardown() {
        TestHelper.chatSDK = null
        TestHelper.coroutineContextProvider = null
    }
}
