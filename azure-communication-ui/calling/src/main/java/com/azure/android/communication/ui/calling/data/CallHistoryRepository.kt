// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.azure.android.communication.ui.calling.data.model.CallHistoryRecordData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import org.threeten.bp.Instant
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneId
import java.util.concurrent.Executors

internal interface CallHistoryRepository {
    suspend fun insert(callId: String, callDateTime: OffsetDateTime)
    suspend fun getAll(): List<CallHistoryRecordData>
}

internal class CallHistoryRepositoryImpl(
    private val context: Context
) : CallHistoryRepository {
    private val insetLock = Any()

    override suspend fun insert(callId: String, callDateTime: OffsetDateTime) {
        return withContext(Executors.newSingleThreadExecutor().asCoroutineDispatcher()) {
            synchronized(insetLock) {
                DbHelper(context).writableDatabase
                    .use { db ->
                        val values = ContentValues().apply {
                            put(CallHistoryContract.COLUMN_NAME_CALL_ID, callId)
                            put(CallHistoryContract.COLUMN_NAME_CALL_DATE, callDateTime.toInstant().epochSecond)
                        }

                        db.insert(CallHistoryContract.TABLE_NAME, null, values)
                        cleanUpOldRecords(db)
                        db.close()
                    }
            }
        }
    }

    override suspend fun getAll(): List<CallHistoryRecordData> {
        return withContext(Dispatchers.IO) {
            DbHelper(context).writableDatabase.use { db ->
                val items = mutableListOf<CallHistoryRecordData>()
                db.rawQuery(
                    "SELECT * FROM ${CallHistoryContract.TABLE_NAME} ORDER BY ${CallHistoryContract.COLUMN_NAME_CALL_DATE} ASC",
                    null
                ).use {
                    if (it.moveToFirst()) {
                        val idColumnIndex = it.getColumnIndexOrThrow(CallHistoryContract.COLUMN_NAME_ID)
                        val nameColumnIndex = it.getColumnIndexOrThrow(CallHistoryContract.COLUMN_NAME_CALL_ID)
                        val dateColumnIndex = it.getColumnIndexOrThrow(CallHistoryContract.COLUMN_NAME_CALL_DATE)
                        do {
                            items.add(
                                CallHistoryRecordData(
                                    id = it.getInt(idColumnIndex),
                                    callId = it.getString(nameColumnIndex),
                                    callStartedOn = OffsetDateTime.ofInstant(
                                        Instant.ofEpochSecond(it.getLong(dateColumnIndex)), ZoneId.systemDefault()
                                    ),
                                )
                            )
                        } while (it.moveToNext())
                    }
                }
                return@withContext items
            }
        }
    }

    private fun cleanUpOldRecords(db: SQLiteDatabase) {
        val threshold = OffsetDateTime.now().minusDays(31).toInstant().epochSecond
        val sql = "DELETE FROM ${CallHistoryContract.TABLE_NAME} " +
                "WHERE ${CallHistoryContract.COLUMN_NAME_CALL_DATE} < $threshold"
        db.execSQL(sql)
    }
}
