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
import com.azure.android.communication.ui.calling.models.CallCompositeTelecomManagerIntegrationMode
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures
import com.google.android.material.textfield.TextInputLayout

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
    private lateinit var remoteDisplayInjectionCheckBox: CheckBox
    private lateinit var skipSetupScreenCheckBox: CheckBox
    private lateinit var audioOnlyModeCheckBox: CheckBox
    private lateinit var micOnByDefaultCheckBox: CheckBox
    private lateinit var cameraOnByDefaultCheckBox: CheckBox
    private lateinit var endCallOnDefaultCheckBox: CheckBox
    private lateinit var supportedScreenOrientations: List<String>
    private lateinit var callScreenOrientationAdapterLayout: TextInputLayout
    private lateinit var setupScreenOrientationAdapterLayout: TextInputLayout
    private lateinit var callScreenOrientationAutoCompleteTextView: AutoCompleteTextView
    private lateinit var setupScreenOrientationAutoCompleteTextView: AutoCompleteTextView
    private lateinit var callScreenOrientationArrayAdapter: ArrayAdapter<String>
    private lateinit var setupScreenOrientationArrayAdapter: ArrayAdapter<String>
    private lateinit var displayLeaveCallConfirmationCheckBox: CheckBox
    private lateinit var enableMultitaskingCheckbox: CheckBox
    private lateinit var enablePipWhenMultitaskingCheckbox: CheckBox
    private lateinit var telecomManagerIntegrationOptions: List<String>
    private lateinit var telecomManagerArrayAdapter: ArrayAdapter<String>
    private lateinit var telecomManagerAutoCompleteTextView: AutoCompleteTextView
    private lateinit var telecomManagerAdapterLayout: TextInputLayout
    private lateinit var useDeprecatedLaunchCheckbox: CheckBox
    private lateinit var disableInternalPushForIncomingCallCheckbox: CheckBox
    private lateinit var autoStartCaptionsCheckbox: CheckBox
    private lateinit var hideCaptionsUiCheckbox: CheckBox

    private lateinit var setupScreenOptionsCameraEnabledCheckbox: CheckBox
    private lateinit var setupScreenOptionsMicEnabledCheckbox: CheckBox
    private lateinit var defaultSpokenLanguageEditText: TextView
    private lateinit var timerStartEditText: TextView
    private lateinit var timerStopEditText: TextView
    private lateinit var callInformationTitleEditText: TextView
    private lateinit var callTimerStartDurationEditText: TextView

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
        val telecomManagerOptions = CallCompositeTelecomManagerIntegrationMode.values().map {
            SettingsFeatures.displayTelecomManagerOptionName(it)
        }
        telecomManagerIntegrationOptions = telecomManagerOptions + DEFAULT_TELECOM_MANAGER_INTEGRATION_OPTION
    }

    override fun onDestroy() {
        super.onDestroy()
        // recreate composite as settings are changed
        val application = application as CallLauncherApplication
        application.getCallCompositeManager(this).dismissCallComposite()
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

        telecomManagerArrayAdapter =
            ArrayAdapter(applicationContext, R.layout.screen_orientation_dropdown_item, telecomManagerIntegrationOptions)
        telecomManagerAutoCompleteTextView.setAdapter(telecomManagerArrayAdapter)
        telecomManagerArrayAdapter.filter.filter(null)

        telecomManagerAutoCompleteAdapter()

        setOrientationInSetupScreenOrientationAdapter()

        updateRTLCheckbox()

        updateAvatarInjectionCheckbox()
        updateDisplayNameInjectionCheckbox()

        updateSkipSetupScreenCheckbox()

        updateDeprecatedLaunchCheckbox()

        updateDisableInternalPushForIncomingCallCheckbox()

        updateAutoStartCaptionsCheckbox()

        updateHideCaptionsUiCheckbox()

        updateMicOnByDefaultCheckbox()

        updateCameraOnByDefaultCheckbox()

        updateEndCallOnDefaultCheckBox()

        updateAudioOnlyDefaultCheckbox()

        updateEnableMultitaskingCheckbox()
        updateEnablePipMultitaskingCheckbox()
        updateSetupScreenCameraEnabledCheckbox()
        updateSetupScreenMicEnabledCheckbox()

        updateRenderedDisplayNameText()
        updateTitle()
        updateSubtitle()

        updateDisplayLeaveCallConfirmationCheckbox()

        defaultSpokenLanguageEditText.text = sharedPreference.getString(DEFAULT_SPOKEN_LANGUAGE_KEY, DEFAULT_SPOKEN_LANGUAGE)
        timerStartEditText.text = sharedPreference.getString(TIMER_START_MRI_KEY, DEFAULT_TIMER_MRI_VALUE)
        timerStopEditText.text = sharedPreference.getString(TIMER_STOP_MRI_KEY, DEFAULT_TIMER_MRI_VALUE)
        callInformationTitleEditText.text = sharedPreference.getString(CALL_INFORMATION_TITLE, DEFAULT_CALL_INFORMATION_TITLE)
        callTimerStartDurationEditText.text = sharedPreference.getLong(TIMER_START_SECONDS_KEY, DEFAULT_TIMER_START_SECONDS).toString()

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

        setupScreenOrientationAutoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem: String = telecomManagerIntegrationOptions[position]
            saveTelecomManagerIntegrationOptionInSharedPref(selectedItem)
        }

        telecomManagerAutoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem: String = telecomManagerIntegrationOptions[position]
            saveTelecomManagerIntegrationOptionInSharedPref(selectedItem)
        }
    }

    private fun telecomManagerAutoCompleteAdapter() {
        telecomManagerAutoCompleteTextView.setText(
            sharedPreference.getString(
                TELECOM_MANAGER_INTEGRATION_OPTION_KEY,
                DEFAULT_TELECOM_MANAGER_INTEGRATION_OPTION
            ),
            true
        )
        telecomManagerArrayAdapter.filter.filter(null)
    }

    fun onCheckBoxTap(view: View) {
        if (view is CheckBox) {
            when (view.id) {
                R.id.language_is_rtl_checkbox -> {
                    sharedPreference.edit().putBoolean(
                        LANGUAGE_ISRTL_VALUE_SHARED_PREF_KEY +
                            sharedPreference.getString(
                                LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY,
                                null,
                            ),
                        view.isChecked
                    ).apply()
                }
                R.id.remote_avatar_injection_check_box -> {
                    sharedPreference.edit().putBoolean(
                        PERSONA_INJECTION_VALUE_PREF_KEY,
                        view.isChecked
                    ).apply()
                }
                R.id.remote_name_injection_check_box -> {
                    sharedPreference.edit().putBoolean(
                        PERSONA_INJECTION_DISPLAY_NAME_KEY,
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
                        DISPLAY_DISMISS_BUTTON_KEY,
                        view.isChecked
                    ).apply()
                }

                R.id.multitasking_check_box -> {
                    sharedPreference.edit().putBoolean(
                        ENABLE_MULTITASKING,
                        view.isChecked
                    ).apply()
                }
                R.id.multitasking_pip_check_box -> {
                    sharedPreference.edit().putBoolean(
                        ENABLE_PIP_WHEN_MULTITASKING,
                        view.isChecked
                    ).apply()
                }
                R.id.audio_only_check_box -> {
                    sharedPreference.edit().putBoolean(
                        AUDIO_ONLY_MODE_ON,
                        view.isChecked
                    ).apply()
                }
                R.id.display_leave_call_confirmation_check_box -> {
                    sharedPreference.edit().putBoolean(
                        DISPLAY_LEAVE_CALL_CONFIRMATION_VALUE,
                        view.isChecked
                    ).apply()
                }
                R.id.deprecated_launch_checkbox -> {
                    sharedPreference.edit().putBoolean(
                        USE_DEPRECATED_LAUNCH_KEY,
                        view.isChecked
                    ).apply()
                }
                R.id.disable_internal_push_checkbox -> {
                    sharedPreference.edit().putBoolean(
                        DISABLE_INTERNAL_PUSH_NOTIFICATIONS,
                        view.isChecked
                    ).apply()
                }
                R.id.setup_screen_camera_check_box -> {
                    sharedPreference.edit().putBoolean(
                        SETUP_SCREEN_CAMERA_ENABLED,
                        view.isChecked
                    ).apply()
                }
                R.id.setup_screen_mic_check_box -> {
                    sharedPreference.edit().putBoolean(
                        SETUP_SCREEN_MIC_ENABLED,
                        view.isChecked
                    ).apply()
                }
                R.id.auto_start_captions_checkbox -> {
                    sharedPreference.edit().putBoolean(
                        AUTO_START_CAPTIONS,
                        view.isChecked
                    ).apply()
                }
                R.id.hide_captions_checkbox -> {
                    sharedPreference.edit().putBoolean(
                        HIDE_CAPTIONS_UI,
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
        remoteDisplayInjectionCheckBox = findViewById(R.id.remote_name_injection_check_box)
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
        callScreenOrientationAdapterLayout = findViewById(R.id.call_screen_orientation_adapter_layout)
        setupScreenOrientationAdapterLayout = findViewById(R.id.setup_screen_orientation_adapter_layout)
        callScreenOrientationAutoCompleteTextView = findViewById(R.id.call_screen_orientation_auto_complete_text_view)
        setupScreenOrientationAutoCompleteTextView = findViewById(R.id.setup_screen_orientation_auto_complete_text_view)
        enableMultitaskingCheckbox = findViewById(R.id.multitasking_check_box)
        enablePipWhenMultitaskingCheckbox = findViewById(R.id.multitasking_pip_check_box)
        audioOnlyModeCheckBox = findViewById(R.id.audio_only_check_box)
        displayLeaveCallConfirmationCheckBox = findViewById(R.id.display_leave_call_confirmation_check_box)
        telecomManagerAutoCompleteTextView = findViewById(R.id.telecom_manager_selection_auto_complete_text_view)
        telecomManagerAdapterLayout = findViewById(R.id.telecom_manager_selection_adapter_layout)
        useDeprecatedLaunchCheckbox = findViewById(R.id.deprecated_launch_checkbox)
        disableInternalPushForIncomingCallCheckbox = findViewById(R.id.disable_internal_push_checkbox)
        autoStartCaptionsCheckbox = findViewById(R.id.auto_start_captions_checkbox)
        hideCaptionsUiCheckbox = findViewById(R.id.hide_captions_checkbox)

        setupScreenOptionsCameraEnabledCheckbox = findViewById(R.id.setup_screen_camera_check_box)
        setupScreenOptionsMicEnabledCheckbox = findViewById(R.id.setup_screen_mic_check_box)
        defaultSpokenLanguageEditText = findViewById(R.id.default_spoken_language_edit_text)
        timerStartEditText = findViewById(R.id.timer_start_edit_text)
        timerStopEditText = findViewById(R.id.timer_stop_edit_text)
        callInformationTitleEditText = findViewById(R.id.call_information_title_edit_text)
        callTimerStartDurationEditText = findViewById(R.id.timer_start_seconds_edit_text)

        renderDisplayNameTextView.addTextChangedListener {
            saveRenderedDisplayName()
        }
        titleTextView.addTextChangedListener {
            saveTitle()
        }
        subtitleTextView.addTextChangedListener {
            saveSubtitle()
        }

        defaultSpokenLanguageEditText.addTextChangedListener {
            sharedPreference.edit().putString(
                DEFAULT_SPOKEN_LANGUAGE_KEY,
                defaultSpokenLanguageEditText.text.toString()
            ).apply()
        }

        timerStartEditText.addTextChangedListener {
            sharedPreference.edit().putString(
                TIMER_START_MRI_KEY,
                timerStartEditText.text.toString()
            ).apply()
        }

        timerStopEditText.addTextChangedListener {
            sharedPreference.edit().putString(
                TIMER_STOP_MRI_KEY,
                timerStopEditText.text.toString()
            ).apply()
        }

        callInformationTitleEditText.addTextChangedListener {
            sharedPreference.edit().putString(
                CALL_INFORMATION_TITLE,
                callInformationTitleEditText.text.toString()
            ).apply()
        }

        callTimerStartDurationEditText.addTextChangedListener {
            sharedPreference.edit().putLong(
                TIMER_START_SECONDS_KEY,
                callTimerStartDurationEditText.text.toString().toLong()
            ).apply()
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

    private fun setLanguageInAdapter() {

        autoCompleteTextView.setText(
            sharedPreference.getString(
                LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY,
                LANGUAGE_IS_YET_TOBE_SET
            ),
            true,
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

    private fun saveTelecomManagerIntegrationOptionInSharedPref(selectedItem: String) {
        sharedPreference.edit().putString(TELECOM_MANAGER_INTEGRATION_OPTION_KEY, selectedItem)
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
                PERSONA_INJECTION_VALUE_PREF_KEY,
                REMOTE_PARTICIPANT_PERSONA_INJECTION_VALUE
            )
    }

    private fun updateDisplayNameInjectionCheckbox() {
        remoteDisplayInjectionCheckBox.isChecked =
            sharedPreference.getBoolean(
                PERSONA_INJECTION_DISPLAY_NAME_KEY,
                DEFAULT_PERSONA_INJECTION_DISPLAY_NAME_KEY
            )
    }

    private fun updateSkipSetupScreenCheckbox() {
        skipSetupScreenCheckBox.isChecked = sharedPreference.getBoolean(
            SKIP_SETUP_SCREEN_VALUE_KEY,
            DEFAULT_SKIP_SETUP_SCREEN_VALUE
        )
    }

    private fun updateDeprecatedLaunchCheckbox() {
        useDeprecatedLaunchCheckbox.isChecked = sharedPreference.getBoolean(
            USE_DEPRECATED_LAUNCH_KEY,
            DEFAULT_USE_DEPRECATED_LAUNCH_VALUE
        )
    }

    private fun updateDisableInternalPushForIncomingCallCheckbox() {
        disableInternalPushForIncomingCallCheckbox.isChecked = sharedPreference.getBoolean(
            DISABLE_INTERNAL_PUSH_NOTIFICATIONS,
            DEFAULT_DISABLE_INTERNAL_PUSH_NOTIFICATIONS
        )
    }

    private fun updateAutoStartCaptionsCheckbox() {
        autoStartCaptionsCheckbox.isChecked = sharedPreference.getBoolean(
            AUTO_START_CAPTIONS,
            DEFAULT_AUTO_START_CAPTIONS
        )
    }

    private fun updateHideCaptionsUiCheckbox() {
        hideCaptionsUiCheckbox.isChecked = sharedPreference.getBoolean(
            HIDE_CAPTIONS_UI,
            DEFAULT_HIDE_CAPTIONS_UI
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
            DISPLAY_DISMISS_BUTTON_KEY,
            DISPLAY_DISMISS_BUTTON_KEY_DEFAULT_VALUE
        )
    }

    private fun updateEnableMultitaskingCheckbox() {
        enableMultitaskingCheckbox.isChecked = sharedPreference.getBoolean(
            ENABLE_MULTITASKING,
            ENABLE_MULTITASKING_DEFAULT_VALUE
        )
    }

    private fun updateEnablePipMultitaskingCheckbox() {
        enablePipWhenMultitaskingCheckbox.isChecked = sharedPreference.getBoolean(
            ENABLE_PIP_WHEN_MULTITASKING,
            ENABLE_PIP_WHEN_MULTITASKING_DEFAULT_VALUE
        )
    }

    private fun updateSetupScreenCameraEnabledCheckbox() {
        setupScreenOptionsCameraEnabledCheckbox.isChecked = sharedPreference.getBoolean(
            SETUP_SCREEN_CAMERA_ENABLED,
            DEFAULT_SETUP_SCREEN_CAMERA_ENABLED_VALUE
        )
    }

    private fun updateSetupScreenMicEnabledCheckbox() {
        setupScreenOptionsMicEnabledCheckbox.isChecked = sharedPreference.getBoolean(
            SETUP_SCREEN_MIC_ENABLED,
            DEFAULT_SETUP_SCREEN_MIC_ENABLED_VALUE
        )
    }

    private fun updateAudioOnlyDefaultCheckbox() {
        audioOnlyModeCheckBox.isChecked = sharedPreference.getBoolean(
            AUDIO_ONLY_MODE_ON,
            DEFAULT_AUDIO_ONLY_MODE_ON
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
const val LANGUAGE_IS_YET_TOBE_SET = "Not selected"

// Shared pref keys for screen orientation settings
const val CALL_SCREEN_ORIENTATION_SHARED_PREF_KEY = "CALL_SCREEN_ORIENTATION_SHARED_PREF_KEY"
const val SETUP_SCREEN_ORIENTATION_SHARED_PREF_KEY = "SETUP_SCREEN_ORIENTATION_SHARED_PREF_KEY"
const val DEFAULT_CALL_SCREEN_ORIENTATION_VALUE = "Not selected"
const val DEFAULT_SETUP_SCREEN_ORIENTATION_VALUE = "Not selected"

// Shared pref default values for language & rtl settings
const val DEFAULT_LANGUAGE_VALUE = "ENGLISH"
const val DEFAULT_RTL_VALUE = false

// Shared pref default values for persona data
const val RENDERED_DISPLAY_NAME = "RENDERED_DISPLAY_NAME"
const val AVATAR_IMAGE = "AVATAR_IMAGE"
const val PERSONA_INJECTION_VALUE_PREF_KEY = "PERSONA_INJECTION_VALUE_PREF_KEY"
const val REMOTE_PARTICIPANT_PERSONA_INJECTION_VALUE = false

const val PERSONA_INJECTION_DISPLAY_NAME_KEY = "PERSONA_INJECTION_DISPLAY_NAME_KEY"
const val DEFAULT_PERSONA_INJECTION_DISPLAY_NAME_KEY = false

const val CALL_TITLE = "CALL_TITLE"
const val CALL_SUBTITLE = "CALL_SUBTITLE"
const val SKIP_SETUP_SCREEN_VALUE_KEY = "SKIP_SETUP_SCREEN_VALUE_KEY"
const val DEFAULT_SKIP_SETUP_SCREEN_VALUE = false
const val MIC_ON_BY_DEFAULT_KEY = "MIC_ON_BY_DEFAULT_KEY"
const val DEFAULT_MIC_ON_BY_DEFAULT_VALUE = false
const val CAMERA_ON_BY_DEFAULT_KEY = "CAMERA_ON_BY_DEFAULT_KEY"
const val DEFAULT_CAMERA_ON_BY_DEFAULT_VALUE = false
const val DISPLAY_DISMISS_BUTTON_KEY = "DISPLAY_DISMISS_BUTTON_KEY"
const val DISPLAY_DISMISS_BUTTON_KEY_DEFAULT_VALUE = false
const val AUDIO_ONLY_MODE_ON = "AUDIO_ONLY_MODE_ON"
const val DEFAULT_AUDIO_ONLY_MODE_ON = false

// Multitasking
const val ENABLE_MULTITASKING = "ENABLE_MULTITASKING"
const val ENABLE_MULTITASKING_DEFAULT_VALUE = false
const val ENABLE_PIP_WHEN_MULTITASKING = "ENABLE_PIP_WHEN_MULTITASKING"
const val ENABLE_PIP_WHEN_MULTITASKING_DEFAULT_VALUE = false
const val END_CALL_ON_BY_DEFAULT_KEY = "END_CALL_ON_BY_DEFAULT_KEY"
const val DEFAULT_END_CALL_ON_BY_DEFAULT_VALUE = false
const val LAUNCH_ON_EXIT_ON_BY_DEFAULT_KEY = "LAUNCH_ON_EXIT_ON_BY_DEFAULT_KEY"
const val LAUNCH_ON_EXIT_ON_BY_DEFAULT_VALUE = false
const val DISPLAY_LEAVE_CALL_CONFIRMATION_VALUE = "DISPLAY_LEAVE_CALL_CONFIRMATION_VALUE_KEY"
const val DEFAULT_DISPLAY_LEAVE_CALL_CONFIRMATION_VALUE = true

// TelecomManager Integration
const val TELECOM_MANAGER_INTEGRATION_OPTION_KEY = "TELECOM_MANAGER_INTEGRATION_OPTION"
const val DEFAULT_TELECOM_MANAGER_INTEGRATION_OPTION = "Not selected"

// Deprecated Launch
const val USE_DEPRECATED_LAUNCH_KEY = "USE_DEPRECATED_LAUNCH"
const val DEFAULT_USE_DEPRECATED_LAUNCH_VALUE = false

// Push Notifications
const val DISABLE_INTERNAL_PUSH_NOTIFICATIONS = "DISABLE_INTERNAL_PUSH_NOTIFICATIONS"
const val DEFAULT_DISABLE_INTERNAL_PUSH_NOTIFICATIONS = false

const val CACHED_TOKEN = "CACHED_TOKEN"
const val CACHED_USER_NAME = "CACHED_USER_NAME"

const val SETUP_SCREEN_CAMERA_ENABLED = "SETUP_SCREEN_CAMERA_ENABLED"
const val DEFAULT_SETUP_SCREEN_CAMERA_ENABLED_VALUE = true

const val SETUP_SCREEN_MIC_ENABLED = "SETUP_SCREEN_MIC_ENABLED"
const val DEFAULT_SETUP_SCREEN_MIC_ENABLED_VALUE = true

const val AUTO_START_CAPTIONS = "AUTO_START_CAPTIONS"
const val DEFAULT_AUTO_START_CAPTIONS = false

const val HIDE_CAPTIONS_UI = "HIDE_CAPTIONS_UI"
const val DEFAULT_HIDE_CAPTIONS_UI = false

const val DEFAULT_SPOKEN_LANGUAGE_KEY = "DEFAULT_SPOKEN_LANGUAGE"
const val DEFAULT_SPOKEN_LANGUAGE = ""

const val TIMER_START_MRI_KEY = "TIMER_START_MRI"
const val TIMER_STOP_MRI_KEY = "TIMER_STOP_MRI"
const val DEFAULT_TIMER_MRI_VALUE = ""

const val CALL_INFORMATION_TITLE = "CALL_INFORMATION_TITLE"
const val DEFAULT_CALL_INFORMATION_TITLE = ""

const val TIMER_START_SECONDS_KEY = "TIMER_START_SECONDS"
const val DEFAULT_TIMER_START_SECONDS = 0L
