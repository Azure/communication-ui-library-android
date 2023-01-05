package com.azure.android.communication.ui.chat.utilities

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test


internal class PagedListTest {

    private val listFull = PagedList(listOf(
        listOf(0,1),
        listOf(2,3,4),
        listOf(5,6,7),
        listOf(8,9),
    ))

    private val listOnePage = PagedList(listOf(
        listOf(0,1),
    ))

    private val listEmpty = PagedList<Int>(emptyList())

    @Test
    fun testPagedListSize() {
        assertEquals(listFull.size, 10);
        assertEquals(listOnePage.size, 2);
        assertEquals(listEmpty.size, 0);
    }

    @Test
    fun testIsEmptyWorks() {
        assertFalse(listFull.isEmpty())
        assertFalse(listOnePage.isEmpty())
        assertTrue(listEmpty.isEmpty())
    }

    @Test
    fun testGetWorks() {
        for (i in 0 until listFull.size) {
            assertEquals(listFull[i], i);
        }
    }


    @Test
    fun testContainsWorks() {
        for (i in 0 until listFull.size) {
            assertTrue(listFull.contains(i))
        }
        assertFalse(listFull.contains(-1))
        assertFalse(listFull.contains(10))
    }

    @Test
    fun testContainsAllWorks() {
        assertTrue(listFull.containsAll(listOf(0,1,2,3,4,5,6,7,8,9)))
        assertFalse(listFull.containsAll(listOf(0,1,2,3,4,5,6,7,8,9,10)))
        assertFalse(listFull.containsAll(listOf(-1,0,1,2,3,4,5,6,7,8,9)))
        assertFalse(listFull.containsAll(listOf(-1,10)))
    }

    @Test
    fun testIndexOfWorks() {
        for (i in 0 until listFull.size) {
            assertEquals(listFull.indexOf(i), i)
        }
        assertEquals(listFull.indexOf(-1), -1)
        assertEquals(listFull.indexOf(10), -1)
    }

    @Test
    fun testLastIndexOfWorks() {
        for (i in 0 until listFull.size) {
            assertEquals(listFull.lastIndexOf(i), i)
        }
        assertEquals(listFull.lastIndexOf(-1), -1)
        assertEquals(listFull.lastIndexOf(10), -1)
    }

}