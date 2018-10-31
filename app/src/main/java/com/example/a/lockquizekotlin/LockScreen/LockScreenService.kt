package com.example.a.lockquizekotlin.LockScreen

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.a.lockquizekotlin.DBContract.QuestionContract
import com.example.a.lockquizekotlin.R
import java.util.*

class LockScreenService : Service() {
    val TAG: String = "LockScreenService"
    var mView: View? = null
    var mWindowManager: WindowManager? = null
    private var questionDbHelper: QuestionContract.DbHelper? = null
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

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mView = inflater.inflate(R.layout.lock_screen, null)

        val yesBtn = mView?.findViewById(R.id.yes_button) as Button?
        yesBtn?.setOnClickListener {
            Log.d(TAG, "yes button click")
            when(answer) {
                "o" -> {
                    unlockLookScreen()
                    Toast.makeText(applicationContext, "정답입니다!", Toast.LENGTH_SHORT).show() // TODO 이거 다른 식으로 변경해야함. 단순한 토스트 로 알림 구현
                }
                else -> {
                    Log.d(TAG, "오답입니다~ 못나가세요")
                    Toast.makeText(applicationContext, "오답입니다! 못나갑니다!", Toast.LENGTH_SHORT).show() // TODO 이거 다른 식으로 변경해야함. 단순한 토스트 로 알림 구현
                }
            }
        }
        val noBtn = mView?.findViewById(R.id.no_button) as Button?
        noBtn?.setOnClickListener {
            Log.d(TAG, "no button click")
            when(answer) {
                "x" -> {
                    unlockLookScreen()
                    Toast.makeText(applicationContext, "정답입니다!", Toast.LENGTH_SHORT).show() // TODO 이거 다른 식으로 변경해야함. 단순한 토스트 로 알림 구현
                }
                else -> {
                    Log.d(TAG, "오답이야 못나가!")
                    Toast.makeText(applicationContext, "오답입니다! 못나갑니다!", Toast.LENGTH_SHORT).show() // TODO 이거 다른 식으로 변경해야함. 단순한 토스트 로 알림 구현
                }
            }
        }

//        val backgroundImage = mView?.findViewById(R.id.background) as ImageView?
//        backgroundImage?.let {
//            // 대체 왜인지는 모르겠으나 여기서 이미지 리소스를 지정해주어야한다.
//            backgroundImage.setImageResource(R.drawable.ic_launcher_background)
//        }

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
        questionDbHelper?.close()
        super.onDestroy()
    }

    private fun unlockLookScreen(){
        stopSelf()
    }

    private fun selectDisplayQuestion() {
        questionDbHelper = QuestionContract.DbHelper(applicationContext)
        val qdb = questionDbHelper?.readableDatabase
        if (qdb == null) {
            unlockLookScreen()
            return
        }

        // 데이터베이스 컬럼 중에서 알아낼 prjection을 정의한다.
        val projection = arrayOf(QuestionContract.Schema.COLUMN_ID, QuestionContract.Schema.COLUMN_NAME_YEAR,QuestionContract.Schema.COLUMN_NAME_CATEGORY,
                QuestionContract.Schema.COLUMN_NAME_QUESTION, QuestionContract.Schema.COLUMN_NAME_ANSWER)

        val cursor = qdb?.query(
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
                    val id = getInt(getColumnIndexOrThrow(QuestionContract.Schema.COLUMN_ID))
                    val year = getString(getColumnIndexOrThrow(QuestionContract.Schema.COLUMN_NAME_YEAR))
                    val category = getString(getColumnIndexOrThrow(QuestionContract.Schema.COLUMN_NAME_CATEGORY))
                    val question = getString(getColumnIndexOrThrow(QuestionContract.Schema.COLUMN_NAME_QUESTION))
                    val answer = getString(getColumnIndexOrThrow(QuestionContract.Schema.COLUMN_NAME_ANSWER))
                    val entry = QuestionContract.Entry(id, year, category, question, answer)
                    Log.d(TAG, "question entry : ${entry.toString()}")
                    items.add(entry)
                }
            }
        }

        val randomQuestion= Random().nextInt(items.size)
        val item = items[randomQuestion]

        Log.d(TAG, "정보 읽어옴 선택된 정보 : {$item}")
        val category = mView?.findViewById(R.id.qcategory_textview) as TextView
        val question = mView?.findViewById(R.id.qquestion_textview) as TextView
        category.text = item.category
        question.text = item.question + " (${item.year})"
        answer = item.answer
    }
}
