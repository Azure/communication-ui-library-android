// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.PowerManager
import android.util.LayoutDirection
import android.view.View
import android.view.accessibility.AccessibilityManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.calling.CallCompositeInstanceManager
import com.azure.android.communication.ui.calling.presentation.DependencyInjectionContainerHolder
import com.azure.android.communication.ui.calling.presentation.fragment.calling.banner.BannerView
import com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.ControlBarView
import com.azure.android.communication.ui.calling.presentation.fragment.calling.hangup.LeaveConfirmView
import com.azure.android.communication.ui.calling.presentation.fragment.calling.header.InfoHeaderView
import com.azure.android.communication.ui.calling.presentation.fragment.calling.hold.OnHoldOverlayView
import com.azure.android.communication.ui.calling.presentation.fragment.calling.lobby.WaitingLobbyOverlayView
import com.azure.android.communication.ui.calling.presentation.fragment.calling.localuser.LocalParticipantView
import com.azure.android.communication.ui.calling.presentation.fragment.calling.participant.grid.ParticipantGridView
import com.azure.android.communication.ui.calling.presentation.fragment.calling.participantlist.ParticipantListView
import com.azure.android.communication.ui.calling.presentation.fragment.common.audiodevicelist.AudioDeviceListView
import com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.more.MoreCallOptionsListView
import com.azure.android.communication.ui.calling.presentation.fragment.calling.lobby.ConnectingLobbyOverlayView
import com.azure.android.communication.ui.calling.presentation.fragment.calling.notification.ToastNotificationView
import com.azure.android.communication.ui.calling.presentation.fragment.calling.notification.UpperMessageBarNotificationLayoutView
import com.azure.android.communication.ui.calling.presentation.fragment.setup.components.ErrorInfoView
import com.azure.android.communication.ui.calling.presentation.navigation.BackNavigation

internal class CallingFragment :
    Fragment(R.layout.azure_communication_ui_calling_call_fragment), BackNavigation, SensorEventListener {
    companion object {
        private const val LEAVE_CONFIRM_VIEW_KEY = "LeaveConfirmView"
        private const val AUDIO_DEVICE_LIST_VIEW_KEY = "AudioDeviceListView"
        private const val PARTICIPANT_LIST_VIEW_KEY = "ParticipantListView"
    }

    // Get the DI Container, which gives us what we need for this fragment (dependencies)
    private val holder: DependencyInjectionContainerHolder by activityViewModels()

    private val videoViewManager get() = holder.container.videoViewManager
    private val avatarViewManager get() = holder.container.avatarViewManager
    private val viewModel get() = holder.callingViewModel

    private val closeToUser = 0f
    private lateinit var controlBarView: ControlBarView
    private lateinit var confirmLeaveOverlayView: LeaveConfirmView
    private lateinit var localParticipantView: LocalParticipantView
    private lateinit var infoHeaderView: InfoHeaderView
    private lateinit var upperMessageBarNotificationLayoutView : UpperMessageBarNotificationLayoutView
    private lateinit var toastNotificationView: ToastNotificationView
    private lateinit var participantGridView: ParticipantGridView
    private lateinit var audioDeviceListView: AudioDeviceListView
    private lateinit var participantListView: ParticipantListView
    private lateinit var bannerView: BannerView
    private lateinit var errorInfoView: ErrorInfoView
    private lateinit var waitingLobbyOverlay: WaitingLobbyOverlayView
    private lateinit var connectingLobbyOverlay: ConnectingLobbyOverlayView
    private lateinit var holdOverlay: OnHoldOverlayView
    private lateinit var sensorManager: SensorManager
    private lateinit var powerManager: PowerManager
    private lateinit var accessibilityManager: AccessibilityManager
    private lateinit var wakeLock: PowerManager.WakeLock
    private lateinit var moreCallOptionsListView: MoreCallOptionsListView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.init(viewLifecycleOwner.lifecycleScope)

        confirmLeaveOverlayView =
            LeaveConfirmView(viewModel.confirmLeaveOverlayViewModel, this.requireContext())
        confirmLeaveOverlayView.layoutDirection =
            activity?.window?.decorView?.layoutDirection ?: LayoutDirection.LOCALE
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
            accessibilityManager.isEnabled
        )

        upperMessageBarNotificationLayoutView = view.findViewById(R.id.azure_communication_ui_upper_message_bar_notifications_layout)
        upperMessageBarNotificationLayoutView.start(
            viewLifecycleOwner,
            viewModel.upperMessageBarNotificationLayoutViewModel,
            accessibilityManager.isEnabled
        )

        toastNotificationView = view.findViewById(R.id.azure_communication_ui_toast_notification)
        toastNotificationView.start(
            viewLifecycleOwner,
            viewModel.toastNotificationViewModel,
            accessibilityManager.isEnabled
        )

        audioDeviceListView =
            AudioDeviceListView(viewModel.audioDeviceListViewModel, this.requireContext())
        audioDeviceListView.layoutDirection =
            activity?.window?.decorView?.layoutDirection ?: LayoutDirection.LOCALE
        audioDeviceListView.start(viewLifecycleOwner)

        participantListView = ParticipantListView(
            viewModel.participantListViewModel,
            this.requireContext(),
            avatarViewManager,
        )
        participantListView.layoutDirection =
            activity?.window?.decorView?.layoutDirection ?: LayoutDirection.LOCALE
        participantListView.start(viewLifecycleOwner)

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
            activity?.window?.decorView?.layoutDirection ?: LayoutDirection.LOCALE
        moreCallOptionsListView.start(viewLifecycleOwner)
    }

    override fun onResume() {
        super.onResume()
        sensorManager =
            context?.applicationContext?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        powerManager =
            context?.applicationContext?.getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock =
            powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, javaClass.name)
        wakeLock.acquire()
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        super.onPause()
        if (this::wakeLock.isInitialized) {
            if (wakeLock.isHeld) {
                wakeLock.setReferenceCounted(false)
                wakeLock.release()
            }
        }
        if (this::sensorManager.isInitialized) sensorManager.unregisterListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (activity?.isChangingConfigurations == false) {
            if (this::participantGridView.isInitialized) participantGridView.stop()
            if (CallCompositeInstanceManager.hasCallComposite(holder.instanceId)) {
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
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_PROXIMITY) {
            if (event.values[0] == closeToUser) {
                if (!wakeLock.isHeld) {
                    wakeLock.acquire()
                }
            } else {
                if (!wakeLock.isHeld) {
                    wakeLock.setReferenceCounted(false)
                    wakeLock.release()
                }
            }
        }
    }

    override fun onBackPressed() {
        requestCallEnd()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mapOf(
            LEAVE_CONFIRM_VIEW_KEY to viewModel.confirmLeaveOverlayViewModel.getShouldDisplayLeaveConfirmFlow(),
            AUDIO_DEVICE_LIST_VIEW_KEY to viewModel.audioDeviceListViewModel.displayAudioDeviceSelectionMenuStateFlow,
            PARTICIPANT_LIST_VIEW_KEY to viewModel.participantListViewModel.getDisplayParticipantListStateFlow()
        ).forEach { (key, element) -> outState.putBoolean(key, element.value) }
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

    private fun requestCallEnd() {
        viewModel.requestCallEnd()
    }

    private fun displayParticipantList() {
        viewModel.participantListViewModel.displayParticipantList()
    }

    private fun switchFloatingHeader() {
        viewModel.switchFloatingHeader()
    }
}
