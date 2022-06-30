package com.azure.android.communication.ui.calling.redux.middleware.audio

import android.os.Handler
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.reducer.Reducer
import com.azure.android.communication.ui.calling.redux.state.*
import com.azure.android.communication.ui.calling.utilities.StoreHandlerThread
import com.azure.android.communication.ui.calling.utilities.audio.AudioSwitchingAdapter
import com.azure.android.communication.ui.helper.HandlerAnswerStub
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any

@RunWith(MockitoJUnitRunner::class)
class AudioSwitchingMiddlewareTest {

    @Mock
    private lateinit var mockStoreHandlerThread: StoreHandlerThread

    @Mock
    private lateinit var mockHandler: Handler

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
    @Test
    fun switchToBluetoothTest() {
        val audioSwitchingMiddleware = AudioSwitchingMiddleware(MockAudioSwitchingAdapter())
        val testReducer = object : Reducer<ReduxState> {
            override fun reduce(state: ReduxState, action: Action): ReduxState {
                return state
            }
        }
        val initialState = AppReduxState("TestDisplayName").apply {
            callState = CallingState(CallingStatus.CONNECTED)
        }

        Mockito.`when`(mockHandler.post(any())).thenAnswer(HandlerAnswerStub())
        Mockito.`when`(mockStoreHandlerThread.startHandlerThread()).thenReturn(mockHandler)
        Mockito.`when`(mockStoreHandlerThread.isHandlerThreadAlive()).thenReturn(true)


        val store = AppStore(
            initialState,
            testReducer,
            mutableListOf(audioSwitchingMiddleware),
            mockStoreHandlerThread
        )

        
    }
}