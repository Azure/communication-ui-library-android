package com.azure.android.communication.ui.callingcompositedemoapp

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.LinearLayoutCompat
import com.azure.android.communication.ui.CallComposite
import com.azure.android.communication.ui.callingcompositedemoapp.launcher.FeatureFlagView
import com.azure.android.communication.ui.utilities.FEATURE_FLAG_SHARED_PREFS_KEY
import com.azure.android.communication.ui.utilities.SupportedLanguages

class SettingsActivity : AppCompatActivity() {

    private val supportedLanguages = SupportedLanguages.values()
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var languageArrayAdapter: ArrayAdapter<SupportedLanguages>
    private val sharedPreference by lazy {
        getSharedPreferences(FEATURE_FLAG_SHARED_PREFS_KEY, Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        languageArrayAdapter = ArrayAdapter(applicationContext, R.layout.language_dropdown_item, supportedLanguages)
        autoCompleteTextView.setAdapter(languageArrayAdapter)
        languageArrayAdapter.filter.filter(null)
    }

    override fun onResume() {
        super.onResume()

        autoCompleteTextView.setText(sharedPreference.getString("LANGUAGE_ADAPTER_POSITION", "ENGLISH_UK"), true)
        languageArrayAdapter.filter.filter(null)

        autoCompleteTextView.setOnItemClickListener{ parent, view, position, id->
            val selectedItem: String = autoCompleteTextView.adapter.getItem(position).toString()
            sharedPreference.edit().putString("LANGUAGE_ADAPTER_POSITION", selectedItem).commit()
        }
    }


}