package com.azure.android.communication.ui.calling.presentation.fragment.calling.support

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.FrameLayout
import com.azure.android.communication.ui.R

class SupportView : FrameLayout {
    constructor(context: Context) : super(context) {
        inflate(context, R.layout.azure_communication_ui_calling_support_view, this)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        inflate(context, R.layout.azure_communication_ui_calling_support_view, this)
    }
}