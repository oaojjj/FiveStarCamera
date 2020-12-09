package com.oaojjj.fivestarcamera.activity

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.oaojjj.fivestarcamera.PreviewImage
import com.oaojjj.fivestarcamera.R
import com.oaojjj.fivestarcamera.adapter.ViewPager2Adapter
import com.oaojjj.fivestarcamera.controller.ImageController
import com.oaojjj.fivestarcamera.utills.Utils
import kotlinx.android.synthetic.main.activity_editor.*
import kotlinx.android.synthetic.main.item_image.*


class EditorActivity : AppCompatActivity() {
    lateinit var mAdapter: ViewPager2Adapter
    private val imageController = ImageController.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)
        Utils.setDisplaySize(this)

        val path = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DCIM
        ).toString() + "/Camera"

        // toolbar 지정
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // toolbar 참조
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val imagePathList = imageController?.getPathOfAllImages(baseContext, path)
        val imagePreviewList: MutableList<PreviewImage> = mutableListOf()

        imagePathList?.forEach { s: String -> imagePreviewList.add(PreviewImage(s)) }

        mAdapter = ViewPager2Adapter(imagePreviewList)
        // 어뎁터 연결~
        vp_image.apply {
            adapter = mAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            offscreenPageLimit = 5
        }
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
                rotateAlertDialog()
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

    private fun rotateAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder
            .setTitle("회전")
            .setCancelable(true)
            .setNegativeButton("취소") { _, _ -> finish() }
            .setItems(
                arrayOf("오른쪽으로 회전", "왼쪽으로 회전")
            ) { _, index ->
                when (index) {
                    0 -> {
                        imageController?.setRotation(iv_image, mAdapter.getCurrentImage(), 90)
                    }
                    1 -> {
                        imageController?.setRotation(iv_image, mAdapter.getCurrentImage(), -90)
                    }
                }
            }.create().show()
    }

}