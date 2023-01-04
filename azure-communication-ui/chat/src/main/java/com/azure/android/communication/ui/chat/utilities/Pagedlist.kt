package com.azure.android.communication.ui.chat.utilities
internal class PagedList<T>(val pages: List<List<T>>, val pageSize: Int) : List<T> {
    private val pageCount = pages.size

    override val size: Int
        get() {
            return when (pages.size) {
                0 -> 0
                1 -> pages.first().size
                else -> pageSize * (pageCount - 2) + pages.first().size + pages.last().size
            }
        }

    override fun contains(element: T): Boolean {
        //TODO: Binary page search, quick boundary checks on pages.
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
                    pages.first()[index];
                } else {
                    val offset = index - firstPageLength;
                    val pageOffset = offset % pageSize
                    val page = offset / pageSize + 1
                    pages[page][pageOffset]
                }
            }
        }
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
        TODO("Not yet implemented")
    }

    override fun listIterator(index: Int): ListIterator<T> {
        TODO("Not yet implemented")
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<T> {
        TODO("Not yet implemented")
    }
}