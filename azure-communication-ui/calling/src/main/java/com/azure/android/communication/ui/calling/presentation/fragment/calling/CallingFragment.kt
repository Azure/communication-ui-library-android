// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling

import android.content.Context
import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.util.DisplayMetrics
import android.view.View
import android.view.accessibility.AccessibilityManager
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.calling.CallCompositeInstanceManager
import com.azure.android.communication.ui.calling.implementation.R
import com.azure.android.communication.ui.calling.presentation.CallCompositeActivityViewModel
import com.azure.android.communication.ui.calling.presentation.MultitaskingCallCompositeActivity
import com.azure.android.communication.ui.calling.presentation.fragment.calling.banner.BannerView
import com.azure.android.communication.ui.calling.presentation.fragment.calling.captions.CaptionsRttView
import com.azure.android.communication.ui.calling.presentation.fragment.calling.connecting.overlay.ConnectingOverlayView
import com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.ControlBarView
import com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.captions.CaptionsLanguageSelectionListView
import com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.captions.CaptionsListView
import com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.more.MoreCallOptionsListView
import com.azure.android.communication.ui.calling.presentation.fragment.calling.hangup.LeaveConfirmView
import com.azure.android.communication.ui.calling.presentation.fragment.calling.header.InfoHeaderView
import com.azure.android.communication.ui.calling.presentation.fragment.calling.hold.OnHoldOverlayView
import com.azure.android.communication.ui.calling.presentation.fragment.calling.lobby.LobbyErrorHeaderView
import com.azure.android.communication.ui.calling.presentation.fragment.calling.lobby.LobbyHeaderView
import com.azure.android.communication.ui.calling.presentation.fragment.calling.lobby.WaitingLobbyOverlayView
import com.azure.android.communication.ui.calling.presentation.fragment.calling.localuser.LocalParticipantView
import com.azure.android.communication.ui.calling.presentation.fragment.calling.notification.ToastNotificationView
import com.azure.android.communication.ui.calling.presentation.fragment.calling.notification.UpperMessageBarNotificationLayoutView
import com.azure.android.communication.ui.calling.presentation.fragment.calling.participant.grid.ParticipantGridView
import com.azure.android.communication.ui.calling.presentation.fragment.calling.participant.menu.ParticipantMenuView
import com.azure.android.communication.ui.calling.presentation.fragment.calling.participantlist.ParticipantListView
import com.azure.android.communication.ui.calling.presentation.fragment.common.audiodevicelist.AudioDeviceListView
import com.azure.android.communication.ui.calling.presentation.fragment.setup.components.ErrorInfoView
import com.azure.android.communication.ui.calling.utilities.convertDpToPx
import com.azure.android.communication.ui.calling.utilities.hideKeyboard
import com.azure.android.communication.ui.calling.utilities.isKeyboardOpen
import com.azure.android.communication.ui.calling.utilities.isTablet
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class CallingFragment :
    Fragment(R.layout.azure_communication_ui_calling_call_fragment), SensorEventListener {
    companion object {
        private const val LEAVE_CONFIRM_VIEW_KEY = "LeaveConfirmView"
        private const val AUDIO_DEVICE_LIST_VIEW_KEY = "AudioDeviceListView"
        private const val PARTICIPANT_LIST_VIEW_KEY = "ParticipantListView"
        private const val CAPTIONS_TABLET_WIDTH = 0.45f
        private const val CAPTIONS_TABLET_WIDTH_LANDSCAPE = 0.33f
        private const val CAPTIONS_ANIMATION_DURATION = 100L
        private const val CAPTIONS_BOTTOM_ANCHOR_HEIGHT = 150
        private const val CAPTIONS_BOTTOM_ANCHOR_HEIGHT_HIDE = 0
        const val MAX_CAPTIONS_DATA_SIZE = 50
        const val MAX_CAPTIONS_PARTIAL_DATA_TIME_LIMIT = 5000
    }

    // Get the DI Container, which gives us what we need for this fragment (dependencies)
    private val activityViewModel: CallCompositeActivityViewModel by activityViewModels()

    private val videoViewManager get() = activityViewModel.container.videoViewManager
    private val avatarViewManager get() = activityViewModel.container.avatarViewManager
    private val viewModel get() = activityViewModel.callingViewModel

    private val closeToUser = 0f
    private lateinit var callScreenLayout: ConstraintLayout
    private lateinit var controlBarView: ControlBarView
    private lateinit var confirmLeaveOverlayView: LeaveConfirmView
    private lateinit var localParticipantView: LocalParticipantView
    private lateinit var infoHeaderView: InfoHeaderView
    private lateinit var upperMessageBarNotificationLayoutView: UpperMessageBarNotificationLayoutView
    private lateinit var toastNotificationView: ToastNotificationView
    private lateinit var participantGridView: ParticipantGridView
    private lateinit var audioDeviceListView: AudioDeviceListView
    private lateinit var participantListView: ParticipantListView
    private lateinit var participantMenuView: ParticipantMenuView
    private lateinit var bannerView: BannerView
    private lateinit var errorInfoView: ErrorInfoView
    private lateinit var waitingLobbyOverlay: WaitingLobbyOverlayView
    private lateinit var connectingLobbyOverlay: ConnectingOverlayView
    private lateinit var holdOverlay: OnHoldOverlayView
    private lateinit var sensorManager: SensorManager
    private lateinit var powerManager: PowerManager
    private lateinit var accessibilityManager: AccessibilityManager
    private lateinit var wakeLock: PowerManager.WakeLock
    private lateinit var moreCallOptionsListView: MoreCallOptionsListView
    private lateinit var lobbyHeaderView: LobbyHeaderView
    private lateinit var lobbyErrorHeaderView: LobbyErrorHeaderView
    private lateinit var captionsListView: CaptionsListView
    private lateinit var captionsLanguageSelectionListView: CaptionsLanguageSelectionListView
    private lateinit var captionsWrapper: View
    private lateinit var captionsRttView: CaptionsRttView
    private lateinit var captionsTopAnchor: View
    private lateinit var captionsBottomAnchor: View
    private lateinit var captionsOverlay: View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.init(viewLifecycleOwner.lifecycleScope)

        callScreenLayout = view.findViewById(R.id.azure_communication_ui_calling_call_frame_layout)

        confirmLeaveOverlayView =
            LeaveConfirmView(viewModel.confirmLeaveOverlayViewModel, this.requireContext())
        confirmLeaveOverlayView.layoutDirection =
            activity?.window?.decorView?.layoutDirection ?: View.LAYOUT_DIRECTION_LOCALE
        confirmLeaveOverlayView.start(
            viewLifecycleOwner
        )

        controlBarView = view.findViewById(R.id.azure_communication_ui_call_call_buttons)
        controlBarView.start(viewLifecycleOwner, viewModel.controlBarViewModel)

        participantGridView =
            view.findViewById(R.id.azure_communication_ui_call_participant_container)
        participantGridView.start(
            viewModel.participantGridViewModel,
            videoViewManager,
            viewLifecycleOwner,
            this::switchFloatingHeader,
            avatarViewManager
        )

        connectingLobbyOverlay = view.findViewById(R.id.azure_communication_ui_call_connecting_lobby_overlay)
        connectingLobbyOverlay.start(viewLifecycleOwner, viewModel.connectingLobbyOverlayViewModel)

        waitingLobbyOverlay = view.findViewById(R.id.azure_communication_ui_call_lobby_overlay)
        waitingLobbyOverlay.start(viewLifecycleOwner, viewModel.waitingLobbyOverlayViewModel)

        holdOverlay = view.findViewById(R.id.azure_communication_ui_call_hold_overlay)
        holdOverlay.start(viewLifecycleOwner, viewModel.holdOverlayViewModel)

        localParticipantView = view.findViewById(R.id.azure_communication_ui_call_local_user_view)
        localParticipantView.start(
            viewLifecycleOwner,
            viewModel.localParticipantViewModel,
            videoViewManager,
            avatarViewManager,
        )

        accessibilityManager =
            context?.applicationContext?.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        infoHeaderView = view.findViewById(R.id.azure_communication_ui_call_floating_header)
        infoHeaderView.start(
            viewLifecycleOwner,
            viewModel.floatingHeaderViewModel,
            this::displayParticipantList,
        )
        lobbyHeaderView = view.findViewById(R.id.azure_communication_ui_calling_lobby_header)
        lobbyHeaderView.start(
            viewLifecycleOwner,
            viewModel.lobbyHeaderViewModel,
            this::displayParticipantList
        )

        lobbyErrorHeaderView = view.findViewById(R.id.azure_communication_ui_calling_lobby_error_header)
        lobbyErrorHeaderView.start(
            viewLifecycleOwner,
            viewModel.lobbyErrorHeaderViewModel
        )

        upperMessageBarNotificationLayoutView = view.findViewById(R.id.azure_communication_ui_calling_upper_message_bar_notifications_layout)
        upperMessageBarNotificationLayoutView.start(
            viewLifecycleOwner,
            viewModel.upperMessageBarNotificationLayoutViewModel,
        )

        toastNotificationView = view.findViewById(R.id.azure_communication_ui_calling_toast_notification)
        toastNotificationView.start(
            viewLifecycleOwner,
            viewModel.toastNotificationViewModel,
            accessibilityManager.isEnabled
        )

        audioDeviceListView =
            AudioDeviceListView(viewModel.audioDeviceListViewModel, this.requireContext())
        audioDeviceListView.layoutDirection =
            activity?.window?.decorView?.layoutDirection ?: View.LAYOUT_DIRECTION_LOCALE
        audioDeviceListView.start(viewLifecycleOwner)

        participantListView = ParticipantListView(
            viewModel.participantListViewModel,
            this.requireContext(),
            avatarViewManager,
        )
        participantListView.layoutDirection =
            activity?.window?.decorView?.layoutDirection ?: View.LAYOUT_DIRECTION_LOCALE
        participantListView.start(viewLifecycleOwner)

        participantMenuView = ParticipantMenuView(
            this.requireContext(),
            viewModel.participantMenuViewModel,
        )
        participantMenuView.layoutDirection =
            activity?.window?.decorView?.layoutDirection ?: View.LAYOUT_DIRECTION_LOCALE
        participantMenuView.start(viewLifecycleOwner)

        bannerView = view.findViewById(R.id.azure_communication_ui_call_banner)
        bannerView.start(
            viewModel.bannerViewModel,
            viewLifecycleOwner,
        )
        participantGridView.setOnClickListener {
            switchFloatingHeader()
        }

        errorInfoView = ErrorInfoView(view)
        errorInfoView.start(viewLifecycleOwner, viewModel.errorInfoViewModel)

        moreCallOptionsListView = MoreCallOptionsListView(
            this.requireContext(),
            viewModel.moreCallOptionsListViewModel
        )
        moreCallOptionsListView.layoutDirection =
            activity?.window?.decorView?.layoutDirection ?: View.LAYOUT_DIRECTION_LOCALE
        moreCallOptionsListView.start(viewLifecycleOwner)

        captionsListView = CaptionsListView(
            context = this.requireContext(),
            viewModel = viewModel.captionsListViewModel,
        )
        captionsListView.layoutDirection =
            activity?.window?.decorView?.layoutDirection ?: View.LAYOUT_DIRECTION_LOCALE
        captionsListView.start(viewLifecycleOwner)

        captionsLanguageSelectionListView = CaptionsLanguageSelectionListView(
            context = this.requireContext(),
            viewModel = viewModel.captionsLanguageSelectionListViewModel
        )
        captionsLanguageSelectionListView.layoutDirection =
            activity?.window?.decorView?.layoutDirection ?: View.LAYOUT_DIRECTION_LOCALE
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val halfScreenHeight = displayMetrics.heightPixels / 2
        captionsLanguageSelectionListView.start(viewLifecycleOwner, halfScreenHeight)

        captionsTopAnchor = view.findViewById(R.id.captions_top_anchor)
        captionsBottomAnchor = view.findViewById(R.id.captions_bottom_anchor)
        captionsWrapper = view.findViewById(R.id.azure_communication_ui_calling_captions_view_wrapper)
        captionsOverlay = view.findViewById(R.id.azure_communication_ui_calling_captions_overlay)
        captionsRttView = view.findViewById(R.id.azure_communication_ui_calling_captions_linear_layout)

        captionsRttView.start(
            viewLifecycleOwner = viewLifecycleOwner,
            viewModel = viewModel.captionsLayoutViewModel,
            maximizeCallback = this::maximizeCaptions,
            minimizeCallback = this::minimizeCaptions
        )

        captionsOverlay.setOnClickListener { viewModel.minimizeCaptions() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireActivity().onBackInvokedDispatcher.registerOnBackInvokedCallback(1000, ::onBackPressed)
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            onBackPressed()
        }
    }

    private fun onBackPressed() {

        // If captions are maximized, minimize them.
        if (viewModel.isCaptionsMaximized) {
            viewModel.minimizeCaptions()
            return
        }

        // On some devices the close keyboard button is triggering back button.
        // If keyboard was open, we should just close it.
        if (activity?.isKeyboardOpen() == true) {
            activity?.hideKeyboard()
            return
        }

        if (viewModel.multitaskingEnabled) {
            (activity as? MultitaskingCallCompositeActivity)?.hide()
        } else {
            viewModel.requestCallEndOnBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager =
            context?.applicationContext?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        powerManager =
            context?.applicationContext?.getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock =
            powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, javaClass.name)

        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
            SensorManager.SENSOR_DELAY_NORMAL
        )

        context?.let { context ->
            if (isTablet(context)) {
                val isLandScape =
                    resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
                val captionsWrapperLayout =
                    captionsWrapper.layoutParams as ConstraintLayout.LayoutParams
                captionsWrapperLayout.matchConstraintPercentWidth =
                    if (isLandScape) CAPTIONS_TABLET_WIDTH_LANDSCAPE else CAPTIONS_TABLET_WIDTH
                captionsWrapper.layoutParams = captionsWrapperLayout
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.isCaptionsVisibleFlow.collect {
                    val height = if (it) CAPTIONS_BOTTOM_ANCHOR_HEIGHT else CAPTIONS_BOTTOM_ANCHOR_HEIGHT_HIDE
                    val layoutParams = captionsBottomAnchor.layoutParams
                    layoutParams.height = context.convertDpToPx(height).toInt()
                    captionsBottomAnchor.layoutParams = layoutParams

                    captionsWrapper.isVisible = it
                }
            }
        }

        captionsTopAnchor.post {
            calculateAndSetCaptionsLayoutMaxHeight()
        }
    }

    override fun onPause() {
        super.onPause()
        if (this::wakeLock.isInitialized) {
            if (wakeLock.isHeld) {
                wakeLock.release()
            }
        }
        if (this::sensorManager.isInitialized) sensorManager.unregisterListener(this)
    }

    override fun onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireActivity().onBackInvokedDispatcher.unregisterOnBackInvokedCallback(::onBackPressed)
        }
        super.onDestroy()
        if (activity?.isChangingConfigurations == false) {
            if (this::participantGridView.isInitialized) participantGridView.stop()
            if (CallCompositeInstanceManager.hasCallComposite(activityViewModel.instanceId)) {
                // Covers edge case where Android tries to recreate call activity after process death
                // (e.g. due to revoked permission).
                // If no configs are detected we can just exit without cleanup.
                viewModel.bannerViewModel.dismissBanner()
            }
        }
        if (this::localParticipantView.isInitialized) localParticipantView.stop()
        if (this::participantListView.isInitialized) participantListView.stop()
        if (this::audioDeviceListView.isInitialized) audioDeviceListView.stop()
        if (this::confirmLeaveOverlayView.isInitialized) confirmLeaveOverlayView.stop()
        if (this::holdOverlay.isInitialized) holdOverlay.stop()
        if (this::errorInfoView.isInitialized) errorInfoView.stop()
        if (this::moreCallOptionsListView.isInitialized) moreCallOptionsListView.stop()
        if (this::upperMessageBarNotificationLayoutView.isInitialized) upperMessageBarNotificationLayoutView.stop()
        if (this::toastNotificationView.isInitialized) toastNotificationView.stop()
        if (this::captionsListView.isInitialized) captionsListView.stop()
        if (this::captionsLanguageSelectionListView.isInitialized) captionsLanguageSelectionListView.stop()
        if (this::captionsRttView.isInitialized) captionsRttView.stop()
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent) {
        if (activity?.isKeyboardOpen() == true) {
            return
        }
        if (event.sensor.type == Sensor.TYPE_PROXIMITY) {
            if (event.values[0] == closeToUser) {
                if (!wakeLock.isHeld) {
                    wakeLock.acquire()
                }
            } else {
                if (wakeLock.isHeld) {
                    wakeLock.release()
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(LEAVE_CONFIRM_VIEW_KEY, viewModel.confirmLeaveOverlayViewModel.getShouldDisplayLeaveConfirmFlow().value)
        outState.putBoolean(AUDIO_DEVICE_LIST_VIEW_KEY, viewModel.audioDeviceListViewModel.displayAudioDeviceSelectionMenuStateFlow.value)
        outState.putBoolean(PARTICIPANT_LIST_VIEW_KEY, viewModel.participantListViewModel.participantListContentStateFlow.value.isDisplayed)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        savedInstanceState?.let {
            mapOf(
                LEAVE_CONFIRM_VIEW_KEY to viewModel.confirmLeaveOverlayViewModel::requestExitConfirmation,
                AUDIO_DEVICE_LIST_VIEW_KEY to viewModel.audioDeviceListViewModel::displayAudioDeviceSelectionMenu,
                PARTICIPANT_LIST_VIEW_KEY to viewModel.participantListViewModel::displayParticipantList
            ).forEach { (key, showDialog) -> if (it.getBoolean(key)) showDialog() }
        }
    }

    private fun displayParticipantList() {
        viewModel.participantListViewModel.displayParticipantList()
    }

    private fun switchFloatingHeader() {
        viewModel.switchFloatingHeader()
    }

    private fun maximizeCaptions() {
        captionsOverlay.isVisible = true
        updateConstraintTopTo(R.id.captions_top_anchor, ConstraintSet.BOTTOM)
    }

    private fun minimizeCaptions() {
        captionsOverlay.isVisible = false
        updateConstraintTopTo(R.id.captions_bottom_anchor, ConstraintSet.TOP)
    }

    private fun updateConstraintTopTo(
        targetViewId: Int,
        constraint: Int,
    ) {
        val nestedViewId = R.id.azure_communication_ui_calling_captions_view_wrapper
        val constraintSet = ConstraintSet()
        constraintSet.clone(callScreenLayout)
        constraintSet.clear(nestedViewId, ConstraintSet.TOP)
        constraintSet.connect(nestedViewId, ConstraintSet.TOP, targetViewId, constraint)

        val animationDuration: Long = CAPTIONS_ANIMATION_DURATION
        val transition = ChangeBounds()
        transition.duration = animationDuration
        TransitionManager.beginDelayedTransition(callScreenLayout, transition)

        constraintSet.applyTo(callScreenLayout)
    }

    private fun calculateAndSetCaptionsLayoutMaxHeight() {
        val location = IntArray(2)
        captionsTopAnchor.getLocationOnScreen(location)
        val captionsTopAnchorBottomY = location[1] + captionsTopAnchor.height

        captionsBottomAnchor.getLocationOnScreen(location)
        val captionsBottomAnchorBottomY = location[1] + captionsBottomAnchor.height

        captionsRttView.maxHeight = captionsBottomAnchorBottomY - captionsTopAnchorBottomY
    }
}
