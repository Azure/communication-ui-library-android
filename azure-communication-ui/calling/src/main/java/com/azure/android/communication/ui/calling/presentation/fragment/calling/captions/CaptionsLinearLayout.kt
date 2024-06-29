// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.captions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.azure.android.communication.ui.calling.implementation.R
import com.azure.android.communication.ui.calling.presentation.fragment.calling.CallingFragment
import com.azure.android.communication.ui.calling.presentation.manager.AvatarViewManager
import com.azure.android.communication.ui.calling.presentation.manager.CaptionsDataManager
import com.azure.android.communication.ui.calling.utilities.LocaleHelper
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class CaptionsLinearLayout : LinearLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var captionsLinearLayout: LinearLayout
    private lateinit var viewModel: CaptionsLinearLayoutViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: CaptionsRecyclerViewAdapter
    private val captionsData = mutableListOf<CaptionsRecyclerViewDataModel>()
    private var isAtBottom = true

    override fun onFinishInflate() {
        super.onFinishInflate()
        captionsLinearLayout = findViewById(R.id.azure_communication_ui_calling_captions_linear_layout)
        recyclerView = findViewById(R.id.azure_communication_ui_calling_captions_recycler_view)
        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }

    @SuppressLint("NotifyDataSetChanged")
    fun start(
        viewLifecycleOwner: LifecycleOwner,
        viewModel: CaptionsLinearLayoutViewModel,
        captionsDataManager: CaptionsDataManager,
        avatarViewManager: AvatarViewManager
    ) {
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
            captionsData.addAll(data.map { it.into(avatarViewManager) })
            recyclerViewAdapter.notifyDataSetChanged()
        }

        recyclerView.post {
            recyclerView.scrollToPosition(recyclerViewAdapter.itemCount - 1)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getDisplayCaptionsInfoViewFlow().collect {
                if (it) {
                    captionsLinearLayout.visibility = View.VISIBLE
                } else {
                    captionsLinearLayout.visibility = View.GONE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            captionsDataManager.getOnLastCaptionsDataUpdatedStateFlow().collect { data ->
                data?.let {
                    val lastCaptionsData = it.into(avatarViewManager)
                    updateLastCaptionsData(lastCaptionsData)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            captionsDataManager.getOnNewCaptionsDataAddedStateFlow().collect { data ->
                data?.let {
                    applyLayoutDirection(it)
                    addNewCaptionsData(it.into(avatarViewManager))
                }
            }
        }
    }

    private fun updateLastCaptionsData(lastCaptionsData: CaptionsRecyclerViewDataModel) {
        val lastDataForSpeaker = captionsData.findLast { it.speakerRawIdentifierId == lastCaptionsData.speakerRawIdentifierId }
        if (lastDataForSpeaker != null) {
            val shouldScrollToBottom = isAtBottom
            val index = captionsData.indexOf(lastDataForSpeaker)
            captionsData[index] = lastCaptionsData
            updateRecyclerViewItem(index)
            if (shouldScrollToBottom) {
                scrollToBottom()
            }
        }
    }

    private fun addNewCaptionsData(newCaptionsData: CaptionsRecyclerViewDataModel) {
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
    }

    private fun insertRecyclerViewItem(position: Int) {
        recyclerViewAdapter.notifyItemInserted(position)
    }

    private fun scrollToBottom() {
        recyclerView.scrollToPosition(recyclerViewAdapter.itemCount - 1)
    }

    private fun applyLayoutDirection(it: CaptionsDataViewModel) {
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
}

internal fun CaptionsDataViewModel.into(avatarViewManager: AvatarViewManager): CaptionsRecyclerViewDataModel {
    var speakerName = this.displayName
    var bitMap: Bitmap? = null

    val remoteParticipantViewData = avatarViewManager.getRemoteParticipantViewData(this.speakerRawIdentifierId)
    if (remoteParticipantViewData != null) {
        speakerName = remoteParticipantViewData.displayName
        bitMap = remoteParticipantViewData.avatarBitmap
    }
    val localParticipantViewData = avatarViewManager.callCompositeLocalOptions?.participantViewData
    if (localParticipantViewData != null && localParticipantViewData.identifier?.rawId == this.speakerRawIdentifierId) {
        speakerName = localParticipantViewData.displayName
        bitMap = localParticipantViewData.avatarBitmap
    }
    return CaptionsRecyclerViewDataModel(
        displayName = speakerName,
        displayText = this.displayText,
        avatarBitmap = bitMap,
        speakerRawIdentifierId = this.speakerRawIdentifierId,
    )
}
