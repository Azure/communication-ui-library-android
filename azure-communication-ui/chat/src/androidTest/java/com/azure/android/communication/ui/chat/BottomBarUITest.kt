// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.azure.android.communication.ui.chat.presentation.ui.chat.UITestTags
import com.azure.android.communication.ui.chat.redux.state.ChatStatus
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

internal class BottomBarUITest : BaseUiTest() {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testOnMessageSentInputViewIsCleared() =
        runTest {
            injectDependencies(testScheduler)

            // launch composite
            chatSDK.setChatStatus(ChatStatus.INITIALIZED)
            launchChatComposite()

            // type message
            val message = "hello"
            composeTestRule.onNodeWithTag(UITestTags.MESSAGE_INPUT_BOX).performTextInput(message)
            composeTestRule.onNodeWithTag(UITestTags.MESSAGE_INPUT_BOX).assert(hasText(message))

            // send message
            composeTestRule.onNodeWithTag(UITestTags.MESSAGE_SEND_BUTTON).performClick()

            // assert message is cleared after send
            composeTestRule.onNodeWithTag(UITestTags.MESSAGE_INPUT_BOX).assert(hasText(""))
        }

    @Test
    fun testOnMessageSentFailedInputViewIsNotCleared() =
        runTest {
            injectDependencies(testScheduler)

            // launch composite
            chatSDK.setChatStatus(ChatStatus.INITIALIZATION)
            launchChatComposite()

            // type message
            val message = "hello"
            composeTestRule.onNodeWithTag(UITestTags.MESSAGE_INPUT_BOX).performTextInput(message)
            composeTestRule.onNodeWithTag(UITestTags.MESSAGE_INPUT_BOX).assert(hasText(message))

            // send message
            composeTestRule.onNodeWithTag(UITestTags.MESSAGE_SEND_BUTTON).performClick()

            // assert message is cleared after send
            composeTestRule.onNodeWithTag(UITestTags.MESSAGE_INPUT_BOX).assert(hasText(message))
        }
}
