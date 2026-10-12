// SPDX-License-Identifier: AGPL-3.0-only
package com.shilapi.xcertplay

import android.content.Context
import android.content.SharedPreferences

/**
 * 零跑 C11 输出设置
 * 
 * 替代 BydOutputSettings，适配零跑车机
 * 无 ADB 环境下的功能开关
 */
object LeapmotorOutputSettings {
    private const val PREFS = "diplay_leapmotor_output"
    private const val KEY_CLUSTER_SPEEDOMETER = "cluster_speedometer"
    private const val KEY_CLUSTER_NAVIGATION = "cluster_navigation"
    private const val KEY_HARMAN_FULLSCREEN = "harman_fullscreen"
    private const val KEY_CARPLAY_CALL_CONTROLS = "carplay_call_controls"
    private const val KEY_PHONE_AUDIO_ROUTE = "phone_audio_route"

    /** 仪表盘速度计（实验性，需车辆数据支持） */
    fun clusterSpeedometer(context: Context): Boolean =
        prefs(context).getBoolean(KEY_CLUSTER_SPEEDOMETER, false)

    fun setClusterSpeedometer(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_CLUSTER_SPEEDOMETER, enabled).apply()
    }

    /** 仪表盘导航显示（通过 CarPlay 仪表盘投屏） */
    fun clusterNavigation(context: Context): Boolean =
        prefs(context).getBoolean(KEY_CLUSTER_NAVIGATION, true)

    fun setClusterNavigation(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_CLUSTER_NAVIGATION, enabled).apply()
    }

    /** 哈曼全屏（零跑非哈曼，默认关闭） */
    fun harmanFullscreen(context: Context): Boolean =
        prefs(context).getBoolean(KEY_HARMAN_FULLSCREEN, false)

    fun setHarmanFullscreen(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_HARMAN_FULLSCREEN, enabled).apply()
    }

    /** CarPlay 通话按键控制（实验性） */
    fun carPlayCallControls(context: Context): Boolean =
        prefs(context).getBoolean(KEY_CARPLAY_CALL_CONTROLS, false)

    fun setCarPlayCallControls(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_CARPLAY_CALL_CONTROLS, enabled).apply()
    }

    /** 蓝牙电话音频路由 */
    fun phoneAudioRouteApps(context: Context): Boolean =
        prefs(context).getBoolean(KEY_PHONE_AUDIO_ROUTE, false)

    fun setPhoneAudioRouteApps(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_PHONE_AUDIO_ROUTE, enabled).apply()
    }

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}