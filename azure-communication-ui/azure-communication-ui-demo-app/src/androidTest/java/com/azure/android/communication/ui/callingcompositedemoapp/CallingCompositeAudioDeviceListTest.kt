package com.azure.android.communication.ui.callingcompositedemoapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.azure.android.communication.ui.callingcompositedemoapp.robots.HomeScreenRobot
import com.azure.android.communication.ui.callingcompositedemoapp.robots.SetupScreenRobot
import com.azure.android.communication.ui.callingcompositedemoapp.util.TestFixture
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CallingCompositeAudioDeviceListTest : BaseUiTest() {

    @Test
    fun selectDefaultAudioDevice() {
        val setupScreen = joinGroupSetupScreen()
        setupScreen.selectAndroidAudioDevice()
    }

    @Test
    fun selectSpeakerAudioDevice() {
        val setupScreen = joinGroupSetupScreen()
        setupScreen.selectSpeakerAudioDevice()
    }
    private fun joinGroupSetupScreen(): SetupScreenRobot {
        val setupScreen = HomeScreenRobot()
            .setGroupIdOrTeamsMeetingUrl(TestFixture.groupId)
            .setAcsToken(TestFixture.acsToken)
            .clickLaunchButton()

        setupScreen.turnCameraOn()
        setupScreen.tapSpeakerIcon()

        return setupScreen
    }
}