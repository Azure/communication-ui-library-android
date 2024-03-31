// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.setup

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.LayoutDirection
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.calling.implementation.R
import com.azure.android.communication.ui.calling.presentation.DependencyInjectionContainerHolder
import com.azure.android.communication.ui.calling.presentation.fragment.common.audiodevicelist.AudioDeviceListView
import com.azure.android.communication.ui.calling.presentation.fragment.setup.components.ErrorInfoView
import com.azure.android.communication.ui.calling.presentation.fragment.setup.components.JoinCallButtonHolderView
import com.azure.android.communication.ui.calling.presentation.fragment.setup.components.PermissionWarningView
import com.azure.android.communication.ui.calling.presentation.fragment.setup.components.PreviewAreaView
import com.azure.android.communication.ui.calling.presentation.fragment.setup.components.SetupControlBarView
import com.azure.android.communication.ui.calling.presentation.fragment.setup.components.SetupGradientView
import com.azure.android.communication.ui.calling.presentation.fragment.setup.components.SetupParticipantAvatarView

internal class SetupFragment :
    Fragment(R.layout.azure_communication_ui_calling_fragment_setup) {

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
    private lateinit var toolbar: Toolbar
    private lateinit var navigationButton: ImageButton
    private lateinit var toolbarTitle: TextView
    private lateinit var toolbarSubtitle: TextView

    private val videoViewManager get() = holder.container.videoViewManager
    private val avatarViewManager get() = holder.container.avatarViewManager
    private val networkManager get() = holder.container.networkManager
    private val viewModel get() = holder.setupViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.init(viewLifecycleOwner.lifecycleScope)
        toolbar = view.findViewById(R.id.azure_communication_setup_toolbar)
        callCompositeActivity?.setSupportActionBar(toolbar)

        navigationButton = view.findViewById<ImageButton>(R.id.navigation_button)
        navigationButton.setOnClickListener {
            callCompositeActivity.finish()
        }

        toolbarTitle = view.findViewById(R.id.toolbar_title)
        toolbarSubtitle = view.findViewById(R.id.toolbar_subtitle)
        setActionBarTitle()

        setupGradientView = view.findViewById(R.id.azure_communication_ui_setup_gradient)
        setupGradientView.start(viewLifecycleOwner, viewModel.setupGradientViewModel)

        setupJoinCallButtonHolderView =
            view.findViewById(R.id.azure_communication_ui_setup_join_call_holder)
        setupJoinCallButtonHolderView.start(
            viewLifecycleOwner,
            viewModel.joinCallButtonHolderViewModel
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

        setupControlsView = view.findViewById(R.id.azure_communication_ui_setup_buttons)
        setupControlsView.start(
            viewLifecycleOwner,
            viewModel.setupControlBarViewModel,
        )

        errorInfoView = ErrorInfoView(view)
        errorInfoView.start(viewLifecycleOwner, viewModel.errorInfoViewModel)

        viewModel.setupCall()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireActivity().onBackInvokedDispatcher.registerOnBackInvokedCallback(1000, viewModel::exitComposite)
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            viewModel.exitComposite()
        }
    }

    override fun onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireActivity().onBackInvokedDispatcher.unregisterOnBackInvokedCallback(viewModel::exitComposite)
        }

        super.onDestroy()
        if (this::audioDeviceListView.isInitialized) audioDeviceListView.stop()
        if (this::errorInfoView.isInitialized) errorInfoView.stop()
    }

    val callCompositeActivity
        get() = (activity as AppCompatActivity)

    private fun setActionBarTitle() {

        val localOptions = holder.container.configuration.callCompositeLocalOptions
        val titleText = if (!TextUtils.isEmpty(localOptions?.setupScreenViewData?.title)) {
            localOptions?.setupScreenViewData?.title
        } else {
            getString(R.string.azure_communication_ui_calling_call_setup_action_bar_title)
        }

        toolbarTitle.text = titleText
        toolbarTitle.contentDescription = titleText + "Title"

        // Only set the subtitle if the title has also been set
        if (!TextUtils.isEmpty(localOptions?.setupScreenViewData?.subtitle)) {
            if (!TextUtils.isEmpty(localOptions?.setupScreenViewData?.title)) {
                val subtitleText = localOptions?.setupScreenViewData?.subtitle
                toolbarSubtitle.visibility = View.VISIBLE
                toolbarSubtitle.text = subtitleText
                toolbarSubtitle.contentDescription = subtitleText + "Subtitle"
            } else {
                holder.container.logger.error(
                    "Provided setupScreenViewData has subtitle, but no title provided. In this case subtitle is not displayed."
                )
            }
        }
    }
}
