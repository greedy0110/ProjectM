package com.example.a.lockquizekotlin.Utils

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


object DbUtils {
    const val TAG = "DbUtils"
    const val DATABASE_NAME = "projectm.db"
    const val PACKAGE_DIR = "/data/data/com.example.a.lockquizekotlin/databases"

    fun saveDbAssetToDevice(ctx: Context, force: Boolean = false){
        val folder = File(PACKAGE_DIR)
        folder.mkdirs()

        val outfile = File("$PACKAGE_DIR/$DATABASE_NAME")

        if (force || outfile.length() <= 0) {
            val assetManager = ctx.getResources().getAssets()
            try {
                val `is` = assetManager.open("db/$DATABASE_NAME", AssetManager.ACCESS_BUFFER)
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
}