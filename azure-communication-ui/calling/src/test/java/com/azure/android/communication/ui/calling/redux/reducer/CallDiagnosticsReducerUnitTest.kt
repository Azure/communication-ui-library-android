// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.models.CallDiagnosticQuality
import com.azure.android.communication.ui.calling.models.MediaCallDiagnostic
import com.azure.android.communication.ui.calling.models.MediaCallDiagnosticModel
import com.azure.android.communication.ui.calling.models.NetworkCallDiagnostic
import com.azure.android.communication.ui.calling.models.NetworkCallDiagnosticModel
import com.azure.android.communication.ui.calling.models.NetworkQualityCallDiagnosticModel
import com.azure.android.communication.ui.calling.redux.action.CallDiagnosticsAction
import com.azure.android.communication.ui.calling.redux.reducer.CallDiagnosticsReducerImpl
import com.azure.android.communication.ui.calling.redux.state.CallDiagnosticsState
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class CallDiagnosticsReducerUnitTest {
    @Test
    fun callingReducer_reduce_when_actionUpdateNetworkQualityDiagnostics_then_changeDiagnosticValue() {
        // arrange
        val reducer = CallDiagnosticsReducerImpl()
        val networkQualityCallDiagnosticModel = NetworkQualityCallDiagnosticModel(NetworkCallDiagnostic.NETWORK_SEND_QUALITY, CallDiagnosticQuality.GOOD)
        val previousState = CallDiagnosticsState(networkQualityCallDiagnosticModel, null, null)
        val newNetworkQualityCallDiagnosticModel = NetworkQualityCallDiagnosticModel(NetworkCallDiagnostic.NETWORK_SEND_QUALITY, CallDiagnosticQuality.BAD)
        val action = CallDiagnosticsAction.NetworkQualityCallDiagnosticsUpdated(newNetworkQualityCallDiagnosticModel)

        // act
        val newState = reducer.reduce(previousState, action)

        // assert
        Assert.assertEquals(CallDiagnosticQuality.BAD, newState.networkQualityCallDiagnostic?.diagnosticValue)
        Assert.assertEquals(null, newState.networkCallDiagnostic)
        Assert.assertEquals(null, newState.mediaCallDiagnostic)
    }

    @Test
    fun callingReducer_reduce_when_actionUpdateNetworkDiagnostics_then_changeDiagnosticValue() {
        // arrange
        val reducer = CallDiagnosticsReducerImpl()
        val networkCallDiagnosticModel = NetworkCallDiagnosticModel(NetworkCallDiagnostic.NETWORK_UNAVAILABLE, true)
        val previousState = CallDiagnosticsState(null, networkCallDiagnosticModel, null)
        val newNetworkCallDiagnosticModel = NetworkCallDiagnosticModel(NetworkCallDiagnostic.NETWORK_UNAVAILABLE, false)
        val action = CallDiagnosticsAction.NetworkCallDiagnosticsUpdated(newNetworkCallDiagnosticModel)

        // act
        val newState = reducer.reduce(previousState, action)

        // assert
        Assert.assertEquals(false, newState.networkCallDiagnostic?.diagnosticValue)
        Assert.assertEquals(null, newState.networkQualityCallDiagnostic)
        Assert.assertEquals(null, newState.mediaCallDiagnostic)
    }

    @Test
    fun callingReducer_reduce_when_actionUpdateMediaDiagnostics_then_changeDiagnosticValue() {
        // arrange
        val reducer = CallDiagnosticsReducerImpl()
        val mediaCallDiagnosticModel = MediaCallDiagnosticModel(MediaCallDiagnostic.SPEAKING_WHILE_MICROPHONE_IS_MUTED, false)
        val previousState = CallDiagnosticsState(null, null, mediaCallDiagnosticModel)
        val newMediaCallDiagnosticModel = MediaCallDiagnosticModel(MediaCallDiagnostic.SPEAKING_WHILE_MICROPHONE_IS_MUTED, true)
        val action = CallDiagnosticsAction.MediaCallDiagnosticsUpdated(newMediaCallDiagnosticModel)

        // act
        val newState = reducer.reduce(previousState, action)

        // assert
        Assert.assertEquals(true, newState.mediaCallDiagnostic?.diagnosticValue)
        Assert.assertEquals(null, newState.networkQualityCallDiagnostic)
        Assert.assertEquals(null, newState.networkCallDiagnostic)
    }
}
