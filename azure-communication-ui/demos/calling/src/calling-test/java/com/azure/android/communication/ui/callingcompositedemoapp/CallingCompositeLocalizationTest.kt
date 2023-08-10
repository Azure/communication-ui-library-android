package com.azure.android.communication.ui.callingcompositedemoapp

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.azure.android.communication.ui.callingcompositedemoapp.robots.HomeScreenRobot
import com.azure.android.communication.ui.callingcompositedemoapp.util.CallIdentifiersHelper
import com.azure.android.communication.ui.callingcompositedemoapp.util.RunWhenScreenOffOrLockedRule
import com.azure.android.communication.ui.callingcompositedemoapp.util.TestFixture
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.util.Locale

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
@RunWith(AndroidJUnit4::class)
class CallingCompositeLocalizationTest : BaseUiTest() {

    @get:Rule
    val screenLockRule = RunWhenScreenOffOrLockedRule()

    private fun setLanguage(language: String) {
        val context = getInstrumentation().targetContext
        context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
            .edit()
            .putString("LANGUAGE_ADAPTER_VALUE", language)
            .commit()
    }

    @Test
    fun testFrenchLocalization() {
        testLocalization(Localize.French)
    }

    @Test
    fun testGermanLocalization() {
        testLocalization(Localize.German)
    }

    @Test
    fun testItalianLocalization() {
        testLocalization(Localize.Italian)
        // The last test in this class has completed.  Restore back to English locale because other
        // tests that run after this expect English Locale
        testLocalization(Localize.English)
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
            .navigateUpFromSetupScreen()
    }
}

enum class Localize(
    val language: String,
    val videoOffText: String,
    val micText: String,
) {

    French(Locale.FRENCH.displayName, "Video désactivé", "Microphone désactivé"),
    German(Locale.GERMAN.displayName, "Video aus", "Mikrofon aus"),
    Italian(Locale.ITALIAN.displayName, "Video disattivato", "Microfono disattivato"),
    English(Locale.ENGLISH.displayName, "Video off", "Mic off")
}
