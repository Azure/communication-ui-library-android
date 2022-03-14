// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.setup.components

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.redux.state.CameraOperationalStatus
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class SetupControlBarView : LinearLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var viewModel: SetupControlBarViewModel
    private lateinit var micButton: SetupButton
    private lateinit var cameraButton: SetupButton
    private lateinit var audioDeviceButton: AudioDeviceSetupButton

    override fun onFinishInflate() {
        super.onFinishInflate()
        micButton = findViewById(R.id.azure_communication_ui_setup_audio_button)
        cameraButton = findViewById(R.id.azure_communication_ui_setup_camera_button)
        audioDeviceButton = findViewById(R.id.azure_communication_ui_setup_audio_device_button)
        micButton.setOnClickListener {
            toggleAudio()
        }
        cameraButton.setOnClickListener {
            toggleVideo()
        }
        audioDeviceButton.setOnClickListener {
            viewModel.openAudioDeviceSelectionMenu()
        }
    }

    fun start(
        viewLifecycleOwner: LifecycleOwner,
        setupControlBarViewModel: SetupControlBarViewModel,
    ) {
        viewModel = setupControlBarViewModel

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getCameraIsEnabled().collect {
                cameraButton.isEnabled = it
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getIsVisibleState().collect { visible ->
                visibility = if (visible) VISIBLE else INVISIBLE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getAudioOperationalStatusStateFlow().collect {
                setMicButtonState(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getMicIsEnabled().collect {
                micButton.isEnabled = it
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getCameraState().collect {
                setCameraButtonState(it)
                setButtonColorOnCameraState(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getAudioDeviceSelectionStatusStateFlow().collect {
                setAudioDeviceButtonState(it.device)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getDeviceIsEnabled().collect {
                audioDeviceButton.isEnabled = it
            }
        }
    }

    private fun setMicButtonState(audioOperationalStatus: AudioOperationalStatus) {
        when (audioOperationalStatus) {
            AudioOperationalStatus.ON -> {
                micButton.isSelected = true
                micButton.text =
                    context.getString(R.string.azure_communication_ui_setup_mic_on)
            }
            AudioOperationalStatus.OFF -> {
                micButton.isSelected = false
                micButton.text =
                    context.getString(R.string.azure_communication_ui_setup_mic_off)
            }
        }
    }

    private fun setCameraButtonState(operation: CameraOperationalStatus) {
        when (operation) {
            CameraOperationalStatus.ON -> {
                cameraButton.isSelected = true
                cameraButton.text = context.getString(R.string.azure_communication_ui_setup_video_on)
            }
            CameraOperationalStatus.OFF -> {
                cameraButton.isSelected = false
                cameraButton.text = context.getString(R.string.azure_communication_ui_setup_video_off)
            }
        }
    }

    private fun setButtonColorOnCameraState(cameraOperationalStatus: CameraOperationalStatus) {
        cameraButton.isCameraON = cameraOperationalStatus == CameraOperationalStatus.ON
        micButton.isCameraON = cameraOperationalStatus == CameraOperationalStatus.ON
        audioDeviceButton.isCameraON = cameraOperationalStatus == CameraOperationalStatus.ON

        cameraButton.refreshDrawableState()
        micButton.refreshDrawableState()
        audioDeviceButton.refreshDrawableState()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            setOldAPIButtonColor(cameraButton)
            setOldAPIButtonColor(micButton)
            setOldAPIButtonColor(audioDeviceButton)
        }
    }

    private fun setOldAPIButtonColor(button: SetupButton) {
        val color = if (!button.isEnabled) R.color.azure_communication_ui_color_on_surface_disabled
        else if (button.isCameraON) R.color.azure_communication_ui_color_on_surface_camera_active
        else R.color.azure_communication_ui_color_on_surface

        button.compoundDrawables[1].colorFilter = PorterDuffColorFilter(
            ContextCompat.getColor(context, color),
            PorterDuff.Mode.SRC_IN
        )
    }

// <<<<<<< HEAD
//    private fun setAudioDeviceButtonState(audioState: AudioState) {
//        when (audioState.device) {
// =======
    private fun setAudioDeviceButtonState(audioDeviceSelectionStatus: AudioDeviceSelectionStatus) {
        audioDeviceButton.text = when (audioDeviceSelectionStatus) {
            AudioDeviceSelectionStatus.SPEAKER_SELECTED -> {
                context.getString(R.string.azure_communication_ui_setup_audio_device_speaker)
            }
            AudioDeviceSelectionStatus.RECEIVER_SELECTED -> {
                context.getString(R.string.azure_communication_ui_setup_audio_device_android)
            }
            AudioDeviceSelectionStatus.BLUETOOTH_SCO_SELECTED -> {
                context.getString(R.string.azure_communication_ui_setup_audio_device_bluetooth)
            }
            else -> { "" }
        }

        audioDeviceButton.isSpeakerON = audioDeviceSelectionStatus == AudioDeviceSelectionStatus.SPEAKER_SELECTED
        audioDeviceButton.isReceiverON = audioDeviceSelectionStatus == AudioDeviceSelectionStatus.RECEIVER_SELECTED
        audioDeviceButton.isBluetoothON = audioDeviceSelectionStatus == AudioDeviceSelectionStatus.BLUETOOTH_SCO_SELECTED
        audioDeviceButton.refreshDrawableState()
    }

    private fun toggleAudio() {
        if (micButton.isSelected) {
            viewModel.turnMicOff()
        } else {
            viewModel.turnMicOn()
        }
    }

    private fun toggleVideo() {
        if (cameraButton.isSelected) {
            viewModel.turnCameraOff()
        } else {
            viewModel.turnCameraOn()
        }
    }
}

internal open class SetupButton(context: Context, attrs: AttributeSet?) :
    AppCompatButton(context, attrs) {

    var isCameraON = false

    override fun onCreateDrawableState(extraSpace: Int): IntArray? {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isCameraON) {
            mergeDrawableStates(drawableState, intArrayOf(R.attr.azure_communication_ui_state_setup_camera_on))
        }
        return drawableState
    }
}

internal class AudioDeviceSetupButton(context: Context, attrs: AttributeSet?) :
    SetupButton(context, attrs) {

    var isSpeakerON = false
    var isReceiverON = false
    var isBluetoothON = false

    override fun onCreateDrawableState(extraSpace: Int): IntArray? {
        val drawableState = super.onCreateDrawableState(extraSpace + 4)
        if (isSpeakerON) {
            mergeDrawableStates(drawableState, intArrayOf(R.attr.azure_communication_ui_state_setup_audio_device_speaker))
        }
        if (isReceiverON) {
            mergeDrawableStates(drawableState, intArrayOf(R.attr.azure_communication_ui_state_setup_audio_device_receiver))
        }
        if (isBluetoothON) {
            mergeDrawableStates(drawableState, intArrayOf(R.attr.azure_communication_ui_state_setup_audio_device_bluetooth))
        }
        return drawableState
    }
}
