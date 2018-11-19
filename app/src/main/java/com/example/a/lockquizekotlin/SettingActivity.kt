package com.example.a.lockquizekotlin

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.a.lockquizekotlin.DBContract.SettingsPref
import com.example.a.lockquizekotlin.User.UserAuth
import com.example.a.lockquizekotlin.Utils.AndroidComponentUtils
import com.example.a.lockquizekotlin.Utils.LayoutUtils
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : GreedyActivity() {
    val TAG = "SettingActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        setButtonEvents()
    }

    private fun setButtonEvents() {
        skin_setting_button.setOnClickListener {
            Log.d(TAG, "skin setting 클릭")
            val intent = Intent(applicationContext, SelectThemeActivity::class.java)
            startActivity(intent)
        }

        lock_slide_setting_button.setOnClickListener {
            Log.d(TAG, "lock slide setting 클릭")
            val intent = Intent(applicationContext, SlideSettingsActivity::class.java)
            startActivity(intent)
        }

        logout_button.setOnClickListener {
            Log.d(TAG, "logout button 클릭")
            val userAuth = UserAuth(applicationContext)
            // 로그아웃 하자.
            Toast.makeText(applicationContext, "로그아웃 하였습니다.", Toast.LENGTH_SHORT).show()
            userAuth.logout()

            // 로그인 화면으로 이동
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        font_setting_button.setOnClickListener {
            Log.d(TAG, "font setting button 클릭")
//            val fonts = Intent(applicationContext, FontSettingActivity::class.java)
//            startActivity(fonts)
            createFontDialog().show()
        }

        as_end_button.setOnClickListener {
            LayoutUtils.goBack(this)
        }

    }

    private val mFontNameList = arrayOf("한마음명조체", "김남윤체", "이숲체", "나눔고딕코딩체")
    private val mFontSettingNameList = arrayOf("han", "kimnamyun", "leehyunji", "nanum")
    private fun createFontDialog(): Dialog {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("폰트 선택")
                .setItems(mFontNameList, DialogInterface.OnClickListener { dialog, which ->
                    Log.d(TAG, "$which : ${mFontNameList[which]}, 선택됌")
                    val settingName = mFontSettingNameList[which]
                    val entry = SettingsPref.getSettings(applicationContext)
                    Log.d(TAG, "font change : ${entry.font} -> $settingName")
                    if (entry.font == settingName) return@OnClickListener
                    entry.font = settingName
                    SettingsPref.setSettings(applicationContext, entry)
                    onStart() // 폰트 변경 설정
                })
        return builder.create()
    }
}
