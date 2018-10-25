package com.example.a.lockquizekotlin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

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
    }
}
