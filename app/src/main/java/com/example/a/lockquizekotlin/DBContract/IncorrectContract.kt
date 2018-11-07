package com.example.a.lockquizekotlin.DBContract

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import com.example.a.lockquizekotlin.Utils.DbUtils



object IncorrectContract {
    val DATABASE_NAME = "incorrectDB"

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

    class DbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        override fun onCreate(db: SQLiteDatabase?) {
            try {
                Log.e("IncorrectContract", "db create")
                db?.execSQL(Schema.SQL_CREATE_ENTRIES)
            }
            catch (e: Exception) {
                Log.e("IncorrectContract", e.toString())
            }
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db?.execSQL(Schema.SQL_DELETE_ENTRIES)
            db?.let { onCreate(db) }
        }

        companion object {
            const val DATABASE_VERSION = 1
        }
    }
}