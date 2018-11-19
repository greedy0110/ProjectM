package com.example.a.lockquizekotlin.User

import android.content.Context
import com.example.a.lockquizekotlin.DBContract.SettingsPref
import com.example.a.lockquizekotlin.DBContract.UserDB
import com.example.a.lockquizekotlin.DBContract.UserEntry

class UserAuth(val context: Context) {
    fun login(user_id: String, pas: String, callback: ((Boolean)->Unit)) {
        val match = UserDB.matchUserIdPas(context, user_id, pas)
        // 로그인 정보 저장
        val settings = SettingsPref.getSettings(context)
        settings.user_id = user_id; settings.pas = pas
        SettingsPref.setSettings(context, settings)
        callback(match)
    }

    fun logout() {
        // 로그인 정보 지우기
        val settings = SettingsPref.getSettings(context)
        settings.user_id = null; settings.pas = null
        SettingsPref.setSettings(context, settings)
    }

    fun signup(name: String, user_id: String, pas: String, mail: String, cell: String, callback: (Boolean) -> Unit) {
        val hasUserId = UserDB.hasUserid(context, user_id)
        if (hasUserId) {
            callback(false)
        }
        else {
            val well = UserDB.writeOne(context, UserEntry(0, name, user_id, pas, mail, cell))
            callback(well)
        }
    }
}