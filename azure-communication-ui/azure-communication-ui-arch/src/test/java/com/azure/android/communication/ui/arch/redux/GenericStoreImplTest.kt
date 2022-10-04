package com.azure.android.communication.ui.arch.redux

import org.junit.Test
import java.util.Collections
import java.util.concurrent.CountDownLatch

class GenericStoreImplTest {
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
            setOf(
                SimpleState(10)
            )
        )

        val genericStoreImpl = GenericStoreImpl(
            initialState = state,
            reducer = SimpleReducer(),
            middlewares = Collections.emptyList(),
            threadingMode = GenericStoreThreadingMode.Immediate
        )

        var listenedState: GenericState? = null

        val testListener = StoreListener {
            listenedState = it
        }
        genericStoreImpl.addListener(testListener)
        assert(genericStoreImpl.getCurrentState().getSubState<SimpleState>().count == 10)
        genericStoreImpl.dispatch(Increment())
        assert(genericStoreImpl.getCurrentState().getSubState<SimpleState>().count == 11)
        assert(listenedState == genericStoreImpl.getCurrentState())
    }

    @Test
    fun testThreadedReduction() {
        // Testing Threaded Reduction is the Same as Immediate
        // But you have to wait for your callback to know the store has been updated
        // A CountDownLatch is used to wait for the thread
        val state = GenericState(
            setOf(
                SimpleState(10)
            )
        )

        val genericStoreImpl = GenericStoreImpl(
            initialState = state,
            reducer = SimpleReducer(),
            middlewares = Collections.emptyList(),
            threadingMode = GenericStoreThreadingMode.Threaded
        )

        var listenedState: GenericState? = null

        // Wait for listener
        val latch = CountDownLatch(1)
        val testListener = StoreListener {
            listenedState = it
            latch.countDown()
        }
        genericStoreImpl.addListener(testListener)
        assert(genericStoreImpl.getCurrentState().getSubState<SimpleState>().count == 10)
        genericStoreImpl.dispatch(Increment())

        // We need to wait now for the latch to clear
        latch.await()

        // And store should be updated
        assert(genericStoreImpl.getCurrentState().getSubState<SimpleState>().count == 11)
        assert(listenedState == genericStoreImpl.getCurrentState())
    }

    @Test
    fun testMiddleware() {
        // To Check Middleware we will create 2 simple Logging Middleware
        // Dispatch some actions, and see if they catch it
        val state = GenericState(
            setOf(
                SimpleState(10)
            )
        )
        val loggerA = LoggingMiddleware()
        val loggerB = LoggingMiddleware()
        val genericStoreImpl = GenericStoreImpl(
            initialState = state,
            reducer = SimpleReducer(),
            middlewares = mutableListOf(loggerA, loggerB),
            threadingMode = GenericStoreThreadingMode.Immediate
        )

        genericStoreImpl.dispatch(Increment())
        assert(loggerA.log.size == 1)
        assert(loggerB.log.size == 1)

        genericStoreImpl.dispatch(Increment())
        assert(loggerA.log.size == 2)
        assert(loggerB.log.size == 2)
    }

    // Simple Logging Middleware
    class LoggingMiddleware : Middleware {

        val log = ArrayList<Any>()

        override fun invoke(store: GenericStore) = { next: Dispatch ->
            { action: Any ->
                log.add(action)
                next(action)
            }
        }
    }
}
