package com.example.a.lockquizekotlin.Utils

import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbInterface
import android.os.Handler
import com.example.a.lockquizekotlin.LockScreen.UnlockCaptureService

// 액티비티, 서비스, 브로드캐스터, 리시버에 관련된 자주쓰는 함수들을 모아놓자
object AndroidComponentUtils {
    fun startUnlockCaptureServiceNoVersionCheck(context: Context){
        val intent = Intent(context, UnlockCaptureService::class.java)
        context.startService(intent)
    }

    fun stopUnlockCaptureService(context: Context){
        val intent = Intent(context, UnlockCaptureService::class.java)
        context.stopService(intent)
    }

    fun postDelayedLaunch(callback: ()->Unit, time: Long){
        Handler().postDelayed(callback, time)
    }
}