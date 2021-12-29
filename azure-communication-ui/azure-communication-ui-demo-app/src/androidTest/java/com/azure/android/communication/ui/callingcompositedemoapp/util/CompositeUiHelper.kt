package com.azure.android.communication.ui.callingcompositedemoapp.util

import androidx.test.espresso.action.ViewActions
import com.azure.android.communication.ui.callingcompositedemoapp.R

object CompositeUiHelper {
    fun setGroupId(groupId: String) {
        val idlingResource = ViewIsDisplayedResource()
        val appCompatEditText = idlingResource.waitUntilViewIsDisplayed {
            UiTestUtils.checkViewIdIsDisplayed(R.id.groupIdOrTeamsMeetingLinkText)
        }
        appCompatEditText.perform(ViewActions.replaceText(groupId))
        appCompatEditText.perform(ViewActions.closeSoftKeyboard())
    }

    fun setAcsToken(token: String) {
        val appCompatEditText = UiTestUtils.checkViewIdIsDisplayed(R.id.acsTokenText)
        ViewIsDisplayedResource().waitUntilViewIsDisplayed {
            appCompatEditText
        }
        appCompatEditText.perform(ViewActions.replaceText(token))
        appCompatEditText.perform(ViewActions.closeSoftKeyboard())
    }

    fun clickLaunchButton() {
        ViewIsDisplayedResource().waitUntilViewIsDisplayed {
            UiTestUtils.checkViewIdIsDisplayed(R.id.launchButton)
        }
        UiTestUtils.clickViewWithId(R.id.launchButton)
    }

    fun toggleCameraButton() {
        ViewIsDisplayedResource().waitUntilViewIsDisplayed {
            UiTestUtils.checkViewIdIsDisplayed(R.id.azure_communication_ui_setup_camera_button)
        }
        Thread.sleep(3000)
        UiTestUtils.clickViewWithId(R.id.azure_communication_ui_setup_camera_button)
    }

    fun clickJoinCallButton() {
        val idlingResource = ViewIsDisplayedResource()

        idlingResource.waitUntilViewIsDisplayed {
            UiTestUtils.checkViewIdIsDisplayed(R.id.azure_communication_ui_setup_join_call_button)
        }
        idlingResource.waitUntilViewIsDisplayed {
            UiTestUtils.checkViewIdIsDisplayed(R.id.azure_communication_ui_setup_start_call_button_text)
        }
        UiTestUtils.clickViewWithId(R.id.azure_communication_ui_setup_join_call_button)
    }
}