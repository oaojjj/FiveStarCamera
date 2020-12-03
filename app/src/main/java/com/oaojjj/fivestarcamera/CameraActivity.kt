package com.oaojjj.fivestarcamera

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_camera.*

class CameraActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var cameraFragment: Camera2Fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        cameraFragment = Camera2Fragment.newInstance()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container_frame, cameraFragment)
                .commit();
        }

        btn_capture.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_capture -> {
                cameraFragment.takePicture()
                Glide.with(this).load(R.drawable.ic_launcher_background).circleCrop().into(iv_thumbnail)
            }
        }
    }

}