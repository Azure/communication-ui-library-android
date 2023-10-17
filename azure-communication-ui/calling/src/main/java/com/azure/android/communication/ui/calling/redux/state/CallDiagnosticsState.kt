// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.state

import com.azure.android.communication.ui.calling.models.MediaCallDiagnosticModel
import com.azure.android.communication.ui.calling.models.NetworkCallDiagnosticModel
import com.azure.android.communication.ui.calling.models.NetworkQualityCallDiagnosticModel

internal data class CallDiagnosticsState(
    val networkQualityCallDiagnostic: NetworkQualityCallDiagnosticModel?,
    val networkCallDiagnostic: NetworkCallDiagnosticModel?,
    val mediaCallDiagnostic: MediaCallDiagnosticModel?

)
