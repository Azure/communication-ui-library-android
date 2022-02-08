// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.android.communication.ui.callingcompositedemoapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.azure.android.communication.ui.callingcompositedemoapp.util.CompositeUiHelper
import com.azure.android.communication.ui.callingcompositedemoapp.util.NetworkUtils
import com.azure.android.communication.ui.callingcompositedemoapp.util.TestFixture
import com.azure.android.communication.ui.callingcompositedemoapp.util.ViewIsDisplayedResource
import org.junit.AfterClass
import org.junit.Assume
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CallingCompositeNetworkTest: BaseUiTest() {
    companion object {
        @BeforeClass
        @JvmStatic
        fun networkTestSetup() {
            ViewIsDisplayedResource.TIMED_OUT_VALUE = 60000L
        }

        @AfterClass
        @JvmStatic
        fun networkTestTeardown() {
            ViewIsDisplayedResource.TIMED_OUT_VALUE = 30000L
        }
    }

    @Test
    fun testJoinTeamsCallAfterNetworkDisconnected() {
        joinAfterNetworkDisconnected(false)
    }

    @Test
    fun testJoinGroupCallAfterNetworkDisconnected() {
        joinAfterNetworkDisconnected()
    }

    private fun joinAfterNetworkDisconnected(isGroupCall: Boolean = true) {

        NetworkUtils.disableNetwork()

        CompositeUiHelper.run {
            if (isGroupCall) {
                setGroupIdOrTeamsMeetingUrl(TestFixture.groupId)
            } else {
                clickTeamsMeetingRadioButton()
                setGroupIdOrTeamsMeetingUrl(TestFixture.teamsUrl)
            }

            try {
                startAndJoinCall(TestFixture.acsToken, true)
                dismissNetworkLossSnackbar()
            } catch (ex: Throwable) {
                println("err: " + ex.message)
                throw ex
            } finally {
                NetworkUtils.enableNetworkThatWasDisabled {
                    navigateUpFromSetupScreen()
                    clickAlertDialogOkButton()
                }
            }
        }
    }
}