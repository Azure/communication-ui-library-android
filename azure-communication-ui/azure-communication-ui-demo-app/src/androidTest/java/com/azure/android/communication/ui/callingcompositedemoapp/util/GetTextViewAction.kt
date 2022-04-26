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

    override fun getDescription() = "getting text from an EditText View"

    override fun perform(uiController: UiController?, view: View?) {
        val editText = view as TextView

        stringHolder = editText.text.toString()
    }

    override fun getText() = stringHolder
}
