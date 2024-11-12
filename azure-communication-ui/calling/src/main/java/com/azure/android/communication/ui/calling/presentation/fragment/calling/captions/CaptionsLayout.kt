// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.captions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
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
import androidx.core.view.isVisible
import androidx.core.view.marginTop
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.ui.calling.implementation.R
import com.azure.android.communication.ui.calling.presentation.fragment.calling.CallingFragment
import com.azure.android.communication.ui.calling.presentation.manager.AvatarViewManager
import com.azure.android.communication.ui.calling.presentation.manager.CaptionsDataManager
import com.azure.android.communication.ui.calling.utilities.LocaleHelper
import com.azure.android.communication.ui.calling.utilities.isTablet
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

internal class CaptionsLayout : FrameLayout {
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
    private var localParticipantIdentifier: CommunicationIdentifier? = null
    private val captionsData = mutableListOf<CaptionsEntryModel>()
    private var isAtBottom = true
    private var isMaximized = false

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
            // TODO: check if rtt is enabled via view model
            rttInputText.isVisible = true
            resizeButton.isVisible = false
            headerDragHandle.isVisible = false

            (captionsButton.layoutParams as MarginLayoutParams).marginEnd = 0
        } else {
            captionsLinearLayout.setOnTouchListener(ResizableTouchListener())
            (captionsLinearLayout.layoutParams as MarginLayoutParams).marginStart = 0

            resizeButton.setOnClickListener { this.onResizeButtonClicked() }
        }

        rttInputText.setOnEditorActionListener { view, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                // Handle the "Send" action
                val message = view.text.toString()
                if (message.isNotBlank()) {
//                    sendMessage(message)

                    view.text = ""

                    // Keep focus on the EditText to prevent the keyboard from hiding
                    view.requestFocus()

                    // Ensure the keyboard remains visible
                    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
                }
                true
            } else {
                false
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun start(
        viewLifecycleOwner: LifecycleOwner,
        viewModel: CaptionsViewModel,
        captionsDataManager: CaptionsDataManager,
        avatarViewManager: AvatarViewManager,
        identifier: CommunicationIdentifier?
    ) {
        this.localParticipantIdentifier = identifier
        this.viewModel = viewModel
        recyclerViewAdapter = CaptionsRecyclerViewAdapter(captionsData)
        recyclerView.adapter = recyclerViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                isAtBottom = !recyclerView.canScrollVertically(1)
            }
        })

        captionsDataManager.captionsDataCache.let { data ->
            captionsData.addAll(data.map { it.into(avatarViewManager, identifier) })
            recyclerViewAdapter.notifyDataSetChanged()
        }

        recyclerView.post {
            recyclerView.scrollToPosition(recyclerViewAdapter.itemCount - 1)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isVisibleFlow.collect {
                if (it) {
                    captionsLinearLayout.visibility = View.VISIBLE
                    captionsLinearLayout.post { scrollToBottom() }
                } else {
                    captionsLinearLayout.visibility = View.GONE
                }
            }
        }

        captionsDataManager.resetFlows()

        viewLifecycleOwner.lifecycleScope.launch {
            captionsDataManager.getOnLastCaptionsDataUpdatedStateFlow().collect { data ->
                data?.let {
                    val lastCaptionsData = it.into(avatarViewManager, identifier)
                    updateLastCaptionsData(lastCaptionsData)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            captionsDataManager.getOnNewCaptionsDataAddedStateFlow().collect { data ->
                data?.let {
                    applyLayoutDirection(it)
                    addNewCaptionsData(it.into(avatarViewManager, identifier))
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.captionsStartProgressStateFlow.collect {
                if (it) {
                    captionsStartProgressLayout.visibility = View.VISIBLE
                } else {
                    captionsStartProgressLayout.visibility = View.GONE
                }
            }
        }
    }

    private fun updateLastCaptionsData(lastCaptionsData: CaptionsEntryModel) {
        val index = captionsData.size - 1
        if (index >= 0 && captionsData[index].speakerRawId == lastCaptionsData.speakerRawId) {
            val shouldScrollToBottom = isAtBottom
            captionsData[index] = lastCaptionsData
            updateRecyclerViewItem(index)
            if (shouldScrollToBottom) {
                scrollToBottom()
            }
        }
    }

    private fun addNewCaptionsData(newCaptionsData: CaptionsEntryModel) {
        if (captionsData.size >= CallingFragment.MAX_CAPTIONS_DATA_SIZE) {
            captionsData.removeAt(0)
            recyclerViewAdapter.notifyItemRemoved(0)
        }

        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val shouldScrollToBottom = isAtBottom || layoutManager.findLastVisibleItemPosition() == layoutManager.itemCount - 1

        captionsData.add(newCaptionsData)
        insertRecyclerViewItem(captionsData.size - 1)
        if (shouldScrollToBottom) {
            scrollToBottom()
        }
    }

    private fun updateRecyclerViewItem(position: Int) {
        recyclerViewAdapter.notifyItemChanged(position)
        requestAccessibilityFocus(position)
    }

    private fun insertRecyclerViewItem(position: Int) {
        recyclerViewAdapter.notifyItemInserted(position)
        requestAccessibilityFocus(position)
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
    private fun applyLayoutDirection(it: CaptionsRecord) {
        if (LocaleHelper.isRTL(it.languageCode) && layoutDirection != LAYOUT_DIRECTION_RTL) {
            captionsLinearLayout.layoutDirection = LAYOUT_DIRECTION_RTL
        } else if (!LocaleHelper.isRTL(it.languageCode) && layoutDirection != LAYOUT_DIRECTION_LTR) {
            captionsLinearLayout.layoutDirection = LAYOUT_DIRECTION_LTR
        }
    }

    fun stop() {
        captionsData.clear()
        recyclerView.adapter = null
        recyclerView.layoutManager = null
        recyclerView.removeAllViews()
        this.removeAllViews()
    }

    private fun onResizeButtonClicked() {
        if (isMaximized) {
            minimizeCaptionsLayout()
        } else {
            maximizeCaptionsLayout()
        }
    }

    private fun maximizeCaptionsLayout() {
        rttInputText.visibility = View.VISIBLE
        resizeButton.setImageResource(R.drawable.azure_communication_ui_calling_ic_fluent_arrow_minimize_20_regular)
        resizeButton.contentDescription = context.getString(R.string.azure_communication_ui_calling_minimize_captions_and_rtt)
        isMaximized = true
        maximizeCallback()
    }

    fun minimizeCaptionsLayout() {
        hideKeyboard(rttInputText)
        rttInputText.visibility = View.GONE
        resizeButton.setImageResource(R.drawable.azure_communication_ui_calling_ic_fluent_arrow_maximize_20_regular)
        resizeButton.contentDescription = context.getString(R.string.azure_communication_ui_calling_maximize_captions_and_rtt)
        isMaximized = false
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

                    if (isMaximized) {
                        maximizedHeight = view.height
                    }
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val deltaY = motionEvent.rawY - initialTouchY

                    var newMarginTop = (deltaY + initialMarginTop).toInt()
                    if (isMaximized) {
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
                        if (isMaximized) {
                            minimizeCaptionsLayout()
                        } else {
                            maximizeCaptionsLayout()
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

internal fun CaptionsRecord.into(avatarViewManager: AvatarViewManager, identifier: CommunicationIdentifier?): CaptionsEntryModel {
    var speakerName = this.displayName
    var bitMap: Bitmap? = null

    val remoteParticipantViewData = avatarViewManager.getRemoteParticipantViewData(this.speakerRawId)
    if (remoteParticipantViewData != null) {
        speakerName = remoteParticipantViewData.displayName
        bitMap = remoteParticipantViewData.avatarBitmap
    }
    val localParticipantViewData = avatarViewManager.callCompositeLocalOptions?.participantViewData
    if (localParticipantViewData != null && identifier?.rawId == this.speakerRawId) {
        speakerName = localParticipantViewData.displayName
        bitMap = localParticipantViewData.avatarBitmap
    }
    return CaptionsEntryModel(
        displayName = speakerName,
        displayText = this.displayText,
        avatarBitmap = bitMap,
        speakerRawId = this.speakerRawId,
    )
}
