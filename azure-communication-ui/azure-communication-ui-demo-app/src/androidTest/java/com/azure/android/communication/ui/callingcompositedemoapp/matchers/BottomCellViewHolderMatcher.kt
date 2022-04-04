// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.android.communication.ui.callingcompositedemoapp.matchers

import android.content.res.Resources
import android.graphics.drawable.Drawable
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
    private val isSelected: Boolean
) : BoundedMatcher<RecyclerView.ViewHolder, RecyclerView.ViewHolder>(RecyclerView.ViewHolder::class.java) {
    private lateinit var resourceName: String

    override fun describeTo(description: Description?) {
        description?.appendText("with $name and drawable from resource id: $expectedId")
        // resourceName.let { description?.appendText("[$it]") }
    }

    override fun matchesSafely(item: RecyclerView.ViewHolder?): Boolean {
        val holderRoot = item?.itemView ?: return false
        val checkMark: ImageView = holderRoot.findViewById(R.id.cell_check_mark)
        val audioDeviceTextView: TextView = holderRoot.findViewById(R.id.cell_text)
        val audioDeviceIcon: ImageView = holderRoot.findViewById(R.id.cell_icon)

        return audioDeviceTextView.text.toString().contains(name) &&
            matchesDrawable(audioDeviceIcon) &&
            verifyCheckMarkMatches(checkMark.visibility == View.VISIBLE)
    }

    private fun verifyCheckMarkMatches(isCheckMarkVisible: Boolean): Boolean {
        isSelected xor isCheckMarkVisible
        if (isSelected && isCheckMarkVisible) return true
        if (!isSelected && !isCheckMarkVisible) return true
        return false
    }

    private fun matchesDrawable(target: ImageView): Boolean {
        val drawable: Drawable = target.drawable ?: return false
        val resources: Resources = target.context.resources
        resourceName = resources.getResourceEntryName(expectedId)
        val expectedDrawable: Drawable? = resources.getDrawable(expectedId, target.context.theme)

        return expectedDrawable?.constantState?.let { it == drawable.constantState } ?: false
    }
}

fun withBottomCellViewHolder(
    name: String,
    @DrawableRes expectedId: Int,
    isSelected: Boolean
): Matcher<RecyclerView.ViewHolder> = BottomCellViewHolderMatcher(name, expectedId, isSelected)
