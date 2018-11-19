package com.example.a.lockquizekotlin

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.a.lockquizekotlin.DBContract.SettingsPref
import com.example.a.lockquizekotlin.DBContract.UserDB
import com.example.a.lockquizekotlin.User.UserAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : GreedyActivity() {
    companion object {
        const val SIGNUP_REQUEST = 1
    }

    private lateinit var userAuth: UserAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        userAuth = UserAuth(applicationContext)

        login_button.setOnClickListener {
            if (al_id_text.text.isNullOrEmpty()
                || al_password_text.text.isNullOrEmpty()) {
                Toast.makeText(applicationContext, "로그인 정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val user_id = al_id_text.text.toString()
            val pas = al_password_text.text.toString()
            tryLogin(user_id, pas)
        }

        signup_button.setOnClickListener {
            val signupIntent = Intent(applicationContext, SignUpActivity::class.java)
            startActivityForResult(signupIntent, SIGNUP_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            SIGNUP_REQUEST -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.let {
                        val isCreated = data.getBooleanExtra("create", false)
                        if (isCreated) {
                            val user_id = data.getStringExtra("user_id")
                            val pas = data.getStringExtra("pas")
                            al_id_text.setText(user_id)
                            al_password_text.setText(pas)
                            tryLogin(user_id, pas)
                        }
                    }
                }
            }
        }
    }

    private fun loginAndGoToMenu(user_id:String, pas:String){
        // 로그인 성공
        Toast.makeText(applicationContext, "${user_id} 님 환영합니다!", Toast.LENGTH_SHORT).show()
        // 메뉴 화면으로 이동
        val menuIntent = Intent(applicationContext, MenuActivity::class.java)
        startActivity(menuIntent)
        finish()
    }

    private fun tryLogin(user_id:String, pas:String) {
        userAuth.login(user_id, pas) { match ->
            if (match) {
                loginAndGoToMenu(user_id, pas)
            } else {
                // 로그인 실패
                Toast.makeText(applicationContext, "로그인 정보를 확인해주세요.", Toast.LENGTH_SHORT).show()
                al_id_text.requestFocus()
            }
        }
    }

    override fun onBackPressed() {
        // 뒤로 가기 버튼 제한
    }
}
