package com.azure.android.communication.ui.chat.utilities

import java.lang.RuntimeException

internal class PagedList<T>(private val pages: List<List<T>>) : List<T> {

    private val indexes: List<Int>
    override val size: Int

    init {
        var offset = 0
        indexes = pages.map {
            offset += it.size
            offset - it.size // Start index on each item
        }
        size = offset
    }

    override fun contains(element: T): Boolean {
        // TODO: Binary page search, quick boundary checks on pages.
        for (page in pages) {
            if (page.contains(element)) {
                return true
            }
        }
        return false
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        for (element in elements) {
            if (!contains(element)) {
                return false
            }
        }
        return true
    }

    override fun get(index: Int): T {
        return when (pages.size) {
            1 -> pages.first()[index]
            else -> {
                val firstPageLength = pages.first().size
                if (index < firstPageLength) {
                    pages.first()[index]
                } else {
                    var page = 0
                    while (page < pages.size) {
                        val startIdx = indexes[page]
                        val endIdx = indexes[page] + pages[page].size
                        if (index in startIdx until endIdx) {
                            return pages[page][index - startIdx]
                        }
                        page++
                    }
                    throw RuntimeException("Out of range of list")
                }
            }
        }
    }

    fun pageOf(element: T): Int {
        var index = 0
        for ((pageNum, page) in pages.withIndex()) {
            val elementIndex = page.indexOf(element)
            if (elementIndex != -1) {
                return pageNum
            }
            index += page.size
        }
        return -1
    }

    override fun indexOf(element: T): Int {
        var index = 0
        for (page in pages) {
            val elementIndex = page.indexOf(element)
            if (elementIndex != -1) {
                return index + elementIndex
            }
            index += page.size
        }
        return -1
    }

    override fun isEmpty(): Boolean {
        return size == 0
    }

    override fun lastIndexOf(element: T): Int {
        // We can cheat here, there won't be duplicates
        // But if you wanted to use this generally this should be implemented proper
        return indexOf(element)
    }

    override fun iterator(): Iterator<T> {
        TODO("Not yet implemented")
    }
    override fun listIterator(): ListIterator<T> {
        return listIterator(0)
    }
    override fun listIterator(index: Int): ListIterator<T> {
        return object : ListIterator<T> {
            var currentIndex = index

            override fun hasNext(): Boolean {
                return currentIndex < size
            }

            override fun next(): T {
                currentIndex++
                return get(currentIndex)
            }

            override fun hasPrevious(): Boolean {
                return currentIndex > 0
            }

            override fun nextIndex(): Int {
                return currentIndex + 1
            }

            override fun previous(): T {
                currentIndex--
                return get(currentIndex)
            }

            override fun previousIndex(): Int {
                return currentIndex - 1
            }
        }
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<T> {
        TODO("Not yet implemented")
    }
}
