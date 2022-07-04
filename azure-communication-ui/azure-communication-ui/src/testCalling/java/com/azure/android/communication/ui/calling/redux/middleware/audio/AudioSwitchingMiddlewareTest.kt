package com.azure.android.communication.ui.calling.redux.middleware.audio

import android.os.Handler
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.LifecycleAction
import com.azure.android.communication.ui.calling.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.calling.redux.reducer.Reducer
import com.azure.android.communication.ui.calling.redux.state.*
import com.azure.android.communication.ui.calling.utilities.StoreHandlerThread
import com.azure.android.communication.ui.calling.utilities.audio.AudioSwitchingAdapter
import com.azure.android.communication.ui.helper.HandlerAnswerStub
import net.bytebuddy.asm.Advice
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
class AudioSwitchingMiddlewareTest {

    @Mock
    private lateinit var mockStoreHandlerThread: StoreHandlerThread

    @Mock
    private lateinit var mockHandler: Handler

    // Manual Mock for Audio Switching Adapter
    class MockAudioSwitchingAdapter : AudioSwitchingAdapter {
        var disconnectedAudio = false
        var bluetoothConnected = false
        var speakerPhoneConnected = false
        var earpieceConnected = false

        override fun disconnectAudio() {
            disconnectedAudio = true
            bluetoothConnected = false
            speakerPhoneConnected = false
            earpieceConnected = false
        }

        override fun enableSpeakerPhone(): Boolean {
            disconnectedAudio = false
            bluetoothConnected = false
            speakerPhoneConnected = true
            earpieceConnected = false
            return true
        }

        override fun enableEarpiece(): Boolean {
            disconnectedAudio = false
            bluetoothConnected = false
            speakerPhoneConnected = false
            earpieceConnected = true
            return true
        }

        override fun enableBluetooth(): Boolean {
            disconnectedAudio = false
            bluetoothConnected = true
            speakerPhoneConnected = true
            earpieceConnected = false
            return true
        }
    }

    private val mockAudioSwitchingAdapter = MockAudioSwitchingAdapter()

    private lateinit var store : AppStore<ReduxState>
    private var lastActions = mutableListOf<Action>()


    private fun setupStoreAndReducer(initialState: AppReduxState) {
        lastActions.clear()

        mockAudioSwitchingAdapter.disconnectAudio()
        val audioSwitchingMiddleware = AudioSwitchingMiddleware(mockAudioSwitchingAdapter)
        val testReducer = object : Reducer<ReduxState> {
            override fun reduce(state: ReduxState, action: Action): ReduxState {
                lastActions.add(action)
                return state
            }
        }


        Mockito.`when`(mockHandler.post(any())).thenAnswer(HandlerAnswerStub())
        Mockito.`when`(mockStoreHandlerThread.startHandlerThread()).thenReturn(mockHandler)
        Mockito.`when`(mockStoreHandlerThread.isHandlerThreadAlive()).thenReturn(true)


        store = AppStore(
            initialState,
            testReducer,
            mutableListOf(audioSwitchingMiddleware),
            mockStoreHandlerThread
        )

    }

    @Test
    fun autoSwitchToBluetoothTest() {
        setupStoreAndReducer(AppReduxState("TestDisplayName").apply {
            callState = CallingState(CallingStatus.CONNECTED)
        })

        store.dispatch(LocalParticipantAction.AudioDeviceBluetoothSCOAvailable(true, "TestDevice"))
        assert(lastActions[0] is LocalParticipantAction.AudioDeviceBluetoothSCOAvailable)
        assert(lastActions[1] is LocalParticipantAction.AudioDeviceChangeRequested)
    }

