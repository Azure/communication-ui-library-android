// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.util

import android.view.View
import android.widget.Button
import androidx.test.espresso.UiController
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf

class GetButtonTextAction : ACSViewAction {
    private lateinit var stringHolder: String

    override fun getConstraints(): Matcher<View> = allOf(isDisplayed(), isAssignableFrom(Button::class.java))

    override fun getDescription() = "getting text from a Button View"

    override fun perform(
        uiController: UiController?,
        view: View?,
    ) {
        val button = view as Button

        stringHolder = button.text.toString()
    }

    override fun getText() = stringHolder
}
