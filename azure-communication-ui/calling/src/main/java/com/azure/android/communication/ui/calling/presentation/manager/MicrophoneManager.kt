package com.azure.android.communication.ui.calling.presentation.manager

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log

internal class MicrophoneManager {

    fun isMicrophoneAvailable(): Boolean {
        var audioRecord: AudioRecord? = null
        var available: Boolean = true
        try {
            val sampleRateInHz = 44100
            val channelConfig: Int = AudioFormat.CHANNEL_IN_MONO
            val audioFormat: Int = AudioFormat.ENCODING_PCM_16BIT
            val bufferSizeInBytes =
                AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat)

            audioRecord = AudioRecord(MediaRecorder.AudioSource.DEFAULT, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes)

            if (audioRecord.state != AudioRecord.STATE_INITIALIZED) {
                available = false
            }

            audioRecord.startRecording()
            available = available && (audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING)
        } catch (ex: Exception) {
            available = false
        } finally {
            audioRecord?.release()
        }
        Log.d("Mohtasim", "MicrophoneManager - $available")
        return available
    }
}
