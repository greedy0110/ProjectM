package com.example.a.lockquizekotlin

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.lock_screen.*

class MainActivity : AppCompatActivity() {
    val TAG: String = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startServiceButton.setOnClickListener {
            Log.d(TAG, "start service button 눌렸다.")
            startLockScreenService()
        }

        stopServiceButton.setOnClickListener {
            Log.d(TAG, "stop service button 눌렸다.")
            val intent = Intent(applicationContext, LockScreenService::class.java)
            applicationContext?.stopService(intent)
        }
    }

    private fun startLockScreenService() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(applicationContext)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                startActivityForResult(intent, 1234)
            } else {
                startLockScreenServiceNoVersionCheck()
            }
        }
        else {
            startLockScreenServiceNoVersionCheck()
        }
    }

    private fun startLockScreenServiceNoVersionCheck(){
        val intent = Intent(applicationContext, LockScreenService::class.java)
        applicationContext?.startService(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            1234 -> {
                startLockScreenServiceNoVersionCheck()
            }
        }
    }
}
