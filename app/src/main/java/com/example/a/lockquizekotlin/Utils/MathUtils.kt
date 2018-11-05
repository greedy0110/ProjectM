package com.example.a.lockquizekotlin.Utils

object MathUtils {
    fun clamp(value:Double, min:Double, max:Double): Double {
        return Math.min(Math.max(value, min), max)
    }
    fun clamp(value:Float, min:Float, max:Float): Float {
        return Math.min(Math.max(value, min), max)
    }
}