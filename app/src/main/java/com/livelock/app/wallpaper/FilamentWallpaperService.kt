package com.livelock.app.wallpaper

import android.content.Context
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import com.livelock.app.render.FilamentRenderer
import java.io.File

class FilamentWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine = FilamentEngine()

    inner class FilamentEngine : Engine() {
        private var renderer: FilamentRenderer? = null
        private var modelFile: File? = null

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            renderer = FilamentRenderer(this@FilamentWallpaperService)
        }

        fun setModel(file: File) {
            modelFile = file
            renderer?.loadModel(file)
        }

        fun playAnimation(index: Int) {
            renderer?.playAnimation(index)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            // TODO: pause/resume rendering
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            super.onSurfaceDestroyed(holder)
            renderer?.destroy()
        }

        override fun onDestroy() {
            super.onDestroy()
            renderer?.destroy()
        }
    }
}
