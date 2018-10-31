package com.example.a.lockquizekotlin

import android.content.ContentValues
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.a.lockquizekotlin.DBContract.CategoryContract
import com.example.a.lockquizekotlin.DBContract.IncorrectContract
import com.example.a.lockquizekotlin.DBContract.QuestionContract
import kotlinx.android.synthetic.main.activity_question.*
import java.util.*

class QuestionActivity : AppCompatActivity() {
    val TAG = "QuestionActivity"
    private var questionDbHelper: QuestionContract.DbHelper? = null
    private var answer: String = ""
    private var justCategory = false
    private var selectedCategoryId = -1
    private var questionList = listOf<QuestionContract.Entry>()
    private var incorrectList = mutableListOf<QuestionContract.Entry>()
    private var currentQuestionIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        setButtonEvents()

        justCategory = intent.extras.getBoolean("ox")
        if (justCategory) { // 카테고리에 해당하는 문제 나열 용
            selectedCategoryId = intent.extras.getInt("category_id")
            questionList = getQuestionsByCategoryId(selectedCategoryId)
            Collections.shuffle(questionList)
            incorrectList = getQuestionsByExistInIncorrectEntryTable(getCategoryNameById(selectedCategoryId))
            if (questionList.isEmpty()) {
                Toast.makeText(applicationContext, "지정된 카테고리에 문제가 없습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
            else setQuestionUIByIndex(0)
        }
        else { // 오답 노트 용
            selectedCategoryId = intent.extras.getInt("category_id") // 오답노트도 법 별로임!
            questionList = getQuestionsByExistInIncorrectEntryTable(getCategoryNameById(selectedCategoryId)) // 오답노트 테이블에 있는 녀석만 가져옴
            Collections.shuffle(questionList)
            incorrectList = questionList.toMutableList()
            if (questionList.isEmpty()) {
                Toast.makeText(applicationContext, "와우! 오답 노트에 저장된 문제가 없습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
            else setQuestionUIByIndex(0)
        }
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

        // yes / no 는 그 시점에 문제 정답에 따라 다른 행동을 한다.
        // 정답일 경우 => 정답임을 띄워주고 (이미지뷰 처리)
        // 오답일 경우 => 오답임을 경고하고 오답 노트에 추가한다. (오답 노트에 없다면!)
        qa_yes_button.setOnClickListener {
            if (currentQuestionIndex < 0 || currentQuestionIndex >= questionList.size) return@setOnClickListener
            when (questionList[currentQuestionIndex].answer) {
                "o" -> correctAnswer()
                "x" -> wrongAnswer()
            }
        }

        qa_no_button.setOnClickListener {
            if (currentQuestionIndex < 0 || currentQuestionIndex >= questionList.size) return@setOnClickListener
            when (questionList[currentQuestionIndex].answer) {
                "o" -> wrongAnswer()
                "x" -> correctAnswer()
            }
        }
    }

    private fun wrongAnswer() {
        Toast.makeText(applicationContext, "오답입니다.", Toast.LENGTH_SHORT).show()
        checkIncorrect(questionList[currentQuestionIndex])
        updateStar()
    }

    private fun correctAnswer() {
        Toast.makeText(applicationContext, "정답입니다.", Toast.LENGTH_SHORT).show()
        goNextQuestion()
    }

    private fun checkIncorrect(entry: QuestionContract.Entry) {
        // incorrect_table 에 적용이 안된 녀석이면, 써준다.
        if (!hasQuestionInIncorrectEntryTable(entry.id)) {
            if (insertQuestionIntoIncoorectEntryTable(entry.id)){
                incorrectList.add(entry)
                Toast.makeText(applicationContext, "오답노트에 체크하였습니다.", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(applicationContext, "마지막 문제입니다.", Toast.LENGTH_SHORT).show()
            return false
        }
        setQuestionUIByIndex(++currentQuestionIndex)
        return true
    }

    private fun goPrevQuestion(): Boolean {
        if (currentQuestionIndex == -1 || currentQuestionIndex <= 0) {
            Toast.makeText(applicationContext, "마지막 문제입니다.", Toast.LENGTH_SHORT).show()
            return false
        }
        setQuestionUIByIndex(--currentQuestionIndex)
        return true
    }

    // category_id에 해당하는 문제 목록을 반환하자
    private fun getQuestionsByCategoryId(category_id: Int): MutableList<QuestionContract.Entry> {
        questionDbHelper = QuestionContract.DbHelper(applicationContext)
        val qdb = questionDbHelper?.readableDatabase

        val categoryName = getCategoryNameById(category_id)
        // 데이터베이스 컬럼 중에서 알아낼 prjection을 정의한다.
        val projection = arrayOf(QuestionContract.Schema.COLUMN_ID, QuestionContract.Schema.COLUMN_NAME_YEAR,
                QuestionContract.Schema.COLUMN_NAME_QUESTION, QuestionContract.Schema.COLUMN_NAME_ANSWER)
        val selectionString = "${QuestionContract.Schema.COLUMN_NAME_CATEGORY} = ?"
        val selectionArgs = arrayOf<String>(categoryName)

        val cursor = qdb?.query(
                QuestionContract.Schema.TABLE_NAME,
                projection,
                selectionString,
                selectionArgs,
                null,
                null,
                null
        )

        val questionList = mutableListOf<QuestionContract.Entry>()
        cursor?.let {
            with(cursor) {
                while (moveToNext()) {
                    val id = getInt(getColumnIndexOrThrow(QuestionContract.Schema.COLUMN_ID))
                    val year = getString(getColumnIndexOrThrow(QuestionContract.Schema.COLUMN_NAME_YEAR))
                    val question = getString(getColumnIndexOrThrow(QuestionContract.Schema.COLUMN_NAME_QUESTION))
                    val answer = getString(getColumnIndexOrThrow(QuestionContract.Schema.COLUMN_NAME_ANSWER))
                    val entry = QuestionContract.Entry(id, year, categoryName, question, answer)
                    Log.d(TAG, "question entry : ${entry.toString()}")
                    questionList.add(entry)
                }
            }
        }

        return questionList
    }

    private fun getQuestionsByExistInIncorrectEntryTable(category_name: String): MutableList<QuestionContract.Entry> {
        questionDbHelper = QuestionContract.DbHelper(applicationContext)
        val qdb = questionDbHelper?.readableDatabase

        val projection = arrayOf(IncorrectContract.Schema.COLUMN_NAME_QUESTION_ID)

        val cursor = qdb?.query(
                IncorrectContract.Schema.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        )

        val questionList = mutableListOf<QuestionContract.Entry>()
        cursor?.let {
            with(cursor) {
                while (moveToNext()) {
                    val question_id = getInt(getColumnIndexOrThrow(IncorrectContract.Schema.COLUMN_NAME_QUESTION_ID))
                    val question_entry = getQuestionEntryEasyById(question_id)
                    Log.d(TAG, "question by exist in incorrect entry table $question_entry")
                    if (question_entry.category == category_name)
                        questionList.add(getQuestionEntryEasyById(question_id))
                }
            }
        }

        return questionList
    }

    private fun getCategoryNameById(id: Int): String{
        questionDbHelper = QuestionContract.DbHelper(applicationContext)
        val qdb = questionDbHelper?.readableDatabase

        val categoryProjection = arrayOf(CategoryContract.Schema.COLUMN_NAME)
        val categorySelectionString = "${CategoryContract.Schema.COLUMN_ID} = ?"
        val categorySelectionArgs = arrayOf<String>(id.toString())

        val categoryCursor = qdb?.query(
                CategoryContract.Schema.TABLE_NAME,
                categoryProjection,
                categorySelectionString,
                categorySelectionArgs,
                null,
                null,
                null
        )

        var name = ""
        categoryCursor?.let {
            with(categoryCursor) {
                while (moveToNext()) {
                    name = getString(getColumnIndexOrThrow(CategoryContract.Schema.COLUMN_NAME))
                }
            }
        }

        return name
    }

    private fun getQuestionEntryEasyById(id: Int): QuestionContract.Entry {
        questionDbHelper = QuestionContract.DbHelper(applicationContext)
        val qdb = questionDbHelper?.readableDatabase

        val projection = arrayOf(QuestionContract.Schema.COLUMN_ID, QuestionContract.Schema.COLUMN_NAME_YEAR ,QuestionContract.Schema.COLUMN_NAME_CATEGORY,
                QuestionContract.Schema.COLUMN_NAME_QUESTION, QuestionContract.Schema.COLUMN_NAME_ANSWER)
        val selectionString = "${QuestionContract.Schema.COLUMN_ID} = ?"
        val selectionArgs = arrayOf<String>(id.toString())

        val cursor = qdb?.query(
                QuestionContract.Schema.TABLE_NAME,
                projection,
                selectionString,
                selectionArgs,
                null,
                null,
                null
        )

        var entry = QuestionContract.Entry(0,"year", "category", "question", "o")
        cursor?.let {
            with(cursor) {
                while (moveToNext()) {
                    val id = getInt(getColumnIndexOrThrow(QuestionContract.Schema.COLUMN_ID))
                    val year = getString(getColumnIndexOrThrow(QuestionContract.Schema.COLUMN_NAME_YEAR))
                    val categoryName = getString(getColumnIndexOrThrow(QuestionContract.Schema.COLUMN_NAME_CATEGORY))
                    val question = getString(getColumnIndexOrThrow(QuestionContract.Schema.COLUMN_NAME_QUESTION))
                    val answer = getString(getColumnIndexOrThrow(QuestionContract.Schema.COLUMN_NAME_ANSWER))
                    entry = QuestionContract.Entry(id, year, categoryName, question, answer)
                    Log.d(TAG, "incorrect question entry : ${entry.toString()}")
                }
            }
        }

        return entry
    }

    private fun hasQuestionInIncorrectEntryTable(question_id: Int): Boolean{
        questionDbHelper = QuestionContract.DbHelper(applicationContext)
        val qdb = questionDbHelper?.readableDatabase

        val projection = arrayOf(IncorrectContract.Schema.COLUMN_NAME_QUESTION_ID)
        val selectionString = "${IncorrectContract.Schema.COLUMN_NAME_QUESTION_ID} = ?"
        val selectionArgs = arrayOf<String>(question_id.toString())

        val cursor = qdb?.query(
                IncorrectContract.Schema.TABLE_NAME,
                projection,
                selectionString,
                selectionArgs,
                null,
                null,
                null
        )

        return cursor?.count != 0
    }

    private fun insertQuestionIntoIncoorectEntryTable(question_id: Int): Boolean{
        questionDbHelper = QuestionContract.DbHelper(applicationContext)
        val qdb = questionDbHelper?.readableDatabase

        val newRowId = qdb?.insert(IncorrectContract.Schema.TABLE_NAME, null, ContentValues().apply {
            put(IncorrectContract.Schema.COLUMN_NAME_QUESTION_ID, question_id)
        })

        if (newRowId == -1L) {
            Toast.makeText(applicationContext, "insert problem", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "insert 에러")
            return false
        }
        return true
    }
}
