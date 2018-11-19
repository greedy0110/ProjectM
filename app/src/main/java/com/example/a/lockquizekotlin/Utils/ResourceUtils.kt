package com.example.a.lockquizekotlin.Utils

import android.content.Context

object ResourceUtils{
    fun findDrawableByName(context: Context, name: String): Int {
        val ress = context.resources
        val resid = ress.getIdentifier(name, "drawable", context.packageName)
        return resid
    }

    fun findIdByName(context: Context, name: String): Int {
        val ress = context.resources
        val resid = ress.getIdentifier(name, "id", context.packageName)
        return resid
    }

}