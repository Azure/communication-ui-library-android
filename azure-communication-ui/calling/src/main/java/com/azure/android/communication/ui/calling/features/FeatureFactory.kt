package com.azure.android.communication.ui.calling.features

class FeatureFactory {
    companion object {
        val myMap: MutableMap<Class<*>, AcsFeature> = mutableMapOf()

        init {
            val stubs = FeatureInterfaces::class.sealedSubclasses

            for (stub in stubs) {
                val implClassName = "com.azure.android.communication.ui.calling.features.implementations.${stub.simpleName}Impl"

                try {
                    val implClass = Class.forName(implClassName)
                    if (AcsFeature::class.java.isAssignableFrom(implClass)) {
                        myMap[stub.java] = implClass.getDeclaredConstructor().newInstance() as AcsFeature
                    }
                } catch (e: ClassNotFoundException) {
                    // If the implementation class was not found, use the stub
                }
            }
        }

        inline fun <reified T : AcsFeature> get(): T {
            val feature = myMap[T::class.java] ?: throw Exception("Feature not found")
            return feature as T
        }
    }
}
