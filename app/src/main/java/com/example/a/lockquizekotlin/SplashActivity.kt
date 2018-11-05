package com.example.a.lockquizekotlin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import com.example.a.lockquizekotlin.DBContract.SettingsContract
import com.example.a.lockquizekotlin.LockScreen.UnlockCaptureService
import com.example.a.lockquizekotlin.Utils.AndroidComponentUtils
import com.example.a.lockquizekotlin.Utils.AndroidComponentUtils.startUnlockCaptureServiceNoVersionCheck
import com.example.a.lockquizekotlin.Utils.DbUtils

class SplashActivity : AppCompatActivity() {

    private var forceLockPeriod = SettingsContract.Schema.DEFAULT_SLIDE_FORCE_PERIOD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // delayTime / 1000 초의 지연시간후에 menuActivity를 켜주자.
        val delayTime = 1000L
        Handler().postDelayed({
            val menuIntent = Intent(this@SplashActivity, MenuActivity::class.java)
            this@SplashActivity.startActivity(menuIntent)
            this@SplashActivity.finish()

        }, delayTime)
//        //이거 꼭 없애야한다! 이거 있으면 오답노트 내용 다 날라갈 것 (테스트 끝나면 바로 제거해야함!!)
//        DbUtils.saveDbAssetToDevice(applicationContext, true) // 초기 DB 파일 다시 쓰기

        // 초기 세팅에 슬라이드 설정을 보고 켜준다.
        val entry = SettingsContract.getSettingsEntry(applicationContext)
        if (entry.slideOnOff == "o") {
            forceLockPeriod = entry.slideForcePeriod
            startUnlockCaptureService()
        }
    }

    private fun startUnlockCaptureService() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(applicationContext)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                startActivityForResult(intent, 1234)
            } else {
                AndroidComponentUtils.startUnlockCaptureServiceNoVersionCheck(applicationContext, forceLockPeriod)
            }
        }
        else {
            AndroidComponentUtils.startUnlockCaptureServiceNoVersionCheck(applicationContext, forceLockPeriod)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            1234 -> {
                if (resultCode == Activity.RESULT_OK)
                    AndroidComponentUtils.startUnlockCaptureServiceNoVersionCheck(applicationContext, forceLockPeriod)
                else
                    startUnlockCaptureService()
            }
        }
    }
}
