// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.setup.components

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
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
import com.azure.android.communication.ui.configuration.LocalizationProvider
import com.azure.android.communication.ui.configuration.events.CallCompositeErrorCode
import com.azure.android.communication.ui.error.CallStateError
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
                if (it == null) {
                    snackBar.dismiss()
                } else {
                    displaySnackBar(it, snackBarViewModel.getLocalizationProvider())
                }
            }
        }
    }

    fun stop() {
        if (snackBar.isShown) {
            snackBar.dismiss()
        }
        rootView.invalidate()
    }

    private fun displaySnackBar(
        error: CallStateError,
        appLocalizationProvider: LocalizationProvider
    ) {
        val errorMessage = getErrorMessage(error, appLocalizationProvider)

        if (errorMessage.isBlank()) return
        snackBarTextView.text = errorMessage
        snackBar.run {
            if (isShown) {
                dismiss()
            }
            show()
            view.accessibilityFocus()
        }
    }

    private fun getErrorMessage(
        it: CallStateError,
        localizationProvider: LocalizationProvider
    ): String {
        val context = rootView.context!!
        return when (it.callCompositeErrorCode) {
            CallCompositeErrorCode.CALL_END -> localizationProvider.getLocalizedString(
                context.resources.getResourceEntryName(R.string.azure_communication_ui_cal_state_error_call_end),
                context.getText(R.string.azure_communication_ui_cal_state_error_call_end)
                    .toString()
            )
            CallCompositeErrorCode.CALL_JOIN -> localizationProvider.getLocalizedString(
                context.resources.getResourceEntryName(R.string.azure_communication_ui_snack_bar_text_error_call_join),
                context.getText(R.string.azure_communication_ui_snack_bar_text_error_call_join)
                    .toString()
            )
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
            setAction(rootView.context!!.getText(R.string.azure_communication_ui_snack_bar_button_dismiss)) {}
            anchorView = rootView.findViewById(R.id.azure_communication_ui_setup_join_call_button)
            view.background.colorFilter = PorterDuffColorFilter(
                ContextCompat.getColor(
                    rootView.context,
                    R.color.azure_communication_ui_color_snack_bar_background
                ),
                PorterDuff.Mode.SRC_IN
            )
            snackBarTextView = view.findViewById(R.id.snackbar_text)
            snackBarTextView.setTextColor(
                ContextCompat.getColor(
                    rootView.context,
                    R.color.azure_communication_ui_color_snack_bar_text_color
                )
            )
            val snackBarActionButton = view.findViewById<AppCompatButton>(R.id.snackbar_action)
            snackBarActionButton.setTextColor(
                ContextCompat.getColor(
                    rootView.context,
                    R.color.azure_communication_ui_color_snack_bar_text_color
                )
            )
            snackBarActionButton.isAllCaps = false
            ViewCompat.setImportantForAccessibility(view, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES)
        }
    }
}

fun View.accessibilityFocus(): View {
    post {
        performAccessibilityAction(AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS, null)
        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED)
    }
    return this
}
