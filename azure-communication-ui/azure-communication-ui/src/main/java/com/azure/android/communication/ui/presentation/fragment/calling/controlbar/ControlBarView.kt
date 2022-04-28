// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.controlbar

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.redux.state.PermissionStatus
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class ControlBarView : LinearLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var viewModel: ControlBarViewModel
    private lateinit var endCallButton: ImageButton
    private lateinit var micButton: ControlButton
    private lateinit var cameraButton: ControlButton
    private lateinit var callAudioDeviceButton: ImageButton
    private lateinit var requestCallEndCallback: () -> Unit
    private lateinit var openAudioDeviceSelectionMenuCallback: () -> Unit

    override fun onFinishInflate() {
        super.onFinishInflate()
        endCallButton = findViewById(R.id.azure_communication_ui_call_end_call_button)
        cameraButton = findViewById(R.id.azure_communication_ui_call_camera_button)
        micButton = findViewById(R.id.azure_communication_ui_call_call_mic_button)
        callAudioDeviceButton = findViewById(R.id.azure_communication_ui_call_audio_device_button)
        subscribeClickListener()
    }

    fun start(
        viewLifecycleOwner: LifecycleOwner,
        viewModel: ControlBarViewModel,
        requestCallEnd: () -> Unit,
        openAudioDeviceSelectionMenu: () -> Unit,
    ) {
        this.viewModel = viewModel
        this.requestCallEndCallback = requestCallEnd
        this.openAudioDeviceSelectionMenuCallback = openAudioDeviceSelectionMenu

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
                micButton.isEnabled = it
            }
        }
    }

    private fun setupAccessibility() {
        endCallButton.contentDescription = context.getString(R.string.azure_communication_ui_calling_view_button_hang_up_accessibility_label)
        callAudioDeviceButton.contentDescription = context.getString(R.string.azure_communication_ui_calling_view_button_device_options_accessibility_label)
}

    private fun updateCamera(cameraModel: ControlBarViewModel.CameraModel) {
        val permissionIsNotDenied = cameraModel.cameraPermissionState != PermissionStatus.DENIED
        when (cameraModel.cameraState.operation) {
            CameraOperationalStatus.ON -> {
                cameraButton.isON = true
                cameraButton.contentDescription = context.getString(R.string.azure_communication_ui_setup_view_button_video_on)
                cameraButton.isEnabled = permissionIsNotDenied
            }
            CameraOperationalStatus.OFF -> {
                cameraButton.isON = false
                cameraButton.contentDescription = context.getString(R.string.azure_communication_ui_setup_view_button_video_off)
                cameraButton.isEnabled = permissionIsNotDenied
            }
            else -> {
                cameraButton.isEnabled = false
            }
        }
        cameraButton.refreshDrawableState()
    }

    private fun updateMic(audioOperationalStatus: AudioOperationalStatus) {
        when (audioOperationalStatus) {
            AudioOperationalStatus.ON -> {
                micButton.isON = true
                micButton.contentDescription = context.getString(R.string.azure_communication_ui_setup_view_button_mic_on)
            }
            AudioOperationalStatus.OFF -> {
                micButton.isON = false
                micButton.contentDescription = context.getString(R.string.azure_communication_ui_setup_view_button_mic_off)
            }
        }
        micButton.refreshDrawableState()
    }

    private fun setAudioDeviceButtonState(audioDeviceSelectionStatus: AudioDeviceSelectionStatus) {
        when (audioDeviceSelectionStatus) {
            AudioDeviceSelectionStatus.SPEAKER_SELECTED -> {
                callAudioDeviceButton.setImageResource(
                    R.drawable.azure_communication_ui_ic_fluent_speaker_2_24_filled_composite_button_enabled
                )
            }
            AudioDeviceSelectionStatus.RECEIVER_SELECTED -> {
                callAudioDeviceButton.setImageResource(
                    R.drawable.azure_communication_ui_ic_fluent_speaker_2_24_regular_composite_button_filled
                )
            }
            AudioDeviceSelectionStatus.BLUETOOTH_SCO_SELECTED -> {
                callAudioDeviceButton.setImageResource(
                    // Needs an icon
                    R.drawable.azure_communication_ui_ic_fluent_speaker_bluetooth_24_regular
                )
            }
        }
    }

    private fun subscribeClickListener() {
        endCallButton.setOnClickListener {
            requestCallEndCallback()
        }
        micButton.setOnClickListener {
            if (micButton.isON) {
                viewModel.turnMicOff()
            } else {
                viewModel.turnMicOn()
            }
        }
        cameraButton.setOnClickListener {
            if (cameraButton.isON) {
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

internal open class ControlButton(context: Context, attrs: AttributeSet?) :
    AppCompatButton(context, attrs) {
    var isON = false
    override fun onCreateDrawableState(extraSpace: Int): IntArray? {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isON) {
            mergeDrawableStates(
                drawableState,
                intArrayOf(R.attr.azure_communication_ui_state_on)
            )
        }
        return drawableState
    }
}
