package com.azure.android.communication.ui.presentation.fragment.setup.components

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.Button
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

    override fun onFinishInflate() {
        super.onFinishInflate()
        setupJoinCallButton = findViewById(R.id.azure_communication_ui_setup_join_call_button)
        setupJoinCallButtonText = findViewById(R.id.azure_communication_ui_setup_start_call_button_text)
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
            viewModel.getJoinCallButtonEnabledFlow().collect {
                setupJoinCallButton.isEnabled = it
                setupJoinCallButtonText.isEnabled = it

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!it) {
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
        }
    }
}
