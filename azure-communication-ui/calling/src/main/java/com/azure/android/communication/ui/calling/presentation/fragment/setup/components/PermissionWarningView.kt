// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.setup.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.calling.implementation.R
import com.azure.android.communication.ui.calling.redux.state.PermissionStatus
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class PermissionWarningView : LinearLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private var cameraPermissionGranted: Boolean = true
    private var micPermissionGranted: Boolean = true
    private lateinit var viewModel: PermissionWarningViewModel
    private lateinit var setupPermissionsHolder: LinearLayout
    private lateinit var setupMissingImage: ImageView
    private lateinit var setupMissingText: AppCompatTextView
    private lateinit var setupSettingsButton: Button

    override fun onFinishInflate() {
        super.onFinishInflate()
        setupPermissionsHolder =
            findViewById(R.id.azure_communication_ui_setup_permission_info)
        setupMissingImage =
            findViewById(R.id.azure_communication_ui_setup_missing_image)
        setupMissingText =
            findViewById(R.id.azure_communication_ui_setup_missing_text)
        setupSettingsButton =
            findViewById(R.id.azure_communication_ui_setup_settings_button)

        setupSettingsButton.setOnClickListener {
            openSettings()
        }
    }

    fun start(
        viewLifecycleOwner: LifecycleOwner,
        viewModel: PermissionWarningViewModel,
    ) {
        this.viewModel = viewModel
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.cameraPermissionStateFlow.collect {
                onCameraPermissionStateUpdated(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.audioPermissionStateFlow.collect {
                onMicPermissionStateUpdated(it)
            }
        }
    }

    private fun onCameraPermissionStateUpdated(permissionState: PermissionStatus) {
        if (permissionState == PermissionStatus.DENIED) {
            cameraPermissionGranted = false
        } else if (permissionState == PermissionStatus.GRANTED) {
            cameraPermissionGranted = true
        }
        updateSetupPermissionHolder()
    }

    private fun onMicPermissionStateUpdated(permissionState: PermissionStatus) {
        if (permissionState == PermissionStatus.DENIED) {
            micPermissionGranted = false
        } else if (permissionState == PermissionStatus.GRANTED) {
            micPermissionGranted = true
        }
        updateSetupPermissionHolder()
    }

    private fun updateSetupPermissionHolder() {
        if (cameraPermissionGranted && micPermissionGranted) {
            setupPermissionsHolder.visibility = View.GONE
        } else if (!micPermissionGranted) {
            setupPermissionsHolder.visibility = View.VISIBLE
            setupMissingImage.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.azure_communication_ui_calling_ic_fluent_mic_off_24_filled_composite_button_enabled
                )
            )
            setupMissingText.setText(context.getString(R.string.azure_communication_ui_calling_setup_view_preview_area_audio_disabled))
        } else if (!cameraPermissionGranted) {
            setupPermissionsHolder.visibility = View.VISIBLE
            setupMissingImage.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.azure_communication_ui_calling_ic_fluent_video_off_24_filled_composite_button_enabled
                )
            )
            setupMissingText.setText(context.getString(R.string.azure_communication_ui_calling_setup_view_preview_area_camera_disabled))
        }
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        context.startActivity(intent)
    }
}
