package com.example.a.lockquizekotlin.Utils

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import com.example.a.lockquizekotlin.DBContract.SettingsContract
import com.example.a.lockquizekotlin.R.id.activity_question_layout
import kotlinx.android.synthetic.main.activity_question.*

object ResourceUtils{
    fun findDrawableByName(context: Context, name: String): Int {
        val ress = context.resources
        val resid = ress.getIdentifier(name, "drawable", context.packageName)
        return resid
    }


}