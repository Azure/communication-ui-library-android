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
import com.azure.android.communication.ui.calling.utilities.isTablet
import com.azure.android.communication.ui.calling.utilities.launchAll
import kotlinx.coroutines.flow.collect
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

internal class CaptionsView : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private var isInitialized = false

    private lateinit var headerDragHandle: View
    private lateinit var headerText: TextView
    private lateinit var captionsButton: ImageButton
    private lateinit var resizeButton: ImageButton
    private lateinit var rttInputText: EditText
    private lateinit var captionsLinearLayout: FrameLayout
    private lateinit var viewModel: CaptionsViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var captionsStartProgressLayout: LinearLayout
    private lateinit var recyclerViewAdapter: CaptionsRecyclerViewAdapter

    private var isAtBottom = true

    var maximizeCallback: () -> Unit = {}
        private set
    var minimizeCallback: () -> Unit = {}
        private set

    private val minHeight = (115 * resources.displayMetrics.density).toInt()
    var maxHeight: Int = 0

    override fun onFinishInflate() {

        super.onFinishInflate()
        headerDragHandle =
            findViewById(R.id.azure_communication_ui_calling_captions_header_drag_handle)
        headerText = findViewById(R.id.azure_communication_ui_call_captions_header_text)
        captionsButton = findViewById(R.id.azure_communication_ui_calling_captions_on_button)
        resizeButton = findViewById(R.id.azure_communication_ui_calling_captions_resize_button)
        rttInputText = findViewById(R.id.rtt_input_text)
        captionsLinearLayout =
            findViewById(R.id.azure_communication_ui_calling_captions_linear_layout)
        recyclerView = findViewById(R.id.azure_communication_ui_calling_captions_recycler_view)
        captionsStartProgressLayout =
            findViewById(R.id.azure_communication_ui_calling_captions_starting_layout)
        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        rttInputText.addTextChangedListener {
            // Ignore initial text restoration
            if (isInitialized) {
                onEditTextChanged()
            }
        }

        rttInputText.setOnEditorActionListener { view, actionId, _ ->
            onEditTextAction(view, actionId)
        }

        captionsButton.setOnClickListener {
            viewModel.toggleCaptions()
        }

        post { isInitialized = true }
    }

    @SuppressLint("NotifyDataSetChanged", "ClickableViewAccessibility")
    fun start(
        viewLifecycleOwner: LifecycleOwner,
        viewModel: CaptionsViewModel,
        maximizeCallback: () -> Unit = {},
        minimizeCallback: () -> Unit = {}
    ) {
        this.viewModel = viewModel
        this.maximizeCallback = maximizeCallback
        this.minimizeCallback = minimizeCallback

        if (isTablet(context) || !viewModel.isPortraitFlow.value) {
            resizeButton.isVisible = false
            headerDragHandle.isVisible = false

            (captionsButton.layoutParams as MarginLayoutParams).marginEnd = 0
        } else {
            captionsLinearLayout.setOnTouchListener(ResizableTouchListener())
            (captionsLinearLayout.layoutParams as MarginLayoutParams).marginStart = 0

            resizeButton.setOnClickListener { this.onResizeButtonClicked() }
        }

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
                viewModel.captionsRttUpdatedSharedFlow.collect {
                    recyclerViewAdapter.notifyDataSetChanged()
                }
            },
            {
                viewModel.recordInsertedAtPositionSharedFlow.collect {
                    onItemAdded(it)
                }
            },
            {
                viewModel.recordRemovedAtPositionSharedFlow.collect {
                    recyclerViewAdapter.notifyItemRemoved(it)
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
                    // Only update the layout if it is not a tablet
                    if (!isTablet(context) && viewModel.isPortraitFlow.value) {
                        if (it) {
                            onMaximizeCaptionsLayout()
                        } else {
                            onMinimizeCaptionsLayout()
                        }
                    }
                }
            },
            {
                viewModel.headerTypeFlow.collect {
                    updateHeader(it)
                }
            },
            {
                viewModel.isCaptionsButtonEnabledStateFlow.collect {
                    captionsButton.isEnabled = it
                }
            },
            {
                viewModel.isCaptionsActiveStateFlow.collect {
                    if (it) {
                        captionsButton.setImageResource(R.drawable.azure_communication_ui_calling_ic_fluent_closed_caption_24_regular_color)
                        captionsButton.contentDescription =
                            context.getString(R.string.azure_communication_ui_calling_captions_turn_off)
                    } else {
                        captionsButton.setImageResource(R.drawable.azure_communication_ui_calling_ic_fluent_closed_caption_off_24_regular)
                        captionsButton.contentDescription =
                            context.getString(R.string.azure_communication_ui_calling_captions_turn_on)
                    }
                }
            }
        )
    }

    private fun onEditTextChanged() {
        val message = rttInputText.text.toString()
        viewModel.sendRttMessage(message, false)
    }

    private fun onEditTextAction(view: TextView, actionId: Int): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            val message = view.text.toString()
            if (message.isNotBlank()) {
                viewModel.sendRttMessage(message, true)
                view.text = ""
                view.requestFocus()

                // Ensure the keyboard remains visible
                val imm =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
            }
            return true
        }

        return false
    }

    private fun onItemUpdated(index: Int) {
        if (index >= 0) {
            clearInputIfLocalFinalized(index)
            val shouldScrollToBottom = isAtBottom
            recyclerViewAdapter.notifyItemChanged(index)
            if (shouldScrollToBottom) {
                scrollToBottom()
            }
            announceAccessibility(index)
        }
    }

    private fun onItemAdded(index: Int) {
        clearInputIfLocalFinalized(index)
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val shouldScrollToBottom =
            isAtBottom || layoutManager.findLastVisibleItemPosition() == layoutManager.itemCount - 1

        recyclerViewAdapter.notifyItemInserted(index)
        if (shouldScrollToBottom) {
            scrollToBottom()
        }
        announceAccessibility(index)
    }

    private fun clearInputIfLocalFinalized(index: Int) {
        val updatedItem = viewModel.captionsAndRttData[index]
        if (updatedItem.type == CaptionsRttType.RTT && updatedItem.isLocal == true && updatedItem.isFinal) {
            rttInputText.text.clear()
        }
    }

    private fun announceAccessibility(position: Int) {
        val accessibilityManager =
            this.context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        if (accessibilityManager.isEnabled) {
            val captionsRttData = viewModel.captionsAndRttData[position]

            val text =
                if (captionsRttData.type == CaptionsRttType.RTT_INFO) {
                    context.getString(R.string.azure_communication_ui_calling_rtt_info)
                } else if (captionsRttData.type == CaptionsRttType.RTT && captionsRttData.isLocal != true && captionsRttData.isFinal) {
                    val name = captionsRttData.displayName.orEmpty().ifEmpty { context.getString(R.string.azure_communication_ui_calling_view_participant_drawer_unnamed) }
                    "$name: ${captionsRttData.displayText}"
                } else null

            text?.let {
                val event = AccessibilityEvent.obtain()
                event.eventType = AccessibilityEvent.TYPE_ANNOUNCEMENT
                event.text.add(it)
                accessibilityManager.sendAccessibilityEvent(event)
            }
        }
    }

    private fun scrollToBottom() {
        recyclerView.scrollToPosition(recyclerViewAdapter.itemCount - 1)
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
        resizeButton.setImageResource(R.drawable.azure_communication_ui_calling_ic_fluent_arrow_minimize_20_regular)
        resizeButton.contentDescription = context.getString(R.string.azure_communication_ui_calling_minimize_captions_and_rtt)
        maximizeCallback()
    }

    private fun onMinimizeCaptionsLayout() {
        hideKeyboard(rttInputText)
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

    private fun updateHeader(headerType: CaptionsViewModel.HeaderType) {
        val stringId =
            when (headerType) {
                CaptionsViewModel.HeaderType.CAPTIONS -> R.string.azure_communication_ui_calling_captions_header
                CaptionsViewModel.HeaderType.RTT -> R.string.azure_communication_ui_calling_rtt_header
                CaptionsViewModel.HeaderType.CAPTIONS_AND_RTT -> R.string.azure_communication_ui_calling_captions_rtt_menu
            }
        headerText.text = context.getString(stringId)
    }
}
