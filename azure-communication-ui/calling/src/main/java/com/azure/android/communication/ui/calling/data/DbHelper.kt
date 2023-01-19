// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

internal class DbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CallHistoryContract.SQL_CREATE_CALL_HISTORY)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "com.azure.android.communication.ui.calling.CallHistoryReader.db"
    }
}

internal object CallHistoryContract {

    const val ID = BaseColumns._ID
    const val TABLE_NAME = "call_history"

    const val COLUMN_NAME_CALL_ID = "call_id"
    const val COLUMN_NAME_CALL_DATE = "call_date"

    const val SQL_CREATE_CALL_HISTORY =
        "CREATE TABLE $TABLE_NAME (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "$COLUMN_NAME_CALL_ID TEXT," +
            "$COLUMN_NAME_CALL_DATE TEXT)"
}
