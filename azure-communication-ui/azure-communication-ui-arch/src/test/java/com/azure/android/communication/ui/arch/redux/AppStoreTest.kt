package com.azure.android.communication.ui.arch.redux

import org.junit.Test
import java.util.*
import java.util.concurrent.CountDownLatch

class AppStoreTest {
    data class SimpleState(val count: Int)

    class Increment

    class SimpleReducer : Reducer<GenericState> {
        override fun reduce(state: GenericState, action: Any): GenericState {
            when (action) {
                is Increment -> {
                    val currentSimpleState = state.getSubState<SimpleState>()
                    return state.replace(SimpleState(currentSimpleState.count + 1))
                }
            }
            return state
        }
    }

    @Test
    fun testImmediateReduction() {
        // Testing Immediate Reduction is Simple
        // We just set up the store in immediate mode
        // And Dispatch a action
        val state = GenericState(
            mapOf(
                SimpleState::class.java to SimpleState(10)
            )
        )

        val appStore = AppStore(
            initialState = state,
            reducer = SimpleReducer(),
            middlewares = Collections.emptyList(),
            threadingMode = AppStoreThreadingMode.Immediate
        )

        var listenedState: GenericState? = null

        val testListener = StoreListener<GenericState> {
            listenedState = it
        }
        appStore.addListener(testListener)
        assert(appStore.getCurrentState().getSubState<SimpleState>().count == 10)
        appStore.dispatch(Increment())
        assert(appStore.getCurrentState().getSubState<SimpleState>().count == 11)
        assert(listenedState == appStore.getCurrentState())
    }


    @Test
    fun testThreadedReduction() {
        // Testing Threaded Reduction is the Same as Immediate
        // But you have to wait for your callback to know the store has been updated
        // A CountDownLatch is used to wait for the thread
        val state = GenericState(
            mapOf(
                SimpleState::class.java to SimpleState(10)
            )
        )

        val appStore = AppStore(
            initialState = state,
            reducer = SimpleReducer(),
            middlewares = Collections.emptyList(),
            threadingMode = AppStoreThreadingMode.Threaded
        )

        var listenedState: GenericState? = null

        // Wait for listener
        val latch = CountDownLatch(1)
        val testListener = StoreListener<GenericState> {
            listenedState = it
            latch.countDown()
        }
        appStore.addListener(testListener)
        assert(appStore.getCurrentState().getSubState<SimpleState>().count == 10)
        appStore.dispatch(Increment())

        // We need to wait now for the latch to clear
        latch.await()

        // And store should be updated
        assert(appStore.getCurrentState().getSubState<SimpleState>().count == 11)
        assert(listenedState == appStore.getCurrentState())
    }


    class LoggingMiddleware : Middleware<GenericState> {

        val log = ArrayList<Any>()

        override fun invoke(store: Store<GenericState>) = { next: Dispatch ->
            { action: Any ->
                log.add(action)
                next(action)
            }
        }
    }

    @Test
    fun testMiddleware() {
        // To Check Middleware we will create 2 simple Logging Middleware
        // Dispatch some actions, and see if they catch it
        val state = GenericState(
            mapOf(
                SimpleState::class.java to SimpleState(10)
            )
        )
        val loggerA = LoggingMiddleware()
        val loggerB = LoggingMiddleware()
        val appStore = AppStore(
            initialState = state,
            reducer = SimpleReducer(),
            middlewares = mutableListOf(loggerA, loggerB),
            threadingMode = AppStoreThreadingMode.Immediate
        )

        appStore.dispatch(Increment())
        assert(loggerA.log.size == 1)
        assert(loggerB.log.size == 1)

        appStore.dispatch(Increment())
        assert(loggerA.log.size == 2)
        assert(loggerB.log.size == 2)
    }
}