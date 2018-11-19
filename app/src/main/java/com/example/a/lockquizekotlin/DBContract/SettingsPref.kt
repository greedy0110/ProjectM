package com.example.a.lockquizekotlin.DBContract

import android.content.Context
import android.content.SharedPreferences

object SettingsPref {
    fun getSettings(context: Context): Entry {
        val pref = getPref(context)
        val theme = pref.getString("theme", "db")
        val slideOnOff = pref.getString("slideOnOff", "o")
        val slideForcePeriod = pref.getInt("slideForcePeriod", 1000)
        val user_id = pref.getString("user_id", null)
        val pas = pref.getString("pas", null)
        val font = pref.getString("font", "han")
        return Entry(theme, slideOnOff, slideForcePeriod, user_id, pas, font)
    }

    fun setSettings(context: Context, entry: Entry) {
        val pref = getPref(context)
        val editor = pref.edit()
        editor.putString("theme", entry.theme)
        editor.putString("slideOnOff", entry.slideOnOff)
        editor.putInt("slideForcePeriod", entry.slideForcePeriod)
        editor.putString("user_id", entry.user_id)
        editor.putString("pas", entry.pas)
        editor.putString("font", entry.font)
        editor.apply()
    }

    fun setTheme(context: Context, theme: String) {
        val pref = getPref(context)
        pref.edit().putString("theme", theme).apply()
    }

    fun getTheme(context: Context): String {
        val pref = getPref(context)
        return pref.getString("theme", "db")
    }

    private fun getPref(context: Context): SharedPreferences {
        return context.getSharedPreferences("settings", 0)
    }
}

data class Entry(var theme: String = "db", var slideOnOff:String = "o", var slideForcePeriod: Int = 1000,
                 var user_id: String? = null, var pas: String? = null, var font: String = "han")