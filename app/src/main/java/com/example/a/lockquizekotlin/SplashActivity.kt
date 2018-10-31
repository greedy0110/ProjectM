package com.example.a.lockquizekotlin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.a.lockquizekotlin.DBContract.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // delayTime / 1000 초의 지연시간후에 mainActivity를 켜주자.
        val delayTime = 1000L
        Handler().postDelayed({
            val menuIntent = Intent(this@SplashActivity, MenuActivity::class.java)
            this@SplashActivity.startActivity(menuIntent)
            this@SplashActivity.finish()

        }, delayTime)
        // TODO 이거 꼭 없애야한다! 이거 있으면 오답노트 내용 다 날라갈 것 (테스트 끝나면 바로 제거해야함!!)
        DbUtils.saveDbAssetToDevice(applicationContext, true) // 초기 DB 파일 다시 쓰기
    }
}
