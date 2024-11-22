// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.captions

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.view.marginTop
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.azure.android.communication.ui.calling.implementation.R
import com.azure.android.communication.ui.calling.utilities.LocaleHelper
import com.azure.android.communication.ui.calling.utilities.isTablet
import com.azure.android.communication.ui.calling.utilities.launchAll
import kotlinx.coroutines.flow.collect
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

internal class CaptionsView : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var headerDragHandle: View
    private lateinit var captionsButton: ImageButton
    private lateinit var captionsExpandingView: View
    private lateinit var resizeButton: ImageButton
    private lateinit var rttInputText: EditText
    private lateinit var captionsLinearLayout: FrameLayout
    private lateinit var viewModel: CaptionsViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var captionsStartProgressLayout: LinearLayout
    private lateinit var recyclerViewAdapter: CaptionsRecyclerViewAdapter

    private var isAtBottom = true

    var maximizeCallback: () -> Unit = {}
    var minimizeCallback: () -> Unit = {}

    private val minHeight = (115 * resources.displayMetrics.density).toInt()
    var maxHeight: Int = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onFinishInflate() {

        super.onFinishInflate()
        headerDragHandle = findViewById(R.id.azure_communication_ui_calling_captions_header_drag_handle)
        captionsButton = findViewById(R.id.azure_communication_ui_calling_captions_on_button)
        captionsExpandingView = findViewById(R.id.azure_communication_ui_calling_captions_expanding_view)
        resizeButton = findViewById(R.id.azure_communication_ui_calling_captions_resize_button)
        rttInputText = findViewById(R.id.rtt_input_text)
        captionsLinearLayout = findViewById(R.id.azure_communication_ui_calling_captions_linear_layout)
        recyclerView = findViewById(R.id.azure_communication_ui_calling_captions_recycler_view)
        captionsStartProgressLayout = findViewById(R.id.azure_communication_ui_calling_captions_starting_layout)
        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        if (isTablet(context)) {
            rttInputText.isVisible = viewModel.isRttInputVisibleFlow.value
            resizeButton.isVisible = false
            headerDragHandle.isVisible = false

            (captionsButton.layoutParams as MarginLayoutParams).marginEnd = 0
        } else {
            captionsLinearLayout.setOnTouchListener(ResizableTouchListener())
            (captionsLinearLayout.layoutParams as MarginLayoutParams).marginStart = 0

            resizeButton.setOnClickListener { this.onResizeButtonClicked() }
        }

        rttInputText.addTextChangedListener {
            onEditTextChanged()
        }

        rttInputText.setOnEditorActionListener { view, actionId, _ ->
            onEditTextAction(view, actionId)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun start(
        viewLifecycleOwner: LifecycleOwner,
        viewModel: CaptionsViewModel,
    ) {
        this.viewModel = viewModel
        recyclerViewAdapter = CaptionsRecyclerViewAdapter(viewModel.captionsAndRttData)
        recyclerView.adapter = recyclerViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this.context).apply { stackFromEnd = true }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                isAtBottom = !recyclerView.canScrollVertically(1)
            }
        })

        recyclerView.post {
            recyclerView.scrollToPosition(recyclerViewAdapter.itemCount - 1)
        }

        viewLifecycleOwner.lifecycleScope.launchAll(
            {
                viewModel.isVisibleFlow.collect {
                    if (it) {
                        captionsLinearLayout.visibility = View.VISIBLE
                        captionsLinearLayout.post { scrollToBottom() }
                    } else {
                        captionsLinearLayout.visibility = View.GONE
                    }
                }
            },
            {
                viewModel.isRttInputVisibleFlow.collect {
                    rttInputText.isVisible = it
                }
            },
            {
                viewModel.recordUpdatedAtPositionSharedFlow.collect {
                    onItemUpdated(it)
                }
            },
            {
                viewModel.recordInsertedAtPositionSharedFlow.collect {
                    onItemAdded(it)
                }
            },
            {
                viewModel.recordRemovedAtPositionSharedFlow.collect {
                    onItemRemoved(it)
                }
            },
            {
                viewModel.captionsStartProgressStateFlow.collect {
                    captionsStartProgressLayout.isVisible = it
                }
            },
            {
                viewModel.softwareKeyboardStateFlow.collect {
                    scrollToBottom()
                }
            },
            {
                viewModel.isMaximizedFlow.collect {
                    if (it) {
                        onMaximizeCaptionsLayout()
                    } else {
                        onMinimizeCaptionsLayout()
                    }
                }
            },
        )
    }

    private fun onEditTextChanged() {
        val message = rttInputText.text.toString()
        if (message.isNotBlank()) {
            viewModel.sendRttMessage(message, false)
        }
    }

    private fun onEditTextAction(view: TextView, actionId: Int): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            val message = view.text.toString()
            viewModel.sendRttMessage(message, true)
            if (message.isNotBlank()) {
                view.text = ""
                view.requestFocus()

                // Ensure the keyboard remains visible
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
            }
            return true
        }

        return false
    }

    private fun onItemUpdated(index: Int) {
        if (index >= 0) {
            val shouldScrollToBottom = isAtBottom
            val updatedItem = viewModel.captionsAndRttData[index]
            if (updatedItem.type == CaptionsRttType.RTT && updatedItem.isLocal == true && updatedItem.isFinal) {
                rttInputText.text.clear()
            }
            recyclerViewAdapter.notifyItemChanged(index)
            requestAccessibilityFocus(index)
            if (shouldScrollToBottom) {
                scrollToBottom()
            }
        }
    }

    private fun onItemRemoved(index: Int) {
        recyclerViewAdapter.notifyItemRemoved(index)
    }

    private fun onItemAdded(index: Int) {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val shouldScrollToBottom = isAtBottom || layoutManager.findLastVisibleItemPosition() == layoutManager.itemCount - 1

        recyclerViewAdapter.notifyItemInserted(index)
        requestAccessibilityFocus(index)
        if (shouldScrollToBottom) {
            scrollToBottom()
        }
    }

    private fun requestAccessibilityFocus(position: Int) {
        val accessibilityManager = this.context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        if (accessibilityManager.isEnabled) {
            recyclerView.post {
                val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
                viewHolder?.itemView?.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
            }
        }
    }

    private fun scrollToBottom() {
        recyclerView.scrollToPosition(recyclerViewAdapter.itemCount - 1)
    }

    // required when RTL language is selected for captions text
    private fun applyLayoutDirection(captionsRecord: CaptionsRttRecord) {
        if (LocaleHelper.isRTL(captionsRecord.languageCode) && layoutDirection != LAYOUT_DIRECTION_RTL) {
            captionsLinearLayout.layoutDirection = LAYOUT_DIRECTION_RTL
        } else if (!LocaleHelper.isRTL(captionsRecord.languageCode) && layoutDirection != LAYOUT_DIRECTION_LTR) {
            captionsLinearLayout.layoutDirection = LAYOUT_DIRECTION_LTR
        }
    }

    fun stop() {
        recyclerView.adapter = null
        recyclerView.layoutManager = null
        recyclerView.removeAllViews()
        this.removeAllViews()
    }

    private fun onResizeButtonClicked() {
        if (viewModel.isMaximizedFlow.value) {
            viewModel.minimizeCaptionsLayout()
        } else {
            viewModel.maximizeCaptionsLayout()
        }
    }

    private fun onMaximizeCaptionsLayout() {
        rttInputText.isVisible = viewModel.isRttInputVisibleFlow.value
        resizeButton.setImageResource(R.drawable.azure_communication_ui_calling_ic_fluent_arrow_minimize_20_regular)
        resizeButton.contentDescription = context.getString(R.string.azure_communication_ui_calling_minimize_captions_and_rtt)
        maximizeCallback()
    }

    private fun onMinimizeCaptionsLayout() {
        hideKeyboard(rttInputText)
        rttInputText.isVisible = false
        resizeButton.setImageResource(R.drawable.azure_communication_ui_calling_ic_fluent_arrow_maximize_20_regular)
        resizeButton.contentDescription = context.getString(R.string.azure_communication_ui_calling_maximize_captions_and_rtt)
        scrollToBottom()
        minimizeCallback()
    }

    private fun hideKeyboard(editText: EditText) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
        editText.clearFocus()
    }

    inner class ResizableTouchListener : OnTouchListener {
        private var initialMarginTop = 0
        private var initialTouchY = 0f
        private var maximizedHeight = 0

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
            return when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialMarginTop = view.marginTop
                    initialTouchY = motionEvent.rawY

                    if (viewModel.isMaximizedFlow.value) {
                        maximizedHeight = view.height
                    }
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val deltaY = motionEvent.rawY - initialTouchY

                    var newMarginTop = (deltaY + initialMarginTop).toInt()
                    if (viewModel.isMaximizedFlow.value) {
                        if (minHeight > maximizedHeight - newMarginTop) {
                            newMarginTop = maximizedHeight - minHeight
                        }
                        newMarginTop = max(0, newMarginTop)
                    } else {
                        if (-newMarginTop + minHeight > maxHeight) {
                            newMarginTop = -(maxHeight - minHeight)
                        }
                        newMarginTop = min(0, newMarginTop)
                    }

                    val layoutParams = view.layoutParams as MarginLayoutParams
                    layoutParams.topMargin = newMarginTop
                    view.layoutParams = layoutParams
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val params = view.layoutParams as MarginLayoutParams

                    if (abs(params.topMargin) > 150) {
                        if (viewModel.isMaximizedFlow.value) {
                            viewModel.minimizeCaptionsLayout()
                        } else {
                            viewModel.maximizeCaptionsLayout()
                        }
                    }

                    params.topMargin = 0
                    view.layoutParams = params
                    true
                }
                else -> false
            }
        }
    }
}
