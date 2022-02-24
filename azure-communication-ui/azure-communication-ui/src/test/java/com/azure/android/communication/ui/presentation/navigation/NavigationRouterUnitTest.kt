// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.navigation

import com.azure.android.communication.ui.helper.MainCoroutineRule
import com.azure.android.communication.ui.redux.AppStore
import com.azure.android.communication.ui.redux.state.AppReduxState
import com.azure.android.communication.ui.redux.state.NavigationState
import com.azure.android.communication.ui.redux.state.NavigationStatus
import com.azure.android.communication.ui.redux.state.ReduxState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

/**
 * This set of unit tests demonstrate how NavigationRouterImpl works
 *
 */
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
internal class NavigationRouterUnitTest {
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private fun createNavigationRouter(stateFlow: MutableStateFlow<ReduxState>): Pair<NavigationRouter, List<NavigationStatus>> {
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { getStateFlow() } doReturn stateFlow
        }
        val navigationRouter = NavigationRouterImpl(mockAppStore)
        val receivedUpdates = mutableListOf<NavigationStatus>()
        navigationRouter.addOnNavigationStateChanged { receivedUpdates.add(it) }

        return Pair(navigationRouter, receivedUpdates)
    }

    @Test
    fun store_onSubscribe_then_invoke_navigationRouterOnStateChange() =
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val appState = AppReduxState("").apply {
                navigationState = NavigationState(NavigationStatus.IN_CALL)
            }

            val stateFlow: MutableStateFlow<ReduxState> = MutableStateFlow(AppReduxState(""))
            val (navigationRouter, receivedUpdates) = createNavigationRouter(stateFlow)

            // act
            val navigationRouterLaunchJob = launch {
                navigationRouter.start()
            }

            stateFlow.value = appState
            // a new state but with same navigation state
            stateFlow.value =
                AppReduxState("").apply {
                    navigationState = NavigationState(NavigationStatus.IN_CALL)
                }

            // assert
            Assert.assertEquals(2, receivedUpdates.count())
            Assert.assertEquals(NavigationStatus.SETUP, receivedUpdates[0])
            Assert.assertEquals(NavigationStatus.IN_CALL, receivedUpdates[1])

            navigationRouterLaunchJob.cancel()
        }

    @Test
    fun store_onSubscribe_then_test_navigationRouterOnStateChange_emits_initial_state() =
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val initialState = AppReduxState("")
            val appState = AppReduxState("").apply {
                navigationState = NavigationState(NavigationStatus.EXIT)
            }

            val stateFlow: MutableStateFlow<ReduxState> = MutableStateFlow(initialState)
            val (navigationRouter, receivedUpdates) = createNavigationRouter(stateFlow)

            // act
            val navigationRouterLaunchJob = launch {
                navigationRouter.start()
            }

            stateFlow.value = appState

            // assert
            Assert.assertEquals(2, receivedUpdates.count())
            Assert.assertEquals(initialState.navigationState.navigationState, receivedUpdates[0])
            Assert.assertEquals(appState.navigationState.navigationState, receivedUpdates[1])

            navigationRouterLaunchJob.cancel()
        }

    @Test
    fun store_onSubscribe_then_test_navigationRouterNewButSameNavigationStateOnNavigationStateChange_called_1x() =
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val initialState = AppReduxState("").apply {
                navigationState = NavigationState(NavigationStatus.IN_CALL)
            }
            val appState = AppReduxState("").apply {
                navigationState = NavigationState(NavigationStatus.IN_CALL)
            }

            val stateFlow: MutableStateFlow<ReduxState> = MutableStateFlow(initialState)
            val (navigationRouter, receivedUpdates) = createNavigationRouter(stateFlow)

            // act
            val navigationRouterLaunchJob = launch {
                navigationRouter.start()
            }

            stateFlow.value = appState
            // a new state but with same navigation state
            stateFlow.value =
                AppReduxState("").apply {
                    navigationState = NavigationState(NavigationStatus.IN_CALL)
                }

            // assert
            Assert.assertEquals(1, receivedUpdates.count())
            Assert.assertEquals(NavigationStatus.IN_CALL, receivedUpdates[0])

            navigationRouterLaunchJob.cancel()
        }

    @Test
    fun store_onSubscribe_then_test_navigationRouterWithSameStateOnNavigationStateChange_called_once() =
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val appState = AppReduxState("").apply {
                navigationState = NavigationState(NavigationStatus.SETUP)
            }

            val stateFlow: MutableStateFlow<ReduxState> = MutableStateFlow(appState)
            val (navigationRouter, receivedUpdates) = createNavigationRouter(stateFlow)

            // act
            val navigationRouterLaunchJob = launch {
                navigationRouter.start()
            }

            stateFlow.value = appState
            // a new state but with same navigation state
            stateFlow.value =
                AppReduxState("").apply {
                    navigationState = NavigationState(NavigationStatus.SETUP)
                }

            // assert
            Assert.assertEquals(1, receivedUpdates.count())
            Assert.assertEquals(NavigationStatus.SETUP, receivedUpdates[0])

            navigationRouterLaunchJob.cancel()
        }

    @Test
    fun store_onSubscribe_then_test_navigationRouterOnNavigationStateChange_called_3x() =
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val initialState = AppReduxState("")
            val appState = AppReduxState("").apply {
                navigationState = NavigationState(NavigationStatus.IN_CALL)
            }

            val stateFlow: MutableStateFlow<ReduxState> = MutableStateFlow(initialState)
            val (navigationRouter, receivedUpdates) = createNavigationRouter(stateFlow)

            // act
            val navigationRouterLaunchJob = launch {
                navigationRouter.start()
            }

            stateFlow.value = appState
            // a new state but with different navigation state
            stateFlow.value =
                AppReduxState("").apply {
                    navigationState = NavigationState(NavigationStatus.SETUP)
                }

            // assert
            Assert.assertEquals(3, receivedUpdates.count())
            Assert.assertEquals(NavigationStatus.SETUP, receivedUpdates[0])
            Assert.assertEquals(NavigationStatus.IN_CALL, receivedUpdates[1])
            Assert.assertEquals(NavigationStatus.SETUP, receivedUpdates[2])

            navigationRouterLaunchJob.cancel()
        }
}
