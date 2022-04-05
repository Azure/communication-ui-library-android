// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.setup

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.presentation.DependencyInjectionContainerHolder
import com.azure.android.communication.ui.presentation.fragment.common.audiodevicelist.AudioDeviceListView
import com.azure.android.communication.ui.presentation.fragment.setup.components.ErrorInfoView
import com.azure.android.communication.ui.presentation.fragment.setup.components.JoinCallButtonHolderView
import com.azure.android.communication.ui.presentation.fragment.setup.components.PermissionWarningView
import com.azure.android.communication.ui.presentation.fragment.setup.components.PreviewAreaView
import com.azure.android.communication.ui.presentation.fragment.setup.components.SetupControlBarView
import com.azure.android.communication.ui.presentation.fragment.setup.components.SetupGradientView
import com.azure.android.communication.ui.presentation.fragment.setup.components.SetupParticipantAvatarView
import com.azure.android.communication.ui.presentation.navigation.BackNavigation

internal class SetupFragment :
    Fragment(R.layout.azure_communication_ui_fragment_setup), BackNavigation {

    // Get the DI Container, which gives us what we need for this fragment (dependencies)
    private val holder: DependencyInjectionContainerHolder by activityViewModels()

    private lateinit var warningsView: PermissionWarningView
    private lateinit var setupControlsView: SetupControlBarView
    private lateinit var participantAvatarView: SetupParticipantAvatarView
    private lateinit var localParticipantRendererView: PreviewAreaView
    private lateinit var audioDeviceListView: AudioDeviceListView
    private lateinit var setupGradientView: SetupGradientView
    private lateinit var errorInfoView: ErrorInfoView
    private lateinit var setupJoinCallButtonHolderView: JoinCallButtonHolderView

    private val videoViewManager get() = holder.container.videoViewManager
    private val viewModel get() = holder.setupViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.init(viewLifecycleOwner.lifecycleScope)

        setActionBarTitle()

        setupGradientView = view.findViewById(R.id.azure_communication_ui_setup_gradient)
        setupGradientView.start(viewLifecycleOwner, viewModel.getSetupGradientViewViewModel())

        setupJoinCallButtonHolderView = view.findViewById(R.id.azure_communication_ui_setup_holder)
        setupJoinCallButtonHolderView.start(
            viewLifecycleOwner,
            viewModel.getJoinCallButtonHolderViewModel(),
        )

        participantAvatarView = view.findViewById(R.id.azure_communication_ui_setup_default_avatar)
        participantAvatarView.start(viewLifecycleOwner, viewModel.getParticipantAvatarViewModel())

        warningsView = view.findViewById(R.id.azure_communication_ui_setup_permission_info)
        warningsView.start(
            viewLifecycleOwner,
            viewModel.getWarningsViewModel(),
        )

        localParticipantRendererView =
            view.findViewById(R.id.azure_communication_ui_setup_local_video_holder)
        localParticipantRendererView.start(
            viewLifecycleOwner,
            viewModel.getLocalParticipantRendererViewModel(),
            videoViewManager,
        )

        audioDeviceListView =
            AudioDeviceListView(viewModel.getAudioDeviceListViewModel(), this.requireContext())
        audioDeviceListView.start(viewLifecycleOwner)

        setupControlsView = view.findViewById(R.id.azure_communication_ui_setup_buttons)
        setupControlsView.start(
            viewLifecycleOwner,
            viewModel.getSetupControlsViewModel()
        )

        errorInfoView = ErrorInfoView(view)
        errorInfoView.start(viewLifecycleOwner, viewModel.getErrorInfoViewModel())

        viewModel.setupCall()
    }

    override fun onDestroy() {
        super.onDestroy()
        audioDeviceListView.stop()
        errorInfoView.stop()
    }

    override fun onBackPressed() {
        viewModel.exitComposite()
    }

    private fun setActionBarTitle() {
        val mSpannableText = SpannableString(getString(R.string.azure_communication_ui_call_setup_action_bar_title))

        mSpannableText.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.azure_communication_ui_color_action_bar_text)),
            0,
            mSpannableText.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )

        activity?.title = mSpannableText
    }
}
