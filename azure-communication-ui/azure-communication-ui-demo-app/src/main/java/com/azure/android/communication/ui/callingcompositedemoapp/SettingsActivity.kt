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
import com.azure.android.communication.ui.configuration.SupportedLanguages
import com.azure.android.communication.ui.utilities.FEATURE_FLAG_SHARED_PREFS_KEY
import com.google.android.material.textfield.TextInputLayout

class SettingsActivity : AppCompatActivity() {

    private val supportedLanguages = SupportedLanguages.values()
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var languageArrayAdapter: ArrayAdapter<SupportedLanguages>
    private lateinit var isRTLCheckBox: CheckBox
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

        callSettingLabelView = findViewById(R.id.callSellingLabelView)
        languageSettingLabelView = findViewById(R.id.languageSettingLabelView)
        languageSettingLabelDivider = findViewById(R.id.languageSettingLabelDivider)
        isRTLCheckBox = findViewById(R.id.languageIsRTL)
        languageAdapterLayout = findViewById(R.id.languageAdapterLayout)
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView)

        languageArrayAdapter =
            ArrayAdapter(applicationContext, R.layout.language_dropdown_item, supportedLanguages)
        autoCompleteTextView.setAdapter(languageArrayAdapter)
        languageArrayAdapter.filter.filter(null)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onResume() {
        super.onResume()

        languageSettingsExperience()

        autoCompleteTextView.setText(
            sharedPreference.getString(
                LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY,
                DEFAULT_LANGUAGE_VALUE
            ),
            true
        )
        languageArrayAdapter.filter.filter(null)
        updateRTLCheckbox()

        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem: String = autoCompleteTextView.adapter.getItem(position).toString()
            sharedPreference.edit().putString(LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY, selectedItem)
                .apply()
            sharedPreference.edit().putInt(LANGUAGE_ADAPTER_POSITION_SHARED_PREF_KEY, position)
                .apply()
            updateRTLCheckbox()
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
            }
        }
    }

    private fun languageSettingsExperience() {
        if (!SettingsFeatures.isLanguageFeatureEnabled) {
            languageSettingLabelView.visibility = View.GONE
            languageSettingLabelDivider.visibility = View.GONE
            languageAdapterLayout.visibility = View.GONE
            isRTLCheckBox.visibility = View.GONE
        } else {
            languageSettingLabelView.visibility = View.VISIBLE
            languageSettingLabelDivider.visibility = View.VISIBLE
            languageAdapterLayout.visibility = View.VISIBLE
            isRTLCheckBox.visibility = View.VISIBLE
        }
    }

    private fun updateRTLCheckbox() {
        val currentSelectedLanguagePosition: Int = sharedPreference.getInt(
            LANGUAGE_ADAPTER_POSITION_SHARED_PREF_KEY,
            DEFAULT_LANGUAGE_POSITION
        )
        val currentSelectedLanguage = sharedPreference.getString(
            LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY,
            DEFAULT_LANGUAGE_VALUE
        )
        val isRTLKey = LANGUAGE_ISRTL_VALUE_SHARED_PREF_KEY + currentSelectedLanguage
        val selectedLanguage = supportedLanguages[currentSelectedLanguagePosition]

        isRTLCheckBox.isChecked =
            sharedPreference.getBoolean(isRTLKey, selectedLanguage.isRTLDefaultValue)
    }
}

// Constants for language & rtl settings

const val LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY = "LANGUAGE_ADAPTER_VALUE"
const val LANGUAGE_ISRTL_VALUE_SHARED_PREF_KEY = "isRTL_VALUE_OF_"
const val LANGUAGE_ADAPTER_POSITION_SHARED_PREF_KEY = "LANGUAGE_ADAPTER_POSITION"

const val DEFAULT_LANGUAGE_POSITION = 0
const val DEFAULT_LANGUAGE_VALUE = "ENGLISH"
const val DEFAULT_ISRTL_VALUE = false
const val HIDDEN_TAP_COUNT_THRESHOLD = 5
