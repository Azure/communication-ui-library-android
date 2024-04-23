// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.PermissionAction
import com.azure.android.communication.ui.calling.redux.state.PermissionState
import com.azure.android.communication.ui.calling.redux.state.PermissionStatus
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class PermissionReduxStateReducerImplUnitTest {
    @Test
    fun permissionStateReducer_reduce_when_actionAudioPermissionChange_then_changeAudioPermissionState() {
        // arrange
        val reducer = PermissionStateReducerImpl()
        val oldState = PermissionState(PermissionStatus.NOT_ASKED, PermissionStatus.NOT_ASKED)
        val action = PermissionAction.AudioPermissionIsSet(PermissionStatus.GRANTED)

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(PermissionStatus.GRANTED, newState.audioPermissionState)
    }

    @Test
    fun permissionStateReducer_reduce_when_actionCameraPermissionChange_then_changeCameraPermissionState() {
        // arrange
        val reducer = PermissionStateReducerImpl()
        val oldState = PermissionState(PermissionStatus.NOT_ASKED, PermissionStatus.NOT_ASKED)
        val action = PermissionAction.CameraPermissionIsSet(PermissionStatus.GRANTED)

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(PermissionStatus.GRANTED, newState.cameraPermissionState)
    }

    @Test
    fun permissionStateReducer_reduce_when_actionRequestAudioPermission_oldStateNotAsked_then_changeStateToRequested() {
        // arrange
        val reducer = PermissionStateReducerImpl()
        val oldState = PermissionState(PermissionStatus.NOT_ASKED, PermissionStatus.NOT_ASKED)
        val action = PermissionAction.AudioPermissionRequested()

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(PermissionStatus.REQUESTING, newState.audioPermissionState)
    }

    @Test
    fun permissionStateReducer_reduce_when_actionRequestAudioPermission_oldStateRequested_then_changeStateToRequested() {
        // arrange
        val reducer = PermissionStateReducerImpl()
        val oldState = PermissionState(PermissionStatus.GRANTED, PermissionStatus.GRANTED)
        val action = PermissionAction.AudioPermissionRequested()

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(PermissionStatus.REQUESTING, newState.audioPermissionState)
    }

    @Test
    fun permissionStateReducer_reduce_when_actionRequestAudioPermission_oldStateRequested_then_changeStateToGranted() {
        // arrange
        val reducer = PermissionStateReducerImpl()
        val oldState = PermissionState(PermissionStatus.REQUESTING, PermissionStatus.GRANTED)
        val action = PermissionAction.AudioPermissionIsSet(PermissionStatus.GRANTED)

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(PermissionStatus.GRANTED, newState.audioPermissionState)
    }

    @Test
    fun permissionStateReducer_reduce_when_actionRequestCameraPermission_oldStateNotAsked_then_changeStateToRequested() {
        // arrange
        val reducer = PermissionStateReducerImpl()
        val oldState = PermissionState(PermissionStatus.NOT_ASKED, PermissionStatus.NOT_ASKED)
        val action = PermissionAction.CameraPermissionRequested()

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(PermissionStatus.REQUESTING, newState.cameraPermissionState)
    }

    @Test
    fun permissionStateReducer_reduce_when_actionRequestCameraPermission_oldStateRequested_then_changeStateToRequested() {
        // arrange
        val reducer = PermissionStateReducerImpl()
        val oldState = PermissionState(PermissionStatus.GRANTED, PermissionStatus.GRANTED)
        val action = PermissionAction.CameraPermissionRequested()

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(PermissionStatus.REQUESTING, newState.cameraPermissionState)
    }

    @Test
    fun permissionStateReducer_reduce_when_actionRequestCameraPermission_oldStateRequested_then_changeStateToGranted() {
        // arrange
        val reducer = PermissionStateReducerImpl()
        val oldState = PermissionState(PermissionStatus.GRANTED, PermissionStatus.REQUESTING)
        val action = PermissionAction.CameraPermissionIsSet(PermissionStatus.GRANTED)

        // act
        val newState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(PermissionStatus.GRANTED, newState.cameraPermissionState)
    }
}
