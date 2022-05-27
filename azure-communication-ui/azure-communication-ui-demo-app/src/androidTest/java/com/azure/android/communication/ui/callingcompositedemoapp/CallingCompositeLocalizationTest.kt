package com.azure.android.communication.ui.callingcompositedemoapp

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.azure.android.communication.ui.callingcompositedemoapp.robots.HomeScreenRobot
import com.azure.android.communication.ui.callingcompositedemoapp.util.*
import org.junit.After
import org.junit.Rule
import org.junit.Test
import java.util.*

class CallingCompositeLocalizationTest : BaseUiTest() {

    @get:Rule
    val screenLockRule = RunWhenScreenOffOrLockedRule()


    private fun setLanguage(language: String) {
        val context = getInstrumentation().targetContext
        context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
            .edit()
            .putString("LANGUAGE_ADAPTER_VALUE", language)
            .apply()
    }

    @After
    fun cleanup() {
        setLanguage(Localize.English.language)
    }
    @Test
    fun testFrenchLocalization() {
        testLocalization(Localize.French)
    }

    @Test
    fun testGermanLocalization() {
        testLocalization(Localize.German)
    }

    private fun testLocalization(localized: Localize) {
        setLanguage(localized.language)
        val homeScreen = HomeScreenRobot()
        val settingsScreen = homeScreen.clickSettings()

        settingsScreen.selectLanguageDropDown(localized.language)
        val setupScreen = homeScreen
            .clickTeamsMeetingRadioButton()
            .setGroupIdOrTeamsMeetingUrl(TestFixture.teamsUrl)
            .setAcsToken(CallIdentifiersHelper.getACSToken())
            .clickLaunchButton()

        setupScreen
            .turnCameraOn(localized.videoOffText)
            .tapMicButton(localized.micText)
    }
}

private enum class Localize(
    val language: String,
    val videoOffText: String,
    val micText: String) {

    French(Locale.FRENCH.displayName, "Video désactivé", "Microphone désactivé"),
    German(Locale.GERMAN.displayName, "Video aus", "Mikrofon aus"),
    English(Locale.ENGLISH.displayName, "Video Off", "Mic Off")
}