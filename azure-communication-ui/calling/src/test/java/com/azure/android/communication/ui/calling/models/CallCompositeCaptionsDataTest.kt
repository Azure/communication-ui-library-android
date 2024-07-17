// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models

import com.azure.android.communication.calling.CallerInfo
import com.azure.android.communication.calling.CommunicationCaptionsReceivedEvent
import com.azure.android.communication.calling.TeamsCaptionsReceivedEvent
import com.azure.android.communication.common.CommunicationUserIdentifier
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.junit.Assert.assertEquals
import org.mockito.Mockito
import java.util.Date

@RunWith(MockitoJUnitRunner::class)
internal class CallCompositeCaptionsDataTest {

    @Test
    fun callCompositeCaptionsData_extension_resultType_conversion() {
        // arrange
        val finalResult = com.azure.android.communication.calling.CaptionsResultType.FINAL.into()
        // assert
        assertEquals(CaptionsResultType.FINAL, finalResult)

        // arrange
        val partialResult = com.azure.android.communication.calling.CaptionsResultType.PARTIAL.into()
        // assert
        assertEquals(CaptionsResultType.PARTIAL, partialResult)
    }

    @Test
    fun communicationCaptionsReceivedEvent_into_conversion() {
        // arrange
        val timestamp = Date()
        val event = Mockito.mock(CommunicationCaptionsReceivedEvent::class.java)
        val callerInfo = Mockito.mock(CallerInfo::class.java)
        Mockito.`when`(event.resultType).thenReturn(com.azure.android.communication.calling.CaptionsResultType.FINAL)
        Mockito.`when`(event.speaker).thenReturn(callerInfo)
        Mockito.`when`(event.speaker.displayName).thenReturn("John Doe")
        Mockito.`when`(event.spokenLanguage).thenReturn("en")
        Mockito.`when`(event.spokenText).thenReturn("Hello world")
        Mockito.`when`(event.timestamp).thenReturn(timestamp)
        Mockito.`when`(callerInfo.identifier).thenReturn(Mockito.mock(CommunicationUserIdentifier::class.java))
        Mockito.`when`(callerInfo.identifier.rawId).thenReturn("rawId")

        // act
        val result = event.into()

        // assert
        assertEquals(CaptionsResultType.FINAL, result.resultType)
        assertEquals("rawId", result.speakerRawId)
        assertEquals("John Doe", result.speakerName)
        assertEquals("en", result.spokenLanguage)
        assertEquals("Hello world", result.spokenText)
        assertEquals(timestamp, result.timestamp)
    }

    @Test
    fun teamsCaptionsReceivedEvent_into_conversion() {
        // arrange
        val timestamp = Date()
        val event = Mockito.mock(TeamsCaptionsReceivedEvent::class.java)
        val callerInfo = Mockito.mock(CallerInfo::class.java)
        Mockito.`when`(event.resultType).thenReturn(com.azure.android.communication.calling.CaptionsResultType.PARTIAL)
        Mockito.`when`(event.speaker).thenReturn(callerInfo)
        Mockito.`when`(event.speaker.displayName).thenReturn("Jane Doe")
        Mockito.`when`(event.spokenLanguage).thenReturn("fr")
        Mockito.`when`(event.spokenText).thenReturn("Bonjour le monde")
        Mockito.`when`(event.timestamp).thenReturn(timestamp)
        Mockito.`when`(event.captionLanguage).thenReturn("fr")
        Mockito.`when`(event.captionText).thenReturn("Bonjour")
        Mockito.`when`(callerInfo.identifier).thenReturn(Mockito.mock(CommunicationUserIdentifier::class.java))
        Mockito.`when`(callerInfo.identifier.rawId).thenReturn("rawId")

        // act
        val result = event.into()

        // assert
        assertEquals(CaptionsResultType.PARTIAL, result.resultType)
        assertEquals("rawId", result.speakerRawId)
        assertEquals("Jane Doe", result.speakerName)
        assertEquals("fr", result.spokenLanguage)
        assertEquals("Bonjour le monde", result.spokenText)
        assertEquals(timestamp, result.timestamp)
        assertEquals("fr", result.captionLanguage)
        assertEquals("Bonjour", result.captionText)
    }
}
