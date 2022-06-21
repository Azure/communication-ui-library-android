// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.arch.locator;


import java.util.HashMap;

/* Heterogeneous Service Locator

Will lazily construct objects as the graph requires.

Register TypedBuilders for each class you want
Use locate<T>(Class<T> clazz) to get/build the instance

 Usage:
    final ServiceLocator locator = new ServiceLocator();
    locator.addTypedBuilder(BasicObjectHello.class, (ServiceLocator inlineLocator) -> new BasicObjectHello());
    locator.addTypedBuilder(BasicObjectWorld.class, (ServiceLocator inlineLocator) -> new BasicObjectWorld());
    locator.addTypedBuilder(BasicObjectHelloWorld.class, (ServiceLocator inlineLocator) ->
        new BasicObjectHelloWorld(
                inlineLocator.locate(BasicObjectHello.class),
                inlineLocator.locate(BasicObjectWorld.class)
        )
    );

    final BasicObjectHelloWorld basicObjectHello = locator.locate(BasicObjectHelloWorld.class);
 */


public class ServiceLocator {

    /// Global Instance
    static final ServiceLocator INSTANCE = new ServiceLocator();

    interface Disposable {
        void dispose();
    }

    interface TypedBuilder<T> {
        // Builds a type
        T build(ServiceLocator locator);
    }

    private final HashMap<Class<?>, TypedBuilder> builders = new HashMap<>();
    private final HashMap<Class<?>, Object> implementations = new HashMap<>();

    <T> void addTypedBuilder(final Class<T> klass, final TypedBuilder<T> builder) {
        builders.put(klass, builder);
    }

    /// Clear the implementation map
    void clear() {
        for (final Object implementation : implementations.values()) {
            if (implementation instanceof Disposable) {
                ((Disposable) implementation).dispose();
            }
        }
        builders.clear();
        implementations.clear();
    }

    // Locate a class
    <T> T locate(final Class<T> klass) {
        if (!builders.containsKey(klass)) {
            throw new IllegalArgumentException("Builder for " + klass + " Does not exist");
        }
        if (implementations.containsKey(klass)) {
            return (T) implementations.get(klass);
        } else {
            final Object instance = builders.get(klass).build(this);
            implementations.put(klass, instance);
            return (T) instance;
        }
    }
}
