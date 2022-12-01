package com.azure.android.communication.ui.chat

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertAny
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.azure.android.communication.ui.chat.presentation.ui.chat.UITestTags
import com.azure.android.communication.ui.chat.redux.state.ChatStatus
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal class MessageViewTest : BaseUiTest() {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testOnMessageContentWithCurrentTime() = runTest {
        injectDependencies(testScheduler)

        // launch composite
        chatSDK.setChatStatus(ChatStatus.INITIALIZED)
        launchChatComposite()

        // send message
        val message = "hello"
        composeTestRule.onNodeWithTag(UITestTags.MESSAGE_INPUT_BOX).performTextInput(message)
        composeTestRule.onNodeWithTag(UITestTags.MESSAGE_SEND_BUTTON).performClick()

        // current system time
        val formattedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("h:mm a"))

        // assert message is cleared after send
        composeTestRule.onNodeWithTag(UITestTags.MESSAGE_TIME_CONTENT).assert(hasText(formattedDate))
    }

    @Test
    fun testOnMessageContentWithNoCurrentTime() = runTest {
        injectDependencies(testScheduler)

        // launch composite
        chatSDK.setChatStatus(ChatStatus.INITIALIZED)
        launchChatComposite()

        // send message
        val message = "hello"
        composeTestRule.onNodeWithTag(UITestTags.MESSAGE_INPUT_BOX).performTextInput(message)
        composeTestRule.onNodeWithTag(UITestTags.MESSAGE_SEND_BUTTON).performClick()

        val secondMessage = "HELLO"
        composeTestRule.onNodeWithTag(UITestTags.MESSAGE_INPUT_BOX).performTextInput(secondMessage)
        composeTestRule.onNodeWithTag(UITestTags.MESSAGE_SEND_BUTTON).performClick()

        // assert message is cleared after send
        composeTestRule.onAllNodesWithTag(UITestTags.MESSAGE_BASIC_CONTENT).assertAny(hasText(secondMessage))
    }
}
