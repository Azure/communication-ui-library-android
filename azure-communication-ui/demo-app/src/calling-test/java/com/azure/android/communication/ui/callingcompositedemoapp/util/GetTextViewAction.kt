// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.util

import android.view.View
import android.widget.TextView
import androidx.test.espresso.UiController
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matcher
import org.hamcrest.Matchers

class GetTextViewAction : ACSViewAction {
    private lateinit var stringHolder: String

    override fun getConstraints(): Matcher<View> =
        Matchers.allOf(
            ViewMatchers.isDisplayed(),
            ViewMatchers.isAssignableFrom(TextView::class.java)
        )

    override fun getDescription() = "getting text from a Text View"

    override fun perform(uiController: UiController?, view: View?) {
        val textView = view as TextView

        stringHolder = textView.text.toString()
    }

    override fun getText() = stringHolder
}
