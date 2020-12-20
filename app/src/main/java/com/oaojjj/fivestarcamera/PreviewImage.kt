package com.oaojjj.fivestarcamera

import android.graphics.Bitmap
import java.io.File

data class PreviewImage(var path: String) {
    var mFile: File? = null
    var mBitmap: Bitmap? = null

    init {
        mFile = File(path)
    }


    constructor(bitmap: Bitmap? = null, path: String) : this(path) {
        mBitmap = bitmap
        this.path = path
    }

    override fun toString(): String {
        return "PreviewImage(path='$path', bitmap=$mBitmap)"
    }
}