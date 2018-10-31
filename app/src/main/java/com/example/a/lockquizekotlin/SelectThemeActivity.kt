package com.example.a.lockquizekotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.a.lockquizekotlin.DBContract.SettingsContract
import com.example.a.lockquizekotlin.Utils.LayoutUtils
import com.example.a.lockquizekotlin.Utils.ResourceUtils
import kotlinx.android.synthetic.main.activity_select_theme.*

class SelectThemeActivity : AppCompatActivity() {
    val TAG = "SelectThemeActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_theme)

        setButtonEvents()
    }

    override fun onStart() {
        super.onStart()
        LayoutUtils.setTheme(applicationContext, activity_select_theme_layout)
    }

    private fun setButtonEvents() {
        dp_button.setOnClickListener {
            onThemeChangeClick("dp")
        }

        db_button.setOnClickListener {
            onThemeChangeClick("db")
        }

        dgr_button.setOnClickListener {
            onThemeChangeClick("dgr")
        }

        dg_button.setOnClickListener {
            onThemeChangeClick("dg")
        }

        ly_button.setOnClickListener {
            onThemeChangeClick("ly")
        }

        lp_button.setOnClickListener {
            onThemeChangeClick("lp")
        }

        lg_button.setOnClickListener {
            onThemeChangeClick("lg")
        }

        ls_button.setOnClickListener {
            onThemeChangeClick("ls")
        }
    }

    private fun onThemeChangeClick(theme: String){
        // 0. 버튼은 thema text 를 알수 있다.
        // 1. db에 thema 를 저장한다.
        SettingsContract.setTheme(applicationContext, theme)
        // 2. 스킨이 UI에 적용된다. (각 activity가 onstart일때, 세팅값을 확인하고 ui를 적용한다면?)
        LayoutUtils.setTheme(applicationContext, activity_select_theme_layout)
    }
}
