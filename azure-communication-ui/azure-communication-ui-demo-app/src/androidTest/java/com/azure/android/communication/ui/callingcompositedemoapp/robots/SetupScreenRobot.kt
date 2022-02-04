package com.azure.android.communication.ui.callingcompositedemoapp.robots

import com.azure.android.communication.ui.callingcompositedemoapp.R
import com.azure.android.communication.ui.callingcompositedemoapp.util.UiTestUtils
import com.azure.android.communication.ui.callingcompositedemoapp.util.ViewIsDisplayedResource

class SetupScreenRobot: ScreenRobot<SetupScreenRobot>() {

    fun turnCameraOn(): SetupScreenRobot {
        UiTestUtils.run {
            val viewDisplayResource = ViewIsDisplayedResource()
            waitUntilViewIdIsDisplayed(R.id.azure_communication_ui_setup_camera_button,
                viewDisplayResource)

            val cameraButtonText = getTextFromButtonView(R.id.azure_communication_ui_setup_camera_button)
            if (cameraButtonText == "Video off") {
                clickViewWithIdAndText(
                    R.id.azure_communication_ui_setup_camera_button,
                    "Video off"
                )
            }

            waitUntilViewIdIsDisplayed(R.id.azure_communication_ui_setup_local_video_holder)
            waitUntilViewIdIsDisplayed(R.id.azure_communication_ui_setup_default_avatar)
        }
        return this
    }

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
}