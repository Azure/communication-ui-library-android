// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.azure.android.communication.ui.calling.models.CallCompositeSupportedLocale
import com.azure.android.communication.ui.calling.models.CallCompositeSupportedScreenOrientation
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures
import com.google.android.material.textfield.TextInputLayout
import java.util.Locale

// Key for the SharedPrefs store that will be used for FeatureFlags
const val SETTINGS_SHARED_PREFS = "Settings"

class SettingsActivity : AppCompatActivity() {

    private lateinit var supportedLanguages: List<String>
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var languageArrayAdapter: ArrayAdapter<String>
    private lateinit var isRTLCheckBox: CheckBox
    private lateinit var languageSettingLabelView: TextView
    private lateinit var callSettingLabelView: TextView
    private lateinit var languageSettingLabelDivider: View
    private lateinit var languageAdapterLayout: TextInputLayout
    private lateinit var renderDisplayNameTextView: TextView
    private lateinit var titleTextView: TextView
    private lateinit var subtitleTextView: TextView
    private lateinit var remoteAvatarInjectionCheckBox: CheckBox
    private lateinit var skipSetupScreenCheckBox: CheckBox
    private lateinit var micOnByDefaultCheckBox: CheckBox
    private lateinit var cameraOnByDefaultCheckBox: CheckBox
    private lateinit var endCallOnDefaultCheckBox: CheckBox
    private lateinit var relaunchCompositeOnExitCheckbox: CheckBox
    private lateinit var supportedScreenOrientations: List<String>
    private lateinit var callScreenOrientationAdapterLayout: TextInputLayout
    private lateinit var setupScreenOrientationAdapterLayout: TextInputLayout
    private lateinit var callScreenOrientationAutoCompleteTextView: AutoCompleteTextView
    private lateinit var setupScreenOrientationAutoCompleteTextView: AutoCompleteTextView
    private lateinit var callScreenOrientationArrayAdapter: ArrayAdapter<String>
    private lateinit var setupScreenOrientationArrayAdapter: ArrayAdapter<String>
    private lateinit var displayLeaveCallConfirmationCheckBox: CheckBox

    private val sharedPreference by lazy {
        getSharedPreferences(SETTINGS_SHARED_PREFS, Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        this.initializeViews()
        SettingsFeatures.initialize(this)
        supportedLanguages = CallCompositeSupportedLocale.getSupportedLocales().map {
            SettingsFeatures.displayLanguageName(it)
        }
        supportedScreenOrientations = CallCompositeSupportedScreenOrientation.values().map {
            SettingsFeatures.displayOrientationName(it)
        }
        setLanguageInSharedPrefForFirstTime()
        setScreenOrientationInSharedPrefForFirstTime()
        updateRenderedDisplayNameText()
        updateTitle()
        updateSubtitle()
    }

    override fun onResume() {
        super.onResume()

        languageArrayAdapter =
            ArrayAdapter(applicationContext, R.layout.language_dropdown_item, supportedLanguages)
        autoCompleteTextView.setAdapter(languageArrayAdapter)
        languageArrayAdapter.filter.filter(null)

        setLanguageInAdapter()

        callScreenOrientationArrayAdapter =
            ArrayAdapter(applicationContext, R.layout.screen_orientation_dropdown_item, supportedScreenOrientations)
        callScreenOrientationAutoCompleteTextView.setAdapter(callScreenOrientationArrayAdapter)
        callScreenOrientationArrayAdapter.filter.filter(null)

        setOrientationInCallScreenOrientationAdapter()

        setupScreenOrientationArrayAdapter =
            ArrayAdapter(applicationContext, R.layout.screen_orientation_dropdown_item, supportedScreenOrientations)
        setupScreenOrientationAutoCompleteTextView.setAdapter(setupScreenOrientationArrayAdapter)
        setupScreenOrientationArrayAdapter.filter.filter(null)

        setOrientationInSetupScreenOrientationAdapter()

        updateRTLCheckbox()

        updateAvatarInjectionCheckbox()

        updateSkipSetupScreenCheckbox()

        updateMicOnByDefaultCheckbox()

        updateCameraOnByDefaultCheckbox()

        updateEndCallOnDefaultCheckBox()

        relaunchCompositeOnExitCheckbox()

        saveRenderedDisplayName()

        updateDisplayLeaveCallConfirmationCheckbox()

        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem: String = supportedLanguages[position]
            setLanguageValueInSharedPref(selectedItem)
            updateRTLCheckbox()
        }

        callScreenOrientationAutoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem: String = supportedScreenOrientations[position]
            saveCallScreenOrientationInSharedPref(selectedItem)
        }

        setupScreenOrientationAutoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem: String = supportedScreenOrientations[position]
            saveSetupScreenOrientationInSharedPref(selectedItem)
        }
    }

    fun onCheckBoxTap(view: View) {
        if (view is CheckBox) {
            when (view.id) {
                R.id.language_is_rtl_checkbox -> {
                    sharedPreference.edit().putBoolean(
                        LANGUAGE_ISRTL_VALUE_SHARED_PREF_KEY +
                            sharedPreference.getString(
                                LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY,
                                DEFAULT_LANGUAGE_VALUE
                            ),
                        view.isChecked
                    ).apply()
                }
                R.id.remote_avatar_injection_check_box -> {
                    sharedPreference.edit().putBoolean(
                        DEFAULT_PERSONA_INJECTION_VALUE_PREF_KEY,
                        view.isChecked
                    ).apply()
                }
                R.id.skip_setup_screen_check_box -> {
                    sharedPreference.edit().putBoolean(
                        SKIP_SETUP_SCREEN_VALUE_KEY,
                        view.isChecked
                    ).apply()
                }
                R.id.mic_control_check_box -> {
                    sharedPreference.edit().putBoolean(
                        MIC_ON_BY_DEFAULT_KEY,
                        view.isChecked
                    ).apply()
                }
                R.id.camera_control_check_box -> {
                    sharedPreference.edit().putBoolean(
                        CAMERA_ON_BY_DEFAULT_KEY,
                        view.isChecked
                    ).apply()
                }
                R.id.composite_end_call_button_checkbox -> {
                    sharedPreference.edit().putBoolean(
                        END_CALL_ON_BY_DEFAULT_KEY,
                        view.isChecked
                    ).apply()
                }
                R.id.re_launch_on_exit_success -> {
                    sharedPreference.edit().putBoolean(
                        LAUNCH_ON_EXIT_ON_BY_DEFAULT_KEY,
                        view.isChecked
                    ).apply()
                }
                R.id.display_leave_call_confirmation_check_box -> {
                    sharedPreference.edit().putBoolean(
                        DISPLAY_LEAVE_CALL_CONFIRMATION_VALUE,
                        view.isChecked
                    ).apply()
                }
            }
        }
    }

    private fun initializeViews() {
        callSettingLabelView = findViewById(R.id.call_setting_text_view)
        languageSettingLabelView = findViewById(R.id.language_setting_text_view)
        languageSettingLabelDivider = findViewById(R.id.language_setting_label_divider)
        isRTLCheckBox = findViewById(R.id.language_is_rtl_checkbox)
        remoteAvatarInjectionCheckBox = findViewById(R.id.remote_avatar_injection_check_box)
        languageAdapterLayout = findViewById(R.id.language_adapter_layout)
        autoCompleteTextView = findViewById(R.id.auto_complete_text_view)
        renderDisplayNameTextView = findViewById(R.id.render_display_name)
        titleTextView = findViewById(R.id.call_title)
        subtitleTextView = findViewById(R.id.call_subtitle)
        skipSetupScreenCheckBox = findViewById(R.id.skip_setup_screen_check_box)
        micOnByDefaultCheckBox = findViewById(R.id.mic_control_check_box)
        cameraOnByDefaultCheckBox = findViewById(R.id.camera_control_check_box)
        callScreenOrientationAdapterLayout = findViewById(R.id.call_screen_orientation_adapter_layout)
        setupScreenOrientationAdapterLayout = findViewById(R.id.setup_screen_orientation_adapter_layout)
        callScreenOrientationAutoCompleteTextView = findViewById(R.id.call_screen_orientation_auto_complete_text_view)
        setupScreenOrientationAutoCompleteTextView = findViewById(R.id.setup_screen_orientation_auto_complete_text_view)
        endCallOnDefaultCheckBox = findViewById(R.id.composite_end_call_button_checkbox)
        relaunchCompositeOnExitCheckbox = findViewById(R.id.re_launch_on_exit_success)
        callScreenOrientationAdapterLayout = findViewById(R.id.call_screen_orientation_adapter_layout)
        setupScreenOrientationAdapterLayout = findViewById(R.id.setup_screen_orientation_adapter_layout)
        callScreenOrientationAutoCompleteTextView = findViewById(R.id.call_screen_orientation_auto_complete_text_view)
        setupScreenOrientationAutoCompleteTextView = findViewById(R.id.setup_screen_orientation_auto_complete_text_view)
        displayLeaveCallConfirmationCheckBox = findViewById(R.id.display_leave_call_confirmation_check_box)

        renderDisplayNameTextView.addTextChangedListener {
            saveRenderedDisplayName()
        }
        titleTextView.addTextChangedListener {
            saveTitle()
        }
        subtitleTextView.addTextChangedListener {
            saveSubtitle()
        }
    }

    private fun updateRTLCheckbox() {
        val selectedLanguage = getSelectedLanguageValue()
        val isRTLKey = LANGUAGE_ISRTL_VALUE_SHARED_PREF_KEY + selectedLanguage
        if (selectedLanguage != null) {
            isRTLCheckBox.isChecked =
                sharedPreference.getBoolean(isRTLKey, DEFAULT_RTL_VALUE)
        }
    }

    private fun setLanguageInSharedPrefForFirstTime() {
        if (isFirstRun()) {
            setLanguageValueInSharedPref(Locale.ENGLISH.displayName)
        }
    }

    private fun setScreenOrientationInSharedPrefForFirstTime() {
        if (isFirstRun()) {
            saveCallScreenOrientationInSharedPref(DEFAULT_CALL_SCREEN_ORIENTATION_VALUE)
            saveSetupScreenOrientationInSharedPref(DEFAULT_SETUP_SCREEN_ORIENTATION_VALUE)
        }
    }

    private fun setLanguageInAdapter() {

        autoCompleteTextView.setText(
            sharedPreference.getString(
                LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY,
                Locale.ENGLISH.displayName
            ),
            true
        )
        languageArrayAdapter.filter.filter(null)
    }

    private fun setOrientationInCallScreenOrientationAdapter() {

        callScreenOrientationAutoCompleteTextView.setText(
            sharedPreference.getString(
                CALL_SCREEN_ORIENTATION_SHARED_PREF_KEY,
                DEFAULT_CALL_SCREEN_ORIENTATION_VALUE
            ),
            true
        )
        callScreenOrientationArrayAdapter.filter.filter(null)
    }

    private fun setOrientationInSetupScreenOrientationAdapter() {
        setupScreenOrientationAutoCompleteTextView.setText(
            sharedPreference.getString(
                SETUP_SCREEN_ORIENTATION_SHARED_PREF_KEY,
                DEFAULT_SETUP_SCREEN_ORIENTATION_VALUE
            ),
            true
        )
        setupScreenOrientationArrayAdapter.filter.filter(null)
    }

    private fun isFirstRun(): Boolean {
        return sharedPreference.getString(
            LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY,
            LANGUAGE_IS_YET_TOBE_SET
        ).equals(
            LANGUAGE_IS_YET_TOBE_SET
        )
    }

    private fun setLanguageValueInSharedPref(languageValue: String) {
        sharedPreference.edit().putString(LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY, languageValue)
            .apply()
    }

    private fun getSelectedLanguageValue(): String? {
        return sharedPreference.getString(
            LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY,
            DEFAULT_LANGUAGE_VALUE
        )
    }

    private fun saveCallScreenOrientationInSharedPref(orientationValue: String) {
        sharedPreference.edit().putString(CALL_SCREEN_ORIENTATION_SHARED_PREF_KEY, orientationValue)
            .apply()
    }

    private fun saveSetupScreenOrientationInSharedPref(orientationValue: String) {
        sharedPreference.edit().putString(SETUP_SCREEN_ORIENTATION_SHARED_PREF_KEY, orientationValue)
            .apply()
    }

    private fun saveRenderedDisplayName() {
        sharedPreference.edit()
            .putString(RENDERED_DISPLAY_NAME, renderDisplayNameTextView.text.toString()).apply()
    }

    private fun saveTitle() {
        sharedPreference.edit()
            .putString(CALL_TITLE, titleTextView.text.toString()).apply()
    }

    private fun saveSubtitle() {
        sharedPreference.edit()
            .putString(CALL_SUBTITLE, subtitleTextView.text.toString()).apply()
    }

    private fun updateRenderedDisplayNameText() {
        renderDisplayNameTextView.text = sharedPreference.getString(RENDERED_DISPLAY_NAME, "")
    }

    private fun updateTitle() {
        titleTextView.text = sharedPreference.getString(CALL_TITLE, null)
    }

    private fun updateSubtitle() {
        subtitleTextView.text = sharedPreference.getString(CALL_SUBTITLE, null)
    }

    private fun updateAvatarInjectionCheckbox() {
        remoteAvatarInjectionCheckBox.isChecked =
            sharedPreference.getBoolean(
                DEFAULT_PERSONA_INJECTION_VALUE_PREF_KEY,
                REMOTE_PARTICIPANT_PERSONA_INJECTION_VALUE
            )
    }

    private fun updateSkipSetupScreenCheckbox() {
        skipSetupScreenCheckBox.isChecked = sharedPreference.getBoolean(
            SKIP_SETUP_SCREEN_VALUE_KEY,
            DEFAULT_SKIP_SETUP_SCREEN_VALUE
        )
    }

    private fun updateMicOnByDefaultCheckbox() {
        micOnByDefaultCheckBox.isChecked = sharedPreference.getBoolean(
            MIC_ON_BY_DEFAULT_KEY,
            DEFAULT_MIC_ON_BY_DEFAULT_VALUE
        )
    }

    private fun updateCameraOnByDefaultCheckbox() {
        cameraOnByDefaultCheckBox.isChecked = sharedPreference.getBoolean(
            CAMERA_ON_BY_DEFAULT_KEY,
            DEFAULT_CAMERA_ON_BY_DEFAULT_VALUE
        )
    }

    private fun updateEndCallOnDefaultCheckBox() {
        endCallOnDefaultCheckBox.isChecked = sharedPreference.getBoolean(
            END_CALL_ON_BY_DEFAULT_KEY,
            DEFAULT_END_CALL_ON_BY_DEFAULT_VALUE
        )
    }

    private fun relaunchCompositeOnExitCheckbox() {
        relaunchCompositeOnExitCheckbox.isChecked = sharedPreference.getBoolean(
            LAUNCH_ON_EXIT_ON_BY_DEFAULT_KEY,
            LAUNCH_ON_EXIT_ON_BY_DEFAULT_VALUE
        )
    }

    private fun updateDisplayLeaveCallConfirmationCheckbox() {
        val isChecked = sharedPreference.getBoolean(
            DISPLAY_LEAVE_CALL_CONFIRMATION_VALUE,
            DEFAULT_DISPLAY_LEAVE_CALL_CONFIRMATION_VALUE
        )
        displayLeaveCallConfirmationCheckBox.isChecked = isChecked
    }
}

// Shared pref Keys for language & rtl settings
const val LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY = "LANGUAGE_ADAPTER_VALUE"
const val LANGUAGE_ISRTL_VALUE_SHARED_PREF_KEY = "RTL_VALUE_OF_"
const val LANGUAGE_IS_YET_TOBE_SET = "LANGUAGE_IS_YET_TOBE_SET"

// Shared pref keys for screen orientation settings
const val CALL_SCREEN_ORIENTATION_SHARED_PREF_KEY = "CALL_SCREEN_ORIENTATION_SHARED_PREF_KEY"
const val SETUP_SCREEN_ORIENTATION_SHARED_PREF_KEY = "SETUP_SCREEN_ORIENTATION_SHARED_PREF_KEY"
const val DEFAULT_CALL_SCREEN_ORIENTATION_VALUE = "ACS_DEFAULT"
const val DEFAULT_SETUP_SCREEN_ORIENTATION_VALUE = "PORTRAIT"

// Shared pref default values for language & rtl settings
const val DEFAULT_LANGUAGE_VALUE = "ENGLISH"
const val DEFAULT_RTL_VALUE = false

// Shared pref default values for persona data
const val RENDERED_DISPLAY_NAME = "RENDERED_DISPLAY_NAME"
const val AVATAR_IMAGE = "AVATAR_IMAGE"
const val DEFAULT_PERSONA_INJECTION_VALUE_PREF_KEY = "PERSONA_INJECTION_VALUE_PREF_KEY"
const val REMOTE_PARTICIPANT_PERSONA_INJECTION_VALUE = false
const val CALL_TITLE = "CALL_TITLE"
const val CALL_SUBTITLE = "CALL_SUBTITLE"
const val SKIP_SETUP_SCREEN_VALUE_KEY = "SKIP_SETUP_SCREEN_VALUE_KEY"
const val DEFAULT_SKIP_SETUP_SCREEN_VALUE = false
const val MIC_ON_BY_DEFAULT_KEY = "MIC_ON_BY_DEFAULT_KEY"
const val DEFAULT_MIC_ON_BY_DEFAULT_VALUE = false
const val CAMERA_ON_BY_DEFAULT_KEY = "CAMERA_ON_BY_DEFAULT_KEY"
const val DEFAULT_CAMERA_ON_BY_DEFAULT_VALUE = false
const val END_CALL_ON_BY_DEFAULT_KEY = "END_CALL_ON_BY_DEFAULT_KEY"
const val DEFAULT_END_CALL_ON_BY_DEFAULT_VALUE = false
const val LAUNCH_ON_EXIT_ON_BY_DEFAULT_KEY = "LAUNCH_ON_EXIT_ON_BY_DEFAULT_KEY"
const val LAUNCH_ON_EXIT_ON_BY_DEFAULT_VALUE = false
const val DISPLAY_LEAVE_CALL_CONFIRMATION_VALUE = "DISPLAY_LEAVE_CALL_CONFIRMATION_VALUE_KEY"
const val DEFAULT_DISPLAY_LEAVE_CALL_CONFIRMATION_VALUE = true
