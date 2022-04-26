// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.android.communication.ui.callingcompositedemoapp.robots

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import com.azure.android.communication.ui.callingcompositedemoapp.R
import com.azure.android.communication.ui.callingcompositedemoapp.util.UiTestUtils
import com.azure.android.communication.ui.callingcompositedemoapp.util.ViewIsDisplayedResource
import org.hamcrest.Matchers

class CallScreenRobot : ScreenRobot<CallScreenRobot>() {

    fun checkWaitForTeamsMeetingMessage(): CallScreenRobot {
        waitUntilViewIdIsDisplayed(R.id.azure_communication_ui_call_lobby_overlay)

        UiTestUtils.clickViewWithIdAndText(
            R.id.azure_communication_ui_call_lobby_overlay_title,
            "Waiting for host"
        )
        return this
    }

    fun checkParticipantList(): CallScreenRobot {
        val viewIds = Triple(
            R.id.cell_icon,
            R.id.azure_communication_ui_participant_list_avatar,
            R.id.cell_text
        )
        UiTestUtils.check3IemRecyclerViewHolderAtPosition(R.id.bottom_drawer_table, 0, viewIds)
        return this
    }

    fun showParticipantList(): CallScreenRobot {
        waitUntilViewIdIsDisplayed(R.id.azure_communication_ui_call_floating_header)
        UiTestUtils.clickViewWithId(R.id.azure_communication_ui_call_bottom_drawer_button)
        return this
    }

    fun dismissParticipantList(): CallScreenRobot {
        val rootView = Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(android.R.id.content),
                ViewMatchers.isDisplayed()
            )
        )
        rootView.perform(ViewActions.click())
        return this
    }

    fun clickEndCall(): CallScreenRobot {
        val idlingResource = ViewIsDisplayedResource()
        waitUntilViewIdIsDisplayed(R.id.azure_communication_ui_call_call_buttons, idlingResource)
        val endCallButton = waitUntilViewIdWithContentDescriptionIsDisplayed(
            R.id.azure_communication_ui_call_end_call_button,
            "Hang Up"
        )

        endCallButton.perform(ViewActions.click())
        return this
    }

    fun clickLeaveCall() {
        val idlingResource = ViewIsDisplayedResource()
        idlingResource.waitUntilViewIsDisplayed {
            UiTestUtils.checkViewWithTextIsDisplayed("Leave call?")
        }
        UiTestUtils.clickViewWithIdAndText(
            R.id.cell_text,
            "Leave"
        )
    }
}
