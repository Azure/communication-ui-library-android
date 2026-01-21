// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.middleware

import android.os.Looper
import com.azure.android.communication.ui.calling.logger.Logger
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.AudioDeviceAction
import com.azure.android.communication.ui.calling.redux.state.AudioDevice
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceState
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceType
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.service.AudioDeviceService

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
class AudioDeviceMiddlewareTest {
    @Mock
    private lateinit var mockStore: Store<ReduxState>
    @Mock
    private lateinit var mockAudioDeviceService: AudioDeviceService
    @Mock
    private lateinit var mockLogger: Logger
    @Mock
    private lateinit var mockNext: (Action) -> Any

    private lateinit var middleware: AudioDeviceMiddlewareImpl
    private lateinit var state: ReduxState
    private val speaker = AudioDevice(AudioDeviceType.SPEAKER, "speaker", "Speaker")
    private val wiredHeadset = AudioDevice(AudioDeviceType.WIRED_HEADSET, "wired_headset", "Wired Headset")
    private val bluetooth = AudioDevice(AudioDeviceType.BLUETOOTH, "bluetooth", "Bluetooth")

    @Test
    fun `test select device requested success`() {
        setupTest()
        `when`(mockAudioDeviceService.configureAudioRouting(wiredHeadset)).thenReturn(true)

        val action = AudioDeviceAction.SelectDeviceRequested(wiredHeadset)
        middleware.invoke(mockStore)(mockNext)(action)

        // Verify DeviceSwitchStarted action is dispatched immediately
        verify(mockNext).invoke(any(AudioDeviceAction.DeviceSwitchStarted::class.java))

        // Execute pending Looper tasks
        shadowOf(Looper.getMainLooper()).idle()

        // Verify DeviceSwitchCompleted action is dispatched after async operation
        verify(mockNext).invoke(any(AudioDeviceAction.DeviceSwitchCompleted::class.java))
    }

    @Test
    fun `test select device requested failure`() {
        setupTest()
        `when`(mockAudioDeviceService.configureAudioRouting(bluetooth)).thenReturn(false)

        val action = AudioDeviceAction.SelectDeviceRequested(bluetooth)
        middleware.invoke(mockStore)(mockNext)(action)

        // Verify DeviceSwitchStarted action is dispatched immediately
        verify(mockNext).invoke(any(AudioDeviceAction.DeviceSwitchStarted::class.java))

        // Execute pending Looper tasks
        shadowOf(Looper.getMainLooper()).idle()

        // Verify DeviceSwitchFailed action is dispatched after async operation
        verify(mockNext).invoke(any(AudioDeviceAction.DeviceSwitchFailed::class.java))

        // Verify fallback to previous device is attempted
        verify(mockAudioDeviceService).configureAudioRouting(speaker)
    }

    @Test
    fun `test select device requested unavailable device`() {
        setupTest()
        val unavailableDevice = AudioDevice(AudioDeviceType.BLUETOOTH, "unavailable", "Unavailable")

        val action = AudioDeviceAction.SelectDeviceRequested(unavailableDevice)
        middleware.invoke(mockStore)(mockNext)(action)

        // Verify DeviceSwitchFailed action is dispatched immediately
        verify(mockNext).invoke(any(AudioDeviceAction.DeviceSwitchFailed::class.java))

        // Verify no device configuration is attempted
        verify(mockAudioDeviceService, never()).configureAudioRouting(any())
    }

    @Test
    fun `test select device requested already selected`() {
        setupTest()

        val action = AudioDeviceAction.SelectDeviceRequested(speaker)
        middleware.invoke(mockStore)(mockNext)(action)

        // Verify no actions are dispatched
        verify(mockNext, never()).invoke(any())
        verify(mockAudioDeviceService, never()).configureAudioRouting(any())
    }

    @Test
    fun `test select device requested already switching`() {
        setupTest(switching = true)

        val action = AudioDeviceAction.SelectDeviceRequested(bluetooth)
        middleware.invoke(mockStore)(mockNext)(action)

        // Verify no actions are dispatched
        verify(mockNext, never()).invoke(any())
        verify(mockAudioDeviceService, never()).configureAudioRouting(any())
    }

    @Test
    fun `test select device requested exception`() {
        setupTest()
        `when`(mockAudioDeviceService.configureAudioRouting(bluetooth))
            .thenThrow(RuntimeException("Test error"))

        val action = AudioDeviceAction.SelectDeviceRequested(bluetooth)
        middleware.invoke(mockStore)(mockNext)(action)

        // Verify DeviceSwitchStarted action is dispatched immediately
        verify(mockNext).invoke(any(AudioDeviceAction.DeviceSwitchStarted::class.java))

        // Execute pending Looper tasks
        shadowOf(Looper.getMainLooper()).idle()

        // Verify DeviceSwitchFailed action is dispatched after async operation
        verify(mockNext).invoke(
            argThat { arg ->
                arg is AudioDeviceAction.DeviceSwitchFailed &&
                arg.error == "Error configuring audio routing: Test error"
            }
        )

        // Verify fallback to previous device is attempted
        verify(mockAudioDeviceService).configureAudioRouting(speaker)
    }

    private fun setupTest(switching: Boolean = false) {
        mockStore = mock(Store::class.java) as Store<ReduxState>
        mockAudioDeviceService = mock(AudioDeviceService::class.java)
        mockLogger = mock(Logger::class.java)
        mockNext = mock((Action) -> Any::class.java)

        middleware = AudioDeviceMiddlewareImpl(mockAudioDeviceService, mockLogger)

        val audioState = AudioDeviceState(
            speaker,
            listOf(speaker, wiredHeadset, bluetooth),
            true,
            true,
            if (switching) com.azure.android.communication.ui.calling.redux.state.AudioDeviceSwitchingState.SWITCHING
            else com.azure.android.communication.ui.calling.redux.state.AudioDeviceSwitchingState.NONE,
            null
        )

        state = mock(ReduxState::class.java)
        `when`(state.audioState).thenReturn(audioState)
        `when`(mockStore.getCurrentState()).thenReturn(state)
    }

    private fun <T> any(type: Class<T>): T = any()
}
