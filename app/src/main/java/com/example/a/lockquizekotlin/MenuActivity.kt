package com.example.a.lockquizekotlin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import com.example.a.lockquizekotlin.DBContract.SettingsContract
import com.example.a.lockquizekotlin.LockScreen.UnlockCaptureService
import com.example.a.lockquizekotlin.Utils.LayoutUtils
import com.example.a.lockquizekotlin.Utils.ResourceUtils
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {
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
            val setting = Intent(applicationContext, SettingActivitiy::class.java)
            startActivity(setting)
        }
    }

    override fun onStart() {
        super.onStart()
        LayoutUtils.setTheme(applicationContext, activity_menu_layout)
    }



}
