package com.oaojjj.fivestarcamera.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.oaojjj.fivestarcamera.PreviewImage
import com.oaojjj.fivestarcamera.R
import com.oaojjj.fivestarcamera.customView.CropImageView

class CropActivity() : AppCompatActivity() {
    enum class CropType { RECTANGLE, FREE }

    companion object {
        private var mImage: PreviewImage? = null
        private var mCropType: CropType? = null
    }

    constructor(image: PreviewImage, cropType: CropType) : this() {
        mImage = image
        mCropType = cropType
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (mCropType == CropType.RECTANGLE) {
            setContentView(R.layout.activity_crop)
        } else {
            setContentView(CropImageView(this, this, mImage!!))
        }
    }
}