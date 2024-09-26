// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

/* <CUSTOM_CALL_HEADER> */
package com.azure.android.communication.ui.calling.redux.action

internal sealed class CallScreenInfoHeaderAction : Action {
    class UpdateTitle(var title: String?) : CallScreenInfoHeaderAction()
    class UpdateSubtitle(var subtitle: String?) : CallScreenInfoHeaderAction()
}
/* </CUSTOM_CALL_HEADER> */
