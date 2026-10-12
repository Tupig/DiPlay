// SPDX-License-Identifier: AGPL-3.0-only
package com.shilapi.xcertplay

import android.content.Context
import android.os.Build
import com.shilapi.xcertplay.airplay.AirPlayDisplayConfig
import com.shilapi.xcertplay.airplay.CarPlayClusterDisplay

/**
 * Geometry for Leapmotor C11 2024 (8295P) cluster display.
 * 
 * 12.3" cluster display with 1920x720 resolution.
 * Uses standard CarPlay Cluster Display protocol.
 * No proprietary DiLink-style firmware fingerprint matching needed.
 */
internal object LeapmotorClusterLayout {
    /** SharedPreferences key for cluster layout settings */
    private const val PREFS = "leapmotor_cluster_layout"

    /** Cluster display theme options */
    enum class Theme(val label: String) {
        FULL_MAP("全屏地图 · Full Map"),
        SIDE_MAP("侧边地图 · Side Map"),
        TURN_GUIDANCE("转向引导 · Turn Guidance"),
    }

    /** Cluster display contrast options */
    enum class Contrast(val label: String) {
        DEFAULT("跟随主题 · Follow Theme"),
        LIGHT("浅色背景 · Light Background"),
        DARK("深色背景 · Dark Background"),
    }

    /** Layout plan for cluster display */
    data class Plan(
        val left: Int,
        val top: Int,
        val width: Int,
        val height: Int,
        val fullMap: Boolean,
    ) {
        /** Left crop offset in source stream coordinates */
        val sourceLeft: Int get() = (STREAM_WIDTH - width) / 2
        /** Top crop offset in source stream coordinates */
        val sourceTop: Int get() = STREAM_HEIGHT - height
    }

    /** Leapmotor C11 cluster stream resolution (12.3" display) */
    const val STREAM_WIDTH = 1920
    const val STREAM_HEIGHT = 720

    /** CarPlay cluster display stream configuration */
    fun streamConfig(): AirPlayDisplayConfig = CarPlayClusterDisplay.config(
        STREAM_WIDTH, STREAM_HEIGHT, scalePercent = 100,
    ).copy(safeArea = null)

    /** Check if current device is Leapmotor C11 2024+ with cluster display support */
    fun supported(context: Context): Boolean {
        return LeapmotorC11Settings.SystemVersion.isLeapmotorC11_2024(context) &&
               CarPlayClusterDisplay.isSupported(context)
    }

    /** Get current theme from preferences */
    fun theme(context: Context): Theme = Theme.entries.firstOrNull {
        it.name == context.getSharedPreferences(PREFS, 0).getString("theme", null)
    } ?: Theme.FULL_MAP

    /** Save theme preference */
    fun saveTheme(context: Context, theme: Theme) {
        context.getSharedPreferences(PREFS, 0).edit().putString("theme", theme.name).apply()
    }

    /** Check if automatic theme switching is enabled */
    fun automatic(context: Context): Boolean = supported(context) &&
        context.getSharedPreferences(PREFS, 0).getBoolean("automatic", false)

