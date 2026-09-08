package com.livelock.app.render

import android.content.Context
import android.view.Surface
import android.view.SurfaceView
import com.google.android.filament.*
import com.google.android.filament.android.UiHelper
import com.google.android.filament.gltfio.*
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer

class FilamentRenderer(context: Context) {
    private val engine: Engine = Engine.create()
    private val renderer: Renderer = engine.createRenderer()
    private val scene: Scene = engine.createScene()
    private val view: View = engine.createView()
    private val camera: Camera = engine.createCamera(engine.entityManager.create())
    private var assetLoader: AssetLoader? = null
    private var resourceLoader: ResourceLoader? = null
    private var asset: FilamentAsset? = null
    private var surfaceView: SurfaceView? = null
    private var uiHelper: UiHelper? = null

    init {
        view.scene = scene
        view.camera = camera
    }

    fun setSurfaceView(surfaceView: SurfaceView) {
        this.surfaceView = surfaceView
        uiHelper = UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK).apply {
            renderCallback = object : UiHelper.RendererCallback {
                override fun onDetachedFromSurface() {}
                override fun onNativeWindowResized(width: Int, height: Int) {}
                override fun onSurfaceCreated(holder: android.view.SurfaceHolder) {}
                override fun onSurfaceDestroyed(holder: android.view.SurfaceHolder) {}
            }
            attachTo(surfaceView)
        }
    }

    fun loadModel(file: File) {
        val provider = MaterialProvider(engine)
        assetLoader = AssetLoader(engine, provider, EntityManager.get())
        resourceLoader = ResourceLoader(engine)

        FileInputStream(file).use { fis ->
            val bytes = fis.readBytes()
            val buffer = ByteBuffer.allocateDirect(bytes.size)
            buffer.put(bytes)
            buffer.flip()
            asset = assetLoader?.createAsset(buffer)
            asset?.let { resourceLoader?.loadResources(it) }
            asset?.entities?.forEach { scene.addEntity(it) }
        }
    }

    fun getAnimationCount(): Int = asset?.animationCount() ?: 0

    fun getAnimationName(index: Int): String {
        return if (index < getAnimationCount()) {
            String(asset!!.getAnimationName(index))
        } else ""
    }

    fun playAnimation(index: Int) {
        val animator = asset?.animator ?: return
        if (index < animator.animationCount) {
            animator.applyAnimation(index, 0f)
        }
    }

    fun update(elapsedSeconds: Float) {
        val animator = asset?.animator ?: return
        animator.updateAnimation(elapsedSeconds)
    }

    fun render(nanoTime: Long) {
        val surface = uiHelper?.let { getSurfaceFromHelper(it) } ?: return
        if (renderer.beginFrame(surface, nanoTime)) {
            renderer.render(view)
            renderer.endFrame()
        }
    }

    private fun getSurfaceFromHelper(helper: UiHelper): Surface? {
        return try {
            val field = UiHelper::class.java.getDeclaredField("mSurface")
            field.isAccessible = true
            field.get(helper) as? Surface
        } catch (e: Exception) {
            null
        }
    }

    fun destroy() {
        asset?.let { assetLoader?.destroyAsset(it) }
        engine.destroyRenderer(renderer)
        engine.destroyScene(scene)
        engine.destroyView(view)
        engine.destroyCameraComponent(camera.entity)
        engine.destroy()
    }
}

