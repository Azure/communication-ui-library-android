// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.util

import android.view.View
import android.widget.EditText
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matcher
import org.hamcrest.Matchers

interface ACSViewAction : ViewAction {
    fun getText(): String
}

class GetEditTextAction : ACSViewAction {
    private lateinit var stringHolder: String

    override fun getConstraints(): Matcher<View> =
        Matchers.allOf(
            ViewMatchers.isDisplayed(),
            ViewMatchers.isAssignableFrom(EditText::class.java)
        )

    override fun getDescription() = "getting text from an EditText View"

    override fun perform(uiController: UiController?, view: View?) {
        val editText = view as EditText

        stringHolder = editText.text.toString()
    }

    override fun getText() = stringHolder
}
