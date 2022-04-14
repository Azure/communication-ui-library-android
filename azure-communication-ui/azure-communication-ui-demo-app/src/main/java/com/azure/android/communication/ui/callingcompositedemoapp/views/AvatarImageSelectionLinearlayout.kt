// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.azure.android.communication.ui.callingcompositedemoapp.AVATAR_IMAGE
import com.azure.android.communication.ui.callingcompositedemoapp.R
import com.azure.android.communication.ui.callingcompositedemoapp.SETTINGS_SHARED_PREFS

class AvatarImageSelectionLinearlayout(context: Context, attrs: AttributeSet?) :
    LinearLayout(context, attrs) {

    private lateinit var catImageButton: AvatarImageButton
    private lateinit var foxImageButton: AvatarImageButton
    private lateinit var koalaImageButton: AvatarImageButton
    private lateinit var monkeyImageButton: AvatarImageButton
    private lateinit var mouseImageButton: AvatarImageButton
    private lateinit var octopusImageButton: AvatarImageButton

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
            sharedPreference.edit().putString(AVATAR_IMAGE, R.drawable.image_cat.toString())
                .apply()
        }

        foxImageButton.setOnClickListener {
            selectImage(foxImageButton)
            sharedPreference.edit().putString(AVATAR_IMAGE, R.drawable.image_fox.toString())
                .apply()
        }

        koalaImageButton.setOnClickListener {
            selectImage(koalaImageButton)
            sharedPreference.edit().putString(AVATAR_IMAGE, R.drawable.image_koala.toString())
                .apply()
        }

        monkeyImageButton.setOnClickListener {
            selectImage(monkeyImageButton)
            sharedPreference.edit().putString(AVATAR_IMAGE, R.drawable.image_monkey.toString())
                .apply()
        }

        mouseImageButton.setOnClickListener {
            selectImage(mouseImageButton)
            sharedPreference.edit().putString(AVATAR_IMAGE, R.drawable.image_mouse.toString())
                .apply()
        }

        octopusImageButton.setOnClickListener {
            selectImage(octopusImageButton)
            sharedPreference.edit()
                .putString(AVATAR_IMAGE, R.drawable.image_octopus.toString()).apply()
        }
    }

    private fun selectImageFromPreferences() {
        val savedImage = sharedPreference.getString(AVATAR_IMAGE, "")

        if (savedImage == R.drawable.image_cat.toString()) {
            catImageButton.select()
        }
        if (savedImage == R.drawable.image_fox.toString()) {
            foxImageButton.select()
        }
        if (savedImage == R.drawable.image_koala.toString()) {
            koalaImageButton.select()
        }
        if (savedImage == R.drawable.image_monkey.toString()) {
            monkeyImageButton.select()
        }
        if (savedImage == R.drawable.image_mouse.toString()) {
            mouseImageButton.select()
        }
        if (savedImage == R.drawable.image_octopus.toString()) {
            octopusImageButton.select()
        }
    }

    private fun selectImage(button: AvatarImageButton) {
        unselectAllImages()
        button.select()
        button.refreshDrawableState()
    }

    private fun unselectAllImages() {
        catImageButton.unSelect()
        catImageButton.refreshDrawableState()
        foxImageButton.unSelect()
        foxImageButton.refreshDrawableState()
        koalaImageButton.unSelect()
        koalaImageButton.refreshDrawableState()
        monkeyImageButton.unSelect()
        monkeyImageButton.refreshDrawableState()
        mouseImageButton.unSelect()
        mouseImageButton.refreshDrawableState()
        octopusImageButton.unSelect()
        octopusImageButton.refreshDrawableState()
    }
}
