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
import androidx.test.espresso.matcher.ViewMatchers
import junit.framework.AssertionFailedError
import org.hamcrest.Matchers

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

internal fun assertViewText(id: Int, text: String) {
    Espresso.onView(
        Matchers.allOf(
            ViewMatchers.withId(id)
        )
    ).check(ViewAssertions.matches(ViewMatchers.withText(text)))
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
