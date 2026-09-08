package com.livelock.app.render

import android.content.Context
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.google.android.filament.*
import com.google.android.filament.android.UiHelper
import com.google.android.filament.gltfio.*
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer

/**
 * Filament 기반 3D 캐릭터 렌더러.
 * - GLB 로드 + 리소스 로딩
 * - 애니메이션 클립 재생 (applyAnimation 기반 수동 시간 진행)
 * - 방향광 조명 + 카메라 + 클리어 색
 * - SurfaceHolder/SurfaceView 둘 다 지원 (월페이퍼/미리보기 공용)
 */
class FilamentRenderer(context: Context) {
    private val engine: Engine = Engine.create()
    private val renderer: Renderer = engine.createRenderer()
    private val scene: Scene = engine.createScene()
    private val view: View = engine.createView()
    private val camera: Camera = engine.createCamera(engine.entityManager.create())
    private var assetLoader: AssetLoader? = null
    private var resourceLoader: ResourceLoader? = null
    private var asset: FilamentAsset? = null
    private var uiHelper: UiHelper? = null
    private var swapChain: SwapChain? = null
    private var animator: Animator? = null
    private var currentAnim = -1
    private var animTime = 0f

    // 방향광(태양광)
    private val lightEntity = engine.entityManager.create()

    init {
        view.scene = scene
        view.camera = camera

        // 배경 클리어 색 설정
        renderer.setClearOptions(
            Renderer.ClearOptions().apply {
                clearColor = Float4(0.13f, 0.18f, 0.30f, 1f)
                clear = true
            }
        )

        val lightManager = engine.lightManager
        LightManager.Builder(LightManager.Type.DIRECTIONAL)
            .castShadows(false)
            .direction(-0.5f, -0.8f, -0.4f)
            .color(1f, 0.97f, 0.92f)
            .intensity(110_000f)
            .build(engine, lightEntity)
        scene.addEntity(lightEntity)
    }
fun setSurfaceView(surfaceView: SurfaceView) {
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
                    updateProjection(width, height)
                }
            }
            attachTo(surfaceView)
        }
    }

    fun setSurfaceHolder(holder: SurfaceHolder) {
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
                    updateProjection(width, height)
                }
            }
            attachTo(holder)
        }
    }

    private fun updateProjection(width: Int, height: Int) {
        if (height > 0) {
            val aspect = width.toDouble() / height.toDouble()
            camera.setProjection(45.0, aspect, 0.1, 100.0, Camera.Projection.PERSPECTIVE)
            camera.lookAt(
                0.0, 1.1, 3.4,
                0.0, 0.9, 0.0,
                0.0, 1.0, 0.0
            )
        }
    }

    fun loadModel(file: File) {
        val provider = UbershaderProvider(engine)
        assetLoader = AssetLoader(engine, provider, EntityManager.get())
        resourceLoader = ResourceLoader(engine)
        scene.addEntity(lightEntity)

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
            animator = try {
                asset?.getInstance()?.animator
                    ?: assetLoader?.createInstance(asset!!)?.animator
            } catch (t: Throwable) {
                null
            }
        }
    }

    fun getAnimationCount(): Int = animator?.animationCount ?: 0

    fun getAnimationName(index: Int): String = try {
        animator?.getAnimationName(index) ?: ""
    } catch (t: Throwable) {
        ""
    }

    fun playAnimation(index: Int) {
        val a = animator ?: return
        if (index in 0 until a.animationCount) {
            currentAnim = index
            animTime = 0f
            a.applyAnimation(index, 0f)
        }
    }

    fun update(deltaSeconds: Float) {
        val a = animator ?: return
        if (currentAnim in 0 until a.animationCount) {
            animTime += deltaSeconds
            val dur = a.getAnimationDuration(currentAnim)
            val t = if (dur > 0f) animTime % dur else animTime
            a.applyAnimation(currentAnim, t)
        }
    }

    fun render(nanoTime: Long) {
        val sc = swapChain ?: return
        if (renderer.beginFrame(sc, nanoTime)) {
            renderer.render(view)
            renderer.endFrame()
        }
    }

    fun destroy() {
        asset?.let { assetLoader?.destroyAsset(it) }
        uiHelper?.detach()
        engine.destroyEntity(lightEntity)
        engine.destroyRenderer(renderer)
        engine.destroyScene(scene)
        engine.destroyView(view)
        engine.destroyCameraComponent(camera.entity)
        engine.destroy()
    }
}
