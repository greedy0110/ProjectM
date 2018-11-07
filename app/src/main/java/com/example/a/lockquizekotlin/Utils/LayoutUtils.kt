package com.example.a.lockquizekotlin.Utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.a.lockquizekotlin.DBContract.SettingsContract
import com.example.a.lockquizekotlin.MenuActivity

object LayoutUtils {
    fun findTextViews(context: Context, v: View): List<TextView> {
        views.clear()
        findTextViewsInner(context,v)
        return views.toList()
    }

    private val views = mutableListOf<TextView>()
    private fun findTextViewsInner(context: Context, v: View){
        try {
            if (v is ViewGroup) {
                for (i in 0..v.childCount) {
                    if (v.getChildAt(i) != null) {
                        findTextViewsInner(context, v.getChildAt(i))
                    }
                }
            }
            else if (v is TextView){
                views.add(v)
            }
        }
        catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun setTheme(context: Context, layout: View) {
        val theme = SettingsContract.getCurrentTheme(context)

        layout.setBackgroundResource(ResourceUtils.findDrawableByName(context,"${theme}_back"))
        val allTextViews = findTextViews(context, layout)
        val textColor = if (theme[0] == 'd') Color.WHITE else Color.BLACK
        for (tv in allTextViews) {
            tv.setTextColor(textColor)
        }
    }

    fun setSlideButtonTheme(context: Context, slide: View, o: View, x: View) {
        val theme = SettingsContract.getCurrentTheme(context)

        slide.setBackgroundResource(ResourceUtils.findDrawableByName(context, "${theme}_sbutton"))
        o.setBackgroundResource(ResourceUtils.findDrawableByName(context, "${theme}_so"))
        x.setBackgroundResource(ResourceUtils.findDrawableByName(context, "${theme}_sx"))
    }

    fun setSlideLeftRightTheme(context: Context, left:View, right: View) {
        val theme = SettingsContract.getCurrentTheme(context)

        left.setBackgroundResource(ResourceUtils.findDrawableByName(context, "${theme}_left"))
        right.setBackgroundResource(ResourceUtils.findDrawableByName(context, "${theme}_right"))
    }

    fun goToMenuActivity(context: Context) {
        val intent = Intent(context, MenuActivity::class.java)
        context.startActivity(intent)
    }

    fun goBack(activity: Activity) {
        activity.finish()
    }
}