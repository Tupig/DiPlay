// SPDX-License-Identifier: AGPL-3.0-only
package com.shilapi.xcertplay

import android.content.Context
import com.shilapi.xcertplay.hud.BydOutputSettings
import com.shilapi.xcertplay.hud.BydOemClusterHold
import com.shilapi.xcertplay.transport.EvChargingConnectors

/**
 * 零跑 C11 输出设置
 *
 * 迁移过渡层：统一委托已恢复的 [BydOutputSettings]，保持重命名前的行为基线。
 * 零跑专属的新增开关保留在本类自己的 SharedPreferences 中。
 */
object LeapmotorOutputSettings {
    private const val PREFS = "diplay_leapmotor_output"
    private const val KEY_CLUSTER_SPEEDOMETER = "cluster_speedometer"
    private const val KEY_CLUSTER_NAVIGATION = "cluster_navigation"
    private const val KEY_HARMAN_FULLSCREEN = "harman_fullscreen"

    // ---- 委托 BydOutputSettings（共享模块，行为与迁移前一致） ----

    const val DEFAULT_LOW_CHARGE_PERCENT = BydOutputSettings.DEFAULT_LOW_CHARGE_PERCENT
    val lowChargePresets: List<Int> get() = BydOutputSettings.lowChargePresets

    fun enabled(context: Context): Boolean = BydOutputSettings.enabled(context)
    fun setEnabled(context: Context, enabled: Boolean) = BydOutputSettings.setEnabled(context, enabled)

    fun clusterStreamPause(context: Context): Boolean = BydOutputSettings.clusterStreamPause(context)
    fun setClusterStreamPause(context: Context, enabled: Boolean) = BydOutputSettings.setClusterStreamPause(context, enabled)

    fun batteryToIphone(context: Context): Boolean = BydOutputSettings.batteryToIphone(context)
    fun setBatteryToIphone(context: Context, enabled: Boolean) = BydOutputSettings.setBatteryToIphone(context, enabled)
    fun batteryToIphoneActive(context: Context): Boolean = BydOutputSettings.batteryToIphoneActive(context)

    fun chargingConnectors(context: Context): EvChargingConnectors = BydOutputSettings.chargingConnectors(context)
    fun setChargingConnectors(context: Context, connectors: EvChargingConnectors) =
        BydOutputSettings.setChargingConnectors(context, connectors)

    fun wheelSpeedToIphone(context: Context): Boolean = BydOutputSettings.wheelSpeedToIphone(context)
    fun setWheelSpeedToIphone(context: Context, enabled: Boolean) = BydOutputSettings.setWheelSpeedToIphone(context, enabled)
    fun wheelSpeedToIphoneActive(context: Context): Boolean = BydOutputSettings.wheelSpeedToIphoneActive(context)

    fun videoWhileParked(context: Context): Boolean = BydOutputSettings.videoWhileParked(context)
    fun setVideoWhileParked(context: Context, enabled: Boolean) = BydOutputSettings.setVideoWhileParked(context, enabled)
    fun videoWhileParkedActive(context: Context): Boolean = BydOutputSettings.videoWhileParkedActive(context)

    fun clusterSong(context: Context): Boolean = BydOutputSettings.clusterSong(context)
    fun setClusterSong(context: Context, enabled: Boolean) = BydOutputSettings.setClusterSong(context, enabled)

    fun carPlayCalls(context: Context): Boolean = BydOutputSettings.carPlayCalls(context)
    fun setCarPlayCalls(context: Context, enabled: Boolean) = BydOutputSettings.setCarPlayCalls(context, enabled)

    fun carPlayCallControls(context: Context): Boolean = BydOutputSettings.carPlayCallControls(context)
    fun setCarPlayCallControls(context: Context, enabled: Boolean) = BydOutputSettings.setCarPlayCallControls(context, enabled)

    fun phoneAudioRoute(context: Context): Boolean = BydOutputSettings.phoneAudioRoute(context)
    fun setPhoneAudioRoute(context: Context, enabled: Boolean) = BydOutputSettings.setPhoneAudioRoute(context, enabled)

    fun phoneAudioRouteApps(context: Context): Boolean = BydOutputSettings.phoneAudioRouteApps(context)
    fun setPhoneAudioRouteApps(context: Context, enabled: Boolean) = BydOutputSettings.setPhoneAudioRouteApps(context, enabled)

    fun clusterSongOnChange(context: Context): Boolean = BydOutputSettings.clusterSongOnChange(context)
    fun setClusterSongOnChange(context: Context, enabled: Boolean) = BydOutputSettings.setClusterSongOnChange(context, enabled)

    fun legacyVehicleProbe(context: Context): Boolean = BydOutputSettings.legacyVehicleProbe(context)
    fun setLegacyVehicleProbe(context: Context, enabled: Boolean) = BydOutputSettings.setLegacyVehicleProbe(context, enabled)

    fun hudSong(context: Context): Boolean = BydOutputSettings.hudSong(context)
    fun setHudSong(context: Context, enabled: Boolean) = BydOutputSettings.setHudSong(context, enabled)

    fun oemClusterHold(context: Context): BydOemClusterHold = BydOutputSettings.oemClusterHold(context)
    fun setOemClusterHold(context: Context, hold: BydOemClusterHold) = BydOutputSettings.setOemClusterHold(context, hold)

    fun lowChargePercent(context: Context): Int = BydOutputSettings.lowChargePercent(context)
    fun setLowChargePercent(context: Context, percent: Int) = BydOutputSettings.setLowChargePercent(context, percent)

    fun standaloneHudAvailable(context: Context): Boolean = BydOutputSettings.standaloneHudAvailable(context)
    fun standaloneHudDiagnosticReport(context: Context): String = BydOutputSettings.standaloneHudDiagnosticReport(context)

    fun navigationAvailable(context: Context): Boolean = BydOutputSettings.navigationAvailable(context)

    fun fceClusterAvailable(): Boolean = BydOutputSettings.fceClusterAvailable()
    fun fceCluster(context: Context): Boolean = BydOutputSettings.fceCluster(context)
    fun setFceCluster(context: Context, enabled: Boolean) = BydOutputSettings.setFceCluster(context, enabled)

    fun available(context: Context): Boolean = BydOutputSettings.available(context)

    // ---- 零跑专属开关（本地持久化） ----

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

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
