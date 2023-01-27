// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.repository.storage

import com.azure.android.communication.ui.chat.repository.MessageRepository
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MessageRepositoryPagedImplDelegateUnitTest {

    private fun getMessageRepo(): MessageRepository {
        return MessageRepository.createPageBackedRepository()
    }

    @Test
    fun messageRepositoryListStorage_addPage_test() =
        MessageRepositoryUnitTest.addPageTest(getMessageRepo())

    @Test
    fun messageRepositoryListStorage_editMessage_test() =
        MessageRepositoryUnitTest.editMessageTest(getMessageRepo())

    @Test
    fun messageRepositoryListStorage_removeMessage_test() =
        MessageRepositoryUnitTest.removeMessageTest(getMessageRepo())

    // This test checks if messages that come in via pages out of order, are correctly installed.
    // Without this, there is a possibility (not guarantee) that messages may come in the wrong pages
    // Or the page may be out of order
    //
    // This functionality has not been implemented yet, it probably looks like
    //   1) Check the min/max of a page, and see if it "fits" after the last page
    //   2) Move any items that don't belong in this page into more appropriate ones
    //   3) Sort incorrectly ordered page if necessary
    //
    // I don't know the true need of this, however have witnessed odd behaviours in paged data after edits.
    // Hence why we have the test case.
    //    @Test
    //    fun messageRepositoryListStorage_OutOfOrderTest() =
    //        MessageRepositoryUnitTest.outOfOrderTest(getMessageRepo())

    @Test
    fun messageRepositoryListStorage_indexOfTest() =
        MessageRepositoryUnitTest.indexOfTest(getMessageRepo())
}
