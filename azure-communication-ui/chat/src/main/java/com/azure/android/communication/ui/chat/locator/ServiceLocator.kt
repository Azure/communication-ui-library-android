// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.locator

/* Heterogeneous Service Locator
Will lazily construct objects as the graph requires.
Register TypedBuilders for each class you want
Use locate<T>(Class<T> clazz) to get/build the instance
 Usage:
        addTypedBuilder {
            BasicObjectHello()
        }

        addTypedBuilder {
            BasicObjectWorld()
        }

        addTypedBuilder {
            BasicObjectHelloWorld(
                locate(),
                locate()
            )
        }
        val basicObjectHello = locate<BasicObjectHelloWorld>()
 */

internal typealias TypedBuilder<T> = () -> T

internal class ServiceLocator {
    internal interface Disposable {
        fun dispose()
    }

    // @PublishedApi on internal will expose the value, but obfuscate it.
    //
    // This has the effect of "hiding" api that must be public
    // The reason for this "reified" type parameters. Reify is to "make more real/concrete"
    //
    // Reify only works on inline functions. inline functions can't use private member variable
    // because they are "inlined" to the calling code and can't see private scope.
    //
    // We Reify our types, because it lets us have much nicer syntax.
    // Instead of having to pass Class objects as parameters, we can get Class from <T>
    // This lets us register typed builders and locate them simple.
    //
    // E.g.
    // ```
    // addTypedBuilder() { MyClassImpl() as MyClass }
    // locate<MyClass>()
    // ```
    @PublishedApi
    internal val builders = HashMap<Any, TypedBuilder<*>>()

    @PublishedApi
    internal val implementations = HashMap<Any, Any>()

    // Adds a typed builder
    // Note: For core services, use Constructor Injection
    // This will allow us to "initializeAll" and validate the tree

    inline fun <reified T> addTypedBuilder(noinline builder: TypedBuilder<T>) {
        builders[T::class.java] = builder
    }

    // This resets the Service Locator to Initial
    // All Implementations are cleared
    // All Builders are removed
    fun clear() {
        for (implementation in implementations.values) {
            if (implementation is Disposable) {
                implementation.dispose()
            }
        }
        builders.clear()
        implementations.clear()
    }

    // Locate a class
    // locate<MyClass()
    //
    // Will initialize the class if it doesn't exist
    inline fun <reified T> locate(): T {
        require(builders.containsKey(T::class.java)) { "Builder for ${T::class.java} Does not exist" }
        return if (implementations.containsKey(T::class.java)) {
            implementations[T::class.java] as T
        } else {
            val instance = builders[T::class.java]!!()!!
            implementations[T::class.java] = instance
            instance as T
        }
    }

    // Initialize the entire tree
    // This will allow us to crash-fast and cache-fast
    inline fun initializeAll() {
        builders.keys.filter { !implementations.containsKey(it) }.forEach {
            implementations[it] = builders[it]?.invoke() as Any
        }
    }

    companion object {
        private val locatorMap = HashMap<Int, ServiceLocator>()

        fun getInstance(instanceId: Int): ServiceLocator {
            if (locatorMap.containsKey(instanceId)) {
                return locatorMap[instanceId]!!
            }
            locatorMap[instanceId] = ServiceLocator()
            return locatorMap[instanceId]!!
        }
    }
}
