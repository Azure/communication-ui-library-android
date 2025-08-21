// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.action

internal sealed class CallScreenInfoHeaderAction : Action {
    class UpdateTitle(var title: String?) : CallScreenInfoHeaderAction()
    class UpdateSubtitle(var subtitle: String?) : CallScreenInfoHeaderAction()
    /* <CALL_START_TIME> */
    class UpdateShowCallDuration(var showCallDuration: Boolean) : CallScreenInfoHeaderAction()
    /* </CALL_START_TIME> */
}
