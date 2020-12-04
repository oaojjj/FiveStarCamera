package com.oaojjj.fivestarcamera.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.oaojjj.fivestarcamera.Camera2Fragment
import com.oaojjj.fivestarcamera.R
import kotlinx.android.synthetic.main.activity_camera.*

@RequiresApi(Build.VERSION_CODES.Q)
class CameraActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var cameraFragment: Camera2Fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        cameraFragment = Camera2Fragment.newInstance(this)
        // 이미지뷰 동그랗게~
        Glide.with(this).load(R.drawable.round_shape).circleCrop()
            .into(iv_thumbnail)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container_frame, cameraFragment)
                .commit();
        }
        btn_capture.setOnClickListener(this)
        iv_thumbnail.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_capture -> {
                cameraFragment.takePicture()
            }
            R.id.iv_thumbnail -> {
                startActivity(Intent(this, EditorActivity::class.java))
            }
        }
    }
}