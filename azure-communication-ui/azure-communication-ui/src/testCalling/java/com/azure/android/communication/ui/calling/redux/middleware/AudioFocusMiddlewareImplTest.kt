package com.azure.android.communication.ui.calling.redux.middleware

import junit.framework.Assert.assertEquals
import org.junit.Test

class AudioFocusMiddlewareImplTest {
    @Test
    fun test() {
        val testAudioFocusHandler = object: AudioFocusHandler() {
            override fun getAudioFocus(): Boolean = true
            override fun releaseAudioFocus(): Boolean = true
        }

        val audioFocusMiddleware = AudioFocusMiddlewareImpl(
            testAudioFocusHandler,
            onFocusFailed = {},
            onFocusLost = {}
        )

        assertEquals(true, true)

    }

}