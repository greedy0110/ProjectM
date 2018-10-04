package com.example.a.lockquizekotlin

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val TAG: String = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startServiceButton.setOnClickListener {
            Log.d(TAG, "start service button 눌렸다.")
            startUnlockCaptureService()
        }

        stopServiceButton.setOnClickListener {
            Log.d(TAG, "stop service button 눌렸다.")
            stopUnlockCaptureService()
        }
    }

    private fun startUnlockCaptureService() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(applicationContext)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                startActivityForResult(intent, 1234)
            } else {
                startUnlockCaptureServiceNoVersionCheck()
            }
        }
        else {
            startUnlockCaptureServiceNoVersionCheck()
        }
    }

    private fun startUnlockCaptureServiceNoVersionCheck(){
        val intent = Intent(applicationContext, UnlockCaptureService::class.java)
        applicationContext?.startService(intent)
    }

    private fun stopUnlockCaptureService(){
        val intent = Intent(applicationContext, UnlockCaptureService::class.java)
        applicationContext?.stopService(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            1234 -> {
                startUnlockCaptureServiceNoVersionCheck()
            }
        }
    }
}
