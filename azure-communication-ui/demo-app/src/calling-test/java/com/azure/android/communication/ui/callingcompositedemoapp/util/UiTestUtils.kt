// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.util

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageButton
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.azure.android.communication.ui.callingcompositedemoapp.R
import com.azure.android.communication.ui.callingcompositedemoapp.matchers.withBottomCellViewHolder
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.not

object UiTestUtils {
    @Throws(NoMatchingViewException::class)
    fun clickBottomCellViewHolder(
        @IdRes recyclerViewId: Int,
        @DrawableRes expectedItemDrawable: Int,
        text: String,
        isSelected: Boolean,
    ): ViewInteraction =
        onView(withId(recyclerViewId))
            .check(ViewAssertions.matches(isDisplayed()))
            .perform(
                RecyclerViewActions.actionOnHolderItem(
                    withBottomCellViewHolder(text, expectedItemDrawable, isSelected),
                    click(),
                ),
            )

    @Throws(NoMatchingViewException::class)
    fun checkViewWithTextIsDisplayed(text: String): ViewInteraction = onView(withText(text)).check(ViewAssertions.matches(isDisplayed()))

    @Throws(NoMatchingViewException::class)
    fun checkViewIdIsDisplayed(
        @IdRes viewId: Int,
    ): ViewInteraction = onView(withId(viewId)).check(ViewAssertions.matches(isDisplayed()))

    @Throws(NoMatchingViewException::class)
    fun checkAllViewIdsAreDisplayed(
        @IdRes viewId: Int,
    ): ViewInteraction = onView(allOf(withId(viewId), isDisplayed()))

    @Throws(NoMatchingViewException::class)
    fun checkViewIdIsNotDisplayed(
        @IdRes viewId: Int,
    ): ViewInteraction = onView(withId(viewId)).check(ViewAssertions.matches(not(isDisplayed())))

    fun checkViewIdDoesNotExist(
        @IdRes viewId: Int,
    ): ViewInteraction = onView(withId(viewId)).check(doesNotExist())

    @Throws(NoMatchingViewException::class)
    fun checkViewIdWithContentDescriptionIsDisplayed(
        @IdRes viewId: Int,
        contentDescription: String,
    ): ViewInteraction =
        onView(
            allOf(
                withId(viewId),
                withContentDescription(contentDescription),
            ),
        ).check(ViewAssertions.matches(isDisplayed()))

    @Throws(NoMatchingViewException::class)
    fun checkViewIdAndTextIsDisplayed(
        @IdRes viewId: Int,
        text: String,
    ): ViewInteraction =
        onView(
            allOf(
                withId(viewId),
                withText(text),
            ),
        ).check(ViewAssertions.matches(isDisplayed()))

    @Throws(NoMatchingViewException::class)
    fun checkViewIdAndTextIsDisplayed(
        @IdRes viewId: Int,
        @StringRes stringId: Int,
    ): ViewInteraction =
        onView(
            allOf(
                withId(viewId),
                withText(stringId),
            ),
        ).check(ViewAssertions.matches(isDisplayed()))

    @Throws(NoMatchingViewException::class)
    fun clickViewWithId(
        @IdRes viewId: Int,
    ): ViewInteraction = onView(allOf(withId(viewId), isDisplayed())).perform(click())

    @Throws(NoMatchingViewException::class)
    fun clickViewWithIdAndText(
        @IdRes viewId: Int,
        @StringRes text: Int,
    ): ViewInteraction =
        onView(
            allOf(withId(viewId), withText(text), isDisplayed()),
        ).perform(click())

    @Throws(NoMatchingViewException::class)
    fun clickViewWithIdAndText(
        @IdRes viewId: Int,
        text: String,
    ): ViewInteraction =
        onView(
            allOf(withId(viewId), withText(text), isDisplayed()),
        ).perform(click())

    @Throws(NoMatchingViewException::class)
    fun clickViewWithIdAndContentDescription(
        @IdRes viewId: Int,
        text: String,
    ): ViewInteraction =
        onView(
            allOf(withId(viewId), withContentDescription(text), isDisplayed()),
        ).perform(click())

    private fun withRecyclerView(
        @IdRes recyclerViewId: Int,
    ): RecyclerViewMatcher = RecyclerViewMatcher(recyclerViewId)

    @Throws(NoMatchingViewException::class)
    fun check3IemRecyclerViewHolderAtPosition(
        @IdRes recyclerViewId: Int,
        position: Int,
        recyclerViewHolderViewIds: Triple<Int, Int, Int>,
    ) {
        onView(withRecyclerView(recyclerViewId).atPosition(position))
            .check(
                matches(
                    allOf(
                        hasDescendant(withId(recyclerViewHolderViewIds.first)),
                        hasDescendant(withId(recyclerViewHolderViewIds.second)),
                        hasDescendant(withId(recyclerViewHolderViewIds.third)),
                        isDisplayed(),
                    ),
                ),
            )
    }

    fun checkRecyclerViewViewHolderText(
        @IdRes recyclerViewId: Int,
        position: Int,
        @IdRes viewId: Int,
        text: String,
    ) {
        onView(withRecyclerView(recyclerViewId).atPositionOnView(position, viewId))
            .check(matches(withText(text)))
    }

    private fun getTextFromViewAction(
        @IdRes viewId: Int,
        viewAction: ACSViewAction,
    ): String {
        val textViewMatcher = allOf(withId(viewId), isDisplayed())

        onView(textViewMatcher).perform(viewAction)
        return viewAction.getText()
    }

    fun getTextFromTextView(
        @IdRes viewId: Int,
    ) = getTextFromViewAction(viewId, GetTextViewAction())

    fun getTextFromButtonView(
        @IdRes viewId: Int,
    ) = getTextFromViewAction(viewId, GetButtonTextAction())

    fun getTextFromEdittextView(
        @IdRes viewId: Int,
    ) = getTextFromViewAction(viewId, GetEditTextAction())

    @Throws(NoMatchingViewException::class)
    fun navigateUp() {
        val upButton =
            onView(
                allOf(
                    instanceOf(AppCompatImageButton::class.java),
                    withContentDescription(androidx.appcompat.R.string.abc_action_bar_up_description),
                    withParent(withId(R.id.action_bar)),
                    isDisplayed(),
                ),
            )
        upButton.perform(click())
    }
}
