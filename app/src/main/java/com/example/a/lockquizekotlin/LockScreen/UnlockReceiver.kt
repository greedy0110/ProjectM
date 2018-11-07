package com.example.a.lockquizekotlin.LockScreen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.a.lockquizekotlin.DBContract.SettingsContract

class UnlockReceiver : BroadcastReceiver() {
    val TAG = "UnlockReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive")
        if (intent.action == Intent.ACTION_SCREEN_ON) {
            // LockScreenService 를 시작해주어야 한다.
            // 이미 LockScreenService 를 띄워줄 권한은 받은 상태여야 한다.
            val intent = Intent(context, LockScreenService::class.java)
            val setting = SettingsContract.getSettingsEntry(context)
            intent.putExtra("forceLockPeriod", setting.slideForcePeriod)
            context.startService(intent)
        }
    }
}
