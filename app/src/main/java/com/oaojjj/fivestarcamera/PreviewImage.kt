package com.oaojjj.fivestarcamera

import android.graphics.Bitmap

data class PreviewImage(var path: String) {
    var bitmap: Bitmap? = null
    override fun toString(): String {
        return "PreviewImage(path='$path', bitmap=$bitmap)"
    }
}