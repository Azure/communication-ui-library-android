package com.azure.android.communication.ui.callingcompositedemoapp.views

import android.content.Context
import android.util.AttributeSet
import com.azure.android.communication.ui.callingcompositedemoapp.R


internal open class AvatarImageButton(context: Context, attrs: AttributeSet?) :
    androidx.appcompat.widget.AppCompatImageButton(context, attrs)  {

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

    fun select() {
        isButtonSelected = true
    }

    fun unSelect() {
        isButtonSelected = false
    }

    fun isButtonSelected() = isButtonSelected

    fun toggle() {
        isButtonSelected = !isButtonSelected
    }


}