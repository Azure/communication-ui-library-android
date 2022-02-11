package com.azure.android.communication.ui.callingcompositedemoapp.launcher

import android.content.Context
import android.content.SharedPreferences
import android.util.AttributeSet
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import com.azure.android.communication.ui.utilities.FeatureFlags
import com.google.android.material.button.MaterialButtonToggleGroup


class FeatureFlagView(context: Context, attrs: AttributeSet?) :
    LinearLayout(context, attrs), SharedPreferences.OnSharedPreferenceChangeListener {

    val sharedPreferences = context.getSharedPreferences("FeatureFlags", Context.MODE_PRIVATE)

    init {
        orientation = LinearLayout.VERTICAL
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        refreshButtons()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        refreshButtons()
    }

    private fun refreshButtons() {
        removeAllViews()
        FeatureFlags.values().forEach {
            val tv = CheckBox(context)
            tv.text = it.label
            tv.isChecked = it.active
            tv.setOnClickListener { _ -> it.toggle() }
            addView(tv)
        }
    }
    override fun onDetachedFromWindow() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onDetachedFromWindow()
    }
}