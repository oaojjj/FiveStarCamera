package com.oaojjj.fivestarcamera.utills

import android.app.Activity
import android.util.DisplayMetrics

object Utils {
    private val displayMetrics = DisplayMetrics()

    var deviceWidth: Int = 0
    var deviceHeight: Int = 0

    fun setDisplaySize(activity: Activity) {
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        deviceWidth = displayMetrics.widthPixels
        deviceHeight = displayMetrics.heightPixels

    }
}