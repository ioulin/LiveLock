package com.livelock.app.render

import android.content.Context
import android.view.SurfaceView
import com.google.android.filament.Engine
import com.google.android.filament.EntityManager
import com.google.android.filament.gltfio.AssetLoader
import com.google.android.filament.gltfio.MaterialProvider
import com.google.android.filament.gltfio.ResourceLoader
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer

class FilamentRenderer(context: Context) {
    private val engine: Engine = Engine.create()
    private val assetLoader: AssetLoader
    private val resourceLoader: ResourceLoader
    private var asset: com.google.android.filament.gltfio.FilamentAsset? = null

    init {
        val provider = MaterialProvider(engine)
        assetLoader = AssetLoader(engine, provider, EntityManager.get())
        resourceLoader = ResourceLoader(engine)
    }

    fun loadModel(file: File) {
        FileInputStream(file).use { fis ->
            val bytes = fis.readBytes()
            val buffer: ByteBuffer = ByteBuffer.allocateDirect(bytes.size)
            buffer.put(bytes)
            buffer.flip()
            asset = assetLoader.createAsset(buffer)
            asset?.let { resourceLoader.loadResources(it) }
        }
    }

    fun getAnimationCount(): Int = asset?.animationCount() ?: 0

    fun getAnimationName(index: Int): String = asset?.getAnimationName(index) ?: ""

    fun playAnimation(index: Int) {
        // TODO: animator setup
    }

    fun destroy() {
        asset?.let { assetLoader.destroyAsset(it) }
        engine.destroy()
    }
}
