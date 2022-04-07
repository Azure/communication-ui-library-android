// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.android.communication.ui.callingcompositedemoapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.azure.android.communication.ui.callingcompositedemoapp.robots.HomeScreenRobot
import com.azure.android.communication.ui.callingcompositedemoapp.util.NetworkUtils
import com.azure.android.communication.ui.callingcompositedemoapp.util.TestFixture
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CallingCompositeNetworkTest : BaseUiTest() {

    @Test
    fun testJoinTeamsCallAfterNetworkDisconnected() {
        NetworkUtils.disableNetwork()
        val homeScreen = HomeScreenRobot()
            .setAcsToken(TestFixture.acsToken)
            .clickTeamsMeetingRadioButton()
            .setGroupIdOrTeamsMeetingUrl(TestFixture.teamsUrl)
        joinAfterNetworkDisconnected(homeScreen)
    }

    @Test
    fun testJoinGroupCallAfterNetworkDisconnected() {
        NetworkUtils.disableNetwork()
        val homeScreen = HomeScreenRobot()
            .setAcsToken(TestFixture.acsToken)
            .setGroupIdOrTeamsMeetingUrl(TestFixture.groupId)

        joinAfterNetworkDisconnected(homeScreen)
    }

    private fun joinAfterNetworkDisconnected(homeScreen: HomeScreenRobot) {

        val setupScreen = homeScreen.clickLaunchButton()
        try {
            val callScreen = setupScreen
                .turnCameraOn()
                .clickJoinCallButton()

            setupScreen.dismissNetworkLossBanner()
            setupScreen.navigateUpFromSetupScreen()
            homeScreen.clickAlertDialogOkButton()
        } catch (ex: Throwable) {
            println("Runtime Error: " + ex.message)
            throw ex
        } finally {
            NetworkUtils.enableNetworkThatWasDisabled {
            }
        }
    }
}
