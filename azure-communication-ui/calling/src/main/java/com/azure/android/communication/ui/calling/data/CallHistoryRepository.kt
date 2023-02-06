// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.data

import android.content.ContentValues
import android.content.Context
import com.azure.android.communication.ui.calling.data.model.CallHistoryRecordData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

internal interface CallHistoryRepository {
    suspend fun insert(callId: String, callDateTime: LocalDateTime)
    suspend fun getAll(): List<CallHistoryRecordData>
    suspend fun remove(ids: List<Int>)
}

internal class CallHistoryRepositoryImpl(
    private val context: Context
) : CallHistoryRepository {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override suspend fun insert(callId: String, callDateTime: LocalDateTime) {
        return withContext(Dispatchers.IO) {
            val db = DbHelper(context).writableDatabase

            val date = callDateTime.format(dateFormatter)

            val values = ContentValues().apply {
                put(CallHistoryContract.COLUMN_NAME_CALL_ID, callId)
                put(CallHistoryContract.COLUMN_NAME_CALL_DATE, date)
            }

            db.insert(CallHistoryContract.TABLE_NAME, null, values)
            db.close()
        }
    }

    override suspend fun getAll(): List<CallHistoryRecordData> {
        return withContext(Dispatchers.IO) {
            val db = DbHelper(context).writableDatabase
            val items = mutableListOf<CallHistoryRecordData>()
            db.rawQuery(
                "SELECT * FROM ${CallHistoryContract.TABLE_NAME} ORDER BY ${CallHistoryContract.COLUMN_NAME_CALL_DATE} ASC",
                null
            ).use {
                if (it.moveToFirst()) {
                    val idColumnIndex = it.getColumnIndex(CallHistoryContract.ID)
                    val nameColumnIndex = it.getColumnIndex(CallHistoryContract.COLUMN_NAME_CALL_ID)
                    val dateColumnIndex = it.getColumnIndex(CallHistoryContract.COLUMN_NAME_CALL_DATE)
                    do {
                        items.add(
                            CallHistoryRecordData(
                                id = it.getInt(idColumnIndex),
                                callId = it.getString(nameColumnIndex),
                                date = LocalDateTime.parse(it.getString(dateColumnIndex), dateFormatter),
                            )
                        )
                    } while (it.moveToNext())
                }
            }
            return@withContext items
        }
    }

    override suspend fun remove(ids: List<Int>) {
        if (ids.isEmpty())
            return

        withContext(Dispatchers.IO) {
            val db = DbHelper(context).writableDatabase
            val idValues = ids.joinToString(separator = ",") { "'$it'" }
            val sql = "DELETE FROM ${CallHistoryContract.TABLE_NAME} WHERE ${CallHistoryContract.ID} in ($idValues)"
            db.execSQL(sql)
            db.close()
        }
    }
}
