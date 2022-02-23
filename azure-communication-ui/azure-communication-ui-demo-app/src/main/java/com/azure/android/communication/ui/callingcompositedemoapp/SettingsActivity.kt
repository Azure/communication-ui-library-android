/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.azure.android.communication.ui.callingcompositedemoapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.azure.android.communication.ui.utilities.FEATURE_FLAG_SHARED_PREFS_KEY
import com.azure.android.communication.ui.utilities.SupportedLanguages

class SettingsActivity : AppCompatActivity() {

    private val supportedLanguages = SupportedLanguages.values()
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var languageArrayAdapter: ArrayAdapter<SupportedLanguages>
    private lateinit var isRTLCheckBox: CheckBox
    private val sharedPreference by lazy {
        getSharedPreferences(FEATURE_FLAG_SHARED_PREFS_KEY, Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        isRTLCheckBox = findViewById(R.id.languageIsRTL)
        autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        languageArrayAdapter = ArrayAdapter(applicationContext, R.layout.language_dropdown_item, supportedLanguages)
        autoCompleteTextView.setAdapter(languageArrayAdapter)
        languageArrayAdapter.filter.filter(null)
    }

    override fun onResume() {
        super.onResume()

        autoCompleteTextView.setText(sharedPreference.getString(LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY, DEFAULT_LANGUAGE_VALUE), true)
        languageArrayAdapter.filter.filter(null)

        autoCompleteTextView.setOnItemClickListener{ parent, view, position, id->
            val selectedItem: String = autoCompleteTextView.adapter.getItem(position).toString()
            sharedPreference.edit().putString(LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY, selectedItem).commit()
        }

        isRTLCheckBox.isChecked = sharedPreference.getBoolean(LANGUAGE_ISRTL_VALUE_SHARED_PREF_KEY, DEFAULT_ISRTL_VALUE)
    }

    fun onCheckBoxTap(view: View) {

        if ( view is CheckBox ) {
            when (view.id) {
                R.id.languageIsRTL -> {
                    sharedPreference.edit().putBoolean(LANGUAGE_ISRTL_VALUE_SHARED_PREF_KEY, view.isChecked).commit()
                }
            }
        }
    }
}

/**
 * Constants for language & rtl settings
 * */

const val LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY = "LANGUAGE_ADAPTER_VALUE"
const val LANGUAGE_ISRTL_VALUE_SHARED_PREF_KEY = "LANGUAGE_ISRTL"

const val DEFAULT_LANGUAGE_VALUE = "ENGLISH_UK"
const val DEFAULT_ISRTL_VALUE = false