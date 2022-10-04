package com.azure.android.communication.ui.arch.redux

import java.lang.reflect.Type

// Generic State Object
//
// A book shelf for typed state
//
// You can check out a book, or replace it. They are indexed by Type
//
// Usage:

// ```
//        val state1 = GenericState(
//            data = setOf(
//                SomeClass(data = "initial")
//            )
//        )
//
//        val state2 = state1.replace(SomeClass(data = "updated"))
// ```
//
// Reified Usage Note:
//   Reify is to "Make more real"
//   In this case, I want the Type Parameters Reified to let me access the ::closs.java
//   Without it, we'd need to pass the types manually through the API, making it much uglier.
data class GenericState(val data: Set<Any>) {

    // Internal + PublishedApi gives it a obfuscated name
    // Needs to be PublishedApi for "reified" to work
    @PublishedApi
    internal val dataMap = HashMap<Type, Any>()

    init {
        data.forEach {
            dataMap[it::class.java] = it
        }
    }
    // / Gets a installed Sub State
    inline fun <reified T> getSubState() = dataMap[T::class.java] as T

    inline fun <reified T> remove(): GenericState =
        GenericState(data.filter { it::class.java != T::class.java }.toSet())

    // Rebuilds this GenericState with a patch of new data
    private fun rebuildWith(patch: Set<Any>): GenericState {
        val newData: HashMap<Type, Any> = HashMap()
        newData.putAll(this.dataMap)

        patch.forEach {
            newData[it::class.java] = it
        }

        return GenericState(newData.values.toSet())
    }

    fun replace(subState: Any) = rebuildWith(setOf(subState))
}
