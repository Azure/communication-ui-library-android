// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.robots

import android.R
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewInteraction
import com.azure.android.communication.ui.callingcompositedemoapp.util.UiTestUtils
import com.azure.android.communication.ui.callingcompositedemoapp.util.ViewIsDisplayedResource
import java.lang.IllegalStateException

abstract class ScreenRobot<T : ScreenRobot<T>> {

    fun waitUntilViewIdDoesNotExist(
        @IdRes viewId: Int,
        idlingResource: ViewIsDisplayedResource = ViewIsDisplayedResource(),
    ): ViewInteraction {
        val viewInteraction = idlingResource.waitUntilViewIsDisplayed {
            UiTestUtils.checkViewIdDoesNotExist(viewId)
        }
        return viewInteraction
    }

    fun waitUntilViewIdIsNotDisplayed(
        @IdRes viewId: Int,
        idlingResource: ViewIsDisplayedResource = ViewIsDisplayedResource(),
    ): ViewInteraction {
        val viewInteraction = idlingResource.waitUntilViewIsDisplayed {
            UiTestUtils.checkViewIdIsNotDisplayed(viewId)
        }
        return viewInteraction
    }

    fun waitUntilAllViewIdIsAreDisplayed(
        @IdRes viewId: Int,
        idlingResource: ViewIsDisplayedResource = ViewIsDisplayedResource(),
    ): ViewInteraction {
        val viewInteraction = idlingResource.waitUntilViewIsDisplayed {
            UiTestUtils.checkAllViewIdsAreDisplayed(viewId)
        }
        return viewInteraction
    }

    fun waitUntilViewIdIsDisplayedWhileCheckingForDialog(
        @IdRes viewId: Int,
        idlingResource: ViewIsDisplayedResource = ViewIsDisplayedResource(),
    ): ViewInteraction {
        val viewInteraction = idlingResource.waitUntilViewIsDisplayed {
            try {
                UiTestUtils.checkViewIdAndTextIsDisplayed(R.id.button1, "OK")
                val message = UiTestUtils.getTextFromTextView(R.id.message)
                throw IllegalStateException(message)
            } catch (ex: NoMatchingViewException) {
                UiTestUtils.checkViewIdIsDisplayed(viewId)
            }
        }
        return viewInteraction
    }

    fun waitUntilViewIdIsDisplayed(
        @IdRes viewId: Int,
        idlingResource: ViewIsDisplayedResource = ViewIsDisplayedResource(),
    ): ViewInteraction {
        val viewInteraction = idlingResource.waitUntilViewIsDisplayed {
            UiTestUtils.checkViewIdIsDisplayed(viewId)
        }
        return viewInteraction
    }

    fun waitUntilTextOnViewIsDisplayed(@IdRes viewId: Int, text: String): ViewInteraction {
        val idlingResource = ViewIsDisplayedResource()
        val viewInteraction = idlingResource.waitUntilViewIsDisplayed {
            UiTestUtils.checkViewIdAndTextIsDisplayed(viewId, text)
        }
        return viewInteraction
    }

    fun waitUntilTextOnViewIsDisplayed(
        @IdRes viewId: Int,
        @StringRes stringId: Int,
    ): ViewInteraction {
        val idlingResource = ViewIsDisplayedResource()
        val viewInteraction = idlingResource.waitUntilViewIsDisplayed {
            UiTestUtils.checkViewIdAndTextIsDisplayed(viewId, stringId)
        }
        return viewInteraction
    }

    fun waitUntilViewIdWithContentDescriptionIsDisplayed(
        @IdRes viewId: Int,
        string: String,
    ): ViewInteraction {
        val idlingResource = ViewIsDisplayedResource()
        val viewInteraction = idlingResource.waitUntilViewIsDisplayed {
            UiTestUtils.checkViewIdWithContentDescriptionIsDisplayed(viewId, string)
        }
        return viewInteraction
    }
}
