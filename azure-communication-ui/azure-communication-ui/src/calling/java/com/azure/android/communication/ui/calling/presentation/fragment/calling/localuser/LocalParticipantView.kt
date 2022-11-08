// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.localuser

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.accessibility.AccessibilityManager
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.calling.presentation.VideoViewManager
import com.azure.android.communication.ui.calling.presentation.manager.AvatarViewManager
import com.azure.android.communication.ui.calling.redux.state.CameraDeviceSelectionStatus
import com.microsoft.fluentui.persona.AvatarView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class LocalParticipantView : ConstraintLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var viewModel: LocalParticipantViewModel
    private lateinit var videoViewManager: VideoViewManager
    private lateinit var localParticipantFullCameraHolder: ConstraintLayout
    private lateinit var localParticipantPip: ConstraintLayout
    private lateinit var localPipWrapper: ConstraintLayout
    private lateinit var localParticipantPipCameraHolder: ConstraintLayout
    private lateinit var switchCameraButton: ImageView
    private lateinit var pipSwitchCameraButton: ConstraintLayout
    private lateinit var avatarHolder: ConstraintLayout
    private lateinit var avatar: AvatarView
    private lateinit var pipAvatar: AvatarView
    private lateinit var displayNameText: TextView
    private lateinit var micImage: ImageView
    private lateinit var dragTouchListener: DragTouchListener
    private lateinit var accessibilityManager: AccessibilityManager

    override fun onFinishInflate() {
        super.onFinishInflate()
        localParticipantFullCameraHolder =
            findViewById(R.id.azure_communication_ui_call_local_full_video_holder)
        localParticipantPip =
            findViewById(R.id.azure_communication_ui_call_local_pip)
        localPipWrapper = findViewById(R.id.azure_communication_ui_call_local_pip_wrapper)
        localParticipantPipCameraHolder =
            findViewById(R.id.azure_communication_ui_call_local_pip_video_holder)
        switchCameraButton =
            findViewById(R.id.azure_communication_ui_call_switch_camera_button)
        pipSwitchCameraButton =
            findViewById(R.id.azure_communication_ui_call_local_pip_switch_camera_button)
        avatarHolder =
            findViewById(R.id.azure_communication_ui_call_local_avatarHolder)

        avatar =
            findViewById(R.id.azure_communication_ui_call_local_avatar)
        pipAvatar =
            findViewById(R.id.azure_communication_ui_call_local_pip_avatar)

        displayNameText =
            findViewById(R.id.azure_communication_ui_call_local_display_name)
        micImage =
            findViewById(R.id.azure_communication_ui_call_local_mic_indicator)
        switchCameraButton.setOnClickListener { viewModel.switchCamera() }
        pipSwitchCameraButton.setOnClickListener { viewModel.switchCamera() }
        dragTouchListener = DragTouchListener()
    }

    fun stop() {
        localParticipantFullCameraHolder.removeAllViews()
        localParticipantPipCameraHolder.removeAllViews()
    }

    fun start(
        viewLifecycleOwner: LifecycleOwner,
        viewModel: LocalParticipantViewModel,
        videoViewManager: VideoViewManager,
        avatarViewManager: AvatarViewManager,
    ) {

        this.viewModel = viewModel
        this.videoViewManager = videoViewManager

        setupAccessibility()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getVideoStatusFlow().collect {
                setLocalParticipantVideo(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getDisplayFullScreenAvatarFlow().collect {
                avatarHolder.visibility = if (it) View.VISIBLE else View.GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getDisplayNameStateFlow().collect {
                it?.let {
                    avatar.name = it
                    pipAvatar.name = it
                    displayNameText.text = it
                    avatarViewManager.callCompositeLocalOptions?.participantViewData?.let { participantViewData ->
                        participantViewData.avatarBitmap?.let { image ->
                            avatar.avatarImageBitmap = image
                            avatar.adjustViewBounds = true
                            avatar.scaleType = participantViewData.scaleType
                            pipAvatar.avatarImageBitmap = image
                            pipAvatar.adjustViewBounds = true
                            pipAvatar.scaleType = participantViewData.scaleType
                        }
                        participantViewData.displayName?.let { name ->
                            avatar.name = name
                            pipAvatar.name = name
                            displayNameText.text = name
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getLocalUserMutedStateFlow().collect { isMuted ->
                micImage.visibility = if (isMuted) View.VISIBLE else View.GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getDisplaySwitchCameraButtonFlow().collect {
                switchCameraButton.visibility = if (it) View.VISIBLE else View.GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getDisplayPipSwitchCameraButtonFlow().collect {
                pipSwitchCameraButton.visibility = if (it) View.VISIBLE else View.GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getEnableCameraSwitchFlow().collect {
                switchCameraButton.isEnabled = it
                pipSwitchCameraButton.isEnabled = it
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getCameraDeviceSelectionFlow().collect { cameraDeviceSelectionStatus ->
                listOf(switchCameraButton, pipSwitchCameraButton).forEach {
                    it.contentDescription = context.getString(
                        when (cameraDeviceSelectionStatus) {
                            CameraDeviceSelectionStatus.FRONT -> R.string.azure_communication_ui_calling_switch_camera_button_front
                            else -> R.string.azure_communication_ui_calling_switch_camera_button_back
                        }
                    )
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getIsOverlayDisplayedFlow().collect {
                if (it) {
                    ViewCompat.setImportantForAccessibility(
                        switchCameraButton,
                        ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
                    )
                } else {
                    ViewCompat.setImportantForAccessibility(
                        switchCameraButton,
                        ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES
                    )
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getNumberOfRemoteParticipantsFlow().collect {
                if (!accessibilityManager.isEnabled && it >= 1) {
                    dragTouchListener.setView(localPipWrapper)
                    localPipWrapper.setOnTouchListener(dragTouchListener)
                } else {
                    localPipWrapper.setOnTouchListener(null)
                }
            }
        }
    }

    private fun setupAccessibility() {
        accessibilityManager =
            context?.applicationContext?.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        switchCameraButton.contentDescription =
            context.getString(R.string.azure_communication_ui_calling_button_switch_camera_accessibility_label)
    }

    private fun setLocalParticipantVideo(model: LocalParticipantViewModel.VideoModel) {
        videoViewManager.updateLocalVideoRenderer(model.videoStreamID)
        localParticipantFullCameraHolder.removeAllViews()
        localParticipantPipCameraHolder.removeAllViews()

        localParticipantPip.visibility =
            if (model.viewMode == LocalParticipantViewMode.PIP) View.VISIBLE else View.GONE

        val videoHolder = when (model.viewMode) {
            LocalParticipantViewMode.PIP -> localParticipantPipCameraHolder
            LocalParticipantViewMode.FULL_SCREEN -> localParticipantFullCameraHolder
        }

        if (model.shouldDisplayVideo) {
            addVideoView(model.videoStreamID!!, videoHolder)
        }
    }

    private fun addVideoView(videoStreamID: String, videoHolder: ConstraintLayout) {
        videoViewManager.getLocalVideoRenderer(videoStreamID)?.let { view ->
            view.background = this.context.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.azure_communication_ui_calling_corner_radius_rectangle_4dp
                )
            }
            videoHolder.addView(view, 0)
        }
    }
}
