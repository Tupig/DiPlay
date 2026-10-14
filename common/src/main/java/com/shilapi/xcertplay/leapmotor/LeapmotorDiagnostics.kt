// SPDX-License-Identifier: AGPL-3.0-only
package com.shilapi.xcertplay

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.content.pm.PackageInfoCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 零跑 C11 专用诊断报告生成
 * 
 * 包含：车型检测详情、系统环境、CarPlay 连接状态、方向盘按键检测记录
 * 用于问题排查和 GitHub Issue 反馈
 */
object LeapmotorDiagnostics {
    private const val TAG = "DiPlay-LeapmotorDiagnostics"

    /** 生成完整诊断报告 */
    fun generateFullReport(context: Context): String {
        val sb = StringBuilder()
        val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        
        sb.appendLine("========================================")
        sb.appendLine("TuPigPlay 零跑 C11 诊断报告")
        sb.appendLine("生成时间: $time")
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        sb.appendLine("App版本: ${packageInfo.versionName ?: "未知"} (${PackageInfoCompat.getLongVersionCode(packageInfo)})")
        sb.appendLine("========================================\n")

        // 1. 车型检测信息
        sb.appendLine("【1. 车型检测信息】")
        appendVehicleDetection(sb, context)
        sb.appendLine()

        // 2. 系统环境
        sb.appendLine("【2. 系统环境】")
        appendSystemEnvironment(sb)
        sb.appendLine()

        // 3. CarPlay 连接状态
        sb.appendLine("【3. CarPlay 连接状态】")
        appendCarPlayStatus(sb, context)
        sb.appendLine()

        // 4. 方向盘按键检测记录
        sb.appendLine("【4. 方向盘按键检测记录】")
        appendWheelKeyLog(sb, context)
        sb.appendLine()

        // 5. 网络/热点状态
        sb.appendLine("【5. 网络/热点状态】")
        appendNetworkStatus(sb, context)
        sb.appendLine()

        // 6. 权限状态
        sb.appendLine("【6. 权限状态】")
        appendPermissionStatus(sb, context)
        sb.appendLine()

        // 7. 设置配置摘要
        sb.appendLine("【7. 关键设置配置】")
        appendKeySettings(sb, context)
        sb.appendLine()

        sb.appendLine("========================================")
        sb.appendLine("报告结束 - 请附带此报告提交 GitHub Issue")
        sb.appendLine("https://github.com/Tupig/DiPlay/issues")
        sb.appendLine("========================================")

        return sb.toString()
    }

    private fun appendVehicleDetection(sb: StringBuilder, context: Context) {
        try {
            val info = LeapmotorVehicleInfo.getVehicleInfo(context)
            sb.appendLine("  检测状态: 成功")
            sb.appendLine("  制造商: ${info.manufacturer}")
            sb.appendLine("  车型: ${info.model}")
            sb.appendLine("  年款: ${info.modelYear}")
            sb.appendLine("  配置: ${info.trim ?: "未知"}")
            sb.appendLine("  系统版本: ${info.systemVersion}")
            sb.appendLine("  硬件平台: ${info.hardware}")
            sb.appendLine("  ModelIdentifier: ${info.toModelIdentifier()}")
        } catch (e: LeapmotorVehicleInfo.DetectionException) {
            sb.appendLine("  检测状态: 失败")
            sb.appendLine("  错误: ${e.message}")
            sb.appendLine("  ⚠️ 请在设置中手动配置车型信息")
        }
    }

