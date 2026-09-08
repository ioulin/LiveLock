package com.livelock.app.media

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object MotionPhotoExtractor {

    private val MP4_SIGNATURE = byteArrayOf(0x66, 0x74, 0x79, 0x70) // "ftyp"

    fun extractVideo(jpegFile: File, outputFile: File): Boolean {
        if (!jpegFile.exists()) return false
        FileInputStream(jpegFile).use { input ->
            val bytes = input.readBytes()
            val mp4Start = findMp4Start(bytes)
            if (mp4Start < 0) return false
            FileOutputStream(outputFile).use { output ->
                output.write(bytes, mp4Start, bytes.size - mp4Start)
            }
            return true
        }
    }

    private fun findMp4Start(data: ByteArray): Int {
        for (i in 0 until data.size - 4) {
            if (data[i] == 0x00.toByte() && data[i + 1] == 0x00.toByte() &&
                data[i + 2] == 0x00.toByte()
            ) {
                val boxSize = ((data[i + 3].toInt() and 0xFF) shl 24) or 0
                if (i + 4 < data.size && matchesSignature(data, i + 4)) {
                    return i
                }
            }
        }
        return -1
    }

    private fun matchesSignature(data: ByteArray, offset: Int): Boolean {
        if (offset + 4 > data.size) return false
        for (i in MP4_SIGNATURE.indices) {
            if (data[offset + i] != MP4_SIGNATURE[i]) return false
        }
        return true
    }

    fun isMotionPhoto(jpegFile: File): Boolean {
        if (!jpegFile.exists()) return false
        FileInputStream(jpegFile).use { input ->
            val bytes = input.readBytes()
            return findMp4Start(bytes) >= 0
        }
    }
}
