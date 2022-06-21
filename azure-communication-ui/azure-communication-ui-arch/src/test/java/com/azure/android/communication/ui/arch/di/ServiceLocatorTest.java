// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.arch.di;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/// Tests for the Service Locator Pattern
public class ServiceLocatorTest {

    class BasicObjectHello {
        final String data = "hello";
    }

    class BasicObjectWorld {
        final String data = "world";
    }

    class BasicObjectHelloWorld {
        final BasicObjectHello hello;
        final BasicObjectWorld world;
        private final String data;

        BasicObjectHelloWorld(final BasicObjectHello hello, final BasicObjectWorld world) {
            this.hello = hello;
            this.world = world;
            this.data = hello.data + " " + world.data;
        }
    }

    @Test
    public void basicTest() {
        final ServiceLocator locator = new ServiceLocator();
        locator.addTypedBuilder(BasicObjectHello.class, (ServiceLocator inlineLocator) -> new BasicObjectHello());
        locator.addTypedBuilder(BasicObjectWorld.class, (ServiceLocator inlineLocator) -> new BasicObjectWorld());

        final BasicObjectHello basicObjectHello = locator.locate(BasicObjectHello.class);
        final BasicObjectWorld basicObjectWorld = locator.locate(BasicObjectWorld.class);
        assertEquals(basicObjectHello.data, "hello");
        assertEquals(basicObjectWorld.data, "world");
    }


    @Test
    public void dependenciesTest() {
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

        assertEquals(basicObjectHello.data, "hello world");
    }
}
