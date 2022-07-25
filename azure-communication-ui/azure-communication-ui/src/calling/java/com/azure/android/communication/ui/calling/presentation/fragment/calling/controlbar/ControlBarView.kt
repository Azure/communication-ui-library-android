// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.updateLayoutParams
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.calling.models.CallCompositeControlCode
import com.azure.android.communication.ui.calling.models.CallCompositeControlOptions
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.PermissionStatus
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class ControlBarView : ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var controlBar: ConstraintLayout
    private lateinit var viewModel: ControlBarViewModel
    private lateinit var endCallButton: ImageButton
    private lateinit var cameraToggle: ImageButton
    private lateinit var micToggle: ImageButton
    private lateinit var callAudioDeviceButton: ImageButton
    private lateinit var requestCallEndCallback: () -> Unit
    private lateinit var openAudioDeviceSelectionMenuCallback: () -> Unit

    private lateinit var firstControl: ImageButton
    private lateinit var secondControl: ImageButton
    private lateinit var thirdControl: ImageButton
    private lateinit var fourthControl: ImageButton

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun onFinishInflate() {
        super.onFinishInflate()
        controlBar = findViewById(R.id.azure_communication_ui_call_call_buttons)
        endCallButton = findViewById(R.id.azure_communication_ui_call_end_call_button)
        cameraToggle = findViewById(R.id.azure_communication_ui_call_cameraToggle)
        micToggle = findViewById(R.id.azure_communication_ui_call_call_audio)
        callAudioDeviceButton = findViewById(R.id.azure_communication_ui_call_audio_device_button)

        subscribeClickListener()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    fun start(
        viewLifecycleOwner: LifecycleOwner,
        viewModel: ControlBarViewModel,
        requestCallEnd: () -> Unit,
        openAudioDeviceSelectionMenu: () -> Unit,
        controlBarConfig: CallCompositeControlOptions
    ) {
        this.viewModel = viewModel
        this.requestCallEndCallback = requestCallEnd
        this.openAudioDeviceSelectionMenuCallback = openAudioDeviceSelectionMenu

        orderAssignment(controlBarConfig)
        reOrder()

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
                    callAudioDeviceButton.drawable.setTint(ContextCompat.getColor(context, R.color.azure_communication_ui_calling_color_participant_list_mute_mic))
                } else {
                    updateCamera(viewModel.getCameraStateFlow().value)
                    micToggle.isEnabled = viewModel.getShouldEnableMicButtonStateFlow().value
                    callAudioDeviceButton.isEnabled = true
                    callAudioDeviceButton.drawable.setTint(ContextCompat.getColor(context, R.color.azure_communication_ui_calling_color_on_background))
                }
            }
        }
    }

    private fun orderAssignment(config: CallCompositeControlOptions) {
        when(config.firstControl) {
            CallCompositeControlCode.CAMERA_CONTROL -> firstControl = cameraToggle
            CallCompositeControlCode.MIC_CONTROL -> firstControl = micToggle
            CallCompositeControlCode.AUDIO_CONTROL -> firstControl = callAudioDeviceButton
            CallCompositeControlCode.HANGUP_CONTROL -> firstControl = endCallButton
        }

        when(config.secondControl) {
            CallCompositeControlCode.CAMERA_CONTROL -> secondControl = cameraToggle
            CallCompositeControlCode.MIC_CONTROL -> secondControl = micToggle
            CallCompositeControlCode.AUDIO_CONTROL -> secondControl = callAudioDeviceButton
            CallCompositeControlCode.HANGUP_CONTROL -> secondControl = endCallButton
        }

        when(config.thirdControl) {
            CallCompositeControlCode.CAMERA_CONTROL -> thirdControl = cameraToggle
            CallCompositeControlCode.MIC_CONTROL -> thirdControl = micToggle
            CallCompositeControlCode.AUDIO_CONTROL -> thirdControl = callAudioDeviceButton
            CallCompositeControlCode.HANGUP_CONTROL -> thirdControl = endCallButton
        }

        when(config.fourthControl) {
            CallCompositeControlCode.CAMERA_CONTROL -> fourthControl = cameraToggle
            CallCompositeControlCode.MIC_CONTROL -> fourthControl = micToggle
            CallCompositeControlCode.AUDIO_CONTROL -> fourthControl = callAudioDeviceButton
            CallCompositeControlCode.HANGUP_CONTROL -> fourthControl = endCallButton
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private fun reOrder() {

        firstControl.updateLayoutParams<ConstraintLayout.LayoutParams> {
            endToStart = secondControl.id
            startToStart = ConstraintSet.PARENT_ID
            horizontalChainStyle = ConstraintSet.CHAIN_SPREAD_INSIDE
        }

        secondControl.updateLayoutParams<ConstraintLayout.LayoutParams> {
            endToStart = thirdControl.id
            startToEnd = firstControl.id
        }
        secondControl.accessibilityTraversalAfter = firstControl.id

        thirdControl.updateLayoutParams<ConstraintLayout.LayoutParams> {
            endToStart = fourthControl.id
            startToEnd = secondControl.id
        }
        thirdControl.accessibilityTraversalAfter = secondControl.id

        fourthControl.updateLayoutParams<ConstraintLayout.LayoutParams> {
            startToEnd = thirdControl.id
        }
        fourthControl.accessibilityTraversalAfter = thirdControl.id
    }

    private fun accessibilityNonSelectableViews() = setOf(micToggle, cameraToggle)

    private val alwaysOffSelectedAccessibilityDelegate = object : AccessibilityDelegateCompat() {
        override fun onInitializeAccessibilityNodeInfo(
            host: View?,
            info: AccessibilityNodeInfoCompat?
        ) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            if (host in accessibilityNonSelectableViews()) {
                // From an accessibility standpoint these views are never "selected"
                info?.isSelected = false
            }
        }
    }

    private fun setupAccessibility() {
        ViewCompat.setAccessibilityDelegate(
            this,
            object : AccessibilityDelegateCompat() {
                override fun onRequestSendAccessibilityEvent(
                    host: ViewGroup?,
                    child: View?,
                    event: AccessibilityEvent?
                ): Boolean {
                    if (child in accessibilityNonSelectableViews() && event?.eventType == AccessibilityEvent.TYPE_VIEW_SELECTED) {
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
        val permissionIsNotDenied = cameraState.cameraPermissionState != PermissionStatus.DENIED

        when (cameraState.cameraState.operation) {
            CameraOperationalStatus.ON -> {
                cameraToggle.isSelected = true
                cameraToggle.isEnabled = permissionIsNotDenied
                cameraToggle.contentDescription = context.getString(R.string.azure_communication_ui_calling_setup_view_button_video_on)
            }
            CameraOperationalStatus.OFF -> {
                cameraToggle.isSelected = false
                cameraToggle.isEnabled = permissionIsNotDenied
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
        }
    }

    private fun setAudioDeviceButtonState(audioDeviceSelectionStatus: AudioDeviceSelectionStatus) {
        when (audioDeviceSelectionStatus) {
            AudioDeviceSelectionStatus.SPEAKER_SELECTED -> {
                callAudioDeviceButton.setImageResource(
                    R.drawable.azure_communication_ui_calling_ic_fluent_speaker_2_24_filled_composite_button_enabled
                )
            }
            AudioDeviceSelectionStatus.RECEIVER_SELECTED -> {
                callAudioDeviceButton.setImageResource(
                    R.drawable.azure_communication_ui_calling_ic_fluent_speaker_2_24_regular_composite_button_filled
                )
            }
            AudioDeviceSelectionStatus.BLUETOOTH_SCO_SELECTED -> {
                callAudioDeviceButton.setImageResource(
                    // Needs an icon
                    R.drawable.azure_communication_ui_calling_ic_fluent_speaker_bluetooth_24_regular
                )
            }
        }
    }

    private fun subscribeClickListener() {
        endCallButton.setOnClickListener {
            requestCallEndCallback()
        }
        micToggle.setOnClickListener {
            if (micToggle.isSelected) {
                viewModel.turnMicOff()
            } else {
                viewModel.turnMicOn()
            }
        }
        cameraToggle.setOnClickListener {
            if (cameraToggle.isSelected) {
                viewModel.turnCameraOff()
            } else {
                viewModel.turnCameraOn()
            }
        }
        callAudioDeviceButton.setOnClickListener {
            openAudioDeviceSelectionMenuCallback()
        }
    }
}
