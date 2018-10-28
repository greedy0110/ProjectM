package com.example.a.lockquizekotlin

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.BaseColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.a.lockquizekotlin.DBContract.QuestionContract

class LockScreenService : Service() {
    val TAG: String = "LockScreenService"
    var mView: View? = null
    var mWindowManager: WindowManager? = null
    private var dbHelper: QuestionContract.DbHelper? = null
    private var answer: String = ""

    override fun onBind(intent: Intent): IBinder? {
        TODO("Return the communication channel to the service.")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }


    // 화면에 최상단 뷰를 추가하자!
    override fun onCreate() {
        super.onCreate()
        // dbHelper 초기화 해준다.
        dbHelper = QuestionContract.DbHelper(applicationContext)

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mView = inflater.inflate(R.layout.lock_screen, null)

        val yesBtn = mView?.findViewById(R.id.yes_button) as Button?
        yesBtn?.setOnClickListener {
            Log.d(TAG, "yes button click")
            when(answer) {
                "yes" -> {unlockLookScreen()}
                else -> { Log.d(TAG, "오답입니다~ 못나가세요")}
            }
        }
        val noBtn = mView?.findViewById(R.id.no_button) as Button?
        noBtn?.setOnClickListener {
            Log.d(TAG, "no button click")
            when(answer) {
                "no" -> {unlockLookScreen()}
                else -> {Log.d(TAG, "오답이야 못나가!")}
            }
        }

        val backgroundImage = mView?.findViewById(R.id.background) as ImageView?
        backgroundImage?.let {
            // 대체 왜인지는 모르겠으나 여기서 이미지 리소스를 지정해주어야한다.
            backgroundImage.setImageResource(R.drawable.ic_launcher_background)
        }

        selectDisplayQuestion()

        val LAYOUT_FLAG = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else
            WindowManager.LayoutParams.TYPE_PHONE

        val params =  WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                LAYOUT_FLAG ,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                        WindowManager.LayoutParams.FLAG_FULLSCREEN or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
        )

        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mWindowManager?.addView(mView, params)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        mView?.let { mWindowManager?.removeView(mView) }

        // dbHelper 자원을 없에자
        dbHelper?.close()
        super.onDestroy()
    }

    private fun unlockLookScreen(){
        stopSelf()
    }

    private fun selectDisplayQuestion() {
        val db = dbHelper?.readableDatabase
        if (db == null) {
            unlockLookScreen()
            return
        }

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

        val item = items[0]
        Log.d(TAG, "정보 읽어옴 선택된 정보 : {$item}")
        val category = mView?.findViewById(R.id.qcategory_textview) as TextView
        val question = mView?.findViewById(R.id.qquestion_textview) as TextView
        category.text = item.category
        question.text = item.question
        answer = item.answer

    }
}
