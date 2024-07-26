// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
/* <RTT_POC>
package com.azure.android.communication.ui.calling.presentation.fragment.calling.rtt

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.azure.android.communication.ui.calling.redux.state.RttMessage
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class RttView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private lateinit var viewModel: RttViewModel
    private val recyclerView: RecyclerView = RecyclerView(context)
    private val adapter: RttMessageAdapter by lazy {
        RttMessageAdapter(emptyList())
    }

    init {
        setupView()
    }

    private fun setupView() {
        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context).apply {
            stackFromEnd = true
        }
        recyclerView.adapter = adapter
        addView(recyclerView)
    }

    fun start(viewLifecycleOwner: LifecycleOwner, viewModel: RttViewModel) {
        this.viewModel = viewModel

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getContent().collect { messages ->
                adapter.updateMessages(messages)
                recyclerView.scrollToPosition(adapter.itemCount - 1)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isDisplayed().collect {
                visibility = if (it) View.VISIBLE else View.GONE
            }
        }
    }

    private class RttMessageAdapter(private var messages: List<RttMessage>) :
        RecyclerView.Adapter<RttMessageAdapter.RttMessageViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RttMessageViewHolder {
            val textView = AppCompatTextView(parent.context)
            return RttMessageViewHolder(textView)
        }

        override fun onBindViewHolder(holder: RttMessageViewHolder, position: Int) {
            holder.bind(messages[position])
        }

        override fun getItemCount(): Int = messages.size

        fun updateMessages(newMessages: List<RttMessage>) {
            this.messages = newMessages
            notifyDataSetChanged()
        }

        inner class RttMessageViewHolder(private val textView: TextView) : RecyclerView.ViewHolder(textView) {
            fun bind(message: RttMessage) {
                textView.text = message.prettyMessage
            }
        }
    }
}

</RTT_POC> */
