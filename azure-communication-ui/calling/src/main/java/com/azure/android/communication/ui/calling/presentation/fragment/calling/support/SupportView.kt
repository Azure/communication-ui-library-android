// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

@file:OptIn(InternalCoroutinesApi::class)
package com.azure.android.communication.ui.calling.presentation.fragment.calling.support

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.EditText
import android.widget.FrameLayout
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.R
import com.microsoft.fluentui.drawer.DrawerDialog
import com.microsoft.fluentui.widget.Button
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * SupportView is a custom view that is used to display the support form.
 * It is displayed when the user clicks on the support button.
 */
internal class SupportView : FrameLayout {

    private val sendButton : Button by lazy { findViewById(R.id.buttonSend) }
    private val cancelButton : Button by lazy { findViewById(R.id.buttonCancel) }
    private val editText : EditText by lazy { findViewById(R.id.editTextMessage) }
    private val menuDrawer by lazy { DrawerDialog(context, DrawerDialog.BehaviorType.BOTTOM).apply {
        setContentView(this@SupportView)
        setCanceledOnTouchOutside(true)
        setFade(0.5f)
    }}

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
        // Text Changed, Submit, Cancel Buttons
        bindViewInputs(viewModel)

        // Send Button stat
        bindViewOutputs(viewLifecycleOwner, viewModel)
    }

    private fun bindViewOutputs(
        viewLifecycleOwner: LifecycleOwner,
        viewModel: SupportViewModel
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isVisibleStateFlow.collect {
                if (it) {
                    menuDrawer.show()
                } else {
                    menuDrawer.hide()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isSubmitEnabledStateFlow.collect {
                sendButton.isEnabled = it
            }
        }
    }

    private fun bindViewInputs(viewModel: SupportViewModel) {
        menuDrawer.setOnDismissListener {
            viewModel.dismissForm()
        }

        sendButton.setOnClickListener {
            viewModel.forwardEventToUser()
            viewModel.dismissForm()
        }

        cancelButton.setOnClickListener {
            viewModel.dismissForm()
        }

        editText.addTextChangedListener { text ->
            viewModel.userMessage = text.toString()
        }
    }
}
