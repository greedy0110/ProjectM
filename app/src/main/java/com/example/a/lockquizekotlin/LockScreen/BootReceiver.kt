package com.example.a.lockquizekotlin.LockScreen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.a.lockquizekotlin.Utils.AndroidComponentUtils

class BootReceiver : BroadcastReceiver() {
    private val TAG = "BootReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        Log.d(TAG, "on Receive!")
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "부트해서 시작해보자")
            AndroidComponentUtils.startUnlockCaptureServiceNoVersionCheck(context)
        }
    }
}
