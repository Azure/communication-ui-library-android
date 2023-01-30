// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.data

import android.content.ContentValues
import android.content.Context
import com.azure.android.communication.ui.calling.data.model.CallHistoryRecordData
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

internal interface CallHistoryRepository {
    fun insert(callId: String, callDateTime: LocalDateTime)
    fun getAll(): List<CallHistoryRecordData>
    fun remove(id: Int)
}

internal class CallHistoryRepositoryImpl(
    private val context: Context
) : CallHistoryRepository {

    private val datePattern: String = "yyyy-MM-dd HH:mm:ss"

    override fun insert(callId: String, callDateTime: LocalDateTime) {
        val db = DbHelper(context).writableDatabase

        val date = callDateTime.format(DateTimeFormatter.ofPattern(datePattern))

        val values = ContentValues().apply {
            put(CallHistoryContract.COLUMN_NAME_CALL_ID, callId)
            put(CallHistoryContract.COLUMN_NAME_CALL_DATE, date)
        }

        db.insert(CallHistoryContract.TABLE_NAME, null, values)
        db.close()
    }

    override fun getAll(): List<CallHistoryRecordData> {
        val db = DbHelper(context).writableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM ${CallHistoryContract.TABLE_NAME} ORDER BY ${CallHistoryContract.COLUMN_NAME_CALL_DATE} ASC",
            null
        )

        val dateFormatter = DateTimeFormatter.ofPattern(datePattern)
        val items = mutableListOf<CallHistoryRecordData>()
        with(cursor) {
            if (moveToFirst()) {
                val idColumnIndex = getColumnIndex(CallHistoryContract.ID)
                val nameColumnIndex = getColumnIndex(CallHistoryContract.COLUMN_NAME_CALL_ID)
                val dateColumnIndex = getColumnIndex(CallHistoryContract.COLUMN_NAME_CALL_DATE)
                do {
                    items.add(
                        CallHistoryRecordData(
                            id = getInt(idColumnIndex),
                            callId = getString(nameColumnIndex),
                            date = LocalDateTime.parse(getString(dateColumnIndex), dateFormatter),
                        )
                    )
                } while (moveToNext())
            }
        }
        cursor.close()
        return items
    }

    override fun remove(id: Int) {
        val db = DbHelper(context).writableDatabase
        db.delete(CallHistoryContract.TABLE_NAME, "${CallHistoryContract.ID} = ?", arrayOf(id.toString()))
        db.close()
    }
}