    @Test
    fun autoSwitchOffBluetoothTest() {
        setupStoreAndReducer(AppReduxState("TestDisplayName").apply {
            callState = CallingState(CallingStatus.CONNECTED)
            localParticipantState = LocalUserState(
                displayName = "",
                videoStreamID = null,
                cameraState = CameraState(
                    device = CameraDeviceSelectionStatus.FRONT,
                    operation = CameraOperationalStatus.OFF,
                    transmission = CameraTransmissionStatus.LOCAL,
                ),
                audioState = AudioState(
                    operation = AudioOperationalStatus.OFF,
                    device = AudioDeviceSelectionStatus.BLUETOOTH_SCO_SELECTED,
                    bluetoothState = BluetoothState(
                        available = true,
                        deviceName = "Test"
                    ),
                    previousDevice = AudioDeviceSelectionStatus.RECEIVER_SELECTED
                ),

            )
        })

        store.dispatch(LocalParticipantAction.AudioDeviceBluetoothSCOAvailable(false, "TestDevice"))
        assert(lastActions[0] is LocalParticipantAction.AudioDeviceBluetoothSCOAvailable)
        assert(lastActions[1] is LocalParticipantAction.AudioDeviceChangeRequested)
        // Verify we requested "Receiver" as the previous device
        val deviceRequest = lastActions[1] as LocalParticipantAction.AudioDeviceChangeRequested
        assert (deviceRequest.requestedAudioDevice == AudioDeviceSelectionStatus.RECEIVER_SELECTED)
    }



    @Test
    fun comeToForegroundWhenNotConnected() {
        /// When we come to the foreground not in a connected state, we should
        /// connect audio
        setupStoreAndReducer(AppReduxState("TestDisplayName").apply {
            callState = CallingState(CallingStatus.EARLY_MEDIA)
            localParticipantState = LocalUserState(
                displayName = "",
                videoStreamID = null,
                cameraState = CameraState(
                    device = CameraDeviceSelectionStatus.FRONT,
                    operation = CameraOperationalStatus.OFF,
                    transmission = CameraTransmissionStatus.LOCAL,
                ),
                audioState = AudioState(
                    operation = AudioOperationalStatus.OFF,
                    device = AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                    bluetoothState = BluetoothState(
                        available = false,
                        deviceName = "Test"
                    ),
                ),
            )
        })

        mockAudioSwitchingAdapter.disconnectAudio()
        assert(mockAudioSwitchingAdapter.disconnectedAudio)
        store.dispatch(LifecycleAction.EnterForegroundSucceeded())
        assert(mockAudioSwitchingAdapter.speakerPhoneConnected)
    }



    @Test
    fun gotoBackgroundWhenNotConnected() {
        /// When we go to the background not in a connected state, we should
        /// disconnect audio
        setupStoreAndReducer(AppReduxState("TestDisplayName").apply {
            callState = CallingState(CallingStatus.EARLY_MEDIA)
            localParticipantState = LocalUserState(
                displayName = "",
                videoStreamID = null,
                cameraState = CameraState(
                    device = CameraDeviceSelectionStatus.FRONT,
                    operation = CameraOperationalStatus.OFF,
                    transmission = CameraTransmissionStatus.LOCAL,
                ),
                audioState = AudioState(
                    operation = AudioOperationalStatus.OFF,
                    device = AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                    bluetoothState = BluetoothState(
                        available = false,
                        deviceName = "Test"
                    ),
                ),
            )
        })

        mockAudioSwitchingAdapter.enableSpeakerPhone()
        assert(!mockAudioSwitchingAdapter.disconnectedAudio)
        store.dispatch(LifecycleAction.EnterBackgroundSucceeded())
        assert(mockAudioSwitchingAdapter.disconnectedAudio)
    }


    @Test
    fun testBasicSwitching() {
        /// When we go to the background not in a connected state, we should
        /// disconnect audio
        setupStoreAndReducer(AppReduxState("TestDisplayName").apply {
            callState = CallingState(CallingStatus.CONNECTED)
            localParticipantState = LocalUserState(
                displayName = "",
                videoStreamID = null,
                cameraState = CameraState(
                    device = CameraDeviceSelectionStatus.FRONT,
                    operation = CameraOperationalStatus.OFF,
                    transmission = CameraTransmissionStatus.LOCAL,
                ),
                audioState = AudioState(
                    operation = AudioOperationalStatus.OFF,
                    device = AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                    bluetoothState = BluetoothState(
                        available = false,
                        deviceName = "Test"
                    ),
                ),
            )
        })

        store.dispatch(LocalParticipantAction.AudioDeviceChangeRequested(requestedAudioDevice = AudioDeviceSelectionStatus.RECEIVER_SELECTED))
        assert(lastActions[0] is LocalParticipantAction.AudioDeviceChangeRequested)
        assert(mockAudioSwitchingAdapter.earpieceConnected)
    }
}