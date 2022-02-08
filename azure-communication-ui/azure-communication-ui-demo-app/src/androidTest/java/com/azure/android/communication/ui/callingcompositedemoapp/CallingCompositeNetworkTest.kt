package com.azure.android.communication.ui.callingcompositedemoapp

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.azure.android.communication.ui.callingcompositedemoapp.util.CompositeUiHelper
import com.azure.android.communication.ui.callingcompositedemoapp.util.NetworkUtils
import com.azure.android.communication.ui.callingcompositedemoapp.util.TestFixture
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CallingCompositeNetworkTest: BaseUiTest() {
    @Test
    fun testJoinTeamsCallAfterNetworkDisconnected() {
        joinAfterNetworkDisconnected(false)
    }

    @Test
    fun testJoinGroupCallAfterNetworkDisconnected() {
        joinAfterNetworkDisconnected()
    }

    private fun joinAfterNetworkDisconnected(isGroupCall: Boolean = true) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Thread.sleep(2000)
        }
        NetworkUtils.disableNetwork()

        CompositeUiHelper.run {
            if (isGroupCall) {
                setGroupIdOrTeamsMeetingUrl(TestFixture.groupId)
            } else {
                clickTeamsMeetingRadioButton()
                setGroupIdOrTeamsMeetingUrl(TestFixture.teamsUrl)
            }

            startAndJoinCall(TestFixture.acsToken, true)
            dismissNetworkLossSnackbar()

            NetworkUtils.enableNetworkThatWasDisabled {
                navigateUpFromSetupScreen()
                clickAlertDialogOkButton()
            }
        }
    }
}