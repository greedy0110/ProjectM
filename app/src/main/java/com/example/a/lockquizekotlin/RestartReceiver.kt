package com.example.a.lockquizekotlin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

// UnlockCaptureService가 종료되지 않도록 하기위해 만든다.
class RestartReceiver : BroadcastReceiver() {
    val TAG = "RestartReceiver"

    companion object {
        val ACTION_RESTART_SERVICE = "RestartReceiver.restart"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive")
        if (intent.action == ACTION_RESTART_SERVICE) {
            val i = Intent(context, UnlockCaptureService::class.java)
            context.startService(i)
        }
    }
}
