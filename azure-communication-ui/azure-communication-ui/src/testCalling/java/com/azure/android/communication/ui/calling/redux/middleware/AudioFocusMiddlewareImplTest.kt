package com.azure.android.communication.ui.calling.redux.middleware

import android.media.AudioManager
import android.os.Handler
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.Middleware
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.CallingAction
import com.azure.android.communication.ui.calling.redux.reducer.Reducer
import com.azure.android.communication.ui.calling.redux.state.AppReduxState
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.utilities.StoreHandlerThread
import com.azure.android.communication.ui.helper.HandlerAnswerStub
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.mock

/// Test Cases
/// 1. on Call Start if getAudioFocus == True, the start call should be dispatched
/// 2. on Call Resume if getAudioFocus == True, the start call should be dispatched
/// 3. onAudioFocus change should trigger onFocusLost

@RunWith(MockitoJUnitRunner::class)
class AudioFocusMiddlewareImplTest {

    @Mock
    private lateinit var mockStoreHandlerThread: StoreHandlerThread

    @Mock
    private lateinit var mockHandler: Handler

    private val alwaysSucessAudioFocusHandler = object: AudioFocusHandler() {
        override fun getAudioFocus(): Boolean = true
        override fun releaseAudioFocus(): Boolean = true
    }

    private val alwaysFailAudioFocusHandler = object: AudioFocusHandler() {
        override fun getAudioFocus(): Boolean = false
        override fun releaseAudioFocus(): Boolean = false
    }

    internal fun testActionPassThrough( audioFocusMiddleware: Middleware<ReduxState>, actionToDispatch: Action, actionExpected: Action?) {
        Mockito.`when`(mockHandler.post(any())).thenAnswer(HandlerAnswerStub())
        Mockito.`when`(mockStoreHandlerThread.startHandlerThread()).thenReturn(mockHandler)
        Mockito.`when`(mockStoreHandlerThread.isHandlerThreadAlive()).thenReturn(true)

        var lastAction: Action? = null

        val testReducer = object : Reducer<ReduxState> {
            override fun reduce(state: ReduxState, action: Action): ReduxState {
                lastAction = action
                return state
            }
        }

        val store = AppStore(
            initialState = AppReduxState("test"),
            middlewares = mutableListOf(audioFocusMiddleware),
            reducer = testReducer,
            storeHandlerThread = mockStoreHandlerThread)


        store.dispatch(actionToDispatch)

        assertEquals(lastAction, actionExpected)
    }


    @Test
    fun audioFocusMiddleware_Verify_Passthrough_OnFocusSuccess_CallStart() {
        val action = CallingAction.CallStartRequested()
        testActionPassThrough(AudioFocusMiddlewareImpl(alwaysSucessAudioFocusHandler, {}, {}), action, action)
    }

    @Test
    fun audioFocusMiddleware_Verify_Passthrough_Denied_OnFocusFailed_CallStart() {
        val action = CallingAction.CallStartRequested()
        var failed = false
        testActionPassThrough(AudioFocusMiddlewareImpl(alwaysFailAudioFocusHandler, {
            failed = true
        }, {}), action, null)

        assertTrue(failed)
    }

    @Test
    fun audioFocusMiddleware_Verify_Passthrough_OnFocusSuccess_CallResume() {
        val action = CallingAction.ResumeRequested()
        testActionPassThrough(AudioFocusMiddlewareImpl(alwaysSucessAudioFocusHandler, {}, {}), action, action)
    }

    @Test
    fun audioFocusMiddleware_Verify_Passthrough_Denied_OnFocusFailed_CallResume() {
        val action = CallingAction.ResumeRequested()
        var failed = false
        testActionPassThrough(AudioFocusMiddlewareImpl(alwaysFailAudioFocusHandler, {
            failed = true
        }, {}), action, null)

        assertTrue(failed)
    }

    @Test
    fun audioFocusMiddleware_Triggers_Callback_OnFocusDropped() {
        var focusLost = false
        val handler = AudioFocusMiddlewareImpl(alwaysSucessAudioFocusHandler, {}, {
            focusLost = true
        })
        alwaysSucessAudioFocusHandler.onAudioFocusChange(AudioManager.AUDIOFOCUS_LOSS)
        assertTrue(focusLost)
    }


}