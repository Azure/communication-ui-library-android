// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

/* <CUSTOM_CALL_HEADER> */
package com.azure.android.communication.ui.calling.redux.action

internal sealed class CallScreenInformationHeaderAction : Action {
    class UpdateTitle(var title: String) : CallScreenInformationHeaderAction()
    class UpdateSubtitle(var subtitle: String) : CallScreenInformationHeaderAction()
}
/* </CUSTOM_CALL_HEADER> */
