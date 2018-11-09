package com.example.a.lockquizekotlin.Utils

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


object DbUtils {
    const val TAG = "DbUtils"
    // DATABASE__NAME 에는 카테고리와 문제 정보가 들어가서 항상 새로 불러오게 만들고
    const val DATABASE_NAME = "projectm.db"
    const val PACKAGE_DIR = "/data/data/com.example.a.lockquizekotlin/databases"

    // 이건 기존의 데이터만 사용할 거니까, projectmu는 의미가 없다.
    fun saveDbAssetToDevice(ctx: Context, databaseName: String = DATABASE_NAME){
        val folder = File(PACKAGE_DIR)
        folder.mkdirs()

        val outfile = File("$PACKAGE_DIR/$databaseName")

            val assetManager = ctx.getResources().getAssets()
            try {
                val `is` = assetManager.open("db/$databaseName", AssetManager.ACCESS_BUFFER)
                Log.d(TAG, "!! ${`is`.available()}")
                val filesize = `is`.available()
                val tempdata = ByteArray(filesize.toInt())
                `is`.read(tempdata)
                `is`.close()
                outfile.createNewFile()
                val fo = FileOutputStream(outfile)
                fo.write(tempdata)
                fo.close()
            } catch (e: IOException) {
                Log.d(TAG, "assetManager Error")
                e.printStackTrace()
            }
    }
}