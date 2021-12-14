// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.util

import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.*
import org.hamcrest.Matchers.allOf

object UiTestUtils {

    @Throws(NoMatchingViewException::class)
    fun checkViewIdIsDisplayed(@IdRes viewId: Int): ViewInteraction =
        onView(withId(viewId)).check(ViewAssertions.matches(isDisplayed()))

    @Throws(NoMatchingViewException::class)
    fun checkViewTextIsDisplayed(@StringRes stringId: Int): ViewInteraction =
        onView(withText(stringId)).check(ViewAssertions.matches(isDisplayed()))

    @Throws(NoMatchingViewException::class)
    fun clickViewWithId(@IdRes viewId: Int): ViewInteraction =
        onView(allOf(withId(viewId), isDisplayed())).perform(click())

    @Throws(NoMatchingViewException::class)
    fun clickViewWithIdAndText(@IdRes viewId: Int, @StringRes text: Int): ViewInteraction =
        onView(
            allOf(withId(viewId), withText(text), isDisplayed())
        ).perform(click())

    @Throws(NoMatchingViewException::class)
    fun clickViewWithIdAndText(@IdRes viewId: Int, text: String): ViewInteraction =
        onView(
            allOf(withId(viewId), withText(text), isDisplayed())
        ).perform(click())
}