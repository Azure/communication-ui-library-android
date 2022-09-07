// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callwithchatdemoapp

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
import com.azure.android.communication.ui.callingcompositedemoapp.R
import com.azure.android.communication.ui.callwithchatdemoapp.features.SettingsFeatures
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
        setLanguageInSharedPrefForFirstTime()
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

        updateRTLCheckbox()

        updateAvatarInjectionCheckbox()

        saveRenderedDisplayName()

        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem: String = supportedLanguages[position]
            setLanguageValueInSharedPref(selectedItem)
            updateRTLCheckbox()
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
        renderDisplayNameTextView.addTextChangedListener {
            saveRenderedDisplayName()
        }
        titleTextView = findViewById(R.id.call_title)
        subtitleTextView = findViewById(R.id.call_subtitle)
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
}

// Shared pref Keys for language & rtl settings
const val LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY = "LANGUAGE_ADAPTER_VALUE"
const val LANGUAGE_ISRTL_VALUE_SHARED_PREF_KEY = "RTL_VALUE_OF_"
const val LANGUAGE_IS_YET_TOBE_SET = "LANGUAGE_IS_YET_TOBE_SET"

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
