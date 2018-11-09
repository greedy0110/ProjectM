package com.example.a.lockquizekotlin

import android.content.ContentValues
import android.database.sqlite.SQLiteOpenHelper
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.example.a.lockquizekotlin.DBContract.*
import com.example.a.lockquizekotlin.R.id.*
import com.example.a.lockquizekotlin.Utils.AndroidComponentUtils
import com.example.a.lockquizekotlin.Utils.LayoutUtils
import com.example.a.lockquizekotlin.Utils.MathUtils
import kotlinx.android.synthetic.main.activity_question.*
import java.util.*

class QuestionActivity : AppCompatActivity() {
    val TAG = "QuestionActivity"
    private var questionDbHelper: SQLiteOpenHelper? = null
    private var userDbHelper: SQLiteOpenHelper? = null
    private var answer: String = ""
    private var justCategory = false
    private var selectedCategoryId = -1
    private var questionList = listOf<QuestionEntry>()
    private var incorrectList = mutableListOf<QuestionEntry>()
    private var currentQuestionIndex = -1
    private var dx: Float = 0F
    private var dy: Float = 0F
    private var originX: Float = 0F
    private var yesButtonX: Float = 0F
    private var noButtonX: Float = 0F
    private var checkNear: Float = 150F
    private var moveableReach: Float = 0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        setButtonEvents()


