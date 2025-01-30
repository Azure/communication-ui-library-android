// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.participant.grid

import android.content.Context
import android.graphics.Rect
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
import com.azure.android.communication.ui.calling.implementation.R
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantViewData
import com.azure.android.communication.ui.calling.presentation.VideoViewManager
import com.azure.android.communication.ui.calling.presentation.manager.AvatarViewManager
import com.azure.android.communication.ui.calling.service.sdk.VideoStreamRenderer
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
        private const val SEVEN_PARTICIPANTS = 7
        private const val EIGHT_PARTICIPANTS = 8
        private const val NINE_PARTICIPANTS = 9
    }

    private lateinit var showFloatingHeaderCallBack: () -> Unit
    private lateinit var videoViewManager: VideoViewManager
    private lateinit var viewLifecycleOwner: LifecycleOwner
    private lateinit var participantGridViewModel: ParticipantGridViewModel
    private lateinit var getVideoStreamCallback: (String, String) -> View?
    private lateinit var getScreenShareVideoStreamRendererCallback: () -> VideoStreamRenderer?
    private lateinit var gridView: ParticipantGridView
    private lateinit var accessibilityManager: AccessibilityManager
    private lateinit var displayedRemoteParticipantsView: MutableList<ParticipantGridCellView>
    private lateinit var getParticipantViewDataCallback: (participantID: String) -> CallCompositeParticipantViewData?

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        post {
            updateGrid(participantGridViewModel.getRemoteParticipantsUpdateStateFlow().value)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        gridView = findViewById(R.id.azure_communication_ui_call_participant_container)
    }

    fun start(
        participantGridViewModel: ParticipantGridViewModel,
        videoViewManager: VideoViewManager,
        viewLifecycleOwner: LifecycleOwner,
        showFloatingHeader: () -> Unit,
        avatarViewManager: AvatarViewManager,
    ) {
        accessibilityManager =
            context?.applicationContext?.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager

        if (accessibilityManager.isEnabled) {
            ViewCompat.setAccessibilityDelegate(
                this,
                object : AccessibilityDelegateCompat() {
                    override fun onInitializeAccessibilityNodeInfo(
                        host: View,
                        info: AccessibilityNodeInfoCompat,
                    ) {
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

        this.getParticipantViewDataCallback = { participantID: String ->
            avatarViewManager.getRemoteParticipantViewData(participantID)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            avatarViewManager.getRemoteParticipantsPersonaSharedFlow()
                .collect { remoteParticipantViewData ->
                    if (::displayedRemoteParticipantsView.isInitialized && displayedRemoteParticipantsView.isNotEmpty()) {
                        displayedRemoteParticipantsView.forEach { displayedParticipant ->
                            val identifier = displayedParticipant.getParticipantIdentifier()
                            if (remoteParticipantViewData.keys.contains(identifier)) {
                                displayedParticipant.updateParticipantViewData()
                            }
                        }
                    }
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            participantGridViewModel.getRemoteParticipantsUpdateStateFlow().collect {
                post {
                    updateGrid(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            participantGridViewModel.getIsOverlayDisplayedFlow().collect {
                if (it) {
                    ViewCompat.setImportantForAccessibility(
                        gridView,
                        ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
                    )
                } else {
                    ViewCompat.setImportantForAccessibility(
                        gridView,
                        ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES
                    )
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

        addOnLayoutChangeListener { _, left, top, right, bottom,
            oldLeft, oldTop, oldRight, oldBottom ->
            if (left != oldLeft ||
                right != oldRight ||
                top != oldTop ||
                bottom != oldBottom
            ) {
                // The playerView's bounds changed, update the source hint rect to
                // reflect its new bounds.
                val sourceRectHint = Rect()
                getGlobalVisibleRect(sourceRectHint)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            participantGridViewModel.participantUpdated.events.collect { updateContentDescription() }
        }
    }

    fun stop() {
        removeAllViews()
    }

    private fun updateGrid(
        displayedRemoteParticipantsViewModel: List<ParticipantGridCellViewModel>,
    ) {
        videoViewManager.updateScalingForRemoteStream()
        removeAllViews()
        displayedRemoteParticipantsView = mutableListOf()
        displayedRemoteParticipantsViewModel.forEach {
            val participantView = createParticipantGridCellView(this.context, it)
            displayedRemoteParticipantsView.add(participantView)
        }

        setGridRowsColumns(displayedRemoteParticipantsViewModel.size)

        displayParticipants(displayedRemoteParticipantsView)
    }

    private fun updateContentDescription() {
        val muted = context.getString(R.string.azure_communication_ui_calling_view_participant_list_muted_accessibility_label)
        val unmuted = context.getString(R.string.azure_communication_ui_calling_view_participant_list_unmuted_accessibility_label)

        val participants =
            participantGridViewModel.getRemoteParticipantsUpdateStateFlow().value.joinToString {
                val muteState = if (it.getIsMutedStateFlow().value) muted else unmuted
                "${it.getDisplayNameStateFlow().value}, $muteState."
            }

        gridView.contentDescription = if (participants.isNotEmpty())
            context.getString(R.string.azure_communication_ui_calling_view_call_with_accessibility_label, participants)
        else context.getString(R.string.azure_communication_ui_calling_view_info_header_waiting_for_others_to_join)
    }

    private fun displayParticipants(
        displayedRemoteParticipantsView: List<ParticipantGridCellView>,
    ) {
        when (displayedRemoteParticipantsView.size) {
            SINGLE_PARTICIPANT, TWO_PARTICIPANTS, FOUR_PARTICIPANTS, SIX_PARTICIPANTS, NINE_PARTICIPANTS, -> {
                displayedRemoteParticipantsView.forEach {
                    addParticipantToGrid(
                        participantGridCellView = it
                    )
                }
            }
            THREE_PARTICIPANTS -> {
                // for 3 first participant will take two cells
                if (participantGridViewModel.isVerticalStyleGridFlow.value) {
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
            FIVE_PARTICIPANTS -> {
                // for 5 number of participants, first participant will take two cells only on phones (< 7inch)
                if (isTabletScreen()) {
                    if (participantGridViewModel.isVerticalStyleGridFlow.value) {
                        addParticipantToGrid(
                            columnSpan = 2,
                            rowSpan = 3,
                            participantGridCellView = displayedRemoteParticipantsView[0]
                        )

                        displayedRemoteParticipantsView.forEachIndexed { index, participantGridCellView ->
                            if (index > 0) {
                                if (index == 3) {
                                    addParticipantToGrid(
                                        columnSpan = 2,
                                        rowSpan = 3,
                                        participantGridCellView = participantGridCellView
                                    )
                                } else {
                                    addParticipantToGrid(
                                        columnSpan = 2,
                                        rowSpan = 2,
                                        participantGridCellView = participantGridCellView
                                    )
                                }
                            }
                        }
                    } else {
                        displayedRemoteParticipantsView.forEachIndexed { index, participantGridCellView ->
                            if (index < 3) {
                                addParticipantToGrid(
                                    rowSpan = 2,
                                    columnSpan = 2,
                                    participantGridCellView = participantGridCellView
                                )
                            } else {
                                addParticipantToGrid(
                                    columnSpan = 3,
                                    rowSpan = 2,
                                    participantGridCellView = participantGridCellView
                                )
                            }
                        }
                    }
                } else {
                    if (participantGridViewModel.isVerticalStyleGridFlow.value) {
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
            SEVEN_PARTICIPANTS -> {
                if (participantGridViewModel.isVerticalStyleGridFlow.value) {
                    addParticipantToGrid(
                        rowSpan = 4,
                        participantGridCellView = displayedRemoteParticipantsView[0]
                    )
                    addParticipantToGrid(
                        rowSpan = 3,
                        participantGridCellView = displayedRemoteParticipantsView[1]
                    )
                    displayedRemoteParticipantsView.forEachIndexed { index, participantGridCellView ->
                        if (index > 1) {
                            if (index % 2 == 0) {
                                addParticipantToGrid(
                                    rowSpan = 3,
                                    participantGridCellView = participantGridCellView
                                )
                            } else {
                                addParticipantToGrid(
                                    rowSpan = 4,
                                    participantGridCellView = participantGridCellView
                                )
                            }
                        }
                    }
                } else {
                    displayedRemoteParticipantsView.forEachIndexed { index, participantGridCellView ->
                        if (index < 4) {
                            addParticipantToGrid(
                                columnSpan = 3,
                                participantGridCellView = participantGridCellView
                            )
                        } else {
                            addParticipantToGrid(
                                columnSpan = 4,
                                participantGridCellView = participantGridCellView
                            )
                        }
                    }
                }
            }
            EIGHT_PARTICIPANTS -> {
                if (participantGridViewModel.isVerticalStyleGridFlow.value) {
                    displayedRemoteParticipantsView.forEachIndexed { index, participantGridCellView ->
                        if (index < 2) {
                            addParticipantToGrid(
                                rowSpan = 2,
                                columnSpan = 3,
                                participantGridCellView = participantGridCellView
                            )
                        } else {
                            addParticipantToGrid(
                                rowSpan = 2,
                                columnSpan = 2,
                                participantGridCellView = participantGridCellView
                            )
                        }
                    }
                } else {
                    addParticipantToGrid(
                        rowSpan = 3,
                        columnSpan = 2,
                        participantGridCellView = displayedRemoteParticipantsView[0]
                    )

                    displayedRemoteParticipantsView.forEachIndexed { index, participantGridCellView ->
                        if (index > 0) {
                            if (index == 5) {
                                addParticipantToGrid(
                                    rowSpan = 3,
                                    columnSpan = 2,
                                    participantGridCellView = participantGridCellView
                                )
                            } else {
                                addParticipantToGrid(
                                    rowSpan = 2,
                                    columnSpan = 2,
                                    participantGridCellView = participantGridCellView
                                )
                            }
                        }
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
                if (participantGridViewModel.isVerticalStyleGridFlow.value) {
                    setGridRowsColumn(rows = 2, columns = 1)
                } else {
                    setGridRowsColumn(rows = 1, columns = 2)
                }
            }
            3, 4 -> {
                setGridRowsColumn(rows = 2, columns = 2)
            } 6 -> {
                if (participantGridViewModel.isVerticalStyleGridFlow.value) {
                    setGridRowsColumn(rows = 3, columns = 2)
                } else {
                    setGridRowsColumn(rows = 2, columns = 3)
                }
            }
            5 -> {
                if (isTabletScreen()) {
                    if (participantGridViewModel.isVerticalStyleGridFlow.value) {
                        setGridRowsColumn(rows = 6, columns = 4)
                    } else {
                        setGridRowsColumn(rows = 4, columns = 6)
                    }
                } else {
                    if (participantGridViewModel.isVerticalStyleGridFlow.value) {
                        setGridRowsColumn(rows = 3, columns = 2)
                    } else {
                        setGridRowsColumn(rows = 2, columns = 3)
                    }
                }
            }
            7 -> {
                if (participantGridViewModel.isVerticalStyleGridFlow.value) {
                    setGridRowsColumn(rows = 12, columns = 2)
                } else {
                    setGridRowsColumn(rows = 2, columns = 12)
                }
            }
            8 -> {
                setGridRowsColumn(rows = 6, columns = 6)
            } 9 -> {
                setGridRowsColumn(rows = 3, columns = 3)
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

        params.width = this.width / (this.columnCount / columnSpan)
        params.height = this.height / (this.rowCount / rowSpan)
        this.orientation = HORIZONTAL

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

    private fun isTabletScreen(): Boolean {
        return participantGridViewModel.getMaxRemoteParticipantsSize() == NINE_PARTICIPANTS
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
            getScreenShareVideoStreamRendererCallback,
            getParticipantViewDataCallback
        )
}
