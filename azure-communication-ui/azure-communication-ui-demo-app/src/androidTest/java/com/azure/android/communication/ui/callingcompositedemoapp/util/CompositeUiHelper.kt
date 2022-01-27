// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.android.communication.ui.callingcompositedemoapp.util

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.azure.android.communication.ui.callingcompositedemoapp.R
import org.hamcrest.Matchers.allOf
import org.junit.Assert

object CompositeUiHelper {

    fun setGroupIdOrTeamsMeetingUrl(groupIdOrTeamsMeetingUrl: String) {
        if (groupIdOrTeamsMeetingUrl.isBlank()) return
        val idlingResource = ViewIsDisplayedResource()
        val editTextInteraction = idlingResource.waitUntilViewIsDisplayed {
            UiTestUtils.checkViewIdIsDisplayed(R.id.groupIdOrTeamsMeetingLinkText)
        }
        editTextInteraction.perform(ViewActions.replaceText(groupIdOrTeamsMeetingUrl))
        editTextInteraction.perform(ViewActions.closeSoftKeyboard())
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

    fun turnCameraOn() {
        UiTestUtils.run {
            ViewIsDisplayedResource().waitUntilViewIsDisplayed {
                checkViewIdIsDisplayed(R.id.azure_communication_ui_setup_camera_button)
            }
            val cameraButtonText = getTextFromButtonView(R.id.azure_communication_ui_setup_camera_button)
            if (cameraButtonText == "Video off") {
                clickViewWithIdAndText(
                    R.id.azure_communication_ui_setup_camera_button,
                    "Video off"
                )
            }
            val viewDisplayResource = ViewIsDisplayedResource()
            viewDisplayResource.waitUntilViewIsDisplayed {
                checkViewIdIsDisplayed(R.id.azure_communication_ui_setup_local_video_holder)
            }
            viewDisplayResource.waitUntilViewIsDisplayed {
                checkViewIdIsNotDisplayed(R.id.azure_communication_ui_setup_default_avatar)
            }
        }
    }

    fun clickTeamsMeetingRadioButton() {
        ViewIsDisplayedResource().waitUntilViewIsDisplayed {
            UiTestUtils.checkViewIdAndTextIsDisplayed(
                R.id.teamsMeetingRadioButton,
                R.string.teamsMeetingLabel
            )
        }
        UiTestUtils.clickViewWithId(R.id.teamsMeetingRadioButton)
    }

    fun checkWaitForTeamsMeetingMessage() {
        ViewIsDisplayedResource().waitUntilViewIsDisplayed {
            UiTestUtils.checkViewIdIsDisplayed(R.id.azure_communication_ui_call_lobby_overlay)
        }
        UiTestUtils.clickViewWithIdAndText(
            R.id.azure_communication_ui_call_lobby_overlay_title,
            "Waiting for host"
        )
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

    fun clickEndCall() {
        val idlingResource = ViewIsDisplayedResource()
        idlingResource.waitUntilViewIsDisplayed {
            UiTestUtils.checkViewIdIsDisplayed(R.id.azure_communication_ui_call_call_buttons)
        }
        val endCallButton = idlingResource.waitUntilViewIsDisplayed {
            UiTestUtils.checkViewIdWithContentDescriptionIsDisplayed(
                R.id.azure_communication_ui_call_end_call_button,
                "Hang Up"
            )
        }
        endCallButton.perform(ViewActions.click())
    }

    fun clickLeaveCall() {
        val idlingResource = ViewIsDisplayedResource()
        idlingResource.waitUntilViewIsDisplayed {
            UiTestUtils.checkViewIdIsDisplayed(R.id.azure_communication_ui_call_leave_confirm)
        }
        UiTestUtils.clickViewWithIdAndText(R.id.azure_communication_ui_call_leave_confirm, "Leave call")
    }

    fun startAndJoinCall(acsToken: String, videoEnabled: Boolean) {
        Assert.assertTrue("empty token! ", acsToken.isNotBlank())
        setAcsToken(acsToken)
        clickLaunchButton()

        if (videoEnabled) {
            turnCameraOn()
        }
        clickJoinCallButton()
    }

    fun dismissNetworkLossSnackbar() {
        ViewIsDisplayedResource().waitUntilViewIsDisplayed {
            UiTestUtils.checkViewIdIsDisplayed(R.id.snackbar_action)
        }
        UiTestUtils.clickViewWithIdAndText(R.id.snackbar_action, "Dismiss")
    }

    fun navigateUpFromSetupScreen() {
        ViewIsDisplayedResource().waitUntilViewIsDisplayed {
            UiTestUtils.checkViewIdIsDisplayed(R.id.action_bar_container)
        }
        UiTestUtils.navigateUp()
    }

    fun clickAlertDialogOkButton() {
        ViewIsDisplayedResource().waitUntilViewIsDisplayed {
            UiTestUtils.checkViewIdIsDisplayed(android.R.id.button1)
        }
        UiTestUtils.clickViewWithIdAndText(android.R.id.button1, "OK")
    }
}
