package com.example.a.lockquizekotlin.DBContract

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns


object IncorrectContract {
    object Schema : BaseColumns {
        const val TABLE_NAME = "incorrect_entry"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME_QUESTION_ID = "question_id"

        const val SQL_CREATE_ENTRIES = "CREATE TABLE $TABLE_NAME (\n" +
                "\t$COLUMN_ID\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\t$COLUMN_NAME_QUESTION_ID\tINTEGER NOT NULL,\n" +
                "\tFOREIGN KEY($COLUMN_NAME_QUESTION_ID) REFERENCES ${QuestionContract.Schema.TABLE_NAME}(${QuestionContract.Schema.COLUMN_ID})\n" +
                ");"
        const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"
    }

    data class Entry(val id: Int, val question_id: Int)

    data class EntryEasy(val id: Int, val question: String)

    class DbHelper(context: Context) : SQLiteOpenHelper(context, "${DbUtils.PACKAGE_DIR}/${DbUtils.DATABASE_NAME}", null, DATABASE_VERSION) {
        init {
            DbUtils.saveDbAssetToDevice(context)
        }

        override fun onCreate(db: SQLiteDatabase?) {
            try {
                db?.execSQL(CategoryContract.Schema.SQL_CREATE_ENTRIES)
            }
            catch (e: Exception) {

            }
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db?.execSQL(Schema.SQL_DELETE_ENTRIES)
            db?.let { onCreate(db) }
        }

        companion object {
            // 이게 되네
            const val DATABASE_VERSION = 1
            const val DATABASE_NAME = DbUtils.DATABASE_NAME
        }
    }
}