package com.azure.android.communication.ui.callingcompositedemoapp.robots

import com.azure.android.communication.ui.callingcompositedemoapp.robots.ScreenRobot
import androidx.annotation.IdRes
import androidx.test.espresso.Espresso
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.assertion.ViewAssertions
import android.view.View
import androidx.annotation.StringRes
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.azure.android.communication.ui.callingcompositedemoapp.R
import com.azure.android.communication.ui.callingcompositedemoapp.util.UiTestUtils
import com.azure.android.communication.ui.callingcompositedemoapp.util.ViewIsDisplayedResource
import org.hamcrest.CoreMatchers

abstract class ScreenRobot<T: ScreenRobot<T>> {

    fun waitUntilViewIdIsDisplayed(
        @IdRes viewId: Int,
        idlingResource: ViewIsDisplayedResource = ViewIsDisplayedResource()
    ): ViewInteraction {
        val viewInteraction = idlingResource.waitUntilViewIsDisplayed {
            UiTestUtils.checkViewIdIsDisplayed(viewId)
        }
        return viewInteraction
    }

    fun waitUntilViewAndTextIsDisplayed(@IdRes viewId: Int, @StringRes stringId: Int): ViewInteraction {
        val idlingResource = ViewIsDisplayedResource()
        val viewInteraction = idlingResource.waitUntilViewIsDisplayed {
            UiTestUtils.checkViewIdAndTextIsDisplayed(viewId, stringId)
        }
        return viewInteraction
    }

    fun waitUntilViewIdWithContentDescriptionIsDisplayed(
        @IdRes viewId: Int,
        string: String
    ): ViewInteraction {
        val idlingResource = ViewIsDisplayedResource()
        val viewInteraction = idlingResource.waitUntilViewIsDisplayed {
            UiTestUtils.checkViewIdWithContentDescriptionIsDisplayed(viewId, string)
        }
        return viewInteraction
    }
}