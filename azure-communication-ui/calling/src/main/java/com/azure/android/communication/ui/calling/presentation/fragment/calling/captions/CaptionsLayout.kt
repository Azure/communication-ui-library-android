// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.captions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


internal class CaptionsLayout : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var captionsExpandingView: View
    private lateinit var expandButton: ImageButton
    private lateinit var rttInputText: EditText
    private lateinit var captionsLinearLayout: FrameLayout
    private lateinit var viewModel: CaptionsViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var captionsStartProgressLayout: LinearLayout
    private lateinit var recyclerViewAdapter: CaptionsRecyclerViewAdapter
    private var localParticipantIdentifier: CommunicationIdentifier? = null
    private val captionsData = mutableListOf<CaptionsEntryModel>()
    private var isAtBottom = true
    private var expanded = false

    override fun onFinishInflate() {

        super.onFinishInflate()
        captionsExpandingView = findViewById(R.id.azure_communication_ui_calling_captions_expanding_view)
        expandButton = findViewById(R.id.azure_communication_ui_calling_captions_expand_button)
        rttInputText = findViewById(R.id.rtt_input_text)
        captionsLinearLayout = findViewById(R.id.azure_communication_ui_calling_captions_linear_layout)
        recyclerView = findViewById(R.id.azure_communication_ui_calling_captions_recycler_view)
        captionsStartProgressLayout = findViewById(R.id.azure_communication_ui_calling_captions_starting_layout)
        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        expandButton.setOnClickListener {
            if (expanded) {
                collapseCaptionsLayout()
            } else {
                expandCaptionsLayout()
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
            viewModel.getDisplayCaptionsInfoViewFlow().collect {
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
            viewModel.getCaptionsStartProgressStateFlow().collect {
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

    private fun expandCaptionsLayout() {
        rttInputText.visibility = View.VISIBLE
        expandButton.setImageResource(R.drawable.azure_communication_ui_calling_ic_fluent_arrow_minimize_24_regular)
        val constraintLayout = captionsLinearLayout.parent.parent as ConstraintLayout
        val nestedViewId: Int = R.id.azure_communication_ui_calling_captions_view_wrapper
        updateConstraintTopToBottom(constraintLayout, nestedViewId, R.id.view3)

        expanded = true
    }

    private fun collapseCaptionsLayout() {
        rttInputText.visibility = View.GONE
        expandButton.setImageResource(R.drawable.azure_communication_ui_calling_ic_fluent_arrow_maximize_24_regular)
        val constraintLayout = captionsLinearLayout.parent.parent as ConstraintLayout
        val nestedViewId: Int = R.id.azure_communication_ui_calling_captions_view_wrapper
        updateConstraintTopToTop(constraintLayout, nestedViewId, R.id.view4)
        expanded = false
    }

    private fun updateConstraintTopToBottom(
        constraintLayout: ConstraintLayout,
        nestedViewId: Int,
        targetViewId: Int
    ) {
        updateConstraintTopTo(constraintLayout, nestedViewId, targetViewId, ConstraintSet.BOTTOM)
    }

    private fun updateConstraintTopToTop(
        constraintLayout: ConstraintLayout,
        nestedViewId: Int,
        targetViewId: Int
    ) {
        updateConstraintTopTo(constraintLayout, nestedViewId, targetViewId, ConstraintSet.TOP)
    }

    private fun updateConstraintTopTo(
        constraintLayout: ConstraintLayout,
        nestedViewId: Int,
        targetViewId: Int,
        constraint: Int,
    ) {
        TransitionManager.beginDelayedTransition(constraintLayout)
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        constraintSet.clear(nestedViewId, ConstraintSet.TOP)
        constraintSet.connect(nestedViewId, ConstraintSet.TOP, targetViewId, constraint)

        constraintSet.applyTo(constraintLayout)
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
