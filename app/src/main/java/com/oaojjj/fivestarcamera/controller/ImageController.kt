package com.oaojjj.fivestarcamera.controller

import android.animation.ValueAnimator
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import com.oaojjj.fivestarcamera.PreviewImage
import com.oaojjj.fivestarcamera.utills.Utils.deviceHeight
import com.oaojjj.fivestarcamera.utills.Utils.deviceWidth
import java.io.File

class ImageController {
    companion object {
        private var instance: ImageController? = null

        fun getInstance(): ImageController? {
            if (instance == null) {
                instance = ImageController()
            }
            return instance
        }
    }

    /* // 전체 디렉토리 에서 마지막 이미지 가져오기
     @RequiresApi(Build.VERSION_CODES.Q)
     fun getLatestImage(): Bitmap? {
         val projection = arrayOf(
             MediaStore.Images.ImageColumns._ID,
             MediaStore.Images.ImageColumns.DATA,
             MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
             MediaStore.Images.ImageColumns.DATE_TAKEN,
             MediaStore.Images.ImageColumns.MIME_TYPE
         )
         val cursor = mSuper.baseContext.contentResolver.query(
             MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null,
             MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"
         )

         while (cursor!!.moveToFirst()) {
             val latestImageUri = cursor.getString(1)
             val imageFile = File(latestImageUri)
             if (imageFile.exists()) {
                 cursor.close()
                 val exif = ExifInterface(latestImageUri)
                 val exifOrientation = exif.getAttributeInt(
                     ExifInterface.TAG_ORIENTATION,
                     ExifInterface.ORIENTATION_NORMAL
                 )
                 var bitmap = BitmapFactory.decodeFile(latestImageUri)
                 val exifDegree = exifOrientationToDegrees(exifOrientation)
                 bitmap = rotate(bitmap, exifDegree)
                 return bitmap
             }

         }

         cursor.close()
         return null
     }*/

    // 마지막 이미지 불러오기
    fun getLatestImage(context: Context, path: String): Bitmap? {
        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME)
        val cursor: Cursor? =
            context.contentResolver.query(
                uri,
                projection,
                null,
                null,
                MediaStore.MediaColumns.DATE_ADDED + " desc"
            )
        val columnIndex: Int = cursor?.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)!!

        while (cursor.moveToNext()) {
            val absolutePathOfImage: String = cursor.getString(columnIndex)
            val nCol = cursor.getColumnIndex(MediaStore.Images.Media.DATA) // bitmap

            if (cursor.getString(nCol).startsWith(path)) {
                val imageFile = File(absolutePathOfImage)
                if (imageFile.exists()) {
                    cursor.close()
                    val exif = ExifInterface(absolutePathOfImage)
                    val exifOrientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )
                    var bitmap = BitmapFactory.decodeFile(absolutePathOfImage)
                    val exifDegree = exifOrientationToDegrees(exifOrientation)
                    bitmap = rotate(bitmap, exifDegree)
                    return bitmap
                }
            }
        }

        cursor.close()
        return null
    }

    // hint -> https://codechacha.com/ko/android-mediastore-read-media-files/
    // 이미지 목록 불러오기
    fun getPathOfAllImages(context: Context, path: String): MutableList<String>? {
        val result: MutableList<String> = mutableListOf()
        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME)
        val cursor: Cursor? =
            context.contentResolver.query(
                uri,
                projection,
                null,
                null,
                MediaStore.MediaColumns.DATE_TAKEN + " desc"
            )
        val columnIndex: Int = cursor?.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)!!

        while (cursor.moveToNext()) {
            val absolutePathOfImage: String = cursor.getString(columnIndex)
            val nCol = cursor.getColumnIndex(MediaStore.Images.Media.DATA) // bitmap

            if (cursor.getString(nCol).startsWith(path))
                result.add(absolutePathOfImage)
        }

        for ((index, string) in result.withIndex()) {
            Log.i("TAG_EditorActivity", "${index}_${string}")
        }

        cursor.close()
        return result
    }

    // 회전 각도 구하기
    private fun exifOrientationToDegrees(exifOrientation: Int): Int {
        return when (exifOrientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                90
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                180
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                270
            }
            else -> 0
        }
    }

    // 사진 임의로 회전(비트맵 생성할 때)
    private fun rotate(bitmap: Bitmap, degrees: Int): Bitmap {
        if (degrees != 0) {
            val m = Matrix()
            m.setRotate(
                degrees.toFloat(),
                (bitmap.width / 2).toFloat(),
                (bitmap.height / 2).toFloat()
            )
            val converted = Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                m,
                true
            )
            if (bitmap != converted) {
                return converted
            }

        }
        return bitmap
    }

    /**
     * 아래부터는 이미지 편집 기능을 구현한 메소드
     * 이미지 회전 애니메이션
     */
    // hint -> https://dev3m.tistory.com/entry/%EC%9E%90%EC%97%B0%EC%8A%A4%EB%9F%AC%EC%9A%B4-ImageView-%ED%9A%8C%EC%A0%84-%EC%A0%81%EC%9A%A9%ED%95%98%EA%B8%B0-%EC%B9%B4%EC%B9%B4%EC%98%A4%ED%86%A1-%EC%9D%B4%EB%AF%B8%EC%A7%80-%ED%8E%B8%EC%A7%91
    fun setRotation(imageView: ImageView, image: PreviewImage?, toDegree: Int) {
        val currentRotation = imageView.rotation
        val currentImageViewHeight = imageView.height
        val heightGap = if (currentImageViewHeight > deviceWidth) {
            deviceWidth - currentImageViewHeight
        } else {
            deviceHeight - currentImageViewHeight
        }

        if (currentRotation % 90 == 0.toFloat()) {
            ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 500
                addUpdateListener {
                    val animatedValue = it.animatedValue as Float
                    imageView.run {
                        layoutParams.height =
                            currentImageViewHeight + (heightGap * animatedValue).toInt()
                        rotation = currentRotation + toDegree * animatedValue
                        requestLayout()
                    }
                }
            }.start()
        }
        // save rotation bitmap
        Log.d("rotateAlertDialog",image.toString())
    }
}