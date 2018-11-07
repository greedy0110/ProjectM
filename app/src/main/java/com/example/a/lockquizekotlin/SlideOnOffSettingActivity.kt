package com.example.a.lockquizekotlin

import android.opengl.Visibility
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.a.lockquizekotlin.DBContract.SettingsContract
import com.example.a.lockquizekotlin.Utils.AndroidComponentUtils
import com.example.a.lockquizekotlin.Utils.LayoutUtils
import kotlinx.android.synthetic.main.activity_slide_on_off_setting.*

class SlideOnOffSettingActivity : AppCompatActivity() {

    val TAG = "SlideOnOffSetting"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slide_on_off_setting)

        var entry = SettingsContract.getSettingsEntry(applicationContext)
        updateUI(entry.slideOnOff == "o")

        slide_on_button.setOnClickListener {
            if (entry.slideOnOff == "o") return@setOnClickListener
            Log.d(TAG, "slide on button 클릭")
            entry.slideOnOff = "o"
            SettingsContract.setSettingsEntry(applicationContext, entry)
            // 설정을 적용하려면 lock service 를 다시 켜줘야한다.
            AndroidComponentUtils.startUnlockCaptureServiceNoVersionCheck(applicationContext)
            updateUI(true)
        }

        slide_off_button.setOnClickListener {
            if (entry.slideOnOff == "x") return@setOnClickListener
            Log.d(TAG, "slide off button 클릭")
            entry.slideOnOff = "x"
            SettingsContract.setSettingsEntry(applicationContext, entry)
            // 서비스를 끄자!
            AndroidComponentUtils.stopUnlockCaptureService(applicationContext)
            updateUI(false)
        }

        asoos_star_button.setOnClickListener {
            LayoutUtils.goToMenuActivity(applicationContext)
        }
    }

    override fun onStart() {
        super.onStart()
        LayoutUtils.setTheme(applicationContext, activity_slide_on_off_setting_layout)
    }

    private fun updateUI(on: Boolean){
        if (on) {
            // TODO on 이면 on 버튼에 동그라미를
            asoos_check_on_image.visibility = View.VISIBLE
            asoos_check_off_image.visibility = View.INVISIBLE
        }
        else {
            // TODO off 면 off 버튼에 동그라미를
            asoos_check_on_image.visibility = View.INVISIBLE
            asoos_check_off_image.visibility = View.VISIBLE
        }
    }
}
