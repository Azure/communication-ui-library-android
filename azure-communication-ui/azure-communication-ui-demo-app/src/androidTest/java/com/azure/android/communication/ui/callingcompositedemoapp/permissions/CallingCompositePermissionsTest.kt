package com.azure.android.communication.ui.callingcompositedemoapp.permissions

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.azure.android.communication.ui.callingcompositedemoapp.robots.HomeScreenRobot
import com.azure.android.communication.ui.callingcompositedemoapp.robots.SetupScreenRobot
import com.azure.android.communication.ui.callingcompositedemoapp.util.CallIdentifiersHelper
import com.azure.android.communication.ui.callingcompositedemoapp.util.RunWhenScreenOffOrLockedRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CallingCompositePermissionsTest : BaseUiPermissionTest() {

    @get:Rule
    val screenLockRule = RunWhenScreenOffOrLockedRule()

    @Test
    fun rejectAudioPermission() {
        joinGroupSetupScreen()
            .permissionDialogAction(true)
            .verifyIsJoinCallButtonDisabled()
            .isAudioPermissionErrorMessageShown()
    }

    @Test
    fun rejectVideoPermission() {
        joinGroupSetupScreen()
            .permissionDialogAction(false)
            .clickCameraButton()
            .permissionDialogAction(true)
            .verifyIsJoinCallButtonEnabled()
            .isVideoPermissionErrorMessageShown()
    }

    @Test
    fun joinCallWithVideoPermissionRejection() {
        joinGroupSetupScreen()
            .permissionDialogAction(false)
            .clickCameraButton()
            .permissionDialogAction(true)
            .clickJoinCallButton()
            .verifyIsCameraButtonDisabled()
            .clickEndCall()
            .clickLeaveCall()
    }

    private fun joinGroupSetupScreen(): SetupScreenRobot {
        return HomeScreenRobot()
            .setGroupIdOrTeamsMeetingUrl(CallIdentifiersHelper.getGroupId())
            .setAcsToken(CallIdentifiersHelper.getACSToken())
            .clickLaunchButton()
    }
}
