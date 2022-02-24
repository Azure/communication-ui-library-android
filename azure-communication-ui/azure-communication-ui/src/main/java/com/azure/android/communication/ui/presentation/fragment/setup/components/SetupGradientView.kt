// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.setup.components

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class SetupGradientView : AppCompatImageView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    fun start(
        viewLifecycleOwner: LifecycleOwner,
        viewModel: SetupGradientViewModel,
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getDisplaySetupGradientFlow().collect {
                visibility = if (it) VISIBLE else GONE
            }
        }
    }
}
