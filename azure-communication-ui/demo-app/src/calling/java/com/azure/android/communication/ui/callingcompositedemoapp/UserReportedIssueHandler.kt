package com.azure.android.communication.ui.callingcompositedemoapp

import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import com.azure.android.communication.ui.calling.models.CallCompositeUserReportedIssueEvent
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * This class is used to handle user reported issues.
 *
 * It offers a flow that can be used to observe user reported issues.
 */
class UserReportedIssueHandler : CallCompositeEventHandler<CallCompositeUserReportedIssueEvent> {
    val userIssuesFlow = MutableStateFlow<CallCompositeUserReportedIssueEvent?>(null)

    override fun handle(eventData: CallCompositeUserReportedIssueEvent?) {
        userIssuesFlow.value = eventData
    }
}
