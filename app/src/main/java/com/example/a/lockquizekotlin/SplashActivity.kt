package com.example.a.lockquizekotlin

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.widget.Toast
import com.example.a.lockquizekotlin.DBContract.SettingsPref
import com.example.a.lockquizekotlin.DBContract.UserDB
import com.example.a.lockquizekotlin.Utils.AndroidComponentUtils
import com.example.a.lockquizekotlin.Utils.LayoutUtils
import com.example.a.lockquizekotlin.Utils.ResourceUtils
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : GreedyActivity() {

    private var forceLockPeriod = 1000
    private val delayTime = 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val theme = SettingsPref.getTheme(applicationContext)
        staryou_imageview.setImageResource(ResourceUtils.findDrawableByName(applicationContext, "${theme[0]}_staryou"))

        // delayTime / 1000 초의 지연시간후에 menuActivity를 켜주자.

        // 초기 세팅에 슬라이드 설정을 보고 켜준다.
        val entry = SettingsPref.getSettings(applicationContext)
        if (entry.slideOnOff == "o") {
            forceLockPeriod = entry.slideForcePeriod
            startUnlockCaptureService()
        }
        else {
            goToNext()
        }
    }

    private fun startUnlockCaptureService() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(applicationContext)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                startActivityForResult(intent, 1234)
            } else {
                AndroidComponentUtils.startUnlockCaptureServiceNoVersionCheck(applicationContext)
                goToNext()
            }
        }
        else {
            AndroidComponentUtils.startUnlockCaptureServiceNoVersionCheck(applicationContext)
            goToNext()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            1234 -> {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (Settings.canDrawOverlays(applicationContext)) {
                        AndroidComponentUtils.startUnlockCaptureServiceNoVersionCheck(applicationContext)
                        goToNext()
                    } else {
                        Toast.makeText(applicationContext, "권한 설정을 해야함", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                else {
                    goToNext()
                }
            }
        }
    }

    private fun goToNext(){
        Handler().postDelayed({
            val settings = SettingsPref.getSettings(applicationContext)
            if (settings.user_id == null
                    || settings.pas == null
                    || !UserDB.matchUserIdPas(applicationContext, settings.user_id.toString(), settings.pas.toString())) {

                val loginIntent = Intent(this@SplashActivity, LoginActivity::class.java)
                startActivity(loginIntent)
                finish()
            }
            else {
                val menuIntent = Intent(this@SplashActivity, MenuActivity::class.java)
                startActivity(menuIntent)
                finish()
            }

        }, delayTime)
    }
}
