package com.example.a.lockquizekotlin

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.a.lockquizekotlin.User.UserAuth
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : GreedyActivity() {

    private lateinit var userAuth: UserAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        userAuth = UserAuth(applicationContext)

        back_button.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        asu_signup_button.setOnClickListener {
            if (checkSignInfoCorrect()) {
                val name = name_text.text.toString()
                val user_id = id_text.text.toString()
                val pas = pas_text.text.toString()
                val mail = mail_text.text.toString()
                val cell = phone_text.text.toString()
                userAuth.signup(name, user_id, pas, mail, cell) {
                    if (it) {
                        // 회원가입 성공적, 로그인 페이지로 성공적인 결과 반환
                        Toast.makeText(applicationContext, "회원가입에 성공했습니다.", Toast.LENGTH_SHORT).show()
                        val data = Intent()
                        with(data) {
                            putExtra("create", true)
                            putExtra("user_id", user_id)
                            putExtra("pas", pas)
                        }
                        setResult(Activity.RESULT_OK, data)
                        finish()
                    }
                    else {
                        // 회원가입 실패 (지금은 아이디 중복만 실패)
                        Toast.makeText(applicationContext, "이미 가입된 아이디입니다.", Toast.LENGTH_SHORT).show()
                        id_text.setText("")
                        id_text.requestFocus()
                    }
                }
            }
        }
    }

    // 이 함수에서 true가 되면 회원 가입 가능한 상태의 데이터만 들어있는지 확인
    private fun checkSignInfoCorrect(): Boolean {
        if (name_text.text.isNullOrEmpty()) {
            Toast.makeText(applicationContext, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
            name_text.requestFocus()
            return false
        }
        if (id_text.text.isNullOrEmpty()) {
            Toast.makeText(applicationContext, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()
            id_text.requestFocus()
            return false
        }
        if (pas_text.text.isNullOrEmpty() || check_pas_text.text.isNullOrEmpty()
                || !pas_text.text.toString().equals(check_pas_text.text.toString())) {
            Toast.makeText(applicationContext, "비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show()
            pas_text.requestFocus()
            return false
        }
        if (mail_text.text.isNullOrEmpty()) {
            Toast.makeText(applicationContext, "메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            mail_text.requestFocus()
            return false
        }
        if (phone_text.text.isNullOrEmpty()) {
            Toast.makeText(applicationContext, "휴대폰 번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            phone_text.requestFocus()
            return false
        }
        return true
    }
}
