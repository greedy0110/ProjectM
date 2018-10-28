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

        createDBButton.setOnClickListener {
            Log.d(TAG, "create db button 눌렀다.")
            createQuestionoEntryDB()
        }

        readDBButton.setOnClickListener {
            Log.d(TAG, "read db button 눌렀다.")
            readQuestionEntryDB()
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

    private fun createQuestionoEntryDB(){
        if (dbHelper == null) return
        val db = dbHelper?.writableDatabase

        val category = "소득세법"
        val question = "내국법인은 각 사업연도의 소득에 대한 법인세 산출세액에 해당 사업연도에 원천징수 된 세액을 합산한 금액을 각 사업연도 소득에 대한 법인세로서 납부하여야 한다."
        val answer = "yes"
        val values = ContentValues().apply {
            put(QuestionContract.Schema.COLUMN_NAME_CATEGORY_ID, category)
            put(QuestionContract.Schema.COLUMN_NAME_QUESTION, question)
            put(QuestionContract.Schema.COLUMN_NAME_ANSWER, answer)
        }

        val newRowId = db?.insert(QuestionContract.Schema.TABLE_NAME, null, values)
        Log.d(TAG,"item 추가")
    }

    private fun readQuestionEntryDB(){
        if (dbHelper == null) return
        val db = dbHelper?.readableDatabase

        // 데이터베이스 컬럼 중에서 알아낼 prjection을 정의한다.
        val projection = arrayOf(BaseColumns._ID, QuestionContract.Schema.COLUMN_NAME_CATEGORY_ID,
                QuestionContract.Schema.COLUMN_NAME_QUESTION, QuestionContract.Schema.COLUMN_NAME_ANSWER)

        val cursor = db?.query(
                QuestionContract.Schema.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        )

        val items = mutableListOf<QuestionContract.Entry>()
        cursor?.let {
            with(cursor) {
                while (moveToNext()) {
                    val id = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                    val category = getString(getColumnIndexOrThrow(QuestionContract.Schema.COLUMN_NAME_CATEGORY_ID))
                    val question = getString(getColumnIndexOrThrow(QuestionContract.Schema.COLUMN_NAME_QUESTION))
                    val answer = getString(getColumnIndexOrThrow(QuestionContract.Schema.COLUMN_NAME_ANSWER))
                    val entry = QuestionContract.Entry(id, category, question, answer)
                    items.add(entry)
                }
            }
        }

        Log.d(TAG, "####read db items : ")
        for (item in items){
            Log.d(TAG, "db item : $item")
        }
        Log.d(TAG, "###################")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            1234 -> {
                startUnlockCaptureServiceNoVersionCheck()
            }
        }
    }

    override fun onDestroy() {
        dbHelper?.close()
        super.onDestroy()
    }
}
