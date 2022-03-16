// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures
import com.azure.android.communication.ui.configuration.LocalizationConfiguration
import com.azure.android.communication.ui.utilities.implementation.FEATURE_FLAG_SHARED_PREFS_KEY
import com.google.android.material.textfield.TextInputLayout
import java.util.Locale

class SettingsActivity : AppCompatActivity() {

    private lateinit var supportedLanguages: List<String>
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var languageArrayAdapter: ArrayAdapter<String>
    private lateinit var isRTLCheckBox: CheckBox
    private lateinit var customTranslationCheckBox: CheckBox
    private lateinit var languageSettingLabelView: TextView
    private lateinit var callSettingLabelView: TextView
    private lateinit var languageSettingLabelDivider: View
    private lateinit var languageAdapterLayout: TextInputLayout
    private val sharedPreference by lazy {
        getSharedPreferences(FEATURE_FLAG_SHARED_PREFS_KEY, Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        this.initializeViews()
        supportedLanguages = LocalizationConfiguration.getSupportedLanguages()
        setLanguageInSharedPrefForFirstTime()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onResume() {
        super.onResume()

        languageArrayAdapter =
            ArrayAdapter(applicationContext, R.layout.language_dropdown_item, supportedLanguages)
        autoCompleteTextView.setAdapter(languageArrayAdapter)
        languageArrayAdapter.filter.filter(null)

        setLanguageInAdapter()

        SettingsFeatures.isLanguageFeatureEnabled = SettingsFeatures.getIsLanguageFeatureEnabled(this)
        languageSettingsExperience()
        updateRTLCheckbox()
        updateCustomStringCheckBox()

        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem: String = supportedLanguages[position]
            setLanguageValueInSharedPref(selectedItem)
            updateRTLCheckbox()
            updateCustomStringCheckBox()
        }

        callSettingLabelView.setOnTouchListener(
            @SuppressLint("ClickableViewAccessibility")
            object : View.OnTouchListener {

                var numberOfTaps = 0
                var lastTapEventTime: Long = 0
                var touchStartTime: Long = 0

                override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                    when (p1?.action) {
                        MotionEvent.ACTION_DOWN -> {
                            touchStartTime = System.currentTimeMillis()
                        }
                        MotionEvent.ACTION_UP -> {
                            if (System.currentTimeMillis() - touchStartTime > ViewConfiguration.getTapTimeout()) {
                                numberOfTaps = 0
                                lastTapEventTime = 0
                            } else if (numberOfTaps > 0 && System.currentTimeMillis() - lastTapEventTime < ViewConfiguration.getDoubleTapTimeout()) {
                                numberOfTaps += 1
                            } else {
                                numberOfTaps = 1
                            }
                            lastTapEventTime = System.currentTimeMillis()
                        }
                    }
                    if (numberOfTaps > HIDDEN_TAP_COUNT_THRESHOLD) {
                        SettingsFeatures.isLanguageFeatureEnabled =
                            !SettingsFeatures.isLanguageFeatureEnabled
                        languageSettingsExperience()
                        numberOfTaps = 0
                        lastTapEventTime = 0
                    }
                    return true
                }
            }
        )
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
                R.id.customTranslation -> {
                    sharedPreference.edit().putBoolean(
                        LANGUAGE_CUSTOM_TRANSLATION_ENABLE,
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
        customTranslationCheckBox = findViewById(R.id.customTranslation)
    }

    private fun languageSettingsExperience() {
        if (!SettingsFeatures.isLanguageFeatureEnabled) {
            languageSettingLabelView.visibility = View.GONE
            languageSettingLabelDivider.visibility = View.GONE
            languageAdapterLayout.visibility = View.GONE
            isRTLCheckBox.visibility = View.GONE
            customTranslationCheckBox.visibility = View.GONE
        } else {
            languageSettingLabelView.visibility = View.VISIBLE
            languageSettingLabelDivider.visibility = View.VISIBLE
            languageAdapterLayout.visibility = View.VISIBLE
            isRTLCheckBox.visibility = View.VISIBLE
            customTranslationCheckBox.visibility = View.VISIBLE
        }
        SettingsFeatures.setIsLanguageFeatureEnabled(
            this,
            SettingsFeatures.isLanguageFeatureEnabled
        )
    }

    private fun updateRTLCheckbox() {
        val selectedLanguage = getSelectedLanguageValue()
        val isRTLKey = LANGUAGE_ISRTL_VALUE_SHARED_PREF_KEY + selectedLanguage
        if (selectedLanguage != null) {
            isRTLCheckBox.isChecked =
                sharedPreference.getBoolean(isRTLKey, DEFAULT_ISRTL_VALUE)
        }
    }

    private fun updateCustomStringCheckBox() {
        customTranslationCheckBox.isChecked = sharedPreference
            .getBoolean(LANGUAGE_CUSTOM_TRANSLATION_ENABLE, DEFAULT_CUSTOM_TRANSLATION_VALUE)
    }

    private fun getDeviceLanguage(): String {
        return Locale.getDefault().displayLanguage
    }

    private fun setLanguageInSharedPrefForFirstTime() {
        val deviceLocaleLanguage = getDeviceLanguage()
        if (isFirstRun()) {
            for (language in supportedLanguages) {
                if (language == deviceLocaleLanguage) {
                    setLanguageValueInSharedPref(language)
                    break
                }
            }
            if (isFirstRun()) setLanguageValueInSharedPref(DEFAULT_LANGUAGE_VALUE)
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
}

// Shared pref Keys for language & rtl settings

const val LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY = "LANGUAGE_ADAPTER_VALUE"
const val LANGUAGE_ISRTL_VALUE_SHARED_PREF_KEY = "isRTL_VALUE_OF_"
const val LANGUAGE_IS_YET_TOBE_SET = "LANGUAGE_IS_YET_TOBE_SET"
const val LANGUAGE_CUSTOM_TRANSLATION_ENABLE = "LANGUAGE_CUSTOM_TRANSLATION_ENABLE"

// Shared pref default values for language & rtl settings

const val DEFAULT_LANGUAGE_VALUE = "ENGLISH"
const val DEFAULT_ISRTL_VALUE = false
const val DEFAULT_CUSTOM_TRANSLATION_VALUE = false
const val HIDDEN_TAP_COUNT_THRESHOLD = 5
