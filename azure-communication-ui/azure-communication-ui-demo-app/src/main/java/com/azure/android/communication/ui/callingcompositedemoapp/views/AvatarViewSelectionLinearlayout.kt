package com.azure.android.communication.ui.callingcompositedemoapp.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.azure.android.communication.ui.callingcompositedemoapp.R

class AvatarViewSelectionLinearlayout(context: Context, attrs: AttributeSet?): LinearLayout(context, attrs) {

    private lateinit var imageOne: AvatarImageButton
    private lateinit var imageTwo: AvatarImageButton

    override fun onFinishInflate() {
        super.onFinishInflate()
        imageOne = findViewById(R.id.avatarOneImage)
        imageTwo = findViewById(R.id.avatarTwoImage)


        imageOne.setOnClickListener {
            imageOne.toggle()
            imageTwo.unSelect()
            imageOne.refreshDrawableState()
            imageTwo.refreshDrawableState()
        }

        imageTwo.setOnClickListener {
            imageTwo.toggle()
            imageOne.unSelect()
            imageOne.refreshDrawableState()
            imageTwo.refreshDrawableState()
        }
    }

    fun getAvatarName(): String? {

        if(imageOne.isButtonSelected()) return "one"

        if(imageTwo.isButtonSelected()) return "two"

        return null

    }


    fun setAvatar(string: String?) {
        string?.let {
            if(it == "one") {
                imageOne.select()
            }
            if(it == "two") {
                imageTwo.select()
            }
        }
    }
}