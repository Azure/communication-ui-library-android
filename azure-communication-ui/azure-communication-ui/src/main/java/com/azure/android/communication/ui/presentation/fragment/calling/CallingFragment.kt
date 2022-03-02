// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.PowerManager
import android.view.View
import android.view.accessibility.AccessibilityManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.presentation.DependencyInjectionContainerHolder
import com.azure.android.communication.ui.presentation.fragment.calling.banner.BannerView
import com.azure.android.communication.ui.presentation.fragment.calling.controlbar.ControlBarView
import com.azure.android.communication.ui.presentation.fragment.calling.hangup.ConfirmLeaveOverlayView
import com.azure.android.communication.ui.presentation.fragment.calling.header.InfoHeaderView
import com.azure.android.communication.ui.presentation.fragment.calling.lobby.LobbyOverlayView
import com.azure.android.communication.ui.presentation.fragment.calling.localuser.LocalParticipantView
import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.ParticipantGridView
import com.azure.android.communication.ui.presentation.fragment.calling.participantlist.ParticipantListView
import com.azure.android.communication.ui.presentation.fragment.common.audiodevicelist.AudioDeviceListView
import com.azure.android.communication.ui.presentation.navigation.BackNavigation

internal class CallingFragment :
    Fragment(R.layout.azure_communication_ui_call_fragment), BackNavigation, SensorEventListener {

    // Get the DI Container, which gives us what we need for this fragment (dependencies)
    private val holder: DependencyInjectionContainerHolder by activityViewModels()

    private val videoViewManager get() = holder.container.videoViewManager
    private val viewModel get() = holder.callingViewModel

    private val closeToUser = 0f
    private lateinit var controlBarView: ControlBarView
    private lateinit var confirmLeaveOverlayView: ConfirmLeaveOverlayView
    private lateinit var localParticipantView: LocalParticipantView
    private lateinit var infoHeaderView: InfoHeaderView
    private lateinit var participantGridView: ParticipantGridView
    private lateinit var audioDeviceListView: AudioDeviceListView
    private lateinit var participantListView: ParticipantListView
    private lateinit var bannerView: BannerView
    private lateinit var lobbyOverlay: LobbyOverlayView
    private lateinit var sensorManager: SensorManager
    private lateinit var powerManager: PowerManager
    private lateinit var accessibilityManager: AccessibilityManager
    private lateinit var wakeLock: PowerManager.WakeLock

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.init(viewLifecycleOwner.lifecycleScope)

        confirmLeaveOverlayView = view.findViewById(R.id.azure_communication_ui_call_leave_overlay)
        confirmLeaveOverlayView.start(
            viewLifecycleOwner,
            viewModel.getConfirmLeaveOverlayViewModel()
        )

        controlBarView = view.findViewById(R.id.azure_communication_ui_call_call_buttons)
        controlBarView.start(
            viewLifecycleOwner,
            viewModel.getControlBarViewModel(),
            this::requestCallEnd,
            this::openAudioDeviceSelectionMenu
        )

        participantGridView =
            view.findViewById(R.id.azure_communication_ui_call_participant_container)
        participantGridView.start(
            viewModel.getParticipantGridViewModel(),
            videoViewManager,
            viewLifecycleOwner,
            this::switchFloatingHeader
        )

        lobbyOverlay = view.findViewById(R.id.azure_communication_ui_call_lobby_overlay)
        lobbyOverlay.start(viewLifecycleOwner, viewModel.getLobbyOverlayViewModel())

        localParticipantView = view.findViewById(R.id.azure_communication_ui_call_local_user_view)
        localParticipantView.start(
            viewLifecycleOwner,
            viewModel.getLocalParticipantViewModel(),
            videoViewManager,
        )

        accessibilityManager = context?.applicationContext?.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        infoHeaderView = view.findViewById(R.id.azure_communication_ui_call_floating_header)
        infoHeaderView.start(
            viewLifecycleOwner,
            viewModel.getFloatingHeaderViewModel(),
            this::displayParticipantList,
            accessibilityManager.isEnabled
        )

        audioDeviceListView =
            AudioDeviceListView(viewModel.getAudioDeviceListViewModel(), this.requireContext())
        audioDeviceListView.start(viewLifecycleOwner)

        participantListView = ParticipantListView(
            viewModel.getParticipantListViewModel(),
            this.requireContext(),
        )
        participantListView.start(viewLifecycleOwner)

        bannerView = view.findViewById(R.id.azure_communication_ui_call_banner)
        bannerView.start(
            viewModel.getBannerViewModel(),
            viewLifecycleOwner,
        )

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
        participantGridView.setOnClickListener {
            switchFloatingHeader()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (activity?.isChangingConfigurations == false) {
            participantGridView.stop()
            confirmLeaveOverlayView.stop()
            viewModel.getBannerViewModel().dismissBanner()
        }
        localParticipantView.stop()
        participantListView.stop()
        audioDeviceListView.stop()
        if (wakeLock.isHeld) {
            wakeLock.setReferenceCounted(false)
            wakeLock.release()
        }
        sensorManager.unregisterListener(this)
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

    private fun requestCallEnd() {
        viewModel.requestCallEnd()
    }

    private fun openAudioDeviceSelectionMenu() {
        viewModel.getAudioDeviceListViewModel().displayAudioDeviceSelectionMenu()
    }

    private fun displayParticipantList() {
        viewModel.getParticipantListViewModel().displayParticipantList()
    }

    private fun switchFloatingHeader() {
        viewModel.switchFloatingHeader()
    }
}
