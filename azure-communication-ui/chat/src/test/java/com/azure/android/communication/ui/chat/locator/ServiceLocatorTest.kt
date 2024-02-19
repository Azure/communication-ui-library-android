// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.android.communication.ui.chat.locator

import org.junit.Assert
import org.junit.Test

// / Tests for the Service Locator Pattern
class ServiceLocatorTest {
    /*
        ScaffoldingObjects
        BasicObjectHello = stores: "hello"
        BasicObjectWorld = stores: "world"
        BasicObjectHelloWorld = Combines above objects to output "hello world"
     */
    internal inner class BasicObjectHello {
        val data = "hello"
    }

    internal inner class BasicObjectWorld : ServiceLocator.Disposable {
        val data = "world"
        var disposed = false

        override fun dispose() {
            disposed = true
        }
    }

    internal inner class BasicObjectHelloWorld(
        hello: BasicObjectHello?,
        world: BasicObjectWorld?,
    ) {
        val data: String

        init {
            data = hello!!.data + " " + world!!.data
        }
    }

    // Test basic, no-dep location
    @Test
    fun basicTest() {
        val locator = ServiceLocator()
        locator.addTypedBuilder {
            BasicObjectHello()
        }

        locator.addTypedBuilder {
            BasicObjectWorld()
        }

        val basicObjectHello = locator.locate<BasicObjectHello>()
        val basicObjectWorld = locator.locate<BasicObjectWorld>()

        Assert.assertEquals(basicObjectHello.data, "hello")
        Assert.assertEquals(basicObjectWorld.data, "world")
    }

    // Test that Dispose is called after clear()
    @Test
    fun disposeTest() {
        val locator = ServiceLocator()

        locator.addTypedBuilder { BasicObjectWorld() }
        val basicObjectWorld = locator.locate<BasicObjectWorld>()
        Assert.assertEquals(basicObjectWorld.disposed, false)
        locator.clear()
        Assert.assertEquals(basicObjectWorld.disposed, true)
    }

    // Test to see if the HelloWorld object can be built
    @Test
    fun dependenciesTest() {
        val locator = ServiceLocator()

        locator.addTypedBuilder {
            BasicObjectHello()
        }

        locator.addTypedBuilder {
            BasicObjectWorld()
        }

        locator.addTypedBuilder {
            BasicObjectHelloWorld(
                locator.locate(),
                locator.locate(),
            )
        }

        val basicObjectHello = locator.locate<BasicObjectHelloWorld>()

        Assert.assertEquals(basicObjectHello.data, "hello world")
    }

    @Test
    fun failedDependencyTestWithInitializeAll() {
        val locator = ServiceLocator()

        locator.addTypedBuilder {
            BasicObjectHello()
        }

        locator.addTypedBuilder {
            BasicObjectHelloWorld(
                locator.locate(),
                locator.locate(),
            )
        }

        Assert.assertThrows(java.lang.IllegalArgumentException::class.java) {
            locator.initializeAll()
        }
    }
}
