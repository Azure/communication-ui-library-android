// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.CallingStatus
import com.azure.android.communication.ui.calling.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.PermissionStatus
import com.azure.android.communication.ui.calling.utilities.isAndroidTV
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class ControlBarView : ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var viewModel: ControlBarViewModel
    private lateinit var endCallButton: ImageButton
    private lateinit var cameraToggle: ImageButton
    private lateinit var micToggle: ImageButton
    private lateinit var callAudioDeviceButton: ImageButton
    private lateinit var moreButton: ImageButton

    private var callStatePassedConnecting: Boolean = false

    override fun onFinishInflate() {
        super.onFinishInflate()
        endCallButton = findViewById(R.id.azure_communication_ui_call_end_call_button)
        cameraToggle = findViewById(R.id.azure_communication_ui_call_cameraToggle)
        micToggle = findViewById(R.id.azure_communication_ui_call_call_audio)
        callAudioDeviceButton = findViewById(R.id.azure_communication_ui_call_audio_device_button)
        moreButton = findViewById(R.id.azure_communication_ui_call_control_bar_more)

        subscribeClickListener()
    }

    fun start(
        viewLifecycleOwner: LifecycleOwner,
        viewModel: ControlBarViewModel,
    ) {
        this.viewModel = viewModel

        setupAccessibility()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getAudioOperationalStatusStateFlow().collect {
                updateMic(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getCameraStateFlow().collect {
                updateCamera(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getAudioDeviceSelectionStatusStateFlow().collect {
                setAudioDeviceButtonState(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getShouldEnableMicButtonStateFlow().collect {
                micToggle.isEnabled = it
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getOnHoldCallStatusStateFlowStateFlow().collect {
                if (it) {
                    cameraToggle.isEnabled = false
                    micToggle.isEnabled = false
                    callAudioDeviceButton.isEnabled = false
                } else {
                    updateCamera(viewModel.getCameraStateFlow().value)
                    micToggle.isEnabled = viewModel.getShouldEnableMicButtonStateFlow().value
                    callAudioDeviceButton.isEnabled = true
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getCallStateFlow().collect() {
                if (it == CallingStatus.NONE || it == CallingStatus.CONNECTING ||
                    it == CallingStatus.RINGING
                ) {
                    cameraToggle.isEnabled = false
                    micToggle.isEnabled = false
                    callAudioDeviceButton.isEnabled = false
                    moreButton.isEnabled = false
                    callStatePassedConnecting = false
                } else {
                    callStatePassedConnecting = true
                    updateCamera(viewModel.getCameraStateFlow().value)
                    micToggle.isEnabled = viewModel.getShouldEnableMicButtonStateFlow().value
                    callAudioDeviceButton.isEnabled = true
                    moreButton.isEnabled = true
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isVisible.collect {
                visibility = if (it) View.GONE else View.VISIBLE
            }
        }
    }

    private fun accessibilityNonSelectableViews() = setOf(micToggle, cameraToggle)

    private val alwaysOffSelectedAccessibilityDelegate = object : AccessibilityDelegateCompat() {
        override fun onInitializeAccessibilityNodeInfo(
            host: View,
            info: AccessibilityNodeInfoCompat
        ) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            if (host in accessibilityNonSelectableViews()) {
                // From an accessibility standpoint these views are never "selected"
                info.isSelected = false
            }
        }
    }

    private fun setupAccessibility() {
        ViewCompat.setAccessibilityDelegate(
            this,
            object : AccessibilityDelegateCompat() {
                override fun onRequestSendAccessibilityEvent(
                    host: ViewGroup,
                    child: View,
                    event: AccessibilityEvent
                ): Boolean {
                    if (child in accessibilityNonSelectableViews() && event.eventType == AccessibilityEvent.TYPE_VIEW_SELECTED) {
                        // We don't want Accessibility TalkBock to read out the "Selected" status of
                        // these views because that's just the way we've internally set up the
                        // icons to have different drawables based on the current status
                        return false
                    }
                    return super.onRequestSendAccessibilityEvent(host, child, event)
                }
            }
        )
        ViewCompat.setAccessibilityDelegate(micToggle, alwaysOffSelectedAccessibilityDelegate)
        ViewCompat.setAccessibilityDelegate(cameraToggle, alwaysOffSelectedAccessibilityDelegate)

        endCallButton.contentDescription = context.getString(R.string.azure_communication_ui_calling_view_button_hang_up_accessibility_label)

        callAudioDeviceButton.contentDescription = context.getString(R.string.azure_communication_ui_calling_view_button_device_options_accessibility_label)
    }

    private fun updateCamera(cameraState: ControlBarViewModel.CameraModel) {
        val cameraPermissionIsNotDenied = (cameraState.cameraPermissionState != PermissionStatus.DENIED)
        val shouldBeEnabled = (cameraPermissionIsNotDenied && callStatePassedConnecting)
        cameraToggle.isEnabled = shouldBeEnabled

        when (cameraState.cameraState.operation) {
            CameraOperationalStatus.ON -> {
                cameraToggle.isSelected = true
                cameraToggle.isEnabled = shouldBeEnabled
                cameraToggle.contentDescription = context.getString(R.string.azure_communication_ui_calling_setup_view_button_video_on)
            }
            CameraOperationalStatus.OFF -> {
                cameraToggle.isSelected = false
                cameraToggle.isEnabled = shouldBeEnabled
                cameraToggle.contentDescription = context.getString(R.string.azure_communication_ui_calling_setup_view_button_video_off)
            }
            else -> {
                // disable button
                cameraToggle.isEnabled = false
            }
        }
    }

    private fun updateMic(audioOperationalStatus: AudioOperationalStatus) {
        when (audioOperationalStatus) {
            AudioOperationalStatus.ON -> {
                // show un-mute icon
                micToggle.isSelected = true
                micToggle.contentDescription = context.getString(R.string.azure_communication_ui_calling_setup_view_button_mic_on)
            }
            AudioOperationalStatus.OFF -> {
                // show mute icon
                micToggle.isSelected = false
                micToggle.contentDescription = context.getString(R.string.azure_communication_ui_calling_setup_view_button_mic_off)
            }
            else -> {}
        }
    }

    private fun setAudioDeviceButtonState(audioDeviceSelectionStatus: AudioDeviceSelectionStatus) {
        when (audioDeviceSelectionStatus) {
            AudioDeviceSelectionStatus.SPEAKER_SELECTED -> {
                callAudioDeviceButton.setImageResource(
                    R.drawable.azure_communication_ui_calling_speaker_speakerphone_selector
                )
            }
            AudioDeviceSelectionStatus.RECEIVER_SELECTED -> {
                callAudioDeviceButton.setImageResource(
                    R.drawable.azure_communication_ui_calling_speaker_receiver_selector
                )
            }
            AudioDeviceSelectionStatus.BLUETOOTH_SCO_SELECTED -> {
                callAudioDeviceButton.setImageResource(
                    // Needs an icon
                    R.drawable.azure_communication_ui_calling_speaker_bluetooth_selector
                )
            }
            else -> {}
        }
    }

    private fun subscribeClickListener() {
        endCallButton.setOnClickListener {
            viewModel.requestCallEnd()
        }
        micToggle.setOnClickListener {
            if (micToggle.isSelected) {
                viewModel.turnMicOff()
            } else {
                viewModel.turnMicOn()
            }

            if (isAndroidTV(context)) {
                // Steal focus back after 1 frame
                // This isn't ideal, focus is lost because button is enabled->disabled->enabled
                // As there is intermediary "camera toggling/audio toggling" state
                postDelayed({ micToggle.requestFocus() }, 33)
            }
        }
        cameraToggle.setOnClickListener {
            if (cameraToggle.isSelected) {
                viewModel.turnCameraOff()
            } else {
                viewModel.turnCameraOn()
            }

            if (isAndroidTV(context)) {
                // Same as above (Steal back focus)
                postDelayed({ cameraToggle.requestFocus() }, 33)
            }
        }
        callAudioDeviceButton.setOnClickListener {
            viewModel.openAudioDeviceSelectionMenu()
        }
        moreButton.setOnClickListener {
            viewModel.openMoreMenu()
        }
    }
}
