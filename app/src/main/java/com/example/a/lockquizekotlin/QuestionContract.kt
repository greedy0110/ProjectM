package com.example.a.lockquizekotlin

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

object QuestionContract {
    object QuestionSchema : BaseColumns{
        const val TABLE_NAME = "question_entry"
        const val COLUMN_NAME_CATEGORY = "category"
        const val COLUMN_NAME_QUESTION = "question"
        const val COLUMN_NAME_ANSWER = "answer"

        const val SQL_CREATE_ENTRIES =
                "CREATE TABLE ${QuestionSchema.TABLE_NAME} (" +
                        "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                        "${QuestionSchema.COLUMN_NAME_CATEGORY} TEXT," +
                        "${QuestionSchema.COLUMN_NAME_QUESTION} TEXT, " +
                        "${QuestionSchema.COLUMN_NAME_ANSWER} TEXT)" // 정답이 yes, no인지 아닌지 모름 일단 text로 함

        const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${QuestionSchema.TABLE_NAME}"
    }

    data class QuestionEntry(val id: Long, val category: String, val question: String, val answer: String)

    class DbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        override fun onCreate(db: SQLiteDatabase?) {
            db?.execSQL(QuestionSchema.SQL_CREATE_ENTRIES)
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db?.execSQL(QuestionSchema.SQL_DELETE_ENTRIES)
            db?.let { onCreate(db) }
        }

        companion object {
            // 이게 되네
            const val DATABASE_VERSION = 1
            const val DATABASE_NAME = "Question.db"
        }
    }
}