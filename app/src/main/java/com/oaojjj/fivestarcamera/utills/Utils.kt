package com.oaojjj.fivestarcamera.utills

import android.app.Activity
import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import java.io.File

object Utils {
    /**
     * 이미지 저장 경로
     */
    var path = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_DCIM
    ).toString() + "/FSCamera"

    var mDir = File(path)

    private val displayMetrics = DisplayMetrics()

    var deviceWidth: Int = 0
    var deviceHeight: Int = 0

    fun setDisplaySize(activity: Activity) {
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        deviceWidth = displayMetrics.widthPixels
        deviceHeight = displayMetrics.heightPixels

    }

    fun onRefreshGallery(context: Context?, file: File?) {
        Log.d("onRefreshGallery", "onRefreshGallery start")
        MediaScannerConnection.scanFile(
            context, arrayOf(file!!.path), null
        ) { path, _ -> Log.i("TAG", "Finished scanning $path") }
    }
}