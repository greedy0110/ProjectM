package com.example.a.lockquizekotlin.DBContract

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.example.a.lockquizekotlin.Utils.DbUtils


object QuestionDB: IDB<QuestionEntry> {
    private var mQuestionList = mutableListOf<QuestionEntry>()
    private var mUpdated = false

    override fun readAll(context: Context): List<QuestionEntry> {
        if (mQuestionList.size == 0 || mUpdated == true) {
            val dbHelper = QuestionDbHelper(context)
            val db = dbHelper.readableDatabase

            val projection = arrayOf(QuestionSchema.COLUMN_ID, QuestionSchema.COLUMN_NAME_YEAR,
                    QuestionSchema.COLUMN_NAME_CATEGORY, QuestionSchema.COLUMN_NAME_QUESTION, QuestionSchema.COLUMN_NAME_ANSWER)

            val cursor = db?.query(
                    QuestionSchema.TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    null
            )

            val items = mutableListOf<QuestionEntry>()
            cursor?.let {
                with(cursor) {
                    while (moveToNext()) {
                        val id = getInt(getColumnIndexOrThrow(QuestionSchema.COLUMN_ID))
                        val year = getString(getColumnIndexOrThrow(QuestionSchema.COLUMN_NAME_YEAR))
                        val category = getString(getColumnIndexOrThrow(QuestionSchema.COLUMN_NAME_CATEGORY))
                        val question = getString(getColumnIndexOrThrow(QuestionSchema.COLUMN_NAME_QUESTION))
                        val answer = getString(getColumnIndexOrThrow(QuestionSchema.COLUMN_NAME_ANSWER))
                        val entry = QuestionEntry(id, year, category, question, answer)
                        items.add(entry)
                    }
                }
            }

            mUpdated = false
            mQuestionList = items
            return items
        }
        else {
            return mQuestionList
        }
    }

    override fun readOne(context: Context, id: Int): QuestionEntry? {
        readAll(context)
        val one = mQuestionList.find {
            it.id == id
        }
        return one
    }

    // 문제에 쓸일 은 없다. - 181109 신승민
    override fun writeOne(context: Context, entry: QuestionEntry): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun searchOne(context: Context, id: Int): Boolean {
        return readOne(context, id) != null
    }

    // 문제에 지울 일은 없다. - 181109 신승민
    override fun deleteOne(context: Context, id: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

data class QuestionEntry(val id: Int, val year: String, val category: String, val question: String, val answer: String)
// ----------- CategoryEntry - DB

// Schema 랑 DbHelper는 내부에서만 사용한다. -> private

private object QuestionSchema : BaseColumns {
    const val TABLE_NAME = "question_entry"
    const val COLUMN_ID = "id"
    const val COLUMN_NAME_YEAR = "year"
    const val COLUMN_NAME_CATEGORY = "category"
    const val COLUMN_NAME_QUESTION = "question"
    const val COLUMN_NAME_ANSWER = "answer"

    const val SQL_CREATE_ENTRIES = "CREATE TABLE $TABLE_NAME ( $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_NAME_YEAR TEXT ,$COLUMN_NAME_CATEGORY  TEXT NOT NULL, $COLUMN_NAME_QUESTION TEXT NOT NULL, $COLUMN_NAME_ANSWER TEXT NOT NULL)"
    const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"
}

private class QuestionDbHelper(context: Context) : SQLiteOpenHelper(context, "${DbUtils.PACKAGE_DIR}/${DbUtils.DATABASE_NAME}", null, DATABASE_VERSION) {
    init {
        DbUtils.saveDbAssetToDevice(context, databaseName = DATABASE_NAME)
    }

    override fun onCreate(db: SQLiteDatabase?) {
        try {
            db?.execSQL(QuestionSchema.SQL_CREATE_ENTRIES)
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(QuestionSchema.SQL_DELETE_ENTRIES)
        db?.let { onCreate(db) }
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = DbUtils.DATABASE_NAME
    }
}