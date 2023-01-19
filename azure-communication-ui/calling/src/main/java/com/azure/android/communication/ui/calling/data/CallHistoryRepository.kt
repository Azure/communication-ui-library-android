// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.data

import android.content.ContentValues
import android.content.Context
import com.azure.android.communication.ui.calling.configuration.CallType
import com.azure.android.communication.ui.calling.data.model.CallHistoryRecord
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

internal interface CallHistoryRepository {
    fun insertCallHistoryRecord(callId: String, callDateTime: LocalDateTime, callType: CallType, callLocator: String)
    fun getAllCallHistoryRecords(): List<CallHistoryRecord>
    fun removeCallHistoryRecord(id: Int)
}

internal class CallHistoryRepositoryImpl(
    private val context: Context
) : CallHistoryRepository {

    private val datePattern: String = "yyyy-MM-dd HH:mm:ss"

    override fun insertCallHistoryRecord(callId: String, callDateTime: LocalDateTime, callType: CallType, callLocator: String) {
        val db = DbHelper(context).writableDatabase

        val date = callDateTime.format(DateTimeFormatter.ofPattern(datePattern))

        val values = ContentValues().apply {
            put(CallHistoryContract.COLUMN_NAME_CALL_ID, callId)
            put(CallHistoryContract.COLUMN_NAME_CALL_DATE, date)
            put(CallHistoryContract.COLUMN_NAME_CALL_TYPE, callType.name)
            put(CallHistoryContract.COLUMN_NAME_CALL_LOCATOR, callLocator)
        }

        db.insert(CallHistoryContract.TABLE_NAME, null, values)
        db.close()
    }

    override fun getAllCallHistoryRecords(): List<CallHistoryRecord> {
        val db = DbHelper(context).writableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM ${CallHistoryContract.TABLE_NAME} ORDER BY ${CallHistoryContract.COLUMN_NAME_CALL_DATE} ASC",
            null
        )

        val dateFormatter = DateTimeFormatter.ofPattern(datePattern)
        val items = mutableListOf<CallHistoryRecord>()
        with(cursor) {
            if (moveToFirst()) {
                do {
                    items.add(
                        CallHistoryRecord(
                            id = getInt(getColumnIndex(CallHistoryContract.ID)),
                            callId = getString(getColumnIndex(CallHistoryContract.COLUMN_NAME_CALL_ID)),
                            date = LocalDateTime.parse(
                                getString(getColumnIndex(CallHistoryContract.COLUMN_NAME_CALL_DATE)), dateFormatter
                            ),
                            callType = getString(getColumnIndex(CallHistoryContract.COLUMN_NAME_CALL_TYPE)),
                            callLocator = getString(getColumnIndex(CallHistoryContract.COLUMN_NAME_CALL_LOCATOR)),
                        )
                    )
                } while (moveToNext())
            }
        }
        cursor.close()
        return items
    }

    override fun removeCallHistoryRecord(id: Int) {
        val db = DbHelper(context).writableDatabase
        db.delete(CallHistoryContract.TABLE_NAME, "${CallHistoryContract.ID} = ?", arrayOf(id.toString()))
        db.close()
    }
}
