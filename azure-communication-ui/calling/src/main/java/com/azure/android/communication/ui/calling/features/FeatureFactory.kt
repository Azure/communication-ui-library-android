package com.azure.android.communication.ui.calling.features

import com.azure.android.communication.ui.calling.features.stubs.TestFeatureA

class FeatureFactory {
    companion object {
        val myMap: MutableMap<Class<*>, AcsFeature> = mutableMapOf()

        init {
            listOf(
                TestFeatureA::class.java
            ).forEach { stub ->
                val implClassName = "com.azure.android.communication.ui.calling.features.implementations.${stub.simpleName}Impl"


                try {
                    // Try and install the implementation
                    val implClass = Class.forName(implClassName)
                    if (AcsFeature::class.java.isAssignableFrom(implClass)) {
                        myMap[stub] = implClass.getDeclaredConstructor().newInstance() as AcsFeature
                    }
                } catch (e: ClassNotFoundException) {
                    // If the implementation class was not found, use the stub
                    myMap[stub] = stub.newInstance()
                }
            }
        }

        inline fun <reified T : AcsFeature> get(): T {
            val feature = myMap[T::class.java] ?: throw Exception("Feature not found")
            return feature as T
        }


    }
}
