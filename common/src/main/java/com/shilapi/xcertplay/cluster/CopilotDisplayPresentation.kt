// SPDX-License-Identifier: AGPL-3.0-only
package com.shilapi.xcertplay.cluster

import android.app.Presentation
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Point
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.Gravity
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ProgressBar
import com.shilapi.xcertplay.host.R
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

    // The placeholder until the stream arrives: a small spinner.
    private var waitingLabel: View? = null
    private var videoView: View? = null
    private var streamActive = false
    var outputSurface: Surface? = null
        private set
    var contentVisible = true
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dark = context.resolveAppNightNow()
        val placeholder = if (dark) Color.BLACK else Color.rgb(233, 238, 246)

        val root = FrameLayout(context).apply {
            setBackgroundColor(placeholder)
        }

        window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(Color.TRANSPARENT))
        window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        val textureView = TextureView(context).apply {
            // 硬件层渲染，降低合成开销
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
        }
        fun transform() {
            // Copilot display uses full screen, no cropping needed
            textureView.setTransform(android.graphics.Matrix())
        }

        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(texture: SurfaceTexture, width: Int, height: Int) {
                texture.setDefaultBufferSize(width, height)
                transform()
                outputSurface = Surface(texture).also(onSurface)
            }
            override fun onSurfaceTextureSizeChanged(texture: SurfaceTexture, width: Int, height: Int) = transform()
            override fun onSurfaceTextureUpdated(texture: SurfaceTexture) = Unit
            override fun onSurfaceTextureDestroyed(texture: SurfaceTexture): Boolean {
                onSurface(null)
                outputSurface?.release()
                outputSurface = null
                return true
            }
        }

        root.addView(textureView, FrameLayout.LayoutParams(-1, -1))
        videoView = textureView

        val waitingColor = if (dark) Color.WHITE else Color.DKGRAY
        val density = context.resources.displayMetrics.density
        waitingLabel = FrameLayout(context).apply {
            addView(ProgressBar(context).apply {
                isIndeterminate = true
                indeterminateTintList = ColorStateList.valueOf(waitingColor)
                contentDescription = context.getString(R.string.copilot_waiting_content)
            }, FrameLayout.LayoutParams((40 * density).toInt(), (40 * density).toInt(), Gravity.CENTER))
        }
        root.addView(waitingLabel, FrameLayout.LayoutParams(-1, -1))
        // Start with the placeholder visible; TextureView would otherwise cover the
        // backdrop with an empty black surface before the first stream notification.
        videoView?.alpha = if (streamActive) 1f else 0f
        waitingLabel?.apply {
            alpha = if (streamActive) 0f else 1f
            visibility = if (streamActive) View.GONE else View.VISIBLE
        }
        setContentView(root)
    }

    /**
     * Cross-fades the waiting placeholder over the live content.
     */
    fun setStreamActive(active: Boolean) {
        if (streamActive == active) return
        // Update before cancelling: a cancelled fade must not hide the newly restored label.
        streamActive = active
        val video = videoView
        val label = waitingLabel
        video?.animate()?.cancel()
        label?.animate()?.cancel()
        if (video == null || label == null) return
        label.visibility = View.VISIBLE
        // Animate from the current opacity so a stream reversal does not jump or restart.
        video.animate().alpha(if (active) 1f else 0f).setDuration(300).start()
        label.animate().alpha(if (active) 0f else 1f).setDuration(300).withEndAction {
            if (streamActive == active) label.visibility = if (active) View.GONE else View.VISIBLE
        }.start()
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

        /** Copilot display resolutions seen on supported vehicles. */
        private val SUPPORTED_SIZES = setOf(1280 to 720, 1920 to 720, 1280 to 800)

        /** Find the copilot display for Leapmotor C11. */
        fun findDisplay(context: Context): Display? {
            val displays = context.getSystemService(android.hardware.display.DisplayManager::class.java)
                ?.getDisplays(android.hardware.display.DisplayManager.DISPLAY_CATEGORY_PRESENTATION) ?: return null

            // Leapmotor C11 copilot display name pattern
            val candidate = displays.firstOrNull {
                it.name.contains("copilot", ignoreCase = true) ||
                    it.name.contains("passenger", ignoreCase = true) ||
                    it.name.contains("entertainment", ignoreCase = true)
            } ?: run {
                Log.i(TAG, "no named copilot display among ${displays.map { it.name }}")
                return null
            }
            val size = sizeOf(candidate)
            val whitelisted = (size.x to size.y) in SUPPORTED_SIZES
            if (!whitelisted) {
                // Real vehicles expose differently sized displays; log the rejection for calibration.
                Log.w(TAG, "copilot display rejected by resolution whitelist: " +
                    "${candidate.displayId}:${candidate.name} ${size.x}x${size.y}")
                return null
            }
            Log.i(TAG, "copilot display found: ${candidate.displayId}:${candidate.name} ${size.x}x${size.y}")
            return candidate
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
