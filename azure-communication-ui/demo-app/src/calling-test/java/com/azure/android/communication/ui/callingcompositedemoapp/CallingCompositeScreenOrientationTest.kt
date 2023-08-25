package com.azure.android.communication.ui.callingcompositedemoapp

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.azure.android.communication.ui.callingcompositedemoapp.robots.HomeScreenRobot
import com.azure.android.communication.ui.callingcompositedemoapp.util.CallIdentifiersHelper
import com.azure.android.communication.ui.callingcompositedemoapp.util.RunWhenScreenOffOrLockedRule
import com.azure.android.communication.ui.callingcompositedemoapp.util.TestFixture
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
@RunWith(AndroidJUnit4::class)
class CallingCompositeScreenOrientationTest : BaseUiTest() {

    @get:Rule
    val screenLockRule = RunWhenScreenOffOrLockedRule()

    private fun setSetupScreenOrientation(orientation: String) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
            .edit()
            .putString(SETUP_SCREEN_ORIENTATION_SHARED_PREF_KEY, orientation)
            .apply()
    }

    private fun setCallScreenOrientation(orientation: String) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
            .edit()
            .putString(CALL_SCREEN_ORIENTATION_SHARED_PREF_KEY, orientation)
            .apply()
    }

    @Test
    fun testSetOrientationPortrait() {
        testSetupOrientation("PORTRAIT")
        testCallOrientation("PORTRAIT")
    }

    @Test
    fun testSetOrientationLandscape() {
        testSetupOrientation("LANDSCAPE")
    }

    @Test
    fun testSetOrientationUserLandscape() {
        testSetupOrientation("USER_LANDSCAPE")
    }

    @Test
    fun testSetOrientationReverseLandscape() {
        testSetupOrientation("REVERSE_LANDSCAPE")
    }

    @Test
    fun testSetOrientationFullSensor() {
        testSetupOrientation("FULL_SENSOR")
    }

    @Test
    fun testSetOrientationUser() {
        testSetupOrientation("USER")
    }

    private fun testSetupOrientation(orientation: String) {
        setSetupScreenOrientation(orientation)
        val homeScreen = HomeScreenRobot()
        val settingsScreen = homeScreen.clickSettings()

        settingsScreen.selectSetupScreenOrientationDropDown(orientation)

        val setupScreen = homeScreen
            .clickTeamsMeetingRadioButton()
            .setGroupIdOrTeamsMeetingUrl(TestFixture.teamsUrl)
            .setAcsToken(CallIdentifiersHelper.getACSToken())
            .clickLaunchButton()

        setupScreen.navigateUpFromSetupScreen()
    }

    private fun testCallOrientation(orientation: String) {
        setCallScreenOrientation(orientation)
        val homeScreen = HomeScreenRobot()
        val settingsScreen = homeScreen.clickSettings()

        settingsScreen.selectCallScreenOrientationDropDown(orientation)

        val setupScreen = homeScreen
            .clickGroupCallRadioButton()
            .setGroupIdOrTeamsMeetingUrl(TestFixture.groupId)
            .setAcsToken(CallIdentifiersHelper.getACSToken())
            .clickLaunchButton()

        val callScreen = setupScreen.clickJoinCallButton()
        callScreen
            .clickEndCall()
            .clickLeaveCall()
    }
}
