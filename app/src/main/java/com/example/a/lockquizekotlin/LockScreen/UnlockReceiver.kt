package com.example.a.lockquizekotlin.LockScreen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import com.example.a.lockquizekotlin.DBContract.SettingsContract
import android.telephony.PhoneStateListener



class UnlockReceiver : BroadcastReceiver() {
    val TAG = "UnlockReceiver"
    private var mTelephonyManager: TelephonyManager? = null
    private val phoneListener = object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, incomingNumber: String) {
            when (state) {
                TelephonyManager.CALL_STATE_IDLE -> mIsPhoneIdle = true
                TelephonyManager.CALL_STATE_RINGING -> mIsPhoneIdle = false
                TelephonyManager.CALL_STATE_OFFHOOK -> mIsPhoneIdle = false
            }
        }
    }
    private var mIsPhoneIdle = true

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive")
        if (intent.action == Intent.ACTION_SCREEN_ON) {
            // 전화 통화가 온 상태면 키면 화가 난다.
            if (mTelephonyManager == null) {
                mTelephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            }
            mTelephonyManager?.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE)

            if (mIsPhoneIdle) {
                // LockScreenService 를 시작해주어야 한다.
                // 이미 LockScreenService 를 띄워줄 권한은 받은 상태여야 한다.
                val intent = Intent(context, LockScreenService::class.java)
                val setting = SettingsContract.getSettingsEntry(context)
                intent.putExtra("forceLockPeriod", setting.slideForcePeriod)
                context.startService(intent)
            }
        }
    }
}
