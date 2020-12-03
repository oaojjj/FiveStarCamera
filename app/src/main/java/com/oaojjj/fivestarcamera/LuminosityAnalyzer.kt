package com.oaojjj.fivestarcamera

import android.media.Image
import android.util.Log
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit

// 밝기(광도) 체크
class LuminosityAnalyzer {
    private var lastAnalyzedTimestamp = 0L
    private fun toByteArray(byteBuffer: ByteBuffer): ByteArray {
        byteBuffer.rewind()
        val data = ByteArray(byteBuffer.remaining())
        byteBuffer[data]
        return data
    }

    fun analyze(image: Image) {
        val currentTimestamp = System.currentTimeMillis()
        if (currentTimestamp - lastAnalyzedTimestamp >= TimeUnit.SECONDS.toMillis(1)) {
            val buffer = image.planes[0].buffer
            val data = toByteArray(buffer)
            var total: Long = 0
            for (aByte in data) {
                total += aByte.toInt() and 0xff.toLong().toInt()
            }
            val luma = total / data.size.toDouble()
            Log.d("Charles", String.format("luminosity=%f", luma))
            lastAnalyzedTimestamp = currentTimestamp
        }
    }
}