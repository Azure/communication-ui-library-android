// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.azure.android.communication.ui.callingcompositedemoapp.robots.HomeScreenRobot
import com.azure.android.communication.ui.callingcompositedemoapp.robots.SetupScreenRobot
import com.azure.android.communication.ui.callingcompositedemoapp.util.CallIdentifiersHelper
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CallingCompositeAudioDeviceListTest : BaseUiTest() {

//    @Test
//    fun selectDefaultAudioDevice() {
//        joinGroupSetupScreen()
//            .tapSpeakerIcon()
//            .selectSpeakerAudioDevice(true)
//            .verifyIsSpeakerAudioDevice()
//            .navigateUpFromSetupScreen()
//    }

//    @Test
//    fun selectAndroidAudioDevice() {
//        joinGroupSetupScreen()
//            .tapSpeakerIcon()
//            .selectAndroidAudioDevice(false)
//            .verifyIsAndroidAudioDevice()
//            .navigateUpFromSetupScreen()
//    }

    private fun joinGroupSetupScreen(): SetupScreenRobot {
        val setupScreen = HomeScreenRobot()
            .setGroupIdOrTeamsMeetingUrl(CallIdentifiersHelper.getGroupId())
            .setAcsToken(CallIdentifiersHelper.getACSToken())
            .clickLaunchButton()

        return setupScreen
    }
}
