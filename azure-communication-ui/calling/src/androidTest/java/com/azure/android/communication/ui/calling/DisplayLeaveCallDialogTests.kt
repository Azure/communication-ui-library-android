package com.azure.android.communication.ui.calling

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import com.azure.android.communication.BaseUiTest
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.tapWhenDisplayed
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.calling.models.CallCompositeCallScreenControlBarOptions
import com.azure.android.communication.ui.calling.models.CallCompositeCallScreenOptions
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateCode
import com.azure.android.communication.ui.calling.models.CallCompositeDismissedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeGroupCallLocator
import com.azure.android.communication.ui.calling.models.CallCompositeLeaveCallConfirmationMode
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteOptions
import com.azure.android.communication.waitUntilDisplayed
import java9.util.concurrent.CompletableFuture
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers
import org.junit.Test
import java.util.UUID

internal class DisplayLeaveCallDialogTests : BaseUiTest() {
    private val leaveCallDrawerId = R.id.bottom_drawer_table
    private var isExitCompositeReceived = false

    private val callCompositeDismissedEvent = CallCompositeEventHandler<CallCompositeDismissedEvent> {
        isExitCompositeReceived = true
        exitCallCompletableFuture.complete(null)
    }
    private val exitCallCompletableFuture = CompletableFuture<Void>()

    @Test
    @ExperimentalCoroutinesApi
    fun displayLeaveCallDialog_turnON() = runTest {
        injectDependencies(testScheduler)

        // Launch the UI.
        val callComposite = createAndLaunchCallCompositeWithOption(CallCompositeLeaveCallConfirmationMode.ALWAYS_ENABLED)

        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)
        tapWhenDisplayed(endCallId)
        waitUntilDisplayed(leaveCallDrawerId)

        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.azure_communication_ui_cell_text),
                ViewMatchers.withText("Leave"),
                ViewMatchers.isDisplayed()
            )
        ).perform(ViewActions.click())
        confirmCallEnded(callComposite)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun displayLeaveCallDialog_turnOF() = runTest {
        injectDependencies(testScheduler)

        // Launch the UI.
        val callComposite = createAndLaunchCallCompositeWithOption(CallCompositeLeaveCallConfirmationMode.ALWAYS_DISABLED)
        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)
        tapWhenDisplayed(endCallId)
        confirmCallEnded(callComposite)
    }

    private fun createAndLaunchCallCompositeWithOption(displayLeaveCall: CallCompositeLeaveCallConfirmationMode) : CallComposite {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val callComposite = CallCompositeBuilder()
            .callScreenOptions(
                CallCompositeCallScreenOptions(
                CallCompositeCallScreenControlBarOptions().setLeaveCallConfirmation(displayLeaveCall)
            )
            )
            .build()

        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions({ "token" }, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)
        val remoteOptions =
            CallCompositeRemoteOptions(
                CallCompositeGroupCallLocator(UUID.fromString("74fce2c1-520f-11ec-97de-71411a9a8e14")),
                communicationTokenCredential,
                "test"
            )
        callComposite.addOnDismissedEventHandler(callCompositeDismissedEvent)
        callComposite.launchTest(appContext, remoteOptions, null)
        return callComposite
    }

    private fun confirmCallEnded(callComposite : CallComposite) {
        exitCallCompletableFuture.whenComplete { _, _ ->
            assert(isExitCompositeReceived)
            assert(callComposite.callState == CallCompositeCallStateCode.NONE)
        }
    }
}