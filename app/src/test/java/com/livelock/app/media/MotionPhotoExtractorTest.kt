package com.livelock.app.media

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class MotionPhotoExtractorTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    @Test
    fun isMotionPhoto_returnsFalseForPlainJpeg() {
        val file = tempFolder.newFile("plain.jpg")
        file.writeBytes(byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte(), 0xE0.toByte()))
        assertFalse(MotionPhotoExtractor.isMotionPhoto(file))
    }

    @Test
    fun extractVideo_failsForPlainJpeg() {
        val input = tempFolder.newFile("plain.jpg")
        input.writeBytes(byteArrayOf(0xFF.toByte(), 0xD8.toByte()))
        val output = tempFolder.newFile("out.mp4")
        assertFalse(MotionPhotoExtractor.extractVideo(input, output))
    }

    @Test
    fun isMotionPhoto_detectsMp4Signature() {
        val bytes = byteArrayOf(
            0xFF.toByte(), 0xD8.toByte(), 0x00.toByte(), 0x00.toByte(),
            0x00.toByte(), 0x00.toByte(),
            0x66, 0x74, 0x79, 0x70
        )
        val file = tempFolder.newFile("motion.jpg")
        file.writeBytes(bytes)
        assertTrue(MotionPhotoExtractor.isMotionPhoto(file))
    }
}
