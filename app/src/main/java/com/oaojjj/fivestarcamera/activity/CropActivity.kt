package com.oaojjj.fivestarcamera.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.oaojjj.fivestarcamera.PreviewImage
import com.oaojjj.fivestarcamera.R
import com.oaojjj.fivestarcamera.customView.CropImageView

class CropActivity() : AppCompatActivity() {
    // 왜 static 으로 해야 이미지가 넘어오는지 알 수가 없네
    companion object {
        private var mImage: PreviewImage? = null
        private var mCropMode: Int = 0
    }

    constructor(image: PreviewImage, cropMode: Int) : this() {
        mImage = image
        mCropMode = cropMode
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (mCropMode == 0) {
            setContentView(R.layout.activity_crop)
        } else {
            setContentView(CropImageView(this, this, mImage!!))
        }

        RESULT_OK

    }
}