package com.oaojjj.fivestarcamera.activity

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.oaojjj.fivestarcamera.R
import kotlinx.android.synthetic.main.activity_editor.*


class EditorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        var path = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DCIM
        ).toString() + "/Camera"

        // toolbar 지정
        setSupportActionBar(toolbar)

        // 툴바 참조
        var actionBar = supportActionBar

        // 뒤로가기 버튼 추가
        actionBar?.setDisplayHomeAsUpEnabled(true)

        getPathOfAllImages(path)
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

    // 이미지 목록 불러오기
    private fun getPathOfAllImages(path: String): ArrayList<String>? {
        val result: ArrayList<String> = ArrayList()
        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaColumns.DATA, MediaColumns.DISPLAY_NAME)
        val cursor: Cursor? =
            contentResolver.query(uri, projection, null, null, MediaColumns.DATE_ADDED + " desc")
        val columnIndex: Int = cursor?.getColumnIndexOrThrow(MediaColumns.DATA)!!
        // val columnDisplayName: Int = cursor?.getColumnIndexOrThrow(MediaColumns.DISPLAY_NAME)
        // var lastIndex: Int

        while (cursor.moveToNext()) {
            val absolutePathOfImage: String = cursor.getString(columnIndex)
            val nCol = cursor.getColumnIndex(MediaStore.Images.Media.DATA) // bitmap
            // val nameOfFile: String = cursor.getString(columnDisplayName)

            // lastIndex = absolutePathOfImage.lastIndexOf(nameOfFile)
            // lastIndex = if (lastIndex >= 0) lastIndex else nameOfFile.length - 1

            if (cursor.getString(nCol).startsWith(path))
                result.add(absolutePathOfImage)
        }

        for (string in result) {
            Log.i("TAG_EditorActivity", string)
        }

        cursor.close()
        return result
    }

}