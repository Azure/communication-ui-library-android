// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.setup.components

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.calling.error.CallStateError
import com.azure.android.communication.ui.calling.error.ErrorCode
import com.azure.android.communication.ui.calling.models.CallCompositeEventCode
import com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_FADE
import com.microsoft.fluentui.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class ErrorInfoView(private val rootView: View) {
    private lateinit var snackBar: Snackbar
    private lateinit var snackBarTextView: TextView

    fun start(viewLifecycleOwner: LifecycleOwner, snackBarViewModel: ErrorInfoViewModel) {
        initSnackBar()
        viewLifecycleOwner.lifecycleScope.launch {
            snackBarViewModel.getCallStateErrorStateFlow().collect {
                if (it == null && snackBarViewModel.callCompositeErrorFlow.value == null) {
                    snackBar.dismiss()
                } else {
                    it?.let { displaySnackBar(it, "") }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            snackBarViewModel.callCompositeErrorFlow.collect {
                if (it == null && snackBarViewModel.getCallStateErrorStateFlow().value == null) {
                    snackBar.dismiss()
                } else {
                    it?.let {
                        displaySnackBar(
                            null,
                            rootView.context.getText(R.string.azure_communication_ui_calling_call_video_fails_error)
                                .toString()
                        )
                    }
                }
            }
        }
    }

    fun stop() {
        if (snackBar.isShown) {
            snackBar.dismiss()
        }
        rootView.invalidate()
        // to fix memory leak
        snackBar.anchorView = null
    }

    private fun displaySnackBar(it: CallStateError?, message: String) {
        val errorMessage = if (it != null) {
            getErrorMessage(it)
        } else {
            message
        }

        if (errorMessage.isBlank()) return
        snackBarTextView.text = errorMessage
        snackBar.run {
            if (isShown) {
                dismiss()
            }
            show()

            view.contentDescription =
                "${context.getString(R.string.azure_communication_ui_calling_alert_title)}: $errorMessage"
            view.accessibilityFocus()
        }
    }

    private fun getErrorMessage(it: CallStateError): CharSequence {
        if (CallCompositeEventCode.CALL_EVICTED == it.callCompositeEventCode) {
            return rootView.context.getText(R.string.azure_communication_ui_calling_call_state_evicted)
        }
        return when (it.errorCode) {
            ErrorCode.CALL_END_FAILED -> rootView.context.getText(R.string.azure_communication_ui_calling_call_state_error_call_end)
            ErrorCode.CALL_JOIN_FAILED -> rootView.context.getText(R.string.azure_communication_ui_calling_snack_bar_text_error_call_join)
            ErrorCode.NETWORK_NOT_AVAILABLE -> rootView.context.getString(R.string.azure_communication_ui_calling_no_connection_available)
            ErrorCode.MICROPHONE_NOT_AVAILABLE -> rootView.context.getString(R.string.azure_communication_ui_calling_mic_used)
            else -> ""
        }
    }

    private fun initSnackBar() {
        snackBar = Snackbar.make(
            rootView,
            "",
            Snackbar.LENGTH_INDEFINITE,
            Snackbar.Style.REGULAR
        ).apply {
            animationMode = ANIMATION_MODE_FADE
            setAction(rootView.context!!.getText(R.string.azure_communication_ui_calling_snack_bar_button_dismiss)) {}
            anchorView =
                rootView.findViewById(R.id.azure_communication_ui_setup_join_call_button)
            view.background.colorFilter = PorterDuffColorFilter(
                ContextCompat.getColor(
                    rootView.context,
                    R.color.azure_communication_ui_calling_color_snack_bar_background
                ),
                PorterDuff.Mode.SRC_IN
            )
            snackBarTextView = view.findViewById(R.id.snackbar_text)
            snackBarTextView.setTextColor(
                ContextCompat.getColor(
                    rootView.context,
                    R.color.azure_communication_ui_calling_color_snack_bar_text_color
                )
            )
            view.findViewById<AppCompatButton>(R.id.snackbar_action).apply {
                setTextColor(
                    ContextCompat.getColor(
                        rootView.context,
                        R.color.azure_communication_ui_calling_color_snack_bar_text_color
                    )
                )
                isAllCaps = false
                contentDescription =
                    rootView.context.getText(R.string.azure_communication_ui_calling_snack_bar_button_dismiss)
            }
            ViewCompat.setImportantForAccessibility(
                view,
                ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES
            )
        }
    }

    private fun View.accessibilityFocus(): View {
        post {
            performAccessibilityAction(AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS, null)
            sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                accessibilityTraversalAfter = R.id.azure_communication_ui_setup_audio_device_button
                accessibilityTraversalBefore = R.id.azure_communication_ui_setup_join_call_holder
            }
        }
        return this
    }
}
