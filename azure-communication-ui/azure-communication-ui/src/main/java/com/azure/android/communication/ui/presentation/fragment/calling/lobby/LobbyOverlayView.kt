package com.azure.android.communication.ui.presentation.fragment.calling.lobby

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class LobbyOverlayView : LinearLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var viewModel: LobbyOverlayViewModel

    fun start(
        viewLifecycleOwner: LifecycleOwner,
        viewModel: LobbyOverlayViewModel,
    ) {
        this.viewModel = viewModel

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getDisplayLobbyOverlayFlow().collect {
                visibility = if (it) VISIBLE else GONE
            }
        }
    }
}
