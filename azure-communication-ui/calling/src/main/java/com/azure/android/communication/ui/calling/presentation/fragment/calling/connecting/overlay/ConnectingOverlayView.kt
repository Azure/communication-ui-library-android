// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.connecting.overlay

import android.content.Context
import android.media.AudioManager
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
import com.azure.android.communication.ui.calling.implementation.R
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class ConnectingOverlayView : LinearLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var connectingProgressBar: ProgressBar
    private lateinit var overlayInfo: AppCompatTextView
    private lateinit var viewModel: ConnectingOverlayViewModel

    override fun onFinishInflate() {
        super.onFinishInflate()
        connectingProgressBar = findViewById(R.id.azure_communication_ui_call_connecting_progress_bar)
        overlayInfo = findViewById(R.id.azure_communication_ui_call_connecting_joining_text)
    }

    fun start(
        viewLifecycleOwner: LifecycleOwner,
        viewModel: ConnectingOverlayViewModel,
    ) {
        this.viewModel = viewModel

        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        // This checks before joining the call, the microphone is free to use or not
        if (viewModel.getDisplayOverlayFlow().value && (audioManager.mode != AudioManager.MODE_NORMAL)) {
            viewModel.handleMicrophoneAccessFailed()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getDisplayOverlayFlow().collect {
                visibility = if (it) VISIBLE else GONE
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
