// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.setup.components

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.redux.state.AudioOperationalStatus
import com.azure.android.communication.ui.redux.state.AudioState
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
                setAudioDeviceButtonState(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getDeviceIsEnabled().collect {
                audioDeviceButton.isEnabled = it
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
                micButton.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.azure_communication_ui_ic_fluent_mic_on_24_filled_composite_button_enabled
                    ),
                    null,
                    null
                )
                micButton.text =
                    getLocalizedString(R.string.azure_communication_ui_setup_view_button_mic_on)
            }
            AudioOperationalStatus.OFF -> {
                micButton.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.azure_communication_ui_ic_fluent_mic_off_24_filled_composite_button_enabled
                    ),
                    null,
                    null
                )
                micButton.text =
                    getLocalizedString(R.string.azure_communication_ui_setup_view_button_mic_off)
            }
        }
    }

    private fun setCameraButtonState(operation: CameraOperationalStatus) {
        when (operation) {
            CameraOperationalStatus.ON -> {
                cameraButton.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.azure_communication_ui_ic_fluent_video_24_filled_composite_button_enabled
                    ),
                    null,
                    null
                )
                cameraButton.text =
                    getLocalizedString(R.string.azure_communication_ui_setup_view_button_video_on)
            }
            CameraOperationalStatus.OFF -> {
                cameraButton.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.azure_communication_ui_ic_fluent_video_off_24_filled_composite_button_enabled
                    ),
                    null,
                    null
                )
                cameraButton.text =
                    getLocalizedString(R.string.azure_communication_ui_setup_view_button_video_off)
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

    private fun setAudioDeviceButtonState(audioState: AudioState) {
        audioDeviceButton.text = when (audioState.device) {
            AudioDeviceSelectionStatus.SPEAKER_SELECTED -> {
                getLocalizedString(R.string.azure_communication_ui_audio_device_drawer_speaker)
            }
            AudioDeviceSelectionStatus.RECEIVER_SELECTED -> {
                when (audioState.isHeadphonePlugged) {
                    true -> getLocalizedString(R.string.azure_communication_ui_audio_device_drawer_headphone)
                    false -> getLocalizedString(R.string.azure_communication_ui_audio_device_drawer_android)
                }
            }
            AudioDeviceSelectionStatus.BLUETOOTH_SCO_SELECTED -> {
                if (audioState.bluetoothState.deviceName.isNotBlank()) {
                    audioState.bluetoothState.deviceName
                } else {
                    getLocalizedString(R.string.azure_communication_ui_audio_device_drawer_bluetooth)
                }
            }
            else -> {
                ""
            }
        }

        audioDeviceButton.isSpeakerON =
            audioState.device == AudioDeviceSelectionStatus.SPEAKER_SELECTED
        audioDeviceButton.isReceiverON =
            audioState.device == AudioDeviceSelectionStatus.RECEIVER_SELECTED
        audioDeviceButton.isBluetoothON =
            audioState.device == AudioDeviceSelectionStatus.BLUETOOTH_SCO_SELECTED

        audioDeviceButton.refreshDrawableState()
    }

    private fun toggleAudio() {
        if (viewModel.getAudioOperationalStatusStateFlow().value == AudioOperationalStatus.ON) {
            viewModel.turnMicOff()
        } else {
            viewModel.turnMicOn()
        }
    }

    private fun toggleVideo() {
        if (viewModel.getCameraState().value == CameraOperationalStatus.ON) {
            viewModel.turnCameraOff()
        } else {
            viewModel.turnCameraOn()
        }
    }

    private fun getLocalizedString(stringId: Int): String {
        return viewModel.getLocalizationProvider().getLocalizedString(context, stringId)
    }
}

internal open class SetupButton(context: Context, attrs: AttributeSet?) :
    AppCompatButton(context, attrs) {

    var isCameraON = false

    override fun onCreateDrawableState(extraSpace: Int): IntArray? {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isCameraON) {
            mergeDrawableStates(
                drawableState,
                intArrayOf(R.attr.azure_communication_ui_state_setup_camera_on)
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
        val drawableState = super.onCreateDrawableState(extraSpace + 4)
        if (isSpeakerON) {
            mergeDrawableStates(
                drawableState,
                intArrayOf(R.attr.azure_communication_ui_state_setup_audio_device_speaker)
            )
        }
        if (isReceiverON) {
            mergeDrawableStates(
                drawableState,
                intArrayOf(R.attr.azure_communication_ui_state_setup_audio_device_receiver)
            )
        }
        if (isBluetoothON) {
            mergeDrawableStates(
                drawableState,
                intArrayOf(R.attr.azure_communication_ui_state_setup_audio_device_bluetooth)
            )
        }
        return drawableState
    }
}
