@file:OptIn(InternalCoroutinesApi::class)

package com.azure.android.communication.ui.calling.presentation.fragment.calling.support

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.R
import com.microsoft.fluentui.widget.Button
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class SupportView : FrameLayout {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        inflate(context, R.layout.azure_communication_ui_calling_support_view, this)
    }

    override fun setVisibility(visibility: Int) {
        if (visibility == View.VISIBLE && this.visibility != View.VISIBLE) {
            super.setVisibility(visibility) // Make the view visible
            startAnimation(TranslateAnimation(0f, 0f, height.toFloat(), 0f).apply {
                duration = 300 // Animation duration in milliseconds
            })
        } else if (visibility == View.GONE && this.visibility != View.GONE) {
            startAnimation(TranslateAnimation(0f, 0f, 0f, height.toFloat()).apply {
                duration = 300 // Animation duration in milliseconds
                setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {}
                    override fun onAnimationEnd(animation: Animation) {
                        super@SupportView.setVisibility(View.GONE) // Hide the view after animation
                    }
                    override fun onAnimationRepeat(animation: Animation) {}
                })
            })
        }
    }

    fun start(viewModel: SupportViewModel, viewLifecycleOwner: LifecycleOwner) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isVisibleStateFlow.collect {
                visibility = if (it) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        }

        val sendButton : Button = findViewById(R.id.buttonSend)
        val cancelButton : Button = findViewById(R.id.buttonCancel)


        sendButton.setOnClickListener {
            viewModel.dismissForm()
        }

        cancelButton.setOnClickListener {
            viewModel.dismissForm()
        }
    }
}
