package com.example.a.lockquizekotlin.LockScreen

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.a.lockquizekotlin.DBContract.QuestionDB
import com.example.a.lockquizekotlin.DBContract.SettingsContract
import com.example.a.lockquizekotlin.R
import com.example.a.lockquizekotlin.Utils.AndroidComponentUtils
import com.example.a.lockquizekotlin.Utils.LayoutUtils
import com.example.a.lockquizekotlin.Utils.MathUtils
import com.example.a.lockquizekotlin.Utils.ResourceUtils
import java.util.*

// TODO  이 녀석이 오답노트 체크가능하도록 만들어야함
class LockScreenService : Service() {
    val TAG: String = "LockScreenService"
    var mView: View? = null
    var mWindowManager: WindowManager? = null
    private var answer: String = ""
    // 문제 틀리고 해당 시간 아무것도 못하고 문제틀린것 확인가능 ㅎㅎ;
    private var forceLockPeriod = SettingsContract.Schema.DEFAULT_SLIDE_FORCE_PERIOD

    private var dx: Float = 0F
    private var dy: Float = 0F
    private var originX: Float = 0F
    private var yesButtonX: Float = 0F
    private var noButtonX: Float = 0F
    private var checkNear: Float = 150F
    private var moveableReach: Float = 0F

    private var mQuestionTextView: TextView? = null
    private var mCategoryTextView: TextView? = null
    private val mNumOfCountDot = 5
    private var mNextCloseDot = 5
    private var dotManager :LockDotManager? = null


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand ${intent?.extras?.getInt("forceLockPeriod")}")
        val flp = intent?.extras?.getInt("forceLockPeriod")
        flp?.let {
            forceLockPeriod = flp
            Log.d(TAG, "$forceLockPeriod 밀리초로 강제 락 시간세팅")
        }
        return super.onStartCommand(intent, flags, startId)
    }


    // 화면에 최상단 뷰를 추가하자!
    override fun onCreate() {
        super.onCreate()

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mView = inflater.inflate(R.layout.lock_screen, null)

        mQuestionTextView = mView?.findViewById(R.id.qquestion_textview)
        mCategoryTextView = mView?.findViewById(R.id.qcategory_textview)
        mView?.let {
            dotManager = LockDotManager(it, mNumOfCountDot)
        }
        val layout = mView?.findViewById(R.id.lock_screen_layout) as View
        LayoutUtils.setTheme(applicationContext, layout)

        selectDisplayQuestion()
        initDraggableButton()

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

        super.onDestroy()
    }

    private fun unlockLookScreen(){
        stopSelf()
    }

    private fun shakeQuestion(){
        val shake = AnimationUtils.loadAnimation(applicationContext, R.anim.shake)

        mQuestionTextView?.startAnimation(shake)
        mCategoryTextView?.startAnimation(shake)
    }

    private fun initDraggableButton() {
        val drag_button_view = mView?.findViewById<View>(R.id.drag_button_view)
        val yes_button = mView?.findViewById<Button>(R.id.yes_button)
        val no_button = mView?.findViewById<Button>(R.id.no_button)
        drag_button_view?.post { // qa_drag_button_view 가 초기화 된후 실행 될거라 originX가 잘 저장될것이다.
            originX = drag_button_view.x
            yes_button?.post {
                yesButtonX = yes_button.x
                moveableReach = Math.max(moveableReach, Math.abs(yesButtonX - originX))
            }

            no_button?.post {
                noButtonX = no_button.x
                moveableReach = Math.max(moveableReach, Math.abs(noButtonX - originX))
            }
        }

        if (drag_button_view != null && yes_button != null && no_button != null) {
            LayoutUtils.setSlideButtonTheme(applicationContext, drag_button_view, yes_button, no_button)
        }

        drag_button_view?.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    dx = v.x - event.rawX
                    //dy = v.y - event.rawY Y 축으로 이동을 제한!
                }
                MotionEvent.ACTION_MOVE -> {
                    var newX = event.rawX + dx
                    newX = MathUtils.clamp(newX, originX-moveableReach, originX+moveableReach)
                    v.animate()
                            .x(newX)
                            //.y(event.rawY + dy) Y 축으로 이동을 제한!
                            .setDuration(0)
                            .start()
                }
                MotionEvent.ACTION_UP -> {
                    dx = 0F

                    // yes / no 는 그 시점에 문제 정답에 따라 다른 행동을 한다.
                    // 정답일 경우 => 정답임을 띄워주고 (이미지뷰 처리)
                    // 오답일 경우 => 오답임을 경고하고 오답 노트에 추가한다. (오답 노트에 없다면!)
                    when (v.x) {
                        in yesButtonX-checkNear..yesButtonX+checkNear -> {
                            Log.d(TAG, "yes button drag click")
                            when(answer) {
                                "o" -> correctAnswer()
                                else -> wrongAnswer()
                            }
                        }
                        in noButtonX-checkNear..noButtonX+checkNear -> {
                            Log.d(TAG, "no button drag click")
                            when(answer) {
                                "x" -> correctAnswer()
                                else -> wrongAnswer()
                            }
                        }
                    }

                    v.animate() // 손을 때면 원위치로 부드럽게 이동하자.
                            .x(originX)
                            .setDuration(300) // 높게 지정할 수록 부드럽게, 느리게 원위치 할것이야~
                            .start()
                }
                else ->
                    return@setOnTouchListener false
            }
            return@setOnTouchListener true
        }
    }

    private fun wrongAnswer(){
        Log.d(TAG, "오답이야 못나가!")
        shakeQuestion()

        // 강제 잠금 시간 만큼 버튼을 누를수가 없어야함 (빨간화면으로 보여주자)
        val layout = mView?.findViewById<View>(R.id.lock_screen_layout)
        val prevMoveableReach = moveableReach
        moveableReach = 0F

        if (layout != null) {
            layout.setBackgroundResource(R.drawable.w_back)
            shakeQuestion()
            // 다른 버튼을 누를 수 없어야함, 강제 잠금 설정 시간 후 만큼 기다려야함
            val countDownTerm = (forceLockPeriod/mNumOfCountDot).toLong()
            dotManager?.open()
            countdown(forceLockPeriod.toLong(), countDownTerm)
            AndroidComponentUtils.postDelayedLaunch({
                LayoutUtils.setTheme(applicationContext, layout)
                // slide 버튼을 움직일수 있게 롤백시킴
                moveableReach = prevMoveableReach
            }, forceLockPeriod.toLong())
            Log.d(TAG, "$forceLockPeriod 만큼 강제 잠금 화면 중")
        }
    }

    private fun countdown(time: Long, term: Long) {
        Log.d(TAG, "$time 밀리초 남음!")
        // 하나의 dot 이 꺼짐
        dotManager?.close()
        if (time - term < 0) {
            // 모든 dot 들이 꺼짐
        } else {
            AndroidComponentUtils.postDelayedLaunch({ countdown(time - term, term) }, term)
        }
    }

    private fun correctAnswer(){
        unlockLookScreen()
        Toast.makeText(applicationContext, "정답입니다!", Toast.LENGTH_SHORT).show()
    }

    private fun selectDisplayQuestion() {
        val questions = QuestionDB.readAll(applicationContext)

        val randomQuestion= Random().nextInt(questions.size)
        val item = questions[randomQuestion]

        Log.d(TAG, "정보 읽어옴 선택된 정보 : {$item}")
        mCategoryTextView?.text = item.category
        mQuestionTextView?.text = item.question + " (${item.year})"
        answer = item.answer
    }
}

class LockDotManager(val mView: View, val mNumOfCountDot: Int = 5) {
    private var mNextCloseCount = mNumOfCountDot
    private val mLockDotList = mutableListOf<View>()

    init {
        val theme = SettingsContract.getCurrentTheme(mView.context)
        for (i in 1..mNumOfCountDot) {
            val dot = mView.findViewWithTag<ImageView>("lock_dot$i")
            dot.setBackgroundResource(ResourceUtils.findDrawableByName(mView.context,"${theme}_dot"))
            mLockDotList.add(dot)
        }
    }

    fun open() {
        for (v in mLockDotList) {
            v.visibility = View.VISIBLE
        }
        mNextCloseCount = mNumOfCountDot
    }

    fun close() {
        if (mNextCloseCount-1 < 0) return
        mLockDotList[mNextCloseCount-1].visibility = View.INVISIBLE
        mNextCloseCount--
    }
}