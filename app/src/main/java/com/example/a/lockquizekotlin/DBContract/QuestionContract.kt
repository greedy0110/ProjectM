package com.example.a.lockquizekotlin.DBContract

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.example.a.lockquizekotlin.Utils.DbUtils

object QuestionContract {
    val DATABASE_NAME = "questionDB"

    object Schema : BaseColumns{
        const val TABLE_NAME = "question_entry"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME_YEAR = "year"
        const val COLUMN_NAME_CATEGORY = "category"
        const val COLUMN_NAME_QUESTION = "question"
        const val COLUMN_NAME_ANSWER = "answer"

        const val SQL_CREATE_ENTRIES = "CREATE TABLE $TABLE_NAME ( $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_NAME_YEAR TEXT ,$COLUMN_NAME_CATEGORY  TEXT NOT NULL, $COLUMN_NAME_QUESTION TEXT NOT NULL, $COLUMN_NAME_ANSWER TEXT NOT NULL)"
        const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"
    }

    data class Entry(val id: Int, val year: String, val category: String, val question: String, val answer: String)

    class DbHelper(context: Context) : SQLiteOpenHelper(context, "${DbUtils.PACKAGE_DIR}/${DbUtils.DATABASE_NAME}", null, DATABASE_VERSION) {
        init {
            DbUtils.saveDbAssetToDevice(context, databaseName = DATABASE_NAME)
        }

        override fun onCreate(db: SQLiteDatabase?) {
            try {
                db?.execSQL(Schema.SQL_CREATE_ENTRIES)
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

