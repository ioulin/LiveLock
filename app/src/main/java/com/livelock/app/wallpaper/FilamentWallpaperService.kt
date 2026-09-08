package com.livelock.app.wallpaper

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.HandlerThread
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import com.livelock.app.render.FilamentRenderer
import java.io.File
import java.io.FileOutputStream

class FilamentWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine = FilamentEngine()

    inner class FilamentEngine : Engine(), SharedPreferences.OnSharedPreferenceChangeListener {
        private var renderer: FilamentRenderer? = null
        private var modelFile: File? = null
        private var thread: HandlerThread? = null
        private var handler: Handler? = null
        private var lastTime = 0L
        private var prefs: SharedPreferences? = null

        private val frameRunnable = object : Runnable {
            override fun run() {
                val now = System.nanoTime()
                if (lastTime != 0L) {
                    renderer?.update((now - lastTime) / 1e9f)
                }
                lastTime = now
                renderer?.render(now)
                handler?.postDelayed(this, 16L)
            }
        }

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            modelFile = copyAssetModel()
            // 알림 반응 이벤트 수신 (reaction_rules prefs)
            prefs = getSharedPreferences("reaction_rules", Context.MODE_PRIVATE)
            prefs?.registerOnSharedPreferenceChangeListener(this)
        }

        override fun onSurfaceCreated(holder: SurfaceHolder?) {
            super.onSurfaceCreated(holder)
            holder ?: return
            val r = FilamentRenderer(this@FilamentWallpaperService)
            r.setSurfaceHolder(holder)
            modelFile?.let { r.loadModel(it) }
            r.playAnimation(0)
            renderer = r

            thread = HandlerThread("filament-render").also { it.start() }
            handler = Handler(thread!!.looper)
            lastTime = 0L
            handler?.post(frameRunnable)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            if (visible) {
                handler?.post(frameRunnable)
            } else {
                handler?.removeCallbacks(frameRunnable)
            }
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            super.onSurfaceDestroyed(holder)
            stopRenderLoop()
        }

        override fun onDestroy() {
            super.onDestroy()
            prefs?.unregisterOnSharedPreferenceChangeListener(this)
            stopRenderLoop()
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
            if (key == "last_anim") {
                val animIndex = sharedPreferences?.getInt("last_anim", 0) ?: 0
                renderer?.let { r ->
                    if (animIndex in 0 until r.getAnimationCount()) {
                        r.playAnimation(animIndex)
                    } else {
                        r.playAnimation(0)
                    }
                }
            }
        }

        fun playAnimation(index: Int) {
            renderer?.playAnimation(index)
        }

        private fun stopRenderLoop() {
            handler?.removeCallbacksAndMessages(null)
            handler = null
            thread?.quitSafely()
            thread = null
            renderer?.destroy()
            renderer = null
        }

        private fun copyAssetModel(): File? {
            return try {
                val dest = File(cacheDir, "Fox.glb")
                if (!dest.exists()) {
                    assets.open("models/Fox.glb").use { input ->
                        FileOutputStream(dest).use { output -> input.copyTo(output) }
                    }
                }
                dest
            } catch (t: Throwable) {
                null
            }
        }
    }
}
