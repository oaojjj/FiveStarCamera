package com.oaojjj.fivestarcamera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import com.oaojjj.fivestarcamera.controller.ImageController

data class PreviewImage(var path: String) {
    var bitmap: Bitmap? = null
    override fun toString(): String {
        return "PreviewImage(path='$path', bitmap=$bitmap)"
    }

    // bitmap = BitmapFactory.decodeFile(this.path) 한 줄만 있어도 되는데
    // 안드로이드 버그로 이미지뷰에 회전을 해서 보여주게되서 다시 정상으로 돌려서 넣음
    // 지금 방법이 뷰페이저를 하나씩 넘길 때 마다 생성해서 엄청 느린데
    // TODO 캐시 같은걸 써봐야할듯
    /*fun createBitmap() {
        val exif = ExifInterface(this.path)
        val exifOrientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        bitmap = BitmapFactory.decodeFile(this.path)
        val exifDegree = ImageController.getInstance()?.exifOrientationToDegrees(exifOrientation)
        bitmap = ImageController.getInstance()?.rotate(bitmap!!, exifDegree!!)!!
    }*/

}