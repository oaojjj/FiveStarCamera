package com.oaojjj.fivestarcamera.activity

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.oaojjj.fivestarcamera.PreviewImage
import com.oaojjj.fivestarcamera.R
import com.oaojjj.fivestarcamera.adapter.ViewPager2Adapter
import com.oaojjj.fivestarcamera.controller.ImageController
import kotlinx.android.synthetic.main.activity_editor.*


class EditorActivity : AppCompatActivity() {
    lateinit var currentImage: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        val path = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DCIM
        ).toString() + "/Camera"

        // toolbar 지정
        setSupportActionBar(toolbar)

        // toolbar 참조
        val actionBar = supportActionBar

        // 뒤로가기 버튼 추가
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val imagePathList = ImageController.instance()?.getPathOfAllImages(baseContext, path)
        var imagePreviewList: MutableList<PreviewImage> = mutableListOf()

        var i = 0
        imagePathList?.forEach { s: String -> imagePreviewList.add(PreviewImage(s)) }

        // 어뎁터 연결~
        vp_image.adapter = ViewPager2Adapter(imagePreviewList)
        vp_image.orientation = ViewPager2.ORIENTATION_HORIZONTAL
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.editor_toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_camera, android.R.id.home -> {
                Log.d("EditorA_option_menu", "카메라")
                finish()
                return true
            }
            R.id.menu_move_image -> {
                Log.d("EditorA_option_menu", "이미지 이동")
                return true
            }
            R.id.menu_rotation_image -> {
                Log.d("EditorA_option_menu", "이미지 회전")
                return true
            }
            R.id.menu_cut_image -> {
                Log.d("EditorA_option_menu", "이미지 자르기")
                return true
            }
            R.id.menu_rename_image -> {
                Log.d("EditorA_option_menu", "이미지 이름 바꾸기")
                return true
            }
            R.id.menu_copy_image -> {
                Log.d("EditorA_option_menu", "이미지 복사")
                return true
            }
            R.id.menu_info_image -> {
                Log.d("EditorA_option_menu", "이미지 세부 정보")
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}