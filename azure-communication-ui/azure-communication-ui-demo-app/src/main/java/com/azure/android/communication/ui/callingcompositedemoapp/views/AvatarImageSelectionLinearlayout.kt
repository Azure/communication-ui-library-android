// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageButton
import com.azure.android.communication.ui.callingcompositedemoapp.AVATAR_IMAGE
import com.azure.android.communication.ui.callingcompositedemoapp.R
import com.azure.android.communication.ui.callingcompositedemoapp.SETTINGS_SHARED_PREFS

class AvatarImageSelectionLinearlayout(context: Context, attrs: AttributeSet?) :
    LinearLayout(context, attrs) {

    private lateinit var catImageButton: AppCompatImageButton
    private lateinit var foxImageButton: AppCompatImageButton
    private lateinit var koalaImageButton: AppCompatImageButton
    private lateinit var monkeyImageButton: AppCompatImageButton
    private lateinit var mouseImageButton: AppCompatImageButton
    private lateinit var octopusImageButton: AppCompatImageButton

    private val sharedPreference by lazy {
        context.getSharedPreferences(SETTINGS_SHARED_PREFS, Context.MODE_PRIVATE)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        catImageButton = findViewById(R.id.avatarCatImageButton)
        foxImageButton = findViewById(R.id.avatarFoxImageButton)
        koalaImageButton = findViewById(R.id.avatarKoalaImageButton)
        monkeyImageButton = findViewById(R.id.avatarMonkeyImageButton)
        mouseImageButton = findViewById(R.id.avatarMouseImageButton)
        octopusImageButton = findViewById(R.id.avatarOctopusImageButton)

        selectImageFromPreferences()

        catImageButton.setOnClickListener {
            selectImage(catImageButton)
            setAvatarImagePreferences(catImageButton, R.drawable.image_cat.toString())
        }

        foxImageButton.setOnClickListener {
            selectImage(foxImageButton)
            setAvatarImagePreferences(foxImageButton, R.drawable.image_fox.toString())
        }

        koalaImageButton.setOnClickListener {
            selectImage(koalaImageButton)
            setAvatarImagePreferences(koalaImageButton, R.drawable.image_koala.toString())
        }

        monkeyImageButton.setOnClickListener {
            selectImage(monkeyImageButton)
            setAvatarImagePreferences(monkeyImageButton, R.drawable.image_monkey.toString())
        }

        mouseImageButton.setOnClickListener {
            selectImage(mouseImageButton)
            setAvatarImagePreferences(mouseImageButton, R.drawable.image_mouse.toString())
        }

        octopusImageButton.setOnClickListener {
            selectImage(octopusImageButton)
            setAvatarImagePreferences(octopusImageButton, R.drawable.image_octopus.toString())
        }
    }

    private fun setAvatarImagePreferences(button: AppCompatImageButton, imageId: String) {
        if (button.isSelected) {
            sharedPreference.edit()
                .putString(AVATAR_IMAGE, imageId).apply()
        } else {
            sharedPreference.edit()
                .putString(AVATAR_IMAGE, "").apply()
        }
    }

    private fun selectImageFromPreferences() {
        val savedImage = sharedPreference.getString(AVATAR_IMAGE, "")

        if (savedImage == R.drawable.image_cat.toString()) {
            catImageButton.isSelected = true
        }
        if (savedImage == R.drawable.image_fox.toString()) {
            foxImageButton.isSelected = true
        }
        if (savedImage == R.drawable.image_koala.toString()) {
            koalaImageButton.isSelected = true
        }
        if (savedImage == R.drawable.image_monkey.toString()) {
            monkeyImageButton.isSelected = true
        }
        if (savedImage == R.drawable.image_mouse.toString()) {
            mouseImageButton.isSelected = true
        }
        if (savedImage == R.drawable.image_octopus.toString()) {
            octopusImageButton.isSelected = true
        }
    }

    private fun selectImage(button: AppCompatImageButton) {
        val previousState = button.isSelected
        unselectAllImages()
        if (!previousState) {
            button.isSelected = true
        }
    }

    private fun unselectAllImages() {
        catImageButton.isSelected = false
        foxImageButton.isSelected = false
        koalaImageButton.isSelected = false
        monkeyImageButton.isSelected = false
        mouseImageButton.isSelected = false
        octopusImageButton.isSelected = false
    }
}
