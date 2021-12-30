// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.util

import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*

object UiTestUtils {

    @Throws(NoMatchingViewException::class)
    fun checkViewIdIsDisplayed(@IdRes viewId: Int): ViewInteraction =
        onView(withId(viewId)).check(ViewAssertions.matches(isDisplayed()))

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

    private fun withRecyclerView(@IdRes recyclerViewId: Int): RecyclerViewMatcher = RecyclerViewMatcher(recyclerViewId)

    @Throws(NoMatchingViewException::class)
    fun check3IemRecyclerViewHolderAtPosition(@IdRes recyclerViewId: Int,
                                              position: Int,
                                              recyclerViewHolderViewIds: Triple<Int, Int, Int>) {
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

    fun withViewAtPosition(position: Int, itemMatcher: Matcher<View?>): Matcher<View?>? {
        return object : BoundedMatcher<View?, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description?) {
                itemMatcher.describeTo(description)
            }

            override fun matchesSafely(recyclerView: RecyclerView): Boolean {
                val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
                return viewHolder != null && itemMatcher.matches(viewHolder.itemView)
            }
        }
    }
}
