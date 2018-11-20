package com.example.a.lockquizekotlin.DBContract

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import com.example.a.lockquizekotlin.Utils.DbUtils

object IncorrectDB: IDB<IncorrectEntry> {
    private var mIncorrectList = mutableListOf<IncorrectEntry>()
    private var mUpdated = false

    override fun readAll(context: Context): List<IncorrectEntry> {
        if (mIncorrectList.size == 0 || mUpdated == true) {
            val dbHelper = IncorrectDbHelper(context)
            val db = dbHelper.readableDatabase

            val projection = arrayOf(IncorrectSchema.COLUMN_ID, IncorrectSchema.COLUMN_NAME_QUESTION_ID)

            val cursor = db?.query(
                    IncorrectSchema.TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    null
            )

            val items = mutableListOf<IncorrectEntry>()
            cursor?.let {
                with(cursor) {
                    while (moveToNext()) {
                        val id = getInt(getColumnIndexOrThrow(IncorrectSchema.COLUMN_ID))
                        val question_id = getInt(getColumnIndexOrThrow(IncorrectSchema.COLUMN_NAME_QUESTION_ID))
                        val entry = IncorrectEntry(id, question_id)
                        items.add(entry)
                    }
                }
            }

            mUpdated = false
            mIncorrectList = items
            return items
        } else {
            return mIncorrectList
        }
    }

    override fun readOne(context: Context, id: Int): IncorrectEntry? {
        readAll(context)
        val one = mIncorrectList.find {
            it.id == id
        }
        return one
    }

    override fun writeOne(context: Context, entry: IncorrectEntry): Boolean {
        val dbHelper = IncorrectDbHelper(context)
        val db = dbHelper?.readableDatabase

        val newRowId = db?.insert(IncorrectSchema.TABLE_NAME, null, ContentValues().apply {
            put(IncorrectSchema.COLUMN_NAME_QUESTION_ID, entry.question_id)
        })

        db?.close()
        if (newRowId == -1L) {
            return false
        }
        if (newRowId == null){
            return false
        }
        mIncorrectList.add(IncorrectEntry(newRowId.toInt(), entry.question_id))
        mUpdated = true
        return true
    }


    override fun searchOne(context: Context, id: Int): Boolean {
        return readOne(context, id) != null
    }

    fun searchOneByQuestionId(context: Context, id: Int): Boolean {
        val has = mIncorrectList.find { it.question_id== id }
        return has!=null
    }

    override fun deleteOne(context: Context, id: Int): Boolean {
        val dbHelper = IncorrectDbHelper(context)
        val db = dbHelper?.readableDatabase

        db?.delete(IncorrectSchema.TABLE_NAME, "${IncorrectSchema.COLUMN_ID} = ?",
                arrayOf(id.toString()))
        db?.close()
        mIncorrectList.removeAll {
            it.id == id
        }
        mUpdated = true
        return true
    }

    fun deleteOneByQuestionId(context: Context, id: Int): Boolean {
        val dbHelper = IncorrectDbHelper(context)
        val db = dbHelper?.readableDatabase

        db?.delete(IncorrectSchema.TABLE_NAME, "${IncorrectSchema.COLUMN_NAME_QUESTION_ID} = ?",
                arrayOf(id.toString()))
        db?.close()
        mIncorrectList.removeAll() {
            it.question_id == id
        }
        mUpdated = true
        return true
    }
}

data class IncorrectEntry(val id: Int, val question_id: Int)
// ----------- CategoryEntry - DB

// Schema 랑 DbHelper는 내부에서만 사용한다. -> private

private object IncorrectSchema : BaseColumns {
    const val TABLE_NAME = "incorrect_entry"
    const val COLUMN_ID = "id"
    const val COLUMN_NAME_QUESTION_ID = "question_id"

    const val SQL_CREATE_ENTRIES = "CREATE TABLE $TABLE_NAME ( $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_NAME_QUESTION_ID INTEGER )"
    const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"
}

private class IncorrectDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        try {
            Log.d("incorrect", "create")
            db?.execSQL(IncorrectSchema.SQL_CREATE_ENTRIES)
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(IncorrectSchema.SQL_DELETE_ENTRIES)
        db?.let { onCreate(db) }
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "incorrectDB"
    }
}