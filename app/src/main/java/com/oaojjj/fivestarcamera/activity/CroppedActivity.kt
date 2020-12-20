package com.oaojjj.fivestarcamera.activity

import android.graphics.*
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import com.oaojjj.fivestarcamera.R
import com.oaojjj.fivestarcamera.controller.ImageController
import com.oaojjj.fivestarcamera.customView.CropImageView
import kotlinx.android.synthetic.main.activity_cropped.*


class CroppedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cropped)

        val bytes = intent.getByteArrayExtra("image")
        var bitmap: Bitmap? = BitmapFactory.decodeByteArray(
            bytes, 0, bytes!!.size
        )

        var widthOfScreen = 0
        var heightOfScreen = 0

        val dm = DisplayMetrics()
        try {
            windowManager.defaultDisplay.getMetrics(dm)
            widthOfScreen = dm.widthPixels
            heightOfScreen = dm.heightPixels
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        val resultingImage = Bitmap.createBitmap(widthOfScreen, heightOfScreen, bitmap!!.config)

        val canvas = Canvas(resultingImage)
        val paint = Paint()
        paint.isAntiAlias = true

        val path = Path()
        for (i in 0 until CropImageView.points.size) {
            path.lineTo(CropImageView.points[i]!!.x, CropImageView.points[i]!!.y)
        }

        canvas.drawPath(path, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        iv_cropped_container.setImageBitmap(resultingImage)

        btn_save.setOnClickListener {
            ImageController.getInstance()
                ?.saveBitmap(applicationContext, resultingImage)
            finish()
        }
    }
}