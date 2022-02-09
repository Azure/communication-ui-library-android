// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.android.communication.ui.callingcompositedemoapp.robots

import com.azure.android.communication.ui.callingcompositedemoapp.R
import com.azure.android.communication.ui.callingcompositedemoapp.util.UiTestUtils
import com.azure.android.communication.ui.callingcompositedemoapp.util.ViewIsDisplayedResource

class SetupScreenRobot : ScreenRobot<SetupScreenRobot>() {

    fun turnCameraOn(): SetupScreenRobot {
        UiTestUtils.run {
            val viewDisplayResource = ViewIsDisplayedResource()
            waitUntilViewIdIsDisplayed(
                R.id.azure_communication_ui_setup_camera_button,
                viewDisplayResource
            )

            val cameraButtonText = getTextFromButtonView(R.id.azure_communication_ui_setup_camera_button)
            if (cameraButtonText == "Video off") {
                clickViewWithIdAndText(
                    R.id.azure_communication_ui_setup_camera_button,
                    "Video off"
                )
            }

            waitUntilViewIdIsDisplayed(R.id.azure_communication_ui_setup_local_video_holder, viewDisplayResource)
            waitUntilViewIdIsNotDisplayed(R.id.azure_communication_ui_setup_default_avatar, viewDisplayResource)
        }
        return this
    }

    @Throws(RuntimeException::class)
    fun clickJoinCallButton(): CallScreenRobot {
        val idlingResource = ViewIsDisplayedResource()

        idlingResource.waitUntilViewIsDisplayed {
            UiTestUtils.checkViewIdIsDisplayed(R.id.azure_communication_ui_setup_join_call_button)
        }
        idlingResource.waitUntilViewIsDisplayed {
            UiTestUtils.checkViewIdIsDisplayed(R.id.azure_communication_ui_setup_start_call_button_text)
        }
        UiTestUtils.clickViewWithId(R.id.azure_communication_ui_setup_join_call_button)
        return CallScreenRobot()
    }

    fun dismissNetworkLossBanner(): SetupScreenRobot {
        waitUntilViewIdIsDisplayed(R.id.snackbar_action)
        UiTestUtils.clickViewWithIdAndText(R.id.snackbar_action, "Dismiss")
        return this
    }

    fun dismissJoinFailureBanner(): SetupScreenRobot {
        waitUntilViewIdIsDisplayed(R.id.snackbar_action)
        UiTestUtils.checkViewWithTextIsDisplayed("Unable to join the call due to an error.")
        UiTestUtils.clickViewWithIdAndText(R.id.snackbar_action, "Dismiss")
        return this
    }

    fun navigateUpFromSetupScreen() {
        waitUntilViewIdIsDisplayed(R.id.action_bar_container)
        UiTestUtils.navigateUp()
    }
}
