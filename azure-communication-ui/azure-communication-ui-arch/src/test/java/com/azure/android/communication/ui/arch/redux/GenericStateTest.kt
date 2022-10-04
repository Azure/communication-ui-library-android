package com.azure.android.communication.ui.arch.redux

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Test
import java.lang.NullPointerException

class GenericStateTest {
    data class TestClass(val data: String)
    data class TestClass2(val data: String)
    @Test
    fun testGetSubState() {
        val genericState = GenericState(
            data = setOf(
                TestClass(data = "initial"),
                TestClass2(data = "another")
            )
        )

        assertNotNull(genericState.getSubState<TestClass>())
        assertNotNull(genericState.getSubState<TestClass2>())
    }

    @Test
    fun testReplace() {
        val genericState = GenericState(
            data = setOf(
                TestClass(data = "initial")
            )
        )

        assertEquals(genericState.getSubState<TestClass>().data, "initial")
        val genericState2 = genericState.replace(TestClass(data = "updated"))
        assertEquals(genericState2.getSubState<TestClass>().data, "updated")
    }

    @Test
    fun testRemove() {
        val genericState = GenericState(
            data = setOf(
                TestClass(data = "initial")
            )
        )
        assertEquals(genericState.getSubState<TestClass>().data, "initial")
        val genericState2 = genericState.remove<TestClass>()
        assertThrows(NullPointerException::class.java) {
            genericState2.getSubState<TestClass>()
        }
    }
}
