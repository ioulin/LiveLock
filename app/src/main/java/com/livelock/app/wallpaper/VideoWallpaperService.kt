package com.livelock.app.wallpaper

import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Paint
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import java.io.IOException

class VideoWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine = VideoEngine()

    inner class VideoEngine : Engine(), SharedPreferences.OnSharedPreferenceChangeListener {
        private var mediaPlayer: MediaPlayer? = null
        private var audioManager: AudioManager? = null
        private var audioFocusRequest: AudioFocusRequest? = null
        private var isVideo = false

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            audioManager = getSystemService(AUDIO_SERVICE) as? AudioManager
        }

        fun setVideo(uri: String) {
            isVideo = true
            playVideo(uri)
        }

        fun playVideo(uri: String) {
            releasePlayer()
            requestAudioFocus()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                isLooping = true
                setDataSource(uri)
                prepare()
                start()
            }
        }

        fun playAudioOnly(uri: String) {
            releasePlayer()
            requestAudioFocus()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                isLooping = true
                setDataSource(uri)
                prepare()
                start()
            }
        }

        fun pausePlayback() {
            mediaPlayer?.takeIf { it.isPlaying }?.pause()
        }

        fun resumePlayback() {
            mediaPlayer?.takeIf { !it.isPlaying }?.start()
        }

        private fun requestAudioFocus() {
            audioManager?.let { am ->
                audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setOnAudioFocusChangeListener { }
                    .build()
                am.requestAudioFocus(audioFocusRequest!!)
            }
        }

        private fun releasePlayer() {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }

        override fun onVisibilityChanged(visible: Boolean) {
            if (visible) resumePlayback() else pausePlayback()
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            super.onSurfaceDestroyed(holder)
            pausePlayback()
        }

        override fun onDestroy() {
            super.onDestroy()
            releasePlayer()
            audioFocusRequest?.let { audioManager?.abandonAudioFocusRequest(it) }
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {}
        override fun onDraw(canvas: Canvas?, holder: SurfaceHolder?) {}
    }
}
