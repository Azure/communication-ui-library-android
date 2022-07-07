package com.azure.android.communication.ui.arch.redux

import org.junit.Assert.*
import org.junit.Test

class GenericStateTest {
    data class TestClass(val data: String)
    @Test
    fun testBasicState() {
        val genericState = GenericState(data = mapOf(
            TestClass::class.java to TestClass(data = "initial")
        ))

        assertNotNull(genericState.getSubState<TestClass>())
    }

    @Test
    fun testStateReplace() {
        val genericState = GenericState(data = mapOf(
            TestClass::class.java to TestClass(data = "initial")
        ))

        assertEquals(genericState.getSubState<TestClass>()?.data, "initial")
        val genericState2 = genericState.copy(
            data = mapOf(
                TestClass::class.java to TestClass(data = "updated")
            )
        )
        assertEquals(genericState2.getSubState<TestClass>()?.data, "updated")
    }


    @Test
    fun testStateRemove() {
        val genericState = GenericState(data = mapOf(
            TestClass::class.java to TestClass(data = "initial")
        ))
        assertEquals(genericState.getSubState<TestClass>()?.data, "initial")
        val genericState2 = genericState.rebuildWith(
            patch = mapOf(
                TestClass::class.java to null
            )
        )
        assertNull(genericState2.getSubState<TestClass>())
    }
}