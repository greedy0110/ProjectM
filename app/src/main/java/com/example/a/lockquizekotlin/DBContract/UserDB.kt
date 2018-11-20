package com.example.a.lockquizekotlin.DBContract

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteCursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log

//- name
//- user_id
//- pas
//- mail
//- cell

object UserDB: IDB<UserEntry> {
    private var mUserList = mutableListOf<UserEntry>()
    private var mUpdated = false

    override fun readAll(context: Context): List<UserEntry> {
        if (mUserList.size == 0 || mUpdated == true) {
            val dbHelper = UserDbHelper(context)
            val db = dbHelper.readableDatabase

            val projection = UserSchema.getAllProjection()
            val cursor = db?.query(
                    UserSchema.TABLE_NAME,
                    projection,
                    null,null,null,null, null
            )

            val items = mutableListOf<UserEntry>()
            cursor?.let {
                with(cursor) {
                    while (moveToNext()) {
                        val entry = UserSchema.getEntry(cursor)
                        items.add(entry)
                    }
                }
            }

            mUpdated = false
            mUserList = items
            return items
        }
        else {
            return mUserList
        }
    }

    override fun readOne(context: Context, id: Int): UserEntry? {
        readAll(context)
        val one = mUserList.find { it.id == id }
        return one
    }

    override fun writeOne(context: Context, entry: UserEntry): Boolean {
        val dbHelper = UserDbHelper(context)
        val db = dbHelper.readableDatabase

        val newRowId = db?.insert(UserSchema.TABLE_NAME, null, ContentValues().apply {
            put(UserSchema.COLUMN_NAME, entry.name)
            put(UserSchema.COLUMN_USER_ID, entry.user_id)
            put(UserSchema.COLUMN_PASSWORD, entry.pas)
            put(UserSchema.COLUMN_MAIL, entry.mail)
            put(UserSchema.COLUMN_CELLPHONE, entry.cell)
        })

        db?.close()
        if (newRowId == -1L) return false
        if (newRowId == null) return false
        mUserList.add(UserEntry(newRowId.toInt(), entry.name, entry.user_id, entry.pas, entry.mail, entry.cell))
        mUpdated = true
        return true
    }

    override fun searchOne(context: Context, id: Int): Boolean {
        return readOne(context, id) != null
    }

    override fun deleteOne(context: Context, id: Int): Boolean {
        // 회원 삭제는 구현하지 말자 아직
        return true
    }

    fun matchUserIdPas(context: Context, user_id: String, pas: String): Boolean {
        readAll(context)
        val one = mUserList.find {
            it.user_id == user_id && it.pas == pas
        }
        return one != null
    }

    fun hasUserid(context: Context, user_id: String): Boolean {
        readAll(context)
        val one = mUserList.find {
            it.user_id == user_id
        }
        return one != null
    }
}

data class UserEntry(val id: Int, val name: String, val user_id: String,
                     val pas:String, val mail:String, val cell: String)

private object UserSchema: BaseColumns {
    const val TABLE_NAME = "user_db"
    const val COLUMN_ID = "id"
    const val COLUMN_NAME = "name"
    const val COLUMN_USER_ID = "user_id"
    const val COLUMN_PASSWORD = "pas"
    const val COLUMN_MAIL = "mail"
    const val COLUMN_CELLPHONE = "cell"

    const val SQL_CREATE_ENTRIES = "CREATE TABLE ${TABLE_NAME} ( ${COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT, ${COLUMN_NAME} TEXT ,${COLUMN_USER_ID}  TEXT NOT NULL," +
            " ${COLUMN_PASSWORD} TEXT NOT NULL, ${COLUMN_MAIL} TEXT, ${COLUMN_CELLPHONE} TEXT)"
    const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${TABLE_NAME}"

    fun getAllProjection(): Array<String> {
        return arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_USER_ID, COLUMN_PASSWORD, COLUMN_MAIL, COLUMN_CELLPHONE)
    }

    fun getEntry(cursor: Cursor): UserEntry {
        with(cursor) {
            val id = getInt(getColumnIndexOrThrow(COLUMN_ID))
            val name = getString(getColumnIndexOrThrow(COLUMN_NAME))
            val user_id = getString(getColumnIndexOrThrow(COLUMN_USER_ID))
            val pas = getString(getColumnIndexOrThrow(COLUMN_PASSWORD))
            val mail = getString(getColumnIndexOrThrow(COLUMN_MAIL))
            val cell = getString(getColumnIndexOrThrow(COLUMN_CELLPHONE))
            return UserEntry(id, name, user_id, pas, mail, cell)
        }
    }
}

private class UserDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        try {
            Log.d("userdb", "craete")
            db?.execSQL(UserSchema.SQL_CREATE_ENTRIES)
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(UserSchema.SQL_DELETE_ENTRIES)
        db?.let { onCreate(db) }
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "userDB"
    }
}