// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.views

import android.content.Context
import android.util.AttributeSet
import com.azure.android.communication.ui.callingcompositedemoapp.R

open class AvatarImageButton(context: Context, attrs: AttributeSet?) :
    androidx.appcompat.widget.AppCompatImageButton(context, attrs) {

    private var isButtonSelected = false

    override fun onCreateDrawableState(extraSpace: Int): IntArray? {
        val drawableState = super.onCreateDrawableState(extraSpace + 2)
        if (isButtonSelected) {
            mergeDrawableStates(
                drawableState,
                intArrayOf(R.attr.is_selected)
            )
        }
        return drawableState
    }

    fun unSelect() {
        isButtonSelected = false
    }

    fun toggle() {
        isButtonSelected = !isButtonSelected
    }

    fun select() {
        isButtonSelected = true
    }
}
