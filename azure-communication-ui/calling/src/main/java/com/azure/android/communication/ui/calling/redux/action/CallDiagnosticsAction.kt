// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.action

import com.azure.android.communication.ui.calling.models.MediaCallDiagnosticModel
import com.azure.android.communication.ui.calling.models.NetworkCallDiagnosticModel
import com.azure.android.communication.ui.calling.models.NetworkQualityCallDiagnosticModel

internal sealed class CallDiagnosticsAction : Action {
    class NetworkQualityCallDiagnosticsUpdated(
        val networkQualityCallDiagnosticModel: NetworkQualityCallDiagnosticModel,
    ) : CallDiagnosticsAction()

    class NetworkCallDiagnosticsUpdated(val networkCallDiagnosticModel: NetworkCallDiagnosticModel) : CallDiagnosticsAction()

    class MediaCallDiagnosticsUpdated(val mediaCallDiagnosticModel: MediaCallDiagnosticModel) : CallDiagnosticsAction()

    class NetworkQualityCallDiagnosticsDismissed(val networkQualityCallDiagnosticModel: NetworkQualityCallDiagnosticModel) : CallDiagnosticsAction()

    class NetworkCallDiagnosticsDismissed(val networkCallDiagnosticModel: NetworkCallDiagnosticModel) : CallDiagnosticsAction()

    class MediaCallDiagnosticsDismissed(val mediaCallDiagnosticModel: MediaCallDiagnosticModel) : CallDiagnosticsAction()
}
