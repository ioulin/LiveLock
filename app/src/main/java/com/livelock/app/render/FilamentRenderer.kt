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
    private var swapChain: SwapChain? = null

    init {
        view.scene = scene
        view.camera = camera
    }

    fun setSurfaceView(surfaceView: SurfaceView) {
        this.surfaceView = surfaceView
        uiHelper = UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK).apply {
            renderCallback = object : UiHelper.RendererCallback {
                override fun onNativeWindowChanged(surface: Surface) {
                    swapChain?.let { engine.destroySwapChain(it) }
                    swapChain = engine.createSwapChain(surface)
                }

                override fun onDetachedFromSurface() {
                    swapChain?.let { engine.destroySwapChain(it) }
                    swapChain = null
                }

                override fun onResized(width: Int, height: Int) {
                    if (height > 0) {
                        val aspect = width.toDouble() / height.toDouble()
                        camera.setProjection(45.0, aspect, 0.1, 100.0, Camera.Fov.VERTICAL)
                    }
                }
            }
            attachTo(surfaceView)
        }
    }

    fun loadModel(file: File) {
        val provider = UbershaderProvider(engine)
        assetLoader = AssetLoader(engine, provider, EntityManager.get())
        resourceLoader = ResourceLoader(engine)

        FileInputStream(file).use { fis ->
            val bytes = fis.readBytes()
            val buffer = ByteBuffer.allocateDirect(bytes.size)
            buffer.put(bytes)
            buffer.flip()
            asset = assetLoader?.createAsset(buffer)
            asset?.let { a ->
                resourceLoader?.loadResources(a)
                a.entities.forEach { scene.addEntity(it) }
            }
        }
    }

    private val animator: Animator? get() = asset?.getInstanceAnimator()

    fun getAnimationCount(): Int = animator?.animationCount ?: 0

    fun getAnimationName(index: Int): String = try {
        animator?.getAnimationName(index) ?: ""
    } catch (t: Throwable) {
        ""
    }

    fun playAnimation(index: Int) {
        val a = animator ?: return
        if (index < a.animationCount) {
            a.applyAnimation(index, 0f)
        }
    }

    fun update(deltaSeconds: Float) {
        animator?.updateAnimations(deltaSeconds)
    }

    fun render(nanoTime: Long) {
        val swapChain = swapChain ?: return
        if (renderer.beginFrame(swapChain, nanoTime)) {
            renderer.render(view)
            renderer.endFrame()
        }
    }

    fun destroy() {
        asset?.let { assetLoader?.destroyAsset(it) }
        uiHelper?.detach()
        engine.destroyRenderer(renderer)
        engine.destroyScene(scene)
        engine.destroyView(view)
        engine.destroyCameraComponent(camera.entity)
        engine.destroy()
    }
}