    private fun appendSystemEnvironment(sb: StringBuilder) {
        sb.appendLine("  Android版本: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
        sb.appendLine("  设备型号: ${Build.MODEL}")
        sb.appendLine("  制造商: ${Build.MANUFACTURER}")
        sb.appendLine("  品牌: ${Build.BRAND}")
        sb.appendLine("  硬件: ${Build.HARDWARE}")
        sb.appendLine("  系统显示: ${Build.DISPLAY}")
        sb.appendLine("  指纹: ${Build.FINGERPRINT}")
        sb.appendLine("  序列号: ${runCatching { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Build.getSerial() else Build.UNKNOWN }.getOrDefault(Build.UNKNOWN)}")
        sb.appendLine("  ABI: ${Build.SUPPORTED_ABIS.joinToString(", ")}")
    }

    private fun appendCarPlayStatus(sb: StringBuilder, context: Context) {
        val session = CarPlayBackgroundSession.snapshot()
        if (session != null) {
            sb.appendLine("  会话状态: 已连接")
            sb.appendLine("  控制器: ${session.controller?.javaClass?.simpleName ?: "null"}")
            sb.appendLine("  视频流: 活跃 (${session.width}x${session.height})")
            sb.appendLine("  音频流: 活跃")
        } else {
            sb.appendLine("  会话状态: 未连接")
        }
        
        // 连接偏好
        sb.appendLine("  无线模式: ${AirPlayPersistence.loadWirelessEnabled(context)}")
        sb.appendLine("  热点模式: ${AirPlayPersistence.loadWirelessHotspotMode(context)}")
        sb.appendLine("  选中iPhone: ${DiPlayPreferences.phoneAddress(context) ?: "无"}")
    }

    private fun appendWheelKeyLog(sb: StringBuilder, context: Context) {
        sb.appendLine("  服务状态: ${if (WheelKeyService.connected()) "运行中" else "未运行"}")
        sb.appendLine("  辅助服务启用: ${WheelKeyService.enabledInSettings(context)}")
        sb.appendLine("  缩放功能: ${WheelZoomSettings.enabled(context)}")
        sb.appendLine("  摇杆功能: ${WheelZoomSettings.joystick(context)}")
        sb.appendLine("  Siri按键: ${WheelZoomSettings.siriKey(context)}")
        sb.appendLine("  零跑预设: ${LeapmotorWheelSettings.getPreset(context).displayName}")
        
        // 记录当前按键映射
        val keyMap = LeapmotorWheelSettings.getKeyMap(context)
        keyMap.forEach { (leapKey, wheelKey) ->
            sb.appendLine("    ${leapKey.description}: keyCode=${wheelKey.code}, scanCode=${wheelKey.scan}")
        }
        
        // 诊断报告
        sb.appendLine("  服务诊断: ${WheelKeyService.report(context)}")
    }

    private fun appendNetworkStatus(sb: StringBuilder, context: Context) {
        // 热点状态
        val hotspotSsid = AirPlayPersistence.loadManualHotspotSsid(context)
        sb.appendLine("  手动热点SSID: ${hotspotSsid.ifBlank { "未设置" }}")
        
        // Wi-Fi Direct
        sb.appendLine("  Wi-Fi Direct频道: ${AirPlayPersistence.loadWifiP2pPreferredChannel(context)}")
        
        // 现有WiFi
        val existingSsid = AirPlayPersistence.loadExistingWifiSsid(context)
        sb.appendLine("  现有WiFi SSID: ${existingSsid.ifBlank { "未设置" }}")
    }

    private fun appendPermissionStatus(sb: StringBuilder, context: Context) {
        val perms = arrayOf(
            "android.permission.BLUETOOTH_CONNECT" to "蓝牙连接",
            "android.permission.NEARBY_WIFI_DEVICES" to "附近设备",
            "android.permission.ACCESS_FINE_LOCATION" to "精确位置",
            "android.permission.RECORD_AUDIO" to "麦克风",
            "android.permission.POST_NOTIFICATIONS" to "通知",
            "android.permission.SYSTEM_ALERT_WINDOW" to "悬浮窗",
            "android.permission.WRITE_SETTINGS" to "修改设置"
        )
        
        perms.forEach { (perm, name) ->
            val granted = context.checkSelfPermission(perm) == android.content.pm.PackageManager.PERMISSION_GRANTED
            sb.appendLine("  $name: ${if (granted) "✅ 已授权" else "❌ 未授权"}")
        }
    }

    private fun appendKeySettings(sb: StringBuilder, context: Context) {
        sb.appendLine("  分辨率缩放: ${AirPlayPersistence.loadDisplayScalePercent(context)}%")
        sb.appendLine("  UI缩放: ${AirPlayPersistence.loadUiScalePercent(context)}%")
        sb.appendLine("  帧率: ${AirPlayPersistence.loadFps(context)}fps")
        sb.appendLine("  HEVC: ${AirPlayPersistence.loadHevcEnabled(context)}")
        sb.appendLine("  HEVC软解: ${AirPlayPersistence.loadHevcSoftwareDecoderEnabled(context)}")
        sb.appendLine("  高效音频: ${AirPlayPersistence.loadAudioFocusEnabled(context)}")
        sb.appendLine("  缓冲音乐: ${AirPlayPersistence.loadMainBufferedAudio(context)}")
        sb.appendLine("  低延迟解码: ${AirPlayPersistence.loadLowLatencyDecoder(context)}")
        sb.appendLine("  直接视频输出: ${AirPlayPersistence.loadDirectVideoOutput(context)}")
        sb.appendLine("  流畅视频: ${AirPlayPersistence.loadSmoothVideo(context)}")
        sb.appendLine("  回声消除: ${AirPlayPersistence.loadCallEchoCancellation(context)}")
        sb.appendLine("  语音增强: ${AirPlayPersistence.loadCallVoiceFilter(context)}")
        sb.appendLine("  仪表盘导航: ${LeapmotorOutputSettings.clusterNavigation(context)}")
        sb.appendLine("  自动连接: ${DiPlayPreferences.autoConnect(context)}")
        sb.appendLine("  开机自启: ${AirPlayPersistence.loadAutoStartOnBoot(context)}")
    }
}