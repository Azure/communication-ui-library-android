package com.azure.android.communication.ui.chat

import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule

import org.junit.Rule
import org.junit.Test

internal class ChatScreenUiTest : BaseUiTest() {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    @Test
    fun testSendButtonContentDescription() {
        launchChatComposite()
        composeTestRule.onNode(hasContentDescription("Send Message Button"))
    }
    
    @Test
    fun testInputLayoutContentDescription() {
        launchChatComposite()
        composeTestRule.onNode(hasContentDescription("Message Input Field"))
    }
}