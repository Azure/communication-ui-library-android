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
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures
import com.azure.android.communication.ui.configuration.LanguageCode
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

    private val sharedPreference by lazy {
        getSharedPreferences(SETTINGS_SHARED_PREFS, Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        this.initializeViews()
        SettingsFeatures.initialize(this)
        supportedLanguages = LanguageCode.values().map { SettingsFeatures.displayLanguageName(it.toString()) }
        setLanguageInSharedPrefForFirstTime()
        updateRenderedDisplayNameText()
    }

    override fun onResume() {
        super.onResume()

        languageArrayAdapter =
            ArrayAdapter(applicationContext, R.layout.language_dropdown_item, supportedLanguages)
        autoCompleteTextView.setAdapter(languageArrayAdapter)
        languageArrayAdapter.filter.filter(null)

        setLanguageInAdapter()

        updateRTLCheckbox()

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
                R.id.languageIsRTL -> {
                    sharedPreference.edit().putBoolean(
                        LANGUAGE_ISRTL_VALUE_SHARED_PREF_KEY +
                            sharedPreference.getString(
                                LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY,
                                DEFAULT_LANGUAGE_VALUE
                            ),
                        view.isChecked
                    ).apply()
                }
            }
        }
    }

    private fun initializeViews() {
        callSettingLabelView = findViewById(R.id.callSellingLabelView)
        languageSettingLabelView = findViewById(R.id.languageSettingLabelView)
        languageSettingLabelDivider = findViewById(R.id.languageSettingLabelDivider)
        isRTLCheckBox = findViewById(R.id.languageIsRTL)
        languageAdapterLayout = findViewById(R.id.languageAdapterLayout)
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView)
        renderDisplayNameTextView = findViewById(R.id.renderDisplayName)
        renderDisplayNameTextView.addTextChangedListener {
            saveRenderedDisplayName()
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
            setLanguageValueInSharedPref(DEFAULT_LANGUAGE_VALUE)
        }
    }

    private fun setLanguageInAdapter() {

        autoCompleteTextView.setText(
            sharedPreference.getString(
                LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY,
                DEFAULT_LANGUAGE_VALUE
            ),
            true
        )
        languageArrayAdapter.filter.filter(null)
    }

    private fun isFirstRun(): Boolean {
        return sharedPreference.getString(LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY, LANGUAGE_IS_YET_TOBE_SET).equals(
            LANGUAGE_IS_YET_TOBE_SET
        )
    }

    private fun setLanguageValueInSharedPref(languageValue: String) {
        sharedPreference.edit().putString(LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY, languageValue).apply()
    }

    private fun getSelectedLanguageValue(): String? {
        return sharedPreference.getString(
            LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY,
            DEFAULT_LANGUAGE_VALUE
        )
    }

    private fun saveRenderedDisplayName() {
        sharedPreference.edit().putString(RENDERED_DISPLAY_NAME, renderDisplayNameTextView.text.toString()).apply()
    }

    private fun updateRenderedDisplayNameText() {
        renderDisplayNameTextView.text = sharedPreference.getString(RENDERED_DISPLAY_NAME, "")
    }
}

// Shared pref Keys for language & rtl settings
const val LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY = "LANGUAGE_ADAPTER_VALUE"
const val LANGUAGE_ISRTL_VALUE_SHARED_PREF_KEY = "RTL_VALUE_OF_"
const val LANGUAGE_IS_YET_TOBE_SET = "LANGUAGE_IS_YET_TOBE_SET"

// Shared pref default values for language & rtl settings
const val DEFAULT_LANGUAGE_VALUE = "ENGLISH"
const val DEFAULT_RTL_VALUE = false
const val DEFAULT_LOCALE_CODE = "en"

// Shared pref default values for persona data
const val RENDERED_DISPLAY_NAME = "RENDERED_DISPLAY_NAME"
const val AVATAR_IMAGE = "AVATAR_IMAGE"
