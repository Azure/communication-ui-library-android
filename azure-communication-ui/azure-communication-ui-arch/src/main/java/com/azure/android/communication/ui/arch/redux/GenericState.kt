package com.azure.android.communication.ui.arch.redux

import java.lang.reflect.Type

/// Generic State Object
///
/// This state object justs holds/replaces types data
/// There should be 1 type to 1 state class
/// RebuildWith will generate a new GenericState

data class GenericState(val data:Map<Type, Any>) {
    init {
        // Validate
        for (key in data.keys) {
            if (data[key] == null) {
                throw IllegalArgumentException("$key has a null entry")
            }
        }
    }
    /// Gets a installed Sub State
    inline fun<reified T> getSubState() = data[T::class.java] as T?;

    /// Rebuilds this GenericState with a patch of new data
    fun rebuildWith(patch:Map<Type, Any>): GenericState {
        val newData:HashMap<Type,Any> = HashMap()
        newData.putAll(this.data)
        newData.putAll(patch)
        newData.keys.forEach() {
            if (newData[it] == null) {
                newData.remove(it)
            }
        }
        return GenericState(newData)
    }
}