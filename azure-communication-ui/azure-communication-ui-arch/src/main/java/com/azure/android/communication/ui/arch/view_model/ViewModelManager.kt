package com.azure.android.communication.ui.arch.view_model

import com.azure.android.communication.ui.arch.locator.ServiceLocator
import com.azure.android.communication.ui.arch.redux.Store
import com.azure.android.communication.ui.arch.redux.StoreListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect

// Builds a Model
// S = State object (E.g. GenericState)
// M = Model Object

class StoreModelBuilder<M,S>(val builder: (state: S)->M, store: Store<S>) {
    private val mutableStateFlow = MutableStateFlow(builder(store.getCurrentState()))
    suspend fun collect() = (mutableStateFlow as StateFlow<M>).collect()

    fun rebuild(state: S) {
        val newState = builder(state)
        if (newState != mutableStateFlow.value) {
            mutableStateFlow.value = newState
        }
    }
}

// Factory that Generates ViewModels from the Store
class ViewModelManager<S>(private val store: Store<S>) {
    private val builders : HashSet<StoreModelBuilder<*, S>> = HashSet()
    private val stateFlows : HashMap<StoreModelBuilder<*, S>, MutableStateFlow<*>> = HashMap();

    fun addBuilder(builder: StoreModelBuilder<*,S>) {
        builders.add(builder)
        stateFlows[builder] = MutableStateFlow(builder.rebuild(store.getCurrentState()))
    }

    fun removeBuilder(builder: StoreModelBuilder<*,S>) {
        builders.add(builder)
        stateFlows.remove(builder)
    }

    private val storeListener = StoreListener<S> { state ->
        builders.forEach { builder -> builder.rebuild(state) }
    }

    fun start() {
        store.addListener(storeListener)
    }

    fun stop() {
        store.removeListener(storeListener)
    }
}

