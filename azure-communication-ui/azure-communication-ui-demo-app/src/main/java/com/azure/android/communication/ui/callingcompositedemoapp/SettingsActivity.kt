package com.azure.android.communication.ui.callingcompositedemoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.LinearLayoutCompat
import com.azure.android.communication.ui.CallComposite
import com.azure.android.communication.ui.callingcompositedemoapp.launcher.FeatureFlagView
import com.azure.android.communication.ui.utilities.SupportedLanguages

class SettingsActivity : AppCompatActivity() {

    val supportedLanguages = SupportedLanguages.values()
    lateinit var autoCompleteTextView: AutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        val languageArrayAdapter = ArrayAdapter(applicationContext, R.layout.language_dropdown_item, supportedLanguages)
        autoCompleteTextView.setAdapter(languageArrayAdapter)

        Toast.makeText(this,"This is settings page", Toast.LENGTH_SHORT).show()
    }



}