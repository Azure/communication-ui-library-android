package com.azure.android.communication.ui.callingcompositedemoapp.util

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.azure.android.communication.ui.callingcompositedemoapp.R
import org.hamcrest.Matchers.allOf


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
    fun checkParticipantList() {
        val viewIds = Triple(
            R.id.cell_icon,
            R.id.azure_communication_ui_participant_list_avatar,
            R.id.cell_text
        )
        UiTestUtils.check3IemRecyclerViewHolderAtPosition(R.id.bottom_drawer_table, 0, viewIds)
    }

    fun showParticipantList() {
        ViewIsDisplayedResource().waitUntilViewIsDisplayed {
            UiTestUtils.checkViewIdIsDisplayed(R.id.azure_communication_ui_call_floating_header)
        }
        UiTestUtils.clickViewWithId(R.id.azure_communication_ui_call_bottom_drawer_button)
    }

    fun dismissParticipantList() {
        val rootView = Espresso.onView(
            allOf(
                withId(android.R.id.content),
                isDisplayed()
            )
        )
        rootView.perform(ViewActions.click())
    }
}
