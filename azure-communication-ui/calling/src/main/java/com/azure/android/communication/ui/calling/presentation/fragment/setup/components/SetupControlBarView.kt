// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.setup.components

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
import com.azure.android.communication.ui.calling.implementation.R
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.AudioState
import com.azure.android.communication.ui.calling.redux.state.CameraOperationalStatus
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
        // viewModel.turnCameraOnDefault()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.cameraIsEnabled.collect {
                cameraButton.isEnabled = it
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.cameraIsVisible.collect {
                cameraButton.visibility = if (it) VISIBLE else GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isVisibleState.collect { visible ->
                visibility = if (visible) VISIBLE else INVISIBLE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.audioOperationalStatusStat.collect {
                setMicButtonState(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.micIsEnabled.collect {
                micButton.isEnabled = it
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.cameraState.collect {
                setCameraButtonState(it)
                setButtonColorOnCameraState(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.audioDeviceSelectionStatusState.collect {
                setAudioDeviceButtonState(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.deviceIsEnabled.collect {
                audioDeviceButton.isEnabled = it
            }
        }
    }

    private fun setMicButtonState(audioOperationalStatus: AudioOperationalStatus) {
        when (audioOperationalStatus) {
            AudioOperationalStatus.ON -> {
                micButton.isON = true
                micButton.text = context.getString(R.string.azure_communication_ui_calling_setup_view_button_mic_on)
            }
            AudioOperationalStatus.OFF -> {
                micButton.isON = false
                micButton.text = context.getString(R.string.azure_communication_ui_calling_setup_view_button_mic_off)
            }
            else -> {}
        }
        micButton.refreshDrawableState()
    }

    private fun setCameraButtonState(operation: CameraOperationalStatus) {
        when (operation) {
            CameraOperationalStatus.ON -> {
                cameraButton.isON = true
                cameraButton.text = context.getString(R.string.azure_communication_ui_calling_setup_view_button_video_on)
            }
            CameraOperationalStatus.OFF -> {
                cameraButton.isON = false
                cameraButton.text = context.getString(R.string.azure_communication_ui_calling_setup_view_button_video_off)
            }
            else -> {}
        }
        cameraButton.refreshDrawableState()
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
        val color = if (!button.isEnabled) R.color.azure_communication_ui_calling_color_on_surface_disabled
        else if (button.isCameraON) R.color.azure_communication_ui_calling_color_on_surface_camera_active
        else R.color.azure_communication_ui_calling_color_on_surface

        button.compoundDrawables[1].colorFilter = PorterDuffColorFilter(
            ContextCompat.getColor(context, color),
            PorterDuff.Mode.SRC_IN
        )
    }

    private fun setAudioDeviceButtonState(audioState: AudioState) {
        audioDeviceButton.text = when (audioState.device) {
            AudioDeviceSelectionStatus.SPEAKER_SELECTED -> {
                context.getString(R.string.azure_communication_ui_calling_audio_device_drawer_speaker)
            }
            AudioDeviceSelectionStatus.RECEIVER_SELECTED -> {
                when (audioState.isHeadphonePlugged) {
                    true -> context.getString(R.string.azure_communication_ui_calling_audio_device_drawer_headphone)
                    false -> context.getString(R.string.azure_communication_ui_calling_audio_device_drawer_android)
                }
            }
            AudioDeviceSelectionStatus.BLUETOOTH_SCO_SELECTED -> {
                if (audioState.bluetoothState.deviceName.isNotBlank()) {
                    audioState.bluetoothState.deviceName
                } else {
                    context.getString(R.string.azure_communication_ui_calling_audio_device_drawer_bluetooth)
                }
            }
            else -> {
                ""
            }
        }

        audioDeviceButton.contentDescription =
            context.getString(
                R.string.azure_communication_ui_calling_setup_audio_device_select_content_description,
                audioDeviceButton.text
            )

        audioDeviceButton.isSpeakerON =
            audioState.device == AudioDeviceSelectionStatus.SPEAKER_SELECTED
        audioDeviceButton.isReceiverON =
            audioState.device == AudioDeviceSelectionStatus.RECEIVER_SELECTED
        audioDeviceButton.isBluetoothON =
            audioState.device == AudioDeviceSelectionStatus.BLUETOOTH_SCO_SELECTED

        audioDeviceButton.refreshDrawableState()
    }

    private fun toggleAudio() {
        if (micButton.isON) {
            viewModel.turnMicOff()
        } else {
            viewModel.turnMicOn()
        }
    }

    private fun toggleVideo() {
        if (cameraButton.isON) {
            viewModel.turnCameraOff()
        } else {
            viewModel.turnCameraOn()
        }
    }
}

internal open class SetupButton(context: Context, attrs: AttributeSet?) :
    AppCompatButton(context, attrs) {

    var isCameraON = false
    var isON = false

    override fun onCreateDrawableState(extraSpace: Int): IntArray? {
        val drawableState = super.onCreateDrawableState(extraSpace + 2)
        if (isCameraON) {
            mergeDrawableStates(
                drawableState,
                intArrayOf(R.attr.azure_communication_ui_calling_state_setup_camera_on)
            )
        }
        if (isON) {
            mergeDrawableStates(
                drawableState,
                intArrayOf(R.attr.azure_communication_ui_calling_state_on)
            )
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
        val drawableState = super.onCreateDrawableState(extraSpace + 3)
        if (isSpeakerON) {
            mergeDrawableStates(
                drawableState,
                intArrayOf(R.attr.azure_communication_ui_calling_state_setup_audio_device_speaker)
            )
        }
        if (isReceiverON) {
            mergeDrawableStates(
                drawableState,
                intArrayOf(R.attr.azure_communication_ui_calling_state_setup_audio_device_receiver)
            )
        }
        if (isBluetoothON) {
            mergeDrawableStates(
                drawableState,
                intArrayOf(R.attr.azure_communication_ui_calling_state_setup_audio_device_bluetooth)
            )
        }
        return drawableState
    }
}
