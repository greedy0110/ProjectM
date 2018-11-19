package com.example.a.lockquizekotlin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.a.lockquizekotlin.Utils.LayoutUtils
import kotlinx.android.synthetic.main.activity_slide_settings.*

class SlideSettingsActivity : GreedyActivity() {

    val TAG = "SlideSettingsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slide_settings)

        ass_go_force_lock_button.setOnClickListener {
            Log.d(TAG, "force lock button 클릭")
            val intent = Intent(applicationContext, ForceLockSettingActivity::class.java)
            startActivity(intent)
        }

        ass_go_slide_onoff_button.setOnClickListener {
            Log.d(TAG, "slide onoff button 클릭")
            val intent = Intent(applicationContext, SlideOnOffSettingActivity::class.java)
            startActivity(intent)
        }

        ass_end_button.setOnClickListener {
            LayoutUtils.goBack(this)
        }
    }
}
