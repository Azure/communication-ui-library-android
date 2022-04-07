// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.participant.grid

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityManager
import android.widget.GridLayout
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.calling.VideoStreamRenderer
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.presentation.VideoViewManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class ParticipantGridView : GridLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    companion object {
        private const val SINGLE_PARTICIPANT = 1
        private const val TWO_PARTICIPANTS = 2
        private const val THREE_PARTICIPANTS = 3
        private const val FOUR_PARTICIPANTS = 4
        private const val FIVE_PARTICIPANTS = 5
        private const val SIX_PARTICIPANTS = 6
    }

    private lateinit var showFloatingHeaderCallBack: () -> Unit
    private lateinit var videoViewManager: VideoViewManager
    private lateinit var viewLifecycleOwner: LifecycleOwner
    private lateinit var participantGridViewModel: ParticipantGridViewModel
    private lateinit var getVideoStreamCallback: (String, String) -> View?
    private lateinit var getScreenShareVideoStreamRendererCallback: () -> VideoStreamRenderer?
    private lateinit var gridView: ParticipantGridView
    private lateinit var accessibilityManager: AccessibilityManager

    override fun onFinishInflate() {
        super.onFinishInflate()
        gridView = findViewById(R.id.azure_communication_ui_call_participant_container)
    }

    fun start(
        participantGridViewModel: ParticipantGridViewModel,
        videoViewManager: VideoViewManager,
        viewLifecycleOwner: LifecycleOwner,
        showFloatingHeader: () -> Unit,
    ) {
        accessibilityManager =
            context?.applicationContext?.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        if (accessibilityManager.isEnabled) {
            ViewCompat.setAccessibilityDelegate(
                this,
                object : AccessibilityDelegateCompat() {
                    override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfoCompat) {
                        super.onInitializeAccessibilityNodeInfo(host, info)
                        info.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK)
                        info.isClickable = false
                    }
                }
            )
        }
        this.videoViewManager = videoViewManager
        this.viewLifecycleOwner = viewLifecycleOwner
        this.participantGridViewModel = participantGridViewModel
        this.showFloatingHeaderCallBack = showFloatingHeader
        this.getVideoStreamCallback = { participantID: String, videoStreamID: String ->
            this.videoViewManager.getRemoteVideoStreamRenderer(
                participantID,
                videoStreamID
            )
        }

        this.getScreenShareVideoStreamRendererCallback = {
            this.videoViewManager.getScreenShareVideoStreamRenderer()
        }

        this.participantGridViewModel.setUpdateVideoStreamsCallback { users: List<Pair<String, String>> ->
            this.videoViewManager.removeRemoteParticipantVideoRenderer(users)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            participantGridViewModel.getRemoteParticipantsUpdateStateFlow().collect {
                post {
                    updateGrid(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            participantGridViewModel.getIsLobbyOverlayDisplayedFlow().collect {
                if (it) {
                    ViewCompat.setImportantForAccessibility(gridView, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS)
                } else {
                    ViewCompat.setImportantForAccessibility(gridView, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES)
                }
            }
        }

        addOnLayoutChangeListener(object : OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int,
            ) {
                if (isLaidOut) {
                    removeOnLayoutChangeListener(this)
                    post {
                        updateGrid(participantGridViewModel.getRemoteParticipantsUpdateStateFlow().value)
                    }
                }
            }
        })
    }

    fun stop() {
        removeAllViews()
    }

    private fun updateGrid(
        displayedRemoteParticipantsViewModel: List<ParticipantGridCellViewModel>,
    ) {
        removeAllViews()
        val displayedRemoteParticipantsView: MutableList<ParticipantGridCellView> = mutableListOf()
        displayedRemoteParticipantsViewModel.forEach {
            val participantView = createParticipantGridCellView(this.context, it)
            displayedRemoteParticipantsView.add(participantView)
        }

        setGridRowsColumns(displayedRemoteParticipantsViewModel.size)

        displayParticipants(displayedRemoteParticipantsView)
    }

    private fun displayParticipants(
        displayedRemoteParticipantsView: List<ParticipantGridCellView>,
    ) {
        when (displayedRemoteParticipantsView.size) {
            SINGLE_PARTICIPANT, TWO_PARTICIPANTS, FOUR_PARTICIPANTS, SIX_PARTICIPANTS -> {
                displayedRemoteParticipantsView.forEach {
                    addParticipantToGrid(
                        participantGridCellView = it
                    )
                }
            }
            THREE_PARTICIPANTS, FIVE_PARTICIPANTS -> {
                // for 3 or 5 number of participants, first participant will take two cells
                if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    addParticipantToGrid(
                        columnSpan = 2,
                        participantGridCellView = displayedRemoteParticipantsView[0]
                    )
                } else {
                    addParticipantToGrid(
                        rowSpan = 2,
                        participantGridCellView = displayedRemoteParticipantsView[0]
                    )
                }

                displayedRemoteParticipantsView.forEachIndexed { index, participantGridCellView ->
                    if (index > 0) {
                        addParticipantToGrid(
                            participantGridCellView = participantGridCellView
                        )
                    }
                }
            }
        }
    }

    private fun setGridRowsColumns(size: Int) {
        when (size) {
            1 -> {
                setGridRowsColumn(rows = 1, columns = 1)
            }
            2 -> {
                if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    setGridRowsColumn(rows = 2, columns = 1)
                } else {
                    setGridRowsColumn(rows = 1, columns = 2)
                }
            }
            3, 4 -> {
                setGridRowsColumn(rows = 2, columns = 2)
            }
            5, 6 -> {
                if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    setGridRowsColumn(rows = 3, columns = 2)
                } else {
                    setGridRowsColumn(rows = 2, columns = 3)
                }
            }
        }
    }

    private fun addParticipantToGrid(
        columnSpan: Int = 1,
        rowSpan: Int = 1,
        participantGridCellView: ParticipantGridCellView,
    ) {
        val rowSpec = spec(UNDEFINED, rowSpan)
        val columnSpec = spec(UNDEFINED, columnSpan)
        val params = LayoutParams(rowSpec, columnSpec)

        if (columnSpan != 2) {
            params.width = this.width / this.columnCount
        }

        if (rowSpan != 2) {
            params.height = this.height / this.rowCount
        }

        participantGridCellView.layoutParams = params
        detachFromParentView(participantGridCellView)
        this.addView(participantGridCellView)
    }

    private fun setGridRowsColumn(rows: Int, columns: Int) {
        this.rowCount = rows
        this.columnCount = columns
    }

    private fun detachFromParentView(view: View?) {
        if (view != null && view.parent != null) {
            (view.parent as ViewGroup).removeView(view)
        }
    }

    private fun createParticipantGridCellView(
        context: Context,
        participantGridCellViewModel: ParticipantGridCellViewModel,
    ): ParticipantGridCellView =
        ParticipantGridCellView(
            context,
            viewLifecycleOwner.lifecycleScope,
            participantGridCellViewModel,
            showFloatingHeaderCallBack,
            getVideoStreamCallback,
            getScreenShareVideoStreamRendererCallback
        )
}
