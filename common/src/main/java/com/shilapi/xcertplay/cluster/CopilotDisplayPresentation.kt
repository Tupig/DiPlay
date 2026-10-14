// SPDX-License-Identifier: AGPL-3.0-only
package com.shilapi.xcertplay.cluster

import android.app.Presentation
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.Gravity
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.shilapi.xcertplay.resolveAppNightNow

/**
 * Shows CarPlay's copilot (passenger) display stream on Leapmotor C11 2024 (8295P) copilot display.
 *
 * Leapmotor C11 2024 (8295P) exposes the copilot entertainment display as a presentation display.
 * This class handles the copilot display projection for media, navigation, or other content.
 */
internal class CopilotDisplayPresentation(
    context: Context,
    display: Display,
    private val onSurface: (Surface?) -> Unit,
) : Presentation(context, display) {

    private var videoView: View? = null
    private var streamActive = false
    var outputSurface: Surface? = null
        private set
    var contentVisible = true
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val size = sizeOf(display)
        
        val dark = context.resolveAppNightNow()
        val placeholder = if (dark) Color.BLACK else Color.rgb(233, 238, 246)
        
        val root = FrameLayout(context).apply {
            setBackgroundColor(Color.TRANSPARENT)
        }
        
        window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(Color.TRANSPARENT))
        window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        
        val videoParams = FrameLayout.LayoutParams(-1, -1)
        
        val textureView = TextureView(context).apply {
            // 硬件层渲染，降低合成开销
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
        }
        fun transform() {
            // Copilot display uses full screen, no cropping needed
            textureView.setTransform(android.graphics.Matrix())
        }
        
        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(texture: android.graphics.SurfaceTexture, width: Int, height: Int) {
                texture.setDefaultBufferSize(width, height)
                transform()
                outputSurface = Surface(texture).also(onSurface)
            }
            override fun onSurfaceTextureSizeChanged(texture: android.graphics.SurfaceTexture, width: Int, height: Int) = transform()
            override fun onSurfaceTextureUpdated(texture: android.graphics.SurfaceTexture) = Unit
            override fun onSurfaceTextureDestroyed(texture: android.graphics.SurfaceTexture): Boolean {
                onSurface(null)
                outputSurface?.release()
                outputSurface = null
                return true
            }
        }
        
        root.addView(textureView, FrameLayout.LayoutParams(-1, -1))
        videoView = textureView
        
        val waitingColor = if (android.os.Build.VERSION.SDK_INT >= 23) {
            ContextCompat.getColor(context, android.R.color.white)
        } else {
            Color.WHITE
        }
        
        val density = context.resources.displayMetrics.density
        val waitingLabel = FrameLayout(context).apply {
            addView(android.widget.ProgressBar(context).apply {
                isIndeterminate = true
                indeterminateTintList = android.content.res.ColorStateList.valueOf(waitingColor)
                contentDescription = "等待副驾屏内容"
            }, FrameLayout.LayoutParams((40 * context.resources.displayMetrics.density).toInt(), (40 * context.resources.displayMetrics.density).toInt(), Gravity.CENTER))
        }
        
        root.addView(waitingLabel, FrameLayout.LayoutParams(-1, -1))
        
        // 默认隐藏等待标签，等待流开始
        waitingLabel?.apply {
            alpha = if (streamActive) 0f else 1f
            visibility = if (streamActive) View.GONE else View.VISIBLE
        }
        
        root.setBackgroundColor(Color.TRANSPARENT)
        setContentView(root)
    }

    /** Cross-fades the waiting placeholder over the live content. */
    fun setStreamActive(active: Boolean) {
        if (streamActive == active) return
        streamActive = active
        // TODO: Add waiting label fade animation if needed
    }

    /** Window alpha hides the pixels without destroying the TextureView/decoder surface. */
    fun setContentVisible(visible: Boolean) {
        contentVisible = visible
        window?.let { window ->
            val alpha = if (visible) 1f else 0f
            if (window.attributes.alpha != alpha) window.attributes = window.attributes.apply { this.alpha = alpha }
        }
    }

    companion object {
        const val TAG = "DiPlay-CopilotDisplay"

        /** Find the copilot display for Leapmotor C11. */
        fun findDisplay(context: Context): Display? {
            val displays = context.getSystemService(android.hardware.display.DisplayManager::class.java)
                ?.getDisplays(android.hardware.display.DisplayManager.DISPLAY_CATEGORY_PRESENTATION) ?: return null
            
            // Leapmotor C11 copilot display name pattern
            val name = displays.map { it.name }.firstOrNull { it.contains("copilot") || it.contains("passenger") || it.contains("entertainment") }
            
            return name?.let { name ->
                displays.firstOrNull { it.name == name }?.takeIf { 
                    val size = sizeOf(it)
                    // Typical copilot display resolutions
                    (size.x == 1280 && size.y == 720) || 
                    (size.x == 1920 && size.y == 720) ||
                    (size.x == 1280 && size.y == 800)
                }
            }
        }

        fun describeDisplays(context: Context): String =
            context.getSystemService(android.hardware.display.DisplayManager::class.java)?.displays
                ?.joinToString {
                    val size = sizeOf(it)
                    "${it.displayId}:${it.name} ${size.x}x${size.y} flags=${it.flags} valid=${it.isValid}"
                }.orEmpty()

        fun sizeOf(display: Display): Point = Point().also {
            @Suppress("DEPRECATION")
            display.getRealSize(it)
        }
    }
}