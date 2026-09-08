package com.livelock.app.wallpaper

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.InputStream

object WallpaperApplier {

    fun setLockScreen(context: Context, imageFile: File): Boolean {
        return try {
            val bmp = BitmapFactory.decodeFile(imageFile.absolutePath) ?: return false
            val wm = WallpaperManager.getInstance(context)
            wm.setBitmap(bmp, null, true, WallpaperManager.FLAG_LOCK)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun setHomeScreen(context: Context, imageFile: File): Boolean {
        return try {
            val bmp = BitmapFactory.decodeFile(imageFile.absolutePath) ?: return false
            val wm = WallpaperManager.getInstance(context)
            wm.setBitmap(bmp, null, true, WallpaperManager.FLAG_SYSTEM)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun setBoth(context: Context, imageFile: File): Boolean {
        return try {
            val bmp = BitmapFactory.decodeFile(imageFile.absolutePath) ?: return false
            val wm = WallpaperManager.getInstance(context)
            wm.setBitmap(bmp) // both
            true
        } catch (e: Exception) {
            false
        }
    }
}
