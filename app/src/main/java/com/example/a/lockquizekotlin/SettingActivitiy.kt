package com.example.a.lockquizekotlin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.a.lockquizekotlin.DBContract.SettingsContract
import com.example.a.lockquizekotlin.Utils.LayoutUtils
import com.example.a.lockquizekotlin.Utils.ResourceUtils
import kotlinx.android.synthetic.main.activity_select_theme.*
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivitiy : AppCompatActivity() {
    val TAG = "SettingActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        setButtonEvents()
    }

    override fun onStart() {
        super.onStart()
        LayoutUtils.setTheme(applicationContext, activity_setting_layout)
    }

    private fun setButtonEvents() {
        skin_setting_button.setOnClickListener {
            val intent = Intent(applicationContext, SelectThemeActivity::class.java)
            startActivity(intent)
        }
    }
}
