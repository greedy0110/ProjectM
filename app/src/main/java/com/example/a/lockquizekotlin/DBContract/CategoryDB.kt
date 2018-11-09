package com.example.a.lockquizekotlin.DBContract

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.example.a.lockquizekotlin.Utils.DbUtils

// CategoryEntry 와 DB 는 외부에서 사용 할 수 있어야 한다.

object CategoryDB: IDB<CategoryEntry> {
    private var mCategoryList = mutableListOf<CategoryEntry>()
    private var mUpdated = false

    override fun readAll(context: Context): List<CategoryEntry> {
        if (mCategoryList.size == 0 || mUpdated == true) {
            val dbHelper = CategoryDbHelper(context)
            val db = dbHelper.readableDatabase

            val projection = arrayOf(CategorySchema.COLUMN_ID, CategorySchema.COLUMN_NAME)

            val cursor = db?.query(
                    CategorySchema.TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    null
            )

            val items = mutableListOf<CategoryEntry>()
            cursor?.let {
                with(cursor) {
                    while (moveToNext()) {
                        val id = getInt(getColumnIndexOrThrow(CategorySchema.COLUMN_ID))
                        val category = getString(getColumnIndexOrThrow(CategorySchema.COLUMN_NAME))
                        val entry = CategoryEntry(id, category)
                        items.add(entry)
                    }
                }
            }

            mUpdated = false
            mCategoryList = items
            return items
        }
        else {
            return mCategoryList
        }
    }

    override fun readOne(context: Context, id: Int): CategoryEntry? {
        readAll(context)
        val one = mCategoryList.find {
            it.id == id
        }
        return one
    }

    // 카테고리 대상으로 해당 함수들을 호출할 일이 없으므로 일단 구현을 미룬다 - 181109 신승민
    override fun writeOne(context: Context, entry: CategoryEntry): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun searchOne(context: Context, id: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteOne(context: Context, id: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

data class CategoryEntry(val id: Int, val category: String)
// ----------- CategoryEntry - DB

// Schema 랑 DbHelper는 내부에서만 사용한다. -> private

private object CategorySchema : BaseColumns {
    const val TABLE_NAME = "category_entry"
    const val COLUMN_ID = "id"
    const val COLUMN_NAME = "name"

    const val SQL_CREATE_ENTRIES = "CREATE TABLE $TABLE_NAME (\n" +
            "\t$COLUMN_ID\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "\t$COLUMN_NAME\tTEXT NOT NULL\n" +
            ");"
    const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"
}

private class CategoryDbHelper(context: Context) : SQLiteOpenHelper(context, "${DbUtils.PACKAGE_DIR}/${DbUtils.DATABASE_NAME}", null, DATABASE_VERSION) {

    init {
        DbUtils.saveDbAssetToDevice(context, databaseName = DATABASE_NAME)
    }

    override fun onCreate(db: SQLiteDatabase?) {
        try {
            db?.execSQL(CategorySchema.SQL_CREATE_ENTRIES)
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(CategorySchema.SQL_DELETE_ENTRIES)
        db?.let { onCreate(db) }
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = DbUtils.DATABASE_NAME
    }
}