// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.setup.components

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.redux.state.PermissionStatus
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class SetupControlBarView : LinearLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var viewModel: SetupControlBarViewModel
    private lateinit var micButton: Button
    private lateinit var cameraButton: Button
    private lateinit var audioDeviceButton: Button

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
                setAudioButtonState(it)
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
                setAudioDeviceButtonState(it)
            }
        }
    }

    private fun setAudioButtonState(audioOperationalStatus: AudioOperationalStatus) {
        when (audioOperationalStatus) {
            AudioOperationalStatus.ON -> {
                setupAudioButton.isSelected = true
                setupAudioButton.isEnabled = true
                setupAudioButton.text =
                    context.getString(R.string.azure_communication_ui_setup_mic_on)
            }
            AudioOperationalStatus.OFF -> {
                setupAudioButton.isSelected = false
                setupAudioButton.isEnabled = true
                setupAudioButton.text =
                    context.getString(R.string.azure_communication_ui_setup_mic_off)
            }
            AudioOperationalStatus.PENDING -> {
                setupAudioButton.isEnabled = false
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
        if (viewModel.getCameraPermissionState().value == PermissionStatus.GRANTED) {
            val buttonColor = if (cameraOperationalStatus == CameraOperationalStatus.ON)
                R.color.azure_communication_ui_color_on_surface_camera_active
            else R.color.azure_communication_ui_toggle_selector

            if (cameraButton.isEnabled) {
                setButtonColor(cameraButton, buttonColor)
                setButtonColor(micButton, buttonColor)
                setButtonColor(audioDeviceButton, buttonColor)
            }
        }
    }

    private fun setAudioDeviceButtonState(audioDeviceSelectionStatus: AudioDeviceSelectionStatus) {
        var text = ""
        var drawable = R.drawable.azure_communication_ui_toggle_selector_mic_for_setup

        when (audioDeviceSelectionStatus) {
            AudioDeviceSelectionStatus.SPEAKER_SELECTED -> {
                text = context.getString(R.string.azure_communication_ui_setup_audio_device_speaker)
                drawable = R.drawable.azure_communication_ui_ic_fluent_speaker_2_24_filled_composite_button_enabled
            }
            AudioDeviceSelectionStatus.RECEIVER_SELECTED -> {
                text = context.getString(R.string.azure_communication_ui_setup_audio_device_android)
                drawable = R.drawable.azure_communication_ui_ic_fluent_speaker_2_24_regular_composite_button_filled
            }
            AudioDeviceSelectionStatus.BLUETOOTH_SCO_SELECTED -> {
                text = context.getString(R.string.azure_communication_ui_setup_audio_device_bluetooth)
                drawable = R.drawable.azure_communication_ui_ic_fluent_speaker_bluetooth_24_regular
            }
        }
        audioDeviceButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0, drawable,0,0)
        audioDeviceButton.text = text

        val audioDeviceButtonColor =
            if (cameraButton.isSelected) R.color.azure_communication_ui_color_on_surface_camera_active
            else R.color.azure_communication_ui_toggle_selector
        setButtonColor(audioDeviceButton, audioDeviceButtonColor)
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

    private fun setButtonColor(button: Button, colorId: Int) {
        button.setTextColor(ContextCompat.getColor(context, colorId))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            button.compoundDrawableTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, colorId))
        } else {
            button.compoundDrawables[1].colorFilter = PorterDuffColorFilter(
                ContextCompat.getColor(context, colorId),
                PorterDuff.Mode.SRC_IN
            )
        }
    }
}
