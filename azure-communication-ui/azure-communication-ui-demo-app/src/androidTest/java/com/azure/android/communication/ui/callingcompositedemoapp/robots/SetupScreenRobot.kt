// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.robots

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.test.espresso.action.ViewActions.click
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiSelector
import com.azure.android.communication.ui.callingcompositedemoapp.Localize
import com.azure.android.communication.ui.callingcompositedemoapp.R
import com.azure.android.communication.ui.callingcompositedemoapp.util.UiTestUtils
import com.azure.android.communication.ui.callingcompositedemoapp.util.UiTestUtils.checkViewIdAndIsNotEnabled
import com.azure.android.communication.ui.callingcompositedemoapp.util.ViewIsDisplayedResource
import junit.framework.Assert.assertTrue

class SetupScreenRobot : ScreenRobot<SetupScreenRobot>() {

    fun tapMicButton(micText: String = Localize.English.micText): SetupScreenRobot {
        val micButton = waitUntilTextOnViewIsDisplayed(
            R.id.azure_communication_ui_setup_audio_button,
            micText
        )

        micButton.perform(click())
        return this
    }

    fun tapSpeakerIcon(): SetupScreenRobot {
        val speakerButton = waitUntilTextOnViewIsDisplayed(
            R.id.azure_communication_ui_setup_audio_device_button,
            "Android"
        )

        speakerButton.perform(click())
        return this
    }

    fun verifyIsAndroidAudioDevice(): SetupScreenRobot {
        verifyAudioDevice("Android")
        return this
    }

    fun verifyIsSpeakerAudioDevice(): SetupScreenRobot {
        verifyAudioDevice("Speaker")
        return this
    }

    fun verifyIsJoinCallButtonDisabled(): SetupScreenRobot {
        checkViewIdAndIsNotEnabled(R.id.azure_communication_ui_setup_join_call_button, "Join call")
        return this
    }

    fun isAudioPermissionErrorMessageShown(): SetupScreenRobot {
        verifyErrorMessage("Your audio is disabled")
        return this
    }

    private fun verifyAudioDevice(deviceText: String) {
        waitUntilViewIdIsNotDisplayed(R.id.bottom_drawer_table)
        waitUntilViewIdIsDisplayed(R.id.azure_communication_ui_setup_audio_device_button)
        val text =
            UiTestUtils.getTextFromButtonView(R.id.azure_communication_ui_setup_audio_device_button)
        assertTrue(text == deviceText)
    }

    private fun verifyErrorMessage(errorText: String) {
        waitUntilViewIdIsDisplayed(R.id.azure_communication_ui_setup_missing_text)
        val text =
            UiTestUtils.getTextFromTextView(R.id.azure_communication_ui_setup_missing_text)
        assertTrue(text.contains(errorText))
    }

    fun selectAndroidAudioDevice(isSelected: Boolean): SetupScreenRobot {
        selectAudioDevice(
            R.drawable.azure_communication_ui_calling_ic_fluent_speaker_2_24_regular_composite_button_filled,
            "Android",
            isSelected
        )
        return this
    }

    fun selectSpeakerAudioDevice(isSelected: Boolean = false): SetupScreenRobot {
        selectAudioDevice(
            R.drawable.azure_communication_ui_calling_ic_fluent_speaker_2_24_filled_composite_button_enabled,
            "Speaker",
            false
        )
        return this
    }

    private fun selectAudioDevice(@DrawableRes iconId: Int, text: String, isSelected: Boolean) {
        val audioDeviceList = waitUntilAllViewIdIsAreDisplayed(R.id.cell_text)
        UiTestUtils.clickBottomCellViewHolder(R.id.bottom_drawer_table, iconId, text, isSelected)
    }

    fun permissionDialogAction(deny: Boolean): SetupScreenRobot {
        Thread.sleep(3000)
        val actionButtonIndex = if (deny) 2 else 1

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val device: UiDevice = UiDevice.getInstance(getInstrumentation())
                val allowPermissions: UiObject = device.findObject(
                    UiSelector()
                        .clickable(true)
                        .checkable(false)
                        .index(actionButtonIndex)
                )
                if (allowPermissions.exists()) {
                    allowPermissions.click()
                }
            }
        } catch (_: UiObjectNotFoundException) {
        }

        return this
    }

    fun turnCameraOn(videoOffText: String = Localize.English.videoOffText): SetupScreenRobot {
        UiTestUtils.run {
            val viewDisplayResource = ViewIsDisplayedResource()
            waitUntilViewIdIsDisplayed(
                R.id.azure_communication_ui_setup_camera_button,
                viewDisplayResource
            )
            waitUntilViewIdIsDisplayed(
                R.id.azure_communication_ui_setup_audio_button,
                viewDisplayResource
            )
            waitUntilViewIdIsDisplayed(
                R.id.azure_communication_ui_setup_audio_device_button,
                viewDisplayResource
            )

            val cameraButtonText =
                getTextFromButtonView(R.id.azure_communication_ui_setup_camera_button)
            if (cameraButtonText == videoOffText) {
                clickViewWithIdAndText(
                    R.id.azure_communication_ui_setup_camera_button,
                    videoOffText
                )
            }

            waitUntilViewIdIsDisplayed(
                R.id.azure_communication_ui_setup_local_video_holder,
                viewDisplayResource
            )
            waitUntilViewIdIsNotDisplayed(
                R.id.azure_communication_ui_setup_default_avatar,
                viewDisplayResource
            )
        }
        return this
    }

    @Throws(RuntimeException::class)
    fun clickJoinCallButton(): CallScreenRobot {
        val idlingResource = ViewIsDisplayedResource()
        UiTestUtils.run {
            idlingResource.waitUntilViewIsDisplayed {
                checkViewIdIsDisplayed(R.id.azure_communication_ui_setup_join_call_button)
            }
            idlingResource.waitUntilViewIsDisplayed {
                checkViewIdIsDisplayed(R.id.azure_communication_ui_setup_start_call_button_text)
            }
            clickViewWithId(R.id.azure_communication_ui_setup_join_call_button)
        }

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
