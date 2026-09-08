package com.livelock.app.audio

import android.content.Context
import android.media.MediaPlayer

/** 앱 번들 assets의 짧은 효과음 한 번 재생하는 유틸. */
object SfxPlayer {
    private var player: MediaPlayer? = null

    /** name 예: "sfx_alert.ogg" (assets/audio/) */
    fun play(context: Context, name: String) {
        try {
            stop()
            val fd = context.assets.openFd("audio/$name")
            val p = MediaPlayer()
            p.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
            p.setOnCompletionListener { it.release() }
            p.prepare()
            p.start()
            player = p
        } catch (t: Throwable) {
            // assets 없거나 재생 실패 - 조용히 무시
        }
    }

    fun stop() {
        try {
            player?.release()
        } catch (_: Throwable) {
        }
        player = null
    }
}