package com.example.a.lockquizekotlin

import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.a.lockquizekotlin.Utils.LayoutUtils
import com.example.a.lockquizekotlin.Utils.ResourceUtils

// setTheme 같은 것을 기본적으로 해주자.
abstract class GreedyActivity: AppCompatActivity() {
    override fun onStart() {
        super.onStart()
        val resid = ResourceUtils.findIdByName(applicationContext, "${javaClass.simpleName}_layout")
        try {
            LayoutUtils.setTheme(applicationContext, findViewById(resid))
        }
        catch (e: Exception) {
            Log.e("GreedyActivty", "스킨 적용할 layout 발견 못함 ${javaClass.simpleName}_layout 가 없다.")
        }
    }
}