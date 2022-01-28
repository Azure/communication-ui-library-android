// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.util

import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageButton
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.not

object UiTestUtils {

    @Throws(NoMatchingViewException::class)
    fun checkViewIdIsDisplayed(@IdRes viewId: Int): ViewInteraction =
        onView(withId(viewId)).check(ViewAssertions.matches(isDisplayed()))

    @Throws(NoMatchingViewException::class)
    fun checkViewIdIsNotDisplayed(@IdRes viewId: Int): ViewInteraction =
        onView(withId(viewId)).check(ViewAssertions.matches(not(isDisplayed())))

    @Throws(NoMatchingViewException::class)
    fun checkViewIdWithContentDescriptionIsDisplayed(@IdRes viewId: Int, contentDescription: String): ViewInteraction =
        onView(
            allOf(
                withId(viewId),
                withContentDescription(contentDescription)
            )
        ).check(ViewAssertions.matches(isDisplayed()))

    @Throws(NoMatchingViewException::class)
    fun checkViewTextIsDisplayed(@StringRes stringId: Int): ViewInteraction =
        onView(withText(stringId)).check(ViewAssertions.matches(isDisplayed()))

    @Throws(NoMatchingViewException::class)
    fun checkViewIdAndTextIsDisplayed(@IdRes viewId: Int, @StringRes stringId: Int): ViewInteraction =
        onView(
            allOf(
                withId(viewId),
                withText(stringId)
            )
        ).check(ViewAssertions.matches(isDisplayed()))

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

    @Throws(NoMatchingViewException::class)
    fun clickViewWithIdAndContentDescription(@IdRes viewId: Int, text: String): ViewInteraction =
        onView(
            allOf(withId(viewId), withContentDescription(text), isDisplayed())
        ).perform(click())

    private fun withRecyclerView(@IdRes recyclerViewId: Int): RecyclerViewMatcher = RecyclerViewMatcher(recyclerViewId)

    @Throws(NoMatchingViewException::class)
    fun check3IemRecyclerViewHolderAtPosition(
        @IdRes recyclerViewId: Int,
        position: Int,
        recyclerViewHolderViewIds: Triple<Int, Int, Int>
    ) {
        onView(withRecyclerView(recyclerViewId).atPosition(position))
            .check(
                ViewAssertions.matches(
                    allOf(
                        hasDescendant(withId(recyclerViewHolderViewIds.first)),
                        hasDescendant(withId(recyclerViewHolderViewIds.second)),
                        hasDescendant(withId(recyclerViewHolderViewIds.third)),
                        isDisplayed()
                    )
                )
            )
    }

    private fun getTextFromViewAction(@IdRes viewId: Int, viewAction: ACSViewAction): String {
        val textViewMatcher = allOf(withId(viewId), isDisplayed())

        onView(textViewMatcher).perform(viewAction)
        return viewAction.getText()
    }

    fun getTextFromButtonView(@IdRes viewId: Int) =
        getTextFromViewAction(viewId, GetButtonTextAction())

    fun getTextFromEdittextView(@IdRes viewId: Int) =
        getTextFromViewAction(viewId, GetEditTextAction())

    @Throws(NoMatchingViewException::class)
    fun navigateUp() {
        val upButton = onView(
            allOf(
                instanceOf(AppCompatImageButton::class.java),
                withContentDescription(androidx.appcompat.R.string.abc_action_bar_up_description),
                isDisplayed()
            )
        )
        upButton.perform(click())
    }
}
