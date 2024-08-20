// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication

import android.os.SystemClock
import androidx.annotation.IdRes
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.AssertionFailedError
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf

// Common UI helper functions.

internal fun waitUntilDisplayed(id: Int) {
    waitUntilViewIsDisplayed { assertDisplayed(id) }
}

internal fun assertDisplayed(id: Int): ViewInteraction {
    return Espresso.onView(
        Matchers.allOf(
            ViewMatchers.withId(id)
        )
    ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
}

internal fun assertNotDisplayed(id: Int): ViewInteraction? {
    return Espresso.onView(
        Matchers.allOf(
            ViewMatchers.withId(id)
        )
    ).check(doesNotExist())
}

internal fun assertViewGone(id: Int): ViewInteraction? {
    return Espresso.onView(
        Matchers.allOf(
            ViewMatchers.withId(id)
        )
    ).check(ViewAssertions.matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
}

internal fun assertViewNotDisplayed(id: Int) {
    Espresso.onView(
        Matchers.allOf(
            ViewMatchers.withId(id)
        )
    ).check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))
}

internal fun assertNotExist(id: Int): ViewInteraction? {
    return Espresso.onView(
        Matchers.allOf(
            ViewMatchers.withId(id)
        )
    ).check(doesNotExist())
}

internal fun assertViewText(id: Int, text: String) {
    Espresso.onView(
        Matchers.allOf(
            ViewMatchers.withId(id)
        )
    ).check(ViewAssertions.matches(ViewMatchers.withText(text)))
}

internal fun tapOnScreen() {
    Espresso.onView(ViewMatchers.isRoot())
        .perform(ViewActions.click())
}
internal fun tapWithTextWhenDisplayed(text: String) {
    // wait until text is displayed
    waitUntilViewIsDisplayed {
        Espresso.onView(
            Matchers.allOf(ViewMatchers.withText(text), withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
    tapDelay()
    Espresso.onView(
        Matchers.allOf(ViewMatchers.withText(text), withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))
    ).perform(ViewActions.click())
}

internal fun assertTextDisplayed(text: String) {
    // wait until text is displayed
    waitUntilViewIsDisplayed {
        Espresso.onView(
            Matchers.allOf(ViewMatchers.withText(text), withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}

internal fun assertViewText(id: Int, textId: Int) {
    Espresso.onView(
        Matchers.allOf(
            ViewMatchers.withId(id)
        )
    ).check(ViewAssertions.matches(ViewMatchers.withText(textId)))
}

internal fun tap(id: Int) {
    assertDisplayed(id)

    Espresso.onView(ViewMatchers.withId(id))
        .perform(ViewActions.click())
}

internal fun tapWhenDisplayed(id: Int) {
    waitUntilDisplayed(id)

    // XXX intermittently, this function seems return without a tap actually taking place.
    // This delay appears to help ¯\_(ツ)_/¯
    SystemClock.sleep(200L)

    tap(id)
}

private fun tapDelay() {
    // XXX intermittently, this function seems return without a tap actually taking place.
    // This delay appears to help ¯\_(ツ)_/¯
    SystemClock.sleep(200L)
}

internal fun waitUntilViewIsDisplayed(idlingCheck: () -> ViewInteraction): ViewInteraction {
    var isReady = false
    var timeOut = 0L
    lateinit var viewInteraction: ViewInteraction

    while (!isReady && timeOut < 70000L) {
        try {
            viewInteraction = idlingCheck()
            isReady = true
        } catch (ex: Throwable) {
            if (ex is AssertionFailedError || ex is NoMatchingViewException) {
                SystemClock.sleep(2000L)
                timeOut += 2000L
            } else throw ex
        }
    }
    if (isReady) return viewInteraction
    throw IllegalStateException("Timed out waiting for view")
}

internal fun assertViewHasChild(@IdRes id: Int, n: Int) {
    Espresso.onView(ViewMatchers.withId(id))
        .check(ViewAssertions.matches(ViewMatchers.hasChildCount(n)))
}

internal fun assertTextDisplayed(stringId: Int) {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val expectedText = context.getString(stringId)
    Espresso.onView(ViewMatchers.withText(expectedText))
        .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
}

internal fun assertTextNotDisplayed(stringId: Int) {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val expectedText = context.getString(stringId)
    Espresso.onView(ViewMatchers.withText(expectedText))
        .check(ViewAssertions.doesNotExist())
}

internal fun assertTextNotDisplayed(text: String) {
    Espresso.onView(ViewMatchers.withText(text))
        .check(ViewAssertions.doesNotExist())
}

internal fun tapOnText(stringId: Int) {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val textToTap = context.getString(stringId)
    Espresso.onView(ViewMatchers.withText(textToTap))
        .perform(ViewActions.click())
}

internal fun waitUntilTextDisplayed(stringId: Int) {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val textToWaitFor = context.getString(stringId)
    waitUntilViewIsDisplayed {
        Espresso.onView(ViewMatchers.withText(textToWaitFor))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}

internal fun confirmTextDisplayed(id: Int, text: String) {
    Espresso.onView(allOf(ViewMatchers.withId(id), ViewMatchers.withText(text)))
        .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
}

internal fun tapWithTextWhenDisplayedById(id: Int) {
    // wait until text is displayed
    waitUntilViewIsDisplayed {
        Espresso.onView(
            Matchers.allOf(ViewMatchers.withId(id), withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
    tapDelay()
    Espresso.onView(
        Matchers.allOf(ViewMatchers.withId(id), withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))
    ).perform(ViewActions.click())
}