    /** Save automatic mode preference */
    fun saveAutomatic(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS, 0).edit().putBoolean("automatic", enabled).apply()
    }

    /** Get current contrast setting */
    fun contrast(context: Context): Contrast = Contrast.entries.firstOrNull {
        it.name == context.getSharedPreferences(PREFS, 0).getString("contrast", null)
    } ?: Contrast.DEFAULT

    /** Save contrast preference */
    fun saveContrast(context: Context, contrast: Contrast) {
        context.getSharedPreferences(PREFS, 0).edit().putString("contrast", contrast.name).apply()
    }

    /** Determine if dark mode should be used for cluster */
    fun dark(context: Context, theme: Theme = theme(context)): Boolean = when (contrast(context)) {
        Contrast.DEFAULT -> theme == Theme.SIDE_MAP
        Contrast.LIGHT -> false
        Contrast.DARK -> true
    }

    /** Find the cluster presentation display name */
    fun displayName(names: List<String>, context: Context, theme: Theme = theme(context)): String? {
        if (supported(context)) {
            // For Leapmotor, we use standard CarPlay cluster display names
            return when (theme) {
                Theme.FULL_MAP -> names.firstOrNull { it.contains("cluster") && it.contains("full") }
                Theme.SIDE_MAP -> names.firstOrNull { it.contains("cluster") && it.contains("side") }
                Theme.TURN_GUIDANCE -> names.firstOrNull { it.contains("cluster") && it.contains("turn") }
                else -> null
            }
        }
        return names.firstOrNull { it.contains("cluster") }
    }

    /** Calculate layout plan for given display size and theme */
    fun plan(width: Int, height: Int, theme: Theme): Plan? {
        // Leapmotor C11 cluster: 1920x720
        if (width != STREAM_WIDTH || height != STREAM_HEIGHT) return null
        
        return when (theme) {
            Theme.FULL_MAP -> {
                // Full map: use most of the display, leave space for speed/status at top
                Plan(0, 120, STREAM_WIDTH, STREAM_HEIGHT - 120, true)
            }
            Theme.SIDE_MAP -> {
                // Side map: right portion, leave space for speed/gear on left
                Plan(720, 0, STREAM_WIDTH - 720, height, false)
            }
            Theme.TURN_GUIDANCE -> {
                // Turn guidance: bottom portion for maneuver card
                Plan(0, height - 300, width, 300, false)
            }
        }
    }

    /** Cluster display theme options */
    enum class Theme(val label: String) {
        FULL_MAP("全屏地图 · Full Map"),
        SIDE_MAP("侧边地图 · Side Map"),
        TURN_GUIDANCE("转向引导 · Turn Guidance"),
    }

    /** Cluster display contrast options */
    enum class Contrast(val label: String) {
        DEFAULT("跟随主题 · Follow Theme"),
        LIGHT("浅色背景 · Light Background"),
        DARK("深色背景 · Dark Background"),
    }

    /** Get current theme from preferences */
    fun theme(context: Context): Theme = Theme.entries.firstOrNull {
        it.name == context.getSharedPreferences(PREFS, 0).getString("theme", null)
    } ?: Theme.FULL_MAP

    /** Save theme preference */
    fun saveTheme(context: Context, theme: Theme) {
        context.getSharedPreferences(PREFS, 0).edit().putString("theme", theme.name).apply()
    }

    /** Check if automatic theme switching is enabled */
    fun automatic(context: Context): Boolean = supported(context) &&
        context.getSharedPreferences(PREFS, 0).getBoolean("automatic", false)

    /** Save automatic mode preference */
    fun saveAutomatic(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS, 0).edit().putBoolean("automatic", enabled).apply()
    }

    /** Get current contrast setting */
    fun contrast(context: Context): Contrast = Contrast.entries.firstOrNull {
        it.name == context.getSharedPreferences(PREFS, 0).getString("contrast", null)
    } ?: Contrast.DEFAULT

    /** Save contrast preference */
    fun saveContrast(context: Context, contrast: Contrast) {
        context.getSharedPreferences(PREFS, 0).edit().putString("contrast", contrast.name).apply()
    }

    /** Determine if dark mode should be used for cluster */
    fun dark(context: Context, theme: Theme = theme(context)): Boolean = when (contrast(context)) {
        Contrast.DEFAULT -> theme == Theme.SIDE_MAP
        Contrast.LIGHT -> false
        Contrast.DARK -> true
    }

    /** Find the cluster presentation display name */
    fun displayName(names: List<String>, context: Context, theme: Theme = theme(context)): String? {
        if (supported(context)) {
            return when (theme) {
                Theme.FULL_MAP -> names.firstOrNull { it.contains("cluster") && it.contains("full") }
                Theme.SIDE_MAP -> names.firstOrNull { it.contains("cluster") && it.contains("side") }
                Theme.TURN_GUIDANCE -> names.firstOrNull { it.contains("cluster") && it.contains("turn") }
                else -> null
            }
        }
        return names.firstOrNull { it.contains("cluster") }
    }

    /** Calculate layout plan for given display size and theme */
    fun plan(width: Int, height: Int, theme: Theme): Plan? {
        if (width != STREAM_WIDTH || height != STREAM_HEIGHT) return null
        
        return when (theme) {
            Theme.FULL_MAP -> {
                Plan(0, 120, STREAM_WIDTH, STREAM_HEIGHT - 120, true)
            }
            Theme.SIDE_MAP -> {
                Plan(720, 0, STREAM_WIDTH - 720, height, false)
            }
            Theme.TURN_GUIDANCE -> {
                Plan(0, height - 300, width, 300, false)
            }
        }
    }
}