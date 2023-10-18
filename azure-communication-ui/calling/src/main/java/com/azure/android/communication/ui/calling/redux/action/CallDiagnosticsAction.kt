// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.action

import com.azure.android.communication.ui.calling.models.MediaCallDiagnosticModel
import com.azure.android.communication.ui.calling.models.NetworkCallDiagnosticModel
import com.azure.android.communication.ui.calling.models.NetworkQualityCallDiagnosticModel

internal sealed class CallDiagnosticsAction : Action {
    class NetworkQualityCallDiagnosticsUpdated(val networkQualityUpdated: NetworkQualityCallDiagnosticModel) : CallDiagnosticsAction()
    class NetworkCallDiagnosticsUpdated(val networkUpdated: NetworkCallDiagnosticModel) : CallDiagnosticsAction()
    class MediaCallDiagnosticsUpdated(val mediaUpdated: MediaCallDiagnosticModel) : CallDiagnosticsAction()

    class MediaCallDiagnosticsDismissed(val mediaDiagnosticDismissed: MediaCallDiagnosticModel) : CallDiagnosticsAction()
}
