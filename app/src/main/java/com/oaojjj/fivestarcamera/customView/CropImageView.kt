package com.oaojjj.fivestarcamera.customView

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.oaojjj.fivestarcamera.PreviewImage
import com.oaojjj.fivestarcamera.activity.CropActivity
import com.oaojjj.fivestarcamera.activity.CroppedActivity
import java.io.ByteArrayOutputStream


class CropImageView : View, View.OnTouchListener {
    lateinit var mSuper: CropActivity
    private var paint: Paint
    var flgPathDraw = true

    private var mFirstPoint: Point? = null
    private var bFirstPoint = false

    private var mLastPoint: Point? = null

    var bitmap: Bitmap? = null
    private lateinit var byteArray: ByteArray

    private var mContext: Context
    var mHeight = 0
    var mWidth = 0

    constructor(activity: CropActivity, context: Context, image: PreviewImage) : super(context) {
        mSuper = activity
        mContext = context

        val displayMetrics = DisplayMetrics()
        (mContext as AppCompatActivity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        mHeight = displayMetrics.heightPixels
        mWidth = displayMetrics.widthPixels

        val original = BitmapFactory.decodeFile(image.path)
        val scale = (mWidth / original.width.toFloat())

        val imageWidth = (original.width * scale).toInt()
        val imageHeight = (original.height * scale).toInt()

        val stream = ByteArrayOutputStream()

        bitmap = Bitmap.createScaledBitmap(original, imageWidth, imageHeight, true)
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 50, stream)
        byteArray = stream.toByteArray()

        isFocusable = true
        isFocusableInTouchMode = true

        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.STROKE
        paint.pathEffect = DashPathEffect(floatArrayOf(10f, 20f), 0F)
        paint.strokeWidth = 5f
        paint.color = Color.WHITE

        bFirstPoint = false

        points = ArrayList()

        setOnTouchListener(this)
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        mContext = context
        isFocusable = true
        isFocusableInTouchMode = true

        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        paint.color = Color.WHITE

        setOnTouchListener(this)

        points = ArrayList()
        bFirstPoint = false
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(bitmap!!, 0f, 0f, null)
        val path = Path()
        var first = true
        var i = 0
        while (i < points.size) {
            val point = points[i]
            when {
                first -> {
                    first = false
                    path.moveTo(point!!.x, point.y)
                }
                i < points.size - 1 -> {
                    val next = points[i + 1]
                    path.quadTo(point!!.x, point.y, next!!.x, next.y)
                }
                else -> {
                    mLastPoint = points[i]
                    path.lineTo(point!!.x, point.y)
                }
            }
            i += 2
        }
        canvas.drawPath(path, paint)
    }

    override fun onTouch(view: View?, event: MotionEvent): Boolean {
        val point = Point()
        point.x = (event.x.toInt()).toFloat()
        point.y = (event.y.toInt()).toFloat()

        if (flgPathDraw) {
            if (bFirstPoint) {
                if (comparePoint(mFirstPoint, point)) {
                    points.add(mFirstPoint)
                    flgPathDraw = false
                    showCropDialog()
                } else {
                    points.add(point)
                }
            } else {
                points.add(point)
            }
            if (!bFirstPoint) {
                mFirstPoint = point
                bFirstPoint = true
            }
        }
        invalidate()
        if (event.action == MotionEvent.ACTION_UP) {
            mLastPoint = point
            if (flgPathDraw) {
                if (points.size > 12) {
                    if (!comparePoint(mFirstPoint, mLastPoint)) {
                        flgPathDraw = false
                        points.add(mFirstPoint)
                        showCropDialog()
                    }
                }
            }
        }
        return true
    }

    private fun resetView() {
        points.clear()
        paint.color = Color.WHITE
        paint.style = Paint.Style.STROKE
        flgPathDraw = true
        invalidate()
    }

    private fun comparePoint(first: Point?, current: Point?): Boolean {
        val leftRangeX = (current!!.x - 3).toInt()
        val leftRangeY = (current.y - 3).toInt()
        val rightRangeX = (current.x + 3).toInt()
        val rightRangeY = (current.y + 3).toInt()
        return if (leftRangeX < first!!.x && first.x < rightRangeX
            && leftRangeY < first.y && first.y < rightRangeY
        ) {
            points.size >= 10
        } else {
            false
        }
    }

    private fun showCropDialog() {
        val dialogClickListener =
            DialogInterface.OnClickListener { dialog, which ->
                val intent: Intent
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        intent = Intent(mContext, CroppedActivity::class.java)
                        intent.putExtra("image", byteArray)
                        mContext.startActivity(intent)
                        dialog.cancel()
                        mSuper.finish()
                    }
                    DialogInterface.BUTTON_NEGATIVE -> {
                        bFirstPoint = false
                        resetView()
                    }
                }
            }
        val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
        builder.setMessage("해당영역을 자르기 할까요?")
            .setPositiveButton("자르기", dialogClickListener)
            .setNegativeButton("아니오", dialogClickListener).show()
            .setCancelable(false)
    }

    companion object {
        lateinit var points: MutableList<Point?>
    }
}

class Point {
    var dy = 0f
    var dx = 0f
    var x = 0f
    var y = 0f
    override fun toString(): String {
        return "$x, $y"
    }
}