// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.action

internal sealed class AudioSessionAction : Action {
    class AudioFocusRequesting : AudioSessionAction()

    class AudioFocusApproved : AudioSessionAction()

    class AudioFocusRejected : AudioSessionAction()

    class AudioFocusInterrupted : AudioSessionAction()
}
