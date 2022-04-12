package com.azure.android.communication.ui.callingcompositedemoapp.launcher

import android.content.Context
import android.util.AttributeSet
import android.widget.CheckBox
import android.widget.LinearLayout
import com.azure.android.communication.ui.utilities.implementation.FeatureFlags

// This lists all the Features in the FeatureFlag system
// and lets you enable/disable them.
class FeatureFlagView(context: Context, attrs: AttributeSet?) :
    LinearLayout(context, attrs) {

    //private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
    //    FEATURE_FLAG_SHARED_PREFS_KEY, Context.MODE_PRIVATE
    //)

    init {
        orientation = VERTICAL
    }

    val onFeatureFlagsChanged  = Runnable { refreshButtons() }

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
