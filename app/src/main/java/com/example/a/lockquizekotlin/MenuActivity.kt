package com.example.a.lockquizekotlin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        setting_button.setOnClickListener {
            val setting = Intent(applicationContext, SettingActivitiy::class.java)
            startActivity(setting)
        }
    }
}
