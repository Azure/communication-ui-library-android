package com.azure.android.communication.ui.presentation.fragment.setup.components

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import androidx.annotation.ColorInt
import com.azure.android.communication.ui.R

internal class JoinCallButtonHolderView : ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var setupJoinCallButton: Button
    private lateinit var setupJoinCallButtonText: AppCompatTextView

    private lateinit var progressBar: ProgressBar
    private lateinit var joiningCallText: AppCompatTextView

    override fun onFinishInflate() {
        super.onFinishInflate()
        setupJoinCallButton = findViewById(R.id.azure_communication_ui_setup_join_call_button)
        setupJoinCallButtonText =
            findViewById(R.id.azure_communication_ui_setup_start_call_button_text)
        progressBar = findViewById(R.id.azure_communication_ui_setup_start_call_progress_bar)
        joiningCallText = findViewById(R.id.azure_communication_ui_setup_start_call_joining_text)
        setupJoinCallButton.background = ContextCompat.getDrawable(
            context,
            R.drawable.azure_communication_ui_corner_radius_rectangle_4dp_primary_background
        )
    }

    fun start(
        viewLifecycleOwner: LifecycleOwner,
        viewModel: JoinCallButtonHolderViewModel,
    ) {
        setupJoinCallButton.setOnClickListener {
            viewModel.launchCallScreen()
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getJoinCallButtonEnabledFlow().collect { onJoinCallEnabledChanged(it) }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getDisableJoinCallButtonFlow().collect { onDisableJoinCallButtonChanged(it) }
        }
    }

    private fun onJoinCallEnabledChanged(isEnabled: Boolean) {
        setupJoinCallButton.isEnabled = isEnabled
        setupJoinCallButtonText.isEnabled = isEnabled

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isEnabled) {
                setupJoinCallButton.background.setTint(
                    ContextCompat.getColor(
                        context,
                        R.color.azure_communication_ui_color_disabled
                    )
                )
            } else {
                val typedValue = TypedValue()
                val theme = context.theme
                theme.resolveAttribute(
                    R.attr.azure_communication_ui_calling_primary_color,
                    typedValue,
                    true
                )
                @ColorInt val color = typedValue.data
                setupJoinCallButton.background.setTint(color)
            }
        }
    }

    private fun onDisableJoinCallButtonChanged(isBlocked: Boolean) {
        if (isBlocked) {
            setupJoinCallButton.visibility = GONE
            setupJoinCallButtonText.visibility = GONE
            progressBar.visibility = VISIBLE
            joiningCallText.visibility = VISIBLE
        } else {
            setupJoinCallButton.visibility = VISIBLE
            setupJoinCallButtonText.visibility = VISIBLE
            progressBar.visibility = GONE
            joiningCallText.visibility = GONE
        }
    }
}
