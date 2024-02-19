// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.android.communication.ui.callingcompositedemoapp.matchers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import com.azure.android.communication.ui.callingcompositedemoapp.R
import org.hamcrest.Description
import org.hamcrest.Matcher

class BottomCellViewHolderMatcher(
    private val name: String,
    @DrawableRes private val expectedId: Int,
    private val isSelected: Boolean,
) : BoundedMatcher<RecyclerView.ViewHolder, RecyclerView.ViewHolder>(RecyclerView.ViewHolder::class.java) {
    override fun describeTo(description: Description?) {
        description?.appendText("with $name and drawable from resource id: $expectedId")
    }

    override fun matchesSafely(item: RecyclerView.ViewHolder?): Boolean {
        val holderRoot = item?.itemView ?: return false
        val checkMark: ImageView =
            holderRoot.findViewById(R.id.azure_communication_ui_cell_check_mark)
        val audioDeviceTextView: TextView =
            holderRoot.findViewById(R.id.azure_communication_ui_cell_text)

        // helps to click after delay
        // on API 31, test case will fail if delay is removed
        Thread.sleep(1000)

        return audioDeviceTextView.text.toString().contains(name) &&
            verifyCheckMarkMatches(checkMark.visibility == View.VISIBLE)
    }

    private fun verifyCheckMarkMatches(isCheckMarkVisible: Boolean): Boolean {
        isSelected xor isCheckMarkVisible
        if (isSelected && isCheckMarkVisible) return true
        if (!isSelected && !isCheckMarkVisible) return true
        return false
    }
}

fun withBottomCellViewHolder(
    name: String,
    @DrawableRes expectedId: Int,
    isSelected: Boolean,
): Matcher<RecyclerView.ViewHolder> = BottomCellViewHolderMatcher(name, expectedId, isSelected)
