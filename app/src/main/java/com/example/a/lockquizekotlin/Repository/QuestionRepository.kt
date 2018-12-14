package com.example.a.lockquizekotlin.Repository

import android.content.Context
import com.example.a.lockquizekotlin.DBContract.CategoryDB
import com.example.a.lockquizekotlin.DBContract.IncorrectDB
import com.example.a.lockquizekotlin.DBContract.QuestionDB
import com.example.a.lockquizekotlin.DBContract.QuestionEntry

class QuestionRepository(val context: Context) {
    // category_id에 해당하는 문제 목록을 반환하자
    fun getQuestionsByCategoryId(category_id: Int): List<QuestionEntry> {
        val questions = QuestionDB.readAll(context)
        val categoryName = CategoryDB.readOne(context, category_id)

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

    fun getQuestionsByExistInIncorrectEntryTable(category_id: Int): MutableList<QuestionEntry> {
        val incorrects = IncorrectDB.readAll(context)
        val questions = QuestionDB.readAll(context)
        if (category_id != -1) {
            val category_name = CategoryDB.readOne(context, category_id)?.category
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
        else { // 모든 문제를 가져온다.
            val result = mutableListOf<QuestionEntry>()
            for (inc in incorrects) {
                val q = questions.find {
                    (inc.question_id == it.id)
                }
                if (q == null) continue
                result.add(q)
            }
            return result
        }
    }
}