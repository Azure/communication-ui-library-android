// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.azure.android.communication.ui.calling.data.model.CallHistoryRecordData
import com.azure.android.communication.ui.calling.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.Instant
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneId

internal interface CallHistoryRepository {
    suspend fun insert(
        callId: String,
        callDateTime: OffsetDateTime,
    )

    suspend fun getAll(): List<CallHistoryRecordData>
}

internal class CallHistoryRepositoryImpl(
    private val context: Context,
    private val logger: Logger,
) : CallHistoryRepository {
    override suspend fun insert(
        callId: String,
        callDateTime: OffsetDateTime,
    ) {
        return withContext(Dispatchers.IO) {
            // SQLite does not allow concurrent writes. Need to queue them via lock.
            synchronized(dbAccessLock) {
                // Using a new db instance instead of caching one as we do not have a
                // reliable event when to dispose it.
                DbHelper(context).writableDatabase
                    .use { db ->
                        val values =
                            ContentValues().apply {
                                put(CallHistoryContract.COLUMN_NAME_CALL_ID, callId)
                                put(CallHistoryContract.COLUMN_NAME_CALL_DATE, callDateTime.toInstant().toEpochMilli())
                            }

                        val result = db.insert(CallHistoryContract.TABLE_NAME, null, values)
                        if (result == -1L) {
                            logger.warning("Failed to save call history record.")
                        }

                        // Execute cleanup separately (not in one transaction) in case of it fails,
                        // so it does not affect insert.
                        cleanupOldRecords(db)
                    }
            }
        }
    }

    override suspend fun getAll(): List<CallHistoryRecordData> {
        return withContext(Dispatchers.IO) {
            // Using a new db instance instead of caching one as we do not have a
            // reliable event when to dispose it.
            DbHelper(context).writableDatabase.use { db ->
                synchronized(dbAccessLock) {
                    cleanupOldRecords(db)
                }
                val items = mutableListOf<CallHistoryRecordData>()
                db.rawQuery(
                    "SELECT ${CallHistoryContract.COLUMN_NAME_ID}, " +
                        "${CallHistoryContract.COLUMN_NAME_CALL_DATE}, " +
                        "${CallHistoryContract.COLUMN_NAME_CALL_ID} " +
                        "FROM ${CallHistoryContract.TABLE_NAME}",
                    null,
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
                                    callStartedOn =
                                        OffsetDateTime.ofInstant(
                                            Instant.ofEpochMilli(it.getLong(dateColumnIndex)),
                                            ZoneId.systemDefault(),
                                        ),
                                ),
                            )
                        } while (it.moveToNext())
                    }
                }
                return@withContext items
            }
        }
    }

    private fun cleanupOldRecords(db: SQLiteDatabase) {
        val threshold = OffsetDateTime.now().minusDays(31).toInstant().toEpochMilli()
        db.delete(
            CallHistoryContract.TABLE_NAME,
            "${CallHistoryContract.COLUMN_NAME_CALL_DATE} < ?",
            arrayOf(threshold.toString()),
        )
    }

    private companion object {
        val dbAccessLock = Any()
    }
}
