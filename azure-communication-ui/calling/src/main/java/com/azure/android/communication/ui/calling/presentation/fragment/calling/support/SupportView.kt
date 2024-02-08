// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

@file:OptIn(InternalCoroutinesApi::class)
package com.azure.android.communication.ui.calling.presentation.fragment.calling.support

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.util.AttributeSet
import android.widget.EditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.calling.implementation.R
import com.microsoft.fluentui.drawer.DrawerDialog
import com.microsoft.fluentui.widget.Button
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * SupportView is a custom view that is used to display the support form.
 * It is displayed when the user clicks on the support button.
 */
internal class SupportView : ConstraintLayout {

    private val sendButton: Button by lazy { findViewById(R.id.azure_communication_ui_send_button) }
    private val cancelButton: Button by lazy { findViewById(R.id.azure_communication_ui_cancel_button) }
    private val editText: EditText by lazy { findViewById(R.id.azure_communication_ui_user_message_edit_text) }
    private val screenshotCheckBox: SwitchCompat by lazy { findViewById(R.id.azure_communication_ui_include_screenshot_toggle) }
    private val privacyPolicyButton: AppCompatTextView by lazy { findViewById(R.id.azure_communication_ui_support_form_privacy_link) }

    private val menuDrawer: DrawerDialog by lazy {
        DrawerDialog(context, DrawerDialog.BehaviorType.BOTTOM).apply {
            setContentView(this@SupportView)
            setCanceledOnTouchOutside(true)
            setFade(0.5f)
        }
    }

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

        screenshotCheckBox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.shouldIncludeScreenshot.value = isChecked
        }

        privacyPolicyButton.setOnClickListener {
            val intent = Intent(ACTION_VIEW)
            intent.setData(Uri.parse(context.getString(R.string.azure_communication_ui_calling_view_link_privacy_policy_url)))
            context.startActivity(intent)
        }
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
            viewModel.shouldIncludeScreenshot.collect {
                screenshotCheckBox.isChecked = it
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.clearEditTextStateFlow.collect {
                editText.setText("")
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isSubmitEnabledStateFlow.collect {
                sendButton.isEnabled = it
            }
        }
    }
}
