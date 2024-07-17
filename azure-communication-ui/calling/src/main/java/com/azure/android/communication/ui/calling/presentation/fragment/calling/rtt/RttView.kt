// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
/* <RTT_POC>
package com.azure.android.communication.ui.calling.presentation.fragment.calling.rtt

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class RttView : androidx.appcompat.widget.AppCompatTextView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var viewModel: RttViewModel

    fun start(
        viewLifecycleOwner: LifecycleOwner,
        viewModel: RttViewModel,
    ) {
        this.viewModel = viewModel
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getContent().collect {
                text = it
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isDisplayed().collect {
                visibility = if (it) View.VISIBLE else View.GONE
            }
        }
    }
}
</RTT_POC> */
