package com.example.a.lockquizekotlin

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.provider.Settings
import android.util.Log
import com.example.a.lockquizekotlin.DBContract.QuestionContract
import com.example.a.lockquizekotlin.LockScreen.UnlockCaptureService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val TAG: String = "MainActivity"
    private var dbHelper: QuestionContract.DbHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dbHelper = QuestionContract.DbHelper(applicationContext)

        startServiceButton.setOnClickListener {
            Log.d(TAG, "start service button 눌렸다.")
            startUnlockCaptureService()
        }

        stopServiceButton.setOnClickListener {
            Log.d(TAG, "stop service button 눌렸다.")
            stopUnlockCaptureService()
        }
    }

    private fun startUnlockCaptureService() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(applicationContext)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                startActivityForResult(intent, 1234)
            } else {
                startUnlockCaptureServiceNoVersionCheck()
            }
        }
        else {
            startUnlockCaptureServiceNoVersionCheck()
        }
    }

    private fun startUnlockCaptureServiceNoVersionCheck(){
        val intent = Intent(applicationContext, UnlockCaptureService::class.java)
        applicationContext?.startService(intent)
    }

    private fun stopUnlockCaptureService(){
        val intent = Intent(applicationContext, UnlockCaptureService::class.java)
        applicationContext?.stopService(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            1234 -> {
                startUnlockCaptureServiceNoVersionCheck()
            }
        }
    }
//
//    private fun createQuestionoEntryDB(){
//        if (dbHelper == null) return
//        val db = dbHelper?.writableDatabase
//
//        val category = "소득세법"
//        val question = "내국법인은 각 사업연도의 소득에 대한 법인세 산출세액에 해당 사업연도에 원천징수 된 세액을 합산한 금액을 각 사업연도 소득에 대한 법인세로서 납부하여야 한다."
//        val answer = "yes"
//        val values = ContentValues().apply {
//            put(QuestionContract.Schema.COLUMN_NAME_CATEGORY_ID, category)
//            put(QuestionContract.Schema.COLUMN_NAME_QUESTION, question)
//            put(QuestionContract.Schema.COLUMN_NAME_ANSWER, answer)
//        }
//
//        val newRowId = db?.insert(QuestionContract.Schema.TABLE_NAME, null, values)
//        Log.d(TAG,"item 추가")
//    }

    override fun onDestroy() {
        dbHelper?.close()
        super.onDestroy()
    }
}
