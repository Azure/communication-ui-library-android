// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.setup

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.LayoutDirection
import android.view.View
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.calling.presentation.DependencyInjectionContainerHolder
import com.azure.android.communication.ui.calling.presentation.fragment.common.audiodevicelist.AudioDeviceListView
import com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.more.ControlBarMoreMenuView
import com.azure.android.communication.ui.calling.presentation.fragment.setup.components.ErrorInfoView
import com.azure.android.communication.ui.calling.presentation.fragment.setup.components.JoinCallButtonHolderView
import com.azure.android.communication.ui.calling.presentation.fragment.setup.components.PermissionWarningView
import com.azure.android.communication.ui.calling.presentation.fragment.setup.components.PreviewAreaView
import com.azure.android.communication.ui.calling.presentation.fragment.setup.components.SetupControlBarView
import com.azure.android.communication.ui.calling.presentation.fragment.setup.components.SetupGradientView
import com.azure.android.communication.ui.calling.presentation.fragment.setup.components.SetupParticipantAvatarView
import com.azure.android.communication.ui.calling.presentation.navigation.BackNavigation

internal class SetupFragment :
    Fragment(R.layout.azure_communication_ui_calling_fragment_setup), BackNavigation {

    // Get the DI Container, which gives us what we need for this fragment (dependencies)
    private val holder: DependencyInjectionContainerHolder by activityViewModels()

    private lateinit var warningsView: PermissionWarningView
    private lateinit var setupControlsView: SetupControlBarView
    private lateinit var participantAvatarView: SetupParticipantAvatarView
    private lateinit var localParticipantRendererView: PreviewAreaView
    private lateinit var audioDeviceListView: AudioDeviceListView
    private lateinit var controlBarMoreMenuView: ControlBarMoreMenuView
    private lateinit var setupGradientView: SetupGradientView
    private lateinit var errorInfoView: ErrorInfoView
    private lateinit var setupJoinCallButtonHolderView: JoinCallButtonHolderView

    private val videoViewManager get() = holder.container.videoViewManager
    private val avatarViewManager get() = holder.container.avatarViewManager
    private val networkManager get() = holder.container.networkManager
    private val viewModel get() = holder.setupViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.init(viewLifecycleOwner.lifecycleScope)

        setActionBarTitle()

        setupGradientView = view.findViewById(R.id.azure_communication_ui_setup_gradient)
        setupGradientView.start(viewLifecycleOwner, viewModel.setupGradientViewModel)

        setupJoinCallButtonHolderView =
            view.findViewById(R.id.azure_communication_ui_setup_join_call_holder)
        setupJoinCallButtonHolderView.start(
            viewLifecycleOwner,
            viewModel.joinCallButtonHolderViewModel,
            networkManager
        )

        participantAvatarView = view.findViewById(R.id.azure_communication_ui_setup_default_avatar)
        participantAvatarView.start(
            viewLifecycleOwner,
            viewModel.participantAvatarViewModel,
            avatarViewManager.callCompositeLocalOptions?.participantViewData,
        )

        warningsView = view.findViewById(R.id.azure_communication_ui_setup_permission_info)
        warningsView.start(
            viewLifecycleOwner,
            viewModel.warningsViewModel,
        )

        localParticipantRendererView =
            view.findViewById(R.id.azure_communication_ui_setup_local_video_holder)
        localParticipantRendererView.start(
            viewLifecycleOwner,
            viewModel.localParticipantRendererViewModel,
            videoViewManager,
        )

        audioDeviceListView =
            AudioDeviceListView(viewModel.audioDeviceListViewModel, this.requireContext())
        audioDeviceListView.layoutDirection =
            activity?.window?.decorView?.layoutDirection ?: LayoutDirection.LOCALE
        audioDeviceListView.start(viewLifecycleOwner)

        controlBarMoreMenuView = ControlBarMoreMenuView(
            this.requireContext(),
            viewModel.controlBarMoreMenuViewModel
        )
        controlBarMoreMenuView.layoutDirection =
            activity?.window?.decorView?.layoutDirection ?: LayoutDirection.LOCALE
        controlBarMoreMenuView.start(viewLifecycleOwner)

        setupControlsView = view.findViewById(R.id.azure_communication_ui_setup_buttons)
        setupControlsView.start(
            viewLifecycleOwner,
            viewModel.setupControlsViewModel,
        )

        errorInfoView = ErrorInfoView(view)
        errorInfoView.start(viewLifecycleOwner, viewModel.errorInfoViewModel)

        viewModel.setupCall()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::audioDeviceListView.isInitialized) audioDeviceListView.stop()
        if (this::controlBarMoreMenuView.isInitialized) controlBarMoreMenuView.stop()
        if (this::errorInfoView.isInitialized) errorInfoView.stop()
    }

    override fun onBackPressed() {
        viewModel.exitComposite()
    }

    val callCompositeActivity
        get() = (activity as AppCompatActivity)

    private fun setActionBarTitle() {
        fun setActionbarTextColor(text: SpannableString, @ColorInt color: Int) {
            text.setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(
                        requireContext(),
                        color
                    )
                ),
                0,
                text.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
        }

        val localOptions = holder.container.configuration.callCompositeLocalOptions
        val titleSpan = if (!TextUtils.isEmpty(localOptions?.navigationBarViewData?.title)) {
            SpannableString(localOptions?.navigationBarViewData?.title)
        } else {
            SpannableString(getString(R.string.azure_communication_ui_calling_call_setup_action_bar_title))
        }

        setActionbarTextColor(titleSpan, R.color.azure_communication_ui_calling_color_action_bar_text)

        callCompositeActivity.supportActionBar?.title = titleSpan

        // Only set the subTitle if the title has also been set
        if (!TextUtils.isEmpty(localOptions?.navigationBarViewData?.title) &&
            !TextUtils.isEmpty(localOptions?.navigationBarViewData?.subtitle)
        ) {
            val subtitleSpan = SpannableString(localOptions?.navigationBarViewData?.subtitle)
            setActionbarTextColor(subtitleSpan, R.color.azure_communication_ui_calling_color_action_bar_subtext)
            callCompositeActivity.supportActionBar?.subtitle = subtitleSpan
        }
    }
}
