package com.oaojjj.fivestarcamera.activity

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.oaojjj.fivestarcamera.PreviewImage
import com.oaojjj.fivestarcamera.R
import com.oaojjj.fivestarcamera.adapter.ViewPager2Adapter
import com.oaojjj.fivestarcamera.controller.ImageController
import com.oaojjj.fivestarcamera.utills.Utils
import com.oaojjj.fivestarcamera.utills.Utils.onRefreshGallery
import com.oaojjj.fivestarcamera.utills.Utils.path
import kotlinx.android.synthetic.main.activity_editor.*
import kotlinx.android.synthetic.main.item_image.view.*
import java.io.File


class EditorActivity : AppCompatActivity() {
    companion object {
        private const val CROP_IMAGE: Int = 1000
        private const val MOVE_IMAGE: Int = 1001
        private const val COPY_IMAGE: Int = 1002
    }

    private lateinit var mAdapter: ViewPager2Adapter
    private val imageController = ImageController.getInstance()

    private var currentImage: PreviewImage? = null
    private var currentPosition: Int? = 0
    lateinit var currentView: ImageView
    lateinit var prevView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)
        Utils.setDisplaySize(this)

        // toolbar 지정
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.bringToFront()

        // toolbar 참조
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        // fetch image
        val imagePreviewList: MutableList<PreviewImage> = fetchImageData()
        currentImage = imagePreviewList[0]

        /**
         * adapter
         * offscreenPageLimit()
         * 위의 매소드를 추가안하면 페이지를 엄청 빠르게 슬라이드 할때 이전의 뷰에 보여지는 이미지가 보임.
         * 하지만 그렇다고 밑의 함수를 추가하고 뷰가 재활용되는 시점에
         * 어떤 기능(회전 등등..)을 동작시키면 엄청나게 렉이 걸림..
         * why?
         */
        mAdapter = ViewPager2Adapter(imagePreviewList).apply {
            setOnMyTouchListener(TouchListener())
        }

        vp_image.apply {
            adapter = mAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            offscreenPageLimit = 5
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(newPosition: Int) {
                    mScaleFactor = 1.0f

                    // 현재 뷰홀더의 이미지뷰
                    if (mAdapter.itemCount > 0) {
                        currentImage = mAdapter.getItems()[newPosition]
                    }

                    // 이전의 뷰 저장
                    if (newPosition != currentPosition) {
                        prevView = currentView
                        currentView = getViewFromViewPager()
                        if (isZoom(prevView)) {
                            prevView.scaleX = 1.0f
                            prevView.scaleY = 1.0f
                        }
                    } else {
                        currentView = getViewFromViewPager()
                    }
                    currentPosition = newPosition

                    Log.d("onPageSelected", newPosition.toString())
                    super.onPageSelected(newPosition)
                }
            })
        }

        // pinch to zoom
        mScaleGestureDetector = ScaleGestureDetector(this, mScaleGestureListener)
    }

    private fun fetchImageData(): MutableList<PreviewImage> {
        val imagePathList = imageController?.getPathOfAllImages(baseContext, path)
        val imagePreviewList: MutableList<PreviewImage> = mutableListOf()

        imagePathList?.forEach { s: String -> imagePreviewList.add(PreviewImage(s)) }
        return imagePreviewList
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == MOVE_IMAGE && data != null) {
            if (mAdapter.itemCount > 0) {
                itemRemove()

                val docUri = DocumentsContract.buildDocumentUriUsingTree(
                    data.data,
                    DocumentsContract.getTreeDocumentId(data.data)
                )

                val toFile = File(Utils.getPath(this, docUri)!!)
                currentImage!!.mFile?.let {
                    ImageController.getInstance()
                        ?.copyOrMoveFile(applicationContext, it, toFile, false)
                    mAdapter.notifyDataSetChanged()
                }

                Toast.makeText(this, "이미지가 ${toFile.path}경로로 이동 되었습니다.", Toast.LENGTH_SHORT).show()
            }

        } else if (requestCode == COPY_IMAGE && data != null) {
            val docUri = DocumentsContract.buildDocumentUriUsingTree(
                data.data,
                DocumentsContract.getTreeDocumentId(data.data)
            )

            val toFile = File(Utils.getPath(this, docUri)!!)
            currentImage!!.mFile?.let {
                ImageController.getInstance()
                    ?.copyOrMoveFile(applicationContext, it, toFile, true)
                mAdapter.notifyDataSetChanged()
            }

            Toast.makeText(this, "이미지가 ${toFile.path}경로로 복사 되었습니다.", Toast.LENGTH_SHORT).show()
        }
        super.onActivityResult(requestCode, resultCode, data)
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
                fileCopyOrMove(MOVE_IMAGE)
                Log.d("EditorA_option_menu", "이미지 이동")
                return true
            }
            R.id.menu_rotation_image -> {
                Log.d("EditorA_option_menu", "이미지 회전")
                rotationImage()
                return true
            }
            R.id.menu_cut_image -> {
                cropImage()
                Log.d("EditorA_option_menu", "이미지 자르기")
                return true
            }
            R.id.menu_rename_image -> {
                renameFile()
                Log.d("EditorA_option_menu", "이미지 이름 바꾸기")
                return true
            }
            R.id.menu_copy_image -> {
                fileCopyOrMove(COPY_IMAGE)
                Log.d("EditorA_option_menu", "이미지 복사")
                return true
            }
            R.id.menu_delete_image -> {
                deleteImage()
                Log.d("EditorA_option_menu", "이미지 삭제")
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 이미지 pinch to zoom
     */
    private lateinit var mScaleGestureDetector: ScaleGestureDetector
    private var mScaleFactor = 1.0f

    // 이미지 이동에 사용
    private var xCoOrdinate = 0f
    private var yCoOrdinate = 0f

    private var prevX = 0f
    private var prevY = 0f

    // 두 손가락 터치 이벤트 감지
    enum class TouchMode { DRAG, ZOOM, TOUCH, NONE }

    private lateinit var touchMode: TouchMode

    private val mScaleGestureListener =
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector?): Boolean {
                mScaleFactor *= detector!!.scaleFactor

                // 최대 10배, 최소 1배 zoom
                mScaleFactor = Math.max(1f, Math.min(mScaleFactor, 10.0f))

                setCurrentViewScale(mScaleFactor)

                return true
            }
        }

    private fun setCurrentViewScale(mScaleFactor: Float) {
        currentView.x = 0f
        currentView.y = 0f
        currentView.scaleX = mScaleFactor
        currentView.scaleY = mScaleFactor
    }

    inner class TouchListener : View.OnTouchListener {
        private var gestureDetector = GestureDetector(this@EditorActivity,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onDoubleTap(e: MotionEvent?): Boolean {
                    if (isZoom(currentView)) {
                        setCurrentViewScale(1.0f)
                        vp_image.isUserInputEnabled = true
                    } else {
                        setCurrentViewScale(2.0f)
                        vp_image.isUserInputEnabled = false
                    }

                    return super.onDoubleTap(e)
                }
            })

        // 이미지뷰 이동 hint ->https://sunghyun1038.tistory.com/24
        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(view: View, event: MotionEvent): Boolean {
            mScaleGestureDetector.onTouchEvent(event)
            gestureDetector.onTouchEvent(event)

            /**
             * MotionEvent.ACTION_POINTER_2_DOWN 등과 같은 상수들이 전부 deprecated 되었음
             * 그래서 아래와 같이 멀티터치 이벤트 감지 구현
             * 원래 계획은 zoom in 상태에서도 viewpager swipe 를 하고싶었는데
             * 너무 힘들어서 그냥 zoom out 상태에서만 viewpager swipe 가능
             */
            val eventDuration = event.eventTime - event.downTime
            when (event.action and MotionEvent.ACTION_MASK) {
                // single touch
                MotionEvent.ACTION_DOWN -> {

                    xCoOrdinate = prevX - event.rawX
                    yCoOrdinate = prevY - event.rawY

                    touchMode = TouchMode.TOUCH
                    if (!isZoom(currentView))
                        vp_image.isUserInputEnabled = true
                }
                // multi touch
                MotionEvent.ACTION_POINTER_DOWN -> {
                    vp_image.isUserInputEnabled = false

                    touchMode = TouchMode.ZOOM
                }
                MotionEvent.ACTION_POINTER_UP -> {
                    touchMode = TouchMode.NONE
                }
                MotionEvent.ACTION_UP -> {
                    Log.d("zebar", "${event.pointerCount}/${eventDuration}/${touchMode}")
                    if (event.pointerCount == 1 && eventDuration > 150 && touchMode == TouchMode.TOUCH)
                        changeToolbarVisibility()

                    prevX = currentView.x
                    prevY = currentView.y

                    touchMode = TouchMode.NONE
                }
                MotionEvent.ACTION_MOVE -> {
                    // 확대시 이동
                    if ((touchMode == TouchMode.TOUCH || touchMode == TouchMode.DRAG)
                        && isZoom(currentView)
                    ) {
                        currentView.animate()
                            .x(event.rawX + xCoOrdinate)
                            .y(event.rawY + yCoOrdinate)
                            .setDuration(0)
                            .start()
                        touchMode = TouchMode.DRAG
                    }
                }
            }
            return true
        }
    }

    /**
     * 툴바 유무
     */
    private fun changeToolbarVisibility() {
        if (toolbar.visibility == View.VISIBLE) toolbar.visibility = View.GONE
        else toolbar.visibility = View.VISIBLE
    }

    private fun isZoom(v: ImageView) = v.scaleX != 1.0f || v.scaleY != 1.0f

    /**
     * 이미지 조작 기능 메소드
     */


    private fun rotationImage() {
        val builder = AlertDialog.Builder(this)
        builder
            .setTitle("회전")
            .setCancelable(true)
            .setNegativeButton("취소") { dialog, _ -> dialog.cancel() }
            .setItems(arrayOf("오른쪽으로 회전", "왼쪽으로 회전")) { _, index ->
                rotateImage(index)
            }.create().show()
    }

    private fun deleteImage() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("삭제")
            .setMessage("정말 삭제하겠습니까?")
            .setCancelable(true)
            .setNegativeButton("취소") { dialog, _ -> dialog.cancel() }
            .setPositiveButton("삭제") { _: DialogInterface, _: Int ->
                if (mAdapter.itemCount > 0) {
                    itemRemove()

                    imageController?.deleteImage(currentImage)
                    onRefreshGallery(baseContext, File(currentImage!!.path))
                    mAdapter.notifyDataSetChanged()
                }
            }.create().show()
    }

    private fun itemRemove() {
        if (mAdapter.itemCount == 1)
            mAdapter.getItems().clear()
        else
            mAdapter.getItems().removeAt(currentPosition!!)
    }

    private fun cropImage() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("자르기")
            .setCancelable(true)
            .setNegativeButton("취소") { dialog, _ -> dialog.cancel() }
            .setItems(arrayOf("사각형 자르기", "자유롭게 자르기")) { _, index ->
                startActivity(
                    Intent(
                        this,
                        CropActivity(
                            currentImage!!,
                            if (index == 0) CropActivity.CropType.RECTANGLE
                            else CropActivity.CropType.FREE
                        )::class.java
                    )
                )
            }.create().show()
    }

    private fun rotateImage(index: Int) {
        imageController?.setRotation(
            currentView,
            currentImage,
            if (index == 0) 90 else -90
        )
    }

    private fun getViewFromViewPager(): ImageView {
        return (vp_image[0] as RecyclerView).findViewHolderForAdapterPosition(vp_image.currentItem)!!
            .itemView.iv_image
    }

    private fun fileCopyOrMove(requestCode: Int) {
        startActivityForResult(
            Intent.createChooser(
                Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                    .apply { addCategory(Intent.CATEGORY_DEFAULT) }, "폴더를 선택하세요."
            ),
            requestCode
        )
    }

    private fun renameFile() {
        val etName = EditText(this)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("이름 입력")
            .setCancelable(true)
            .setView(etName)
            .setNegativeButton("취소") { dialog, _ -> dialog.cancel() }
            .setPositiveButton("바꾸기") { dialog, _ ->
                if (etName.text.isNullOrBlank() || etName.text.isEmpty()) {
                    Toast.makeText(this, "이름을 입력하세요.", Toast.LENGTH_SHORT).show()
                } else {
                    ImageController.getInstance()!!
                        .renameFile(this, currentImage?.mFile, etName.text.toString())
                }
            }.create().show()
    }
}