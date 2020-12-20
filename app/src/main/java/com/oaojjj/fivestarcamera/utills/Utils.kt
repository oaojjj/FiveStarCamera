package com.oaojjj.fivestarcamera.utills

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import java.io.File

object Utils {
    /**
     * 이미지 저장 경로
     */
    var path = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_DCIM
    ).toString() + "/FSCamera"

    var mDir = File(path)

    private val displayMetrics = DisplayMetrics()

    var deviceWidth: Int = 0
    var deviceHeight: Int = 0

    fun setDisplaySize(activity: Activity) {
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        deviceWidth = displayMetrics.widthPixels
        deviceHeight = displayMetrics.heightPixels

    }

    fun onRefreshGallery(context: Context?, file: File?) {
        Log.d("onRefreshGallery", "onRefreshGallery start")
        MediaScannerConnection.scanFile(
            context, arrayOf(file!!.path), null
        ) { path, _ -> Log.i("TAG", "Finished scanning $path") }
    }

    /**
     * content provider type
     */
    private val CONTENT_URI_TYPE = listOf(
        "com.android.externalstorage.documents",
        "com.android.providers.downloads.documents",
        "com.android.providers.media.documents"
    )

    fun getPath(context: Context, uri: Uri): String? {
        when {
            DocumentsContract.isDocumentUri(context, uri) -> {
                when (uri.authority) {
                    CONTENT_URI_TYPE[0] -> {
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split = docId.split(":").toTypedArray()
                        val type = split[0]
                        if ("primary".equals(type, ignoreCase = true)) {
                            return Environment.getExternalStorageDirectory()
                                .toString() + "/" + split[1]
                        }
                    }
                    CONTENT_URI_TYPE[1] -> {
                        val id = DocumentsContract.getDocumentId(uri)
                        val contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"),
                            java.lang.Long.valueOf(id)
                        )
                        return getDataColumn(context, contentUri, null, null)
                    }
                    CONTENT_URI_TYPE[2] -> {
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split = docId.split(":").toTypedArray()
                        val contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        val selection = "_id=?"
                        val selectionArgs = arrayOf(split[1])
                        return getDataColumn(context, contentUri, selection, selectionArgs)
                    }
                }
            }
            "content".equals(uri.scheme, ignoreCase = true) -> {
                return getDataColumn(context, uri, null, null)
            }
            "file".equals(uri.scheme, ignoreCase = true) -> {
                return uri.path
            }
        }
        return null
    }

    // 실제 경로에 있는 데이터 찾아서 return
    private fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(columnIndex)
            }
        } finally {
            cursor?.close()
        }
        return null
    }
}