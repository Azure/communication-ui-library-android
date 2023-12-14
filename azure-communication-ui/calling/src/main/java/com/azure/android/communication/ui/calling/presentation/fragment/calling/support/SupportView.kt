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
import com.microsoft.fluentui.drawer.DrawerDialog
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

    fun start(viewModel: SupportViewModel, viewLifecycleOwner: LifecycleOwner) {
        val menuDrawer = DrawerDialog(context, DrawerDialog.BehaviorType.BOTTOM)
        menuDrawer.setContentView(this)
        menuDrawer.setFade(0.5f)
        menuDrawer.setOnDismissListener {
            viewModel.dismissForm()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isVisibleStateFlow.collect {
                if (it) {
                    menuDrawer.show()
                } else {
                    menuDrawer.hide()
                }
            }
        }

        val sendButton : Button = findViewById(R.id.buttonSend)
        val cancelButton : Button = findViewById(R.id.buttonCancel)

        sendButton.setOnClickListener {
            viewModel.forwardEventToUser()
            viewModel.dismissForm()
        }

        cancelButton.setOnClickListener {
            viewModel.dismissForm()
        }
    }
}
