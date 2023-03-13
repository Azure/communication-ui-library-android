package com.azure.android.communication.ui.calling.presentation.fragment.calling.lobby

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.R
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class ConnectingLobbyOverlayView : LinearLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var connectingProgressBar: ProgressBar
    private lateinit var overlayInfo: AppCompatTextView
    private lateinit var viewModel: ConnectingLobbyOverlayViewModel

    override fun onFinishInflate() {
        super.onFinishInflate()
        connectingProgressBar = findViewById(R.id.azure_communication_ui_call_connecting_progress_bar)
        overlayInfo = findViewById(R.id.azure_communication_ui_call_connecting_joining_text)
    }

    private fun setupUi() {

        connectingProgressBar.contentDescription = context.getString(R.string.azure_communication_ui_calling_setup_view_button_connecting_call)
        overlayInfo.text = context.getString(R.string.azure_communication_ui_calling_setup_view_button_connecting_call)
    }

    fun start(
        viewLifecycleOwner: LifecycleOwner,
        viewModel: ConnectingLobbyOverlayViewModel,
    ) {
        this.viewModel = viewModel
        // viewModel.turnCameraOnDefault()

        setupUi()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getDisplayLobbyOverlayFlow().collect {
                visibility = if (it) VISIBLE else GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getCameraStateFlow().collect {
            }
        }

        ViewCompat.setAccessibilityDelegate(
            this,
            object : AccessibilityDelegateCompat() {
                override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfoCompat) {
                    super.onInitializeAccessibilityNodeInfo(host, info)
                    info.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK)
                    info.isClickable = false
                }
            }
        )
    }
}