        justCategory = intent.extras.getBoolean("ox")
        if (justCategory) { // 카테고리에 해당하는 문제 나열 용
            selectedCategoryId = intent.extras.getInt("category_id")
            questionList = getQuestionsByCategoryId(selectedCategoryId)
            Collections.shuffle(questionList)
            incorrectList = getQuestionsByExistInIncorrectEntryTable(selectedCategoryId)
            if (questionList.isEmpty()) {
                Toast.makeText(applicationContext, "지정된 카테고리에 문제가 없습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
            else setQuestionUIByIndex(0)
        }
        else { // 오답 노트 용
            selectedCategoryId = intent.extras.getInt("category_id") // 오답노트도 법 별로임!
            questionList = getQuestionsByExistInIncorrectEntryTable(selectedCategoryId) // 오답노트 테이블에 있는 녀석만 가져옴
            Collections.shuffle(questionList)
            incorrectList = questionList.toMutableList()
            if (questionList.isEmpty()) {
                Toast.makeText(applicationContext, "와우! 오답 노트에 저장된 문제가 없습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
            else setQuestionUIByIndex(0)
        }
    }


    override fun onStart() {
        super.onStart()
        LayoutUtils.setTheme(applicationContext, activity_question_layout)
        LayoutUtils.setSlideButtonTheme(applicationContext, qa_drag_button_view, qa_yes_button, qa_no_button)
        LayoutUtils.setSlideLeftRightTheme(applicationContext, qa_prev_question_button, qa_next_question_button)

        initDraggableButton()
    }

    private fun setButtonEvents(){
        qa_next_question_button.setOnClickListener {
            goNextQuestion()
        }

        qa_prev_question_button.setOnClickListener {
            goPrevQuestion()
        }

        qa_end_button.setOnClickListener {
            finish()
        }

        qa_star_button.setOnClickListener {
            if (incorrectList.contains(questionList[currentQuestionIndex])){ // 오답노트에 있는 내용이면 오답노트에서 제거하고 노란색으로 다시 바꾸자!
                IncorrectDB.deleteOneByQuestionId(applicationContext, questionList[currentQuestionIndex].id)
                incorrectList.remove(questionList[currentQuestionIndex])
                Toast.makeText(applicationContext, "오답노트에서 제거 했어요", Toast.LENGTH_SHORT).show()
                updateStar()
            }
            else { // 오답트에 없는 내용이면 오답노트에 적고 빨간색으로 바꿀꺼!
                checkIncorrect(questionList[currentQuestionIndex])
                updateStar()
            }
        }
    }

    private fun initDraggableButton() {
        qa_drag_button_view.post { // qa_drag_button_view 가 초기화 된후 실행 될거라 originX가 잘 저장될것이다.
            originX = qa_drag_button_view.x
            qa_yes_button.post {
                yesButtonX = qa_yes_button.x
                moveableReach = Math.max(moveableReach, Math.abs(yesButtonX - originX))
            }

            qa_no_button.post {
                noButtonX = qa_no_button.x
                moveableReach = Math.max(moveableReach, Math.abs(noButtonX - originX))
            }
        }


        qa_drag_button_view.setOnTouchListener { v, event ->
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
                            Log.d(TAG, "yes button click by drag")
                            if (currentQuestionIndex >= 0 && currentQuestionIndex < questionList.size) {
                                when (questionList[currentQuestionIndex].answer) {
                                    "o" -> correctAnswer()
                                    "x" -> wrongAnswer()
                                }
                            }
                        }
                        in noButtonX-checkNear..noButtonX+checkNear -> {
                            Log.d(TAG, "no button click by drag")
                            if (currentQuestionIndex >= 0 && currentQuestionIndex < questionList.size) {
                                when (questionList[currentQuestionIndex].answer) {
                                    "o" -> wrongAnswer()
                                    "x" -> correctAnswer()
                                }
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

    private fun wrongAnswer() {
        Toast.makeText(applicationContext, "오답입니다.", Toast.LENGTH_SHORT).show()
        activity_question_layout.setBackgroundResource(R.drawable.w_back)
        shakeQuestion()
        AndroidComponentUtils.postDelayedLaunch({
            LayoutUtils.setTheme(applicationContext, activity_question_layout)
        }, 1000)
    }

    private fun correctAnswer() {
        Toast.makeText(applicationContext, "정답입니다.", Toast.LENGTH_SHORT).show()
        qa_check_image.visibility = View.VISIBLE
        AndroidComponentUtils.postDelayedLaunch({
            qa_check_image.visibility = View.INVISIBLE
            goNextQuestion()
        }, 1000)
    }

    private fun checkIncorrect(entry: QuestionEntry) {
        // incorrect_table 에 적용이 안된 녀석이면, 써준다.
        if (!IncorrectDB.searchOneByQuestionId(applicationContext, entry.id)) {
            if (IncorrectDB.writeOne(applicationContext, IncorrectEntry(0, entry.id))){
                incorrectList.add(entry)
                Toast.makeText(applicationContext, "오답노트에 체크했어요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setQuestionUIByIndex(index: Int) {
        if (questionList.size <= index || index < 0) return
        val q = questionList[index]
        val qs = "${q.question} (${q.year})".toString()
        qa_qquestion_textview.text = qs
        qa_qcategory_textview.text = q.category
        currentQuestionIndex = index

        updateStar()
    }

    private fun updateStar(){
        if (incorrectList.contains(questionList[currentQuestionIndex])){ // 오답노트에 있는 내용이면 붉은 별로
            qa_star_button.setBackgroundResource(R.drawable.red_star)
        }
        else { // 오답노트에 없는 내용이면 노란 별로
            qa_star_button.setBackgroundResource(R.drawable.yellow_star)
        }
    }

    private fun goNextQuestion(): Boolean {
        if (currentQuestionIndex == -1 || currentQuestionIndex >= questionList.size - 1) {
            Toast.makeText(applicationContext, "마지막 문제입니다", Toast.LENGTH_SHORT).show()
            return false
        }
        setQuestionUIByIndex(++currentQuestionIndex)
        return true
    }

    private fun goPrevQuestion(): Boolean {
        if (currentQuestionIndex == -1 || currentQuestionIndex <= 0) {
            Toast.makeText(applicationContext, "마지막 문제입니다", Toast.LENGTH_SHORT).show()
            return false
        }
        setQuestionUIByIndex(--currentQuestionIndex)
        return true
    }

    private fun shakeQuestion(){
        val shake = AnimationUtils.loadAnimation(applicationContext, R.anim.shake)

        qa_qcategory_textview.startAnimation(shake)
        qa_qquestion_textview.startAnimation(shake)
    }

    // category_id에 해당하는 문제 목록을 반환하자
    private fun getQuestionsByCategoryId(category_id: Int): List<QuestionEntry> {
        val questions = QuestionDB.readAll(applicationContext)
        val categoryName = CategoryDB.readOne(applicationContext, category_id)

        if (categoryName != null) {
            val result = mutableListOf<QuestionEntry>()
            for (q in questions) {
                if (q.category == categoryName.category) {
                    result.add(q)
                }
            }
            return result.toList()
        }
        else {
            return listOf()
        }
    }

    private fun getQuestionsByExistInIncorrectEntryTable(category_id: Int): MutableList<QuestionEntry> {
        val incorrects = IncorrectDB.readAll(applicationContext)
        val questions = QuestionDB.readAll(applicationContext)
        val category_name =CategoryDB.readOne(applicationContext, category_id)?.category
        if (category_name == null) return mutableListOf()

        val result = mutableListOf<QuestionEntry>()
        for (inc in incorrects) {
            val q = questions.find {
                (inc.question_id == it.id) && (it.category == category_name)
            }
            if (q == null) continue
            result.add(q)
        }
        return result
    }
}
