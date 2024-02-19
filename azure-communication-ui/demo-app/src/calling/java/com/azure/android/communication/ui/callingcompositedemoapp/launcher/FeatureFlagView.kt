// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.launcher

import android.content.Context
import android.util.AttributeSet
import android.widget.CheckBox
import android.widget.LinearLayout
import com.azure.android.communication.ui.callingcompositedemoapp.features.FeatureFlags

// This lists all the Features in the FeatureFlag system
// and lets you enable/disable them.
class FeatureFlagView(context: Context, attrs: AttributeSet?) :
    LinearLayout(context, attrs) {
    init {
        orientation = VERTICAL
    }

    private val onFeatureFlagsChanged = Runnable { refreshButtons() }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        FeatureFlags.flagStoreDelegate.addListener(onFeatureFlagsChanged)
        refreshButtons()
    }

    private fun refreshButtons() {
        removeAllViews()
        FeatureFlags.features.forEach {
            val tv = CheckBox(context)
            tv.text = it.label
            tv.isChecked = it.active
            tv.setOnClickListener { _ -> it.toggle() }
            addView(tv)
        }
    }

    override fun onDetachedFromWindow() {
        FeatureFlags.flagStoreDelegate.removeListener(onFeatureFlagsChanged)
        super.onDetachedFromWindow()
    }
}
