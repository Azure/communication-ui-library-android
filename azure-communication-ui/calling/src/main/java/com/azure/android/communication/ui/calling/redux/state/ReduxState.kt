// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.state

internal interface ReduxState {
    var callState: CallingState
    var remoteParticipantState: RemoteParticipantsState
    var localParticipantState: LocalUserState
    var permissionState: PermissionState
    var lifecycleState: LifecycleState
    var errorState: ErrorState
    var navigationState: NavigationState
    var audioSessionState: AudioSessionState
    val pipState: PictureInPictureState
}
