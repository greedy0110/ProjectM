package com.example.a.lockquizekotlin

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import java.util.zip.Inflater
import kotlinx.android.synthetic.main.lock_screen.*

class LockScreenService : Service() {
    val TAG: String = "LockScreenService"
    var mView: View? = null
    var mWindowManager: WindowManager? = null

    override fun onBind(intent: Intent): IBinder? {
        TODO("Return the communication channel to the service.")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        mView?.let { mWindowManager?.removeView(mView) }
        super.onDestroy()
    }

    // 화면에 최상단 뷰를 추가하자!
    override fun onCreate() {
        super.onCreate()

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mView = inflater.inflate(R.layout.lock_screen, null)

        val unlockBtn = mView?.findViewById(R.id.unlock_dummy) as Button?
        unlockBtn?.setOnClickListener {
            Log.d(TAG, "unlock button click")
            stopSelf()
        }

        val LAYOUT_FLAG = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else
            WindowManager.LayoutParams.TYPE_PHONE

        val params =  WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                LAYOUT_FLAG ,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                        WindowManager.LayoutParams.FLAG_FULLSCREEN or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.RGBA_8888
        )

        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mWindowManager?.addView(mView, params)
    }
}
