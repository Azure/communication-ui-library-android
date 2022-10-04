// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.externaloverlay

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.R
import kotlinx.coroutines.launch

internal class ExternalOverlayView : ConstraintLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var overlayContainer: ConstraintLayout
    private lateinit var overlayPip: ConstraintLayout

    override fun onFinishInflate() {
        super.onFinishInflate()
        overlayContainer = findViewById(R.id.azure_communication_ui_calling_external_overlay_container)
        overlayPip = findViewById(R.id.azure_communication_ui_call_overlay_pip_container)
    }

    fun start(
        viewLifecycleOwner: LifecycleOwner,
        viewModel: ExternalOverlayViewModel,
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.externalViewFlow.collect { viewBuilder ->
                overlayContainer.removeAllViews()

                if (viewBuilder != null) {
                    val view = viewBuilder.build(context)
                    view.parent?.let { (it as ViewGroup).removeView(view) }
                    val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    params.setMargins(0, 0, 0, 0)
                    view.layoutParams = params
                    overlayContainer.visibility = VISIBLE
                    overlayContainer.addView(view)
                    overlayPip.visibility = VISIBLE
                } else {
                    overlayContainer.visibility = GONE
                    overlayPip.visibility = GONE
                }
            }
        }
    }
}
