// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.azure.android.communication.ui.calling.data.CallHistoryRepository
import com.azure.android.communication.ui.calling.data.CallHistoryRepositoryImpl
import com.azure.android.communication.ui.calling.logger.DefaultLogger
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.threeten.bp.OffsetDateTime
import java.util.UUID

// To test CallHistoryRepository we need to have context, so putting this test the androidTest
internal class CallHistoryRepositoryTest {
    @Test
    @ExperimentalCoroutinesApi
    fun callHistoryService_onCallStateUpdate_callsRepositoryInsert() =
        runTest {
            val context: Context = ApplicationProvider.getApplicationContext()
            AndroidThreeTen.init(context.applicationContext)

            val repository: CallHistoryRepository = CallHistoryRepositoryImpl(context, DefaultLogger())

            val originalList = repository.getAll()
            val olderThanMonth =
                originalList.firstOrNull {
                    // should not have records older then now() - 31 days.
                    // Subtract a min to account possible delay while executing.
                    it.callStartedOn.isBefore(OffsetDateTime.now().minusDays(31).minusMinutes(1))
                }
            Assert.assertNull(olderThanMonth)

            var callId = UUID.randomUUID().toString()
            var callStartDate = OffsetDateTime.now()
            // inserting a record older then 31 days
            repository.insert(callId, callStartDate.minusDays(32))
            // should not return new record
            var freshList = repository.getAll()
            Assert.assertEquals(originalList.count(), freshList.count())
            Assert.assertNull(freshList.find { it.callId == callId })

            // inset new record
            repository.insert(callId, callStartDate)

            freshList = repository.getAll()

            Assert.assertEquals(originalList.count() + 1, freshList.count())

            val retrievedNewRecord = freshList.find { it.callId == callId }
            Assert.assertNotNull(retrievedNewRecord)
            Assert.assertEquals(callStartDate, retrievedNewRecord!!.callStartedOn)
        }
}
