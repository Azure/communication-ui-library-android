package com.azure.android.communication.ui.chat.implementation.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.azure.android.communication.ui.arch.locator.ServiceLocator
import com.azure.android.communication.ui.arch.redux.GenericState
import com.azure.android.communication.ui.arch.redux.GenericStore
import com.azure.android.communication.ui.arch.redux.GenericStoreImpl
import com.azure.android.communication.ui.chat.implementation.redux.reducers.MockReducer
import com.azure.android.communication.ui.chat.implementation.redux.states.MockParticipant
import com.azure.android.communication.ui.chat.implementation.redux.states.MockUIChatState
import com.azure.android.communication.ui.chat.implementation.ui.mock.MeetingRobot
import com.azure.android.communication.ui.chat.implementation.ui.mock.MessageFaker
import com.azure.android.communication.ui.chat.implementation.ui.view.MockStateComposeChatView
import java.util.Collections

class RobotDrivenMockComposeChatActivity : AppCompatActivity() {
    private val serviceLocator = ServiceLocator.getInstance(9999)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            initializeMockUI()
        }
        serviceLocator.locate<MeetingRobot>().start()
        setContentView(MockStateComposeChatView(this))
    }

    override fun onDestroy() {
        serviceLocator.locate<MeetingRobot>().stop()
        super.onDestroy()
    }

    // This setups the service locator
    // It also sets up the ReduxViewModel for this view
    private fun initializeMockUI() {
        serviceLocator.clear()
        serviceLocator.addTypedBuilder { MessageFaker(this) }
        serviceLocator.addTypedBuilder<GenericStore> {
            GenericStoreImpl(
                initialState = GenericState(
                    setOf(
                        MockUIChatState(
                            mockParticipants = listOf(MockParticipant(displayName = "Local Participant", isCurrentUser = true)),
                            mockMessages = Collections.emptyList()
                        )
                    )
                ),
                reducer = MockReducer(), middlewares = mutableListOf()
            ) // Yes Cast Needed (to not use Implementation Type in Service Locator)
        }
        serviceLocator.addTypedBuilder { MeetingRobot(this, serviceLocator.locate()) }
    }
}
