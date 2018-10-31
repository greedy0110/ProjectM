package com.example.a.lockquizekotlin.DBContract

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import android.widget.Toast

object SettingsContract{
    val THEME_LIST = listOf<String>("db", "dg", "dgr", "dp", "lg", "lp", "ls", "ly")

    object Schema : BaseColumns {
        const val TABLE_NAME = "settings"
        const val COLUMN_ID = "id"
        const val COLUMN_THEME = "theme"
        const val DEFAULT_THEME = "db"
        const val DEFAULT_THEME_ID = 110

        const val SQL_CREATE_ENTRIES = "CREATE TABLE $TABLE_NAME ( $COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_THEME TEXT NOT NULL DEFAULT $DEFAULT_THEME )"
        const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"
    }

    data class Entry(val id: Int, val thema: String)

    class DbHelper(context: Context) : SQLiteOpenHelper(context, "${DbUtils.PACKAGE_DIR}/${DbUtils.DATABASE_NAME}", null, DATABASE_VERSION) {
        init {
            DbUtils.saveDbAssetToDevice(context)
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

    fun getCurrentTheme(context: Context): String {
        val dbHelper = DbHelper(context)
        val db = dbHelper?.readableDatabase

        val projection = arrayOf(Schema.COLUMN_THEME)

        val cursor = db?.query(
                Schema.TABLE_NAME,
                projection,
                "${Schema.COLUMN_ID} = ?",
                arrayOf(Schema.DEFAULT_THEME_ID.toString()),
                null,
                null,
                null
        )

        var theme = Schema.DEFAULT_THEME
        cursor?.let {
            with(cursor) {
                while (moveToNext()) {
                    theme = getString(getColumnIndexOrThrow(Schema.COLUMN_THEME))
                    break
                }
            }
        }

        db?.close()
        return theme
    }

    fun setTheme(context: Context, theme: String) {
        if (!isInThemeList(theme)) {
            Toast.makeText(context, "지원하지 않는 테마 입니다", Toast.LENGTH_SHORT).show()
            Log.d("SettingsContract", "지원하지 않는 테마 호출 $theme")
            return
        }

        val dbHelper = DbHelper(context)
        val db = dbHelper.readableDatabase

        val projection = arrayOf(Schema.COLUMN_THEME)

        val cursor = db?.query(
                Schema.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        )

        if (cursor?.count == 0) {

            db.insert(Schema.TABLE_NAME, null, ContentValues().apply {
                put(Schema.COLUMN_ID, Schema.DEFAULT_THEME_ID)
                put(Schema.COLUMN_THEME, theme)
            })
        }
        else {
            db?.update(Schema.TABLE_NAME, ContentValues().apply {
                put(Schema.COLUMN_THEME, theme)
            }, "${Schema.COLUMN_ID} = ?", arrayOf(Schema.DEFAULT_THEME_ID.toString()))
        }

        db?.close()
    }

    fun isInThemeList(thema: String): Boolean {
        return THEME_LIST.contains(thema)
    }
}