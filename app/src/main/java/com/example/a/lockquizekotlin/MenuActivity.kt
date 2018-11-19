package com.example.a.lockquizekotlin

import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : GreedyActivity() {
    val TAG = "MenuActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        ox_quiz_button.setOnClickListener {
            val oxQuiz = Intent(applicationContext, CategoryListActivity::class.java)
            oxQuiz.putExtra("ox", true)
            startActivity(oxQuiz)
        }

        wrong_answer_button.setOnClickListener{
            val wrong = Intent(applicationContext, CategoryListActivity::class.java)
            wrong.putExtra("ox", false)
            startActivity(wrong)
        }

        setting_button.setOnClickListener {
            val setting = Intent(applicationContext, SettingActivity::class.java)
            startActivity(setting)
        }
    }

}
