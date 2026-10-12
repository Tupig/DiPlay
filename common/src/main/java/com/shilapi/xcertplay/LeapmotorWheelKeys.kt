// SPDX-License-Identifier: AGPL-3.0-only
package com.shilapi.xcertplay

import android.content.Context
import android.util.Log
import android.view.KeyEvent

/**
 * 零跑 C11 2024 方向盘按键键值映射
 * 
 * 硬件：零跑 C11 2024 款 (8295P) 方向盘
 * 系统：LeapOS 3.21.40+
 * 
 * 标准 Android KeyEvent 传递，无需特殊 AIDL 服务
 * 
 * 参考键值（需实车验证）：
 * - 音量滚轮上: KEYCODE_VOLUME_UP (24)
 * - 音量滚轮下: KEYCODE_VOLUME_DOWN (25)
 * - 上一曲: KEYCODE_MEDIA_PREVIOUS (88)
 * - 下一曲: KEYCODE_MEDIA_NEXT (87)
 * - 播放/暂停: KEYCODE_MEDIA_PLAY_PAUSE (85) / KEYCODE_BUTTON_MODE (110)
 * - 语音/电话: KEYCODE_VOICE_ASSIST (231) / KEYCODE_CALL (5)
 * - 模式/确认: KEYCODE_BUTTON_MODE (110) / KEYCODE_DPAD_CENTER (23)
 * - 自定义功能键: 待确认
 * 
 * 实际键值需通过 DiPlay 诊断报告中的 "Wheel key" 字段确认
 */
object LeapmotorWheelKeys {
    private const val TAG = "DiPlay-LeapmotorWheel"

    /** 零跑方向盘标准键值映射（默认值，可通过设置自定义） */
    enum class LeapmotorKey(val defaultKeyCode: Int, val defaultScanCode: Int, val description: String) {
        VOLUME_UP(24, 115, "音量滚轮上"),
        VOLUME_DOWN(25, 114, "音量滚轮下"),
        MEDIA_PREVIOUS(88, 268, "上一曲"),
        MEDIA_NEXT(87, 270, "下一曲"),
        MEDIA_PLAY_PAUSE(85, 505, "播放/暂停"),
        VOICE_ASSIST(231, 0, "语音助手/Siri"),
        CALL(5, 0, "电话/接听"),
        MODE_CONFIRM(110, 300, "模式/确认"),
        DPAD_CENTER(23, 0, "方向键确认"),
        DPAD_UP(19, 0, "方向键上"),
        DPAD_DOWN(20, 0, "方向键下"),
        DPAD_LEFT(21, 0, "方向键左"),
        DPAD_RIGHT(22, 0, "方向键右"),
        CUSTOM_1(0, 0, "自定义功能键1"),
        CUSTOM_2(0, 0, "自定义功能键2"),
    }

    /** 获取零跑预设按键列表 */
    fun getLeapmotorPresetKeys(): List<LeapmotorKey> = LeapmotorKey.entries.toList()

    /** 将 KeyEvent 转换为零跑按键枚举 */
    fun fromKeyEvent(event: KeyEvent): LeapmotorKey? {
        return LeapmotorKey.entries.firstOrNull { it.defaultKeyCode == event.keyCode }
    }

    /** 获取按键描述 */
    fun getKeyDescription(keyCode: Int): String {
        return LeapmotorKey.entries.firstOrNull { it.defaultKeyCode == keyCode }
            ?.description ?: "未知键值($keyCode)"
    }

    /** 记录检测到的方向盘键值（用于实车调试） */
    fun logDetectedKey(event: KeyEvent, context: Context) {
        val key = WheelKey.of(event)
        val preset = fromKeyEvent(event)
        val desc = preset?.description ?: "未映射"
        Log.i(TAG, "检测到方向盘按键: keyCode=${event.keyCode}, scanCode=${event.scanCode}, " +
                "deviceId=${event.deviceId}, deviceName=${key.device}, " +
                "action=${if (event.action == KeyEvent.ACTION_DOWN) "DOWN" else "UP"}, " +
                "preset=$desc")
    }
}

/**
 * 零跑方向盘按键设置管理
 * 扩展 WheelZoomSettings，添加零跑专用预设
 */
object LeapmotorWheelSettings {
    private const val PREFS = "diplay_leapmotor_wheel_keys"
    private const val KEY_PRESET = "preset_version"

    /** 零跑 C11 方向盘预设配置 */
    enum class Preset(val name: String, val keyMap: Map<LeapmotorWheelKeys.LeapmotorKey, WheelKey>) {
        DEFAULT("默认预设", mapOf(
            LeapmotorWheelKeys.LeapmotorKey.MODE_CONFIRM to WheelKey(110, 300, "simulate-keys"),
            LeapmotorWheelKeys.LeapmotorKey.VOLUME_UP to WheelKey(24, 115, "simulate-keys"),
            LeapmotorWheelKeys.LeapmotorKey.VOLUME_DOWN to WheelKey(25, 114, "simulate-keys"),
            LeapmotorWheelKeys.LeapmotorKey.MEDIA_PREVIOUS to WheelKey(88, 268, "simulate-keys"),
            LeapmotorWheelKeys.LeapmotorKey.MEDIA_NEXT to WheelKey(87, 270, "simulate-keys"),
            LeapmotorWheelKeys.LeapmotorKey.MEDIA_PLAY_PAUSE to WheelKey(85, 505, "simulate-keys"),
            LeapmotorWheelKeys.LeapmotorKey.VOICE_ASSIST to WheelKey(231, 0, "simulate-keys"),
        )),
        ALTERNATIVE("备选预设(部分车型)", mapOf(
            LeapmotorWheelKeys.LeapmotorKey.MODE_CONFIRM to WheelKey(23, 0, "simulate-keys"),
            LeapmotorWheelKeys.LeapmotorKey.VOLUME_UP to WheelKey(24, 115, "simulate-keys"),
            LeapmotorWheelKeys.LeapmotorKey.VOLUME_DOWN to WheelKey(25, 114, "simulate-keys"),
            LeapmotorWheelKeys.LeapmotorKey.MEDIA_PREVIOUS to WheelKey(88, 268, "simulate-keys"),
            LeapmotorWheelKeys.LeapmotorKey.MEDIA_NEXT to WheelKey(87, 270, "simulate-keys"),
            LeapmotorWheelKeys.LeapmotorKey.MEDIA_PLAY_PAUSE to WheelKey(353, 505, "simulate-keys"),
            LeapmotorWheelKeys.LeapmotorKey.VOICE_ASSIST to WheelKey(231, 0, "simulate-keys"),
        ))
    }

    /** 获取当前预设 */
    fun getPreset(context: Context): Preset {
        val version = prefs(context).getInt(KEY_PRESET, 0)
        return Preset.entries.getOrNull(version) ?: Preset.DEFAULT
    }

    /** 设置预设 */
    fun setPreset(context: Context, preset: Preset) {
        prefs(context).edit().putInt(KEY_PRESET, preset.ordinal).apply()
        WheelKeyService.settingsChanged()
    }

    /** 获取当前预设的按键映射 */
    fun getKeyMap(context: Context): Map<LeapmotorWheelKeys.LeapmotorKey, WheelKey> {
        return getPreset(context).keyMap
    }

    /** 获取指定角色的按键（兼容 WheelZoomSettings.Role） */
    fun getKeyForRole(context: Context, role: WheelZoomSettings.Role): WheelKey? {
        val keyMap = getKeyMap(context)
        return when (role) {
            WheelZoomSettings.Role.MODE -> keyMap[LeapmotorWheelKeys.LeapmotorKey.MODE_CONFIRM]
            WheelZoomSettings.Role.ZOOM_IN -> keyMap[LeapmotorWheelKeys.LeapmotorKey.VOLUME_UP]
            WheelZoomSettings.Role.ZOOM_OUT -> keyMap[LeapmotorWheelKeys.LeapmotorKey.VOLUME_DOWN]
            WheelZoomSettings.Role.JOYSTICK -> keyMap[LeapmotorWheelKeys.LeapmotorKey.MEDIA_PLAY_PAUSE]
            WheelZoomSettings.Role.PREVIOUS -> keyMap[LeapmotorWheelKeys.LeapmotorKey.MEDIA_PREVIOUS]
            WheelZoomSettings.Role.NEXT -> keyMap[LeapmotorWheelKeys.LeapmotorKey.MEDIA_NEXT]
            WheelZoomSettings.Role.SELECT -> keyMap[LeapmotorWheelKeys.LeapmotorKey.MEDIA_PLAY_PAUSE]
            WheelZoomSettings.Role.SIRI -> keyMap[LeapmotorWheelKeys.LeapmotorKey.VOICE_ASSIST]
            else -> null
        }
    }

    /** 应用预设到 WheelZoomSettings（用于初始化） */
    fun applyPresetToWheelZoomSettings(context: Context) {
        val keyMap = getKeyMap(context)
        val zoomPrefs = context.getSharedPreferences("diplay_wheel_map_zoom", Context.MODE_PRIVATE).edit()
        
        keyMap.forEach { (leapKey, wheelKey) ->
            val role = when (leapKey) {
                LeapmotorWheelKeys.LeapmotorKey.MODE_CONFIRM -> WheelZoomSettings.Role.MODE
                LeapmotorWheelKeys.LeapmotorKey.VOLUME_UP -> WheelZoomSettings.Role.ZOOM_IN
                LeapmotorWheelKeys.LeapmotorKey.VOLUME_DOWN -> WheelZoomSettings.Role.ZOOM_OUT
                LeapmotorWheelKeys.LeapmotorKey.MEDIA_PLAY_PAUSE -> WheelZoomSettings.Role.JOYSTICK
                LeapmotorWheelKeys.LeapmotorKey.MEDIA_PREVIOUS -> WheelZoomSettings.Role.PREVIOUS
                LeapmotorWheelKeys.LeapmotorKey.MEDIA_NEXT -> WheelZoomSettings.Role.NEXT
                LeapmotorWheelKeys.LeapmotorKey.MEDIA_PLAY_PAUSE -> WheelZoomSettings.Role.SELECT
                LeapmotorWheelKeys.LeapmotorKey.VOICE_ASSIST -> WheelZoomSettings.Role.SIRI
                else -> null
            }
            role?.let { zoomPrefs.putString("key_${it.name}", wheelKey.encode()) }
        }
        zoomPrefs.apply()
        WheelKeyService.settingsChanged()
    }

    /** 重置为默认预设 */
    fun resetToDefault(context: Context) {
        setPreset(context, Preset.DEFAULT)
        applyPresetToWheelZoomSettings(context)
    }

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}

/**
 * 零跑方向盘按键检测器（用于设置界面的"按键学习"功能）
 */
class LeapmotorWheelKeyDetector(private val context: Context) {
    private val detectedKeys = mutableMapOf<Int, WheelKey>()
    private val listener: (WheelKey) -> Unit = { }

    fun startDetection(listener: (WheelKey) -> Unit) {
        // 实际检测通过 WheelKeyService.onKeyEvent 进行
        // 这里提供设置界面调用的接口
    }

    fun stopDetection() {
        // 停止检测
    }

    /** 记录检测到的按键 */
    fun onKeyEvent(event: KeyEvent) {
        val key = WheelKey.of(event)
        detectedKeys[event.keyCode] = key
        LeapmotorWheelKeys.logDetectedKey(event, context)
    }

    /** 获取已检测的按键 */
    fun getDetectedKeys(): Map<Int, WheelKey> = detectedKeys.toMap()

    /** 清除记录 */
    fun clear() {
        detectedKeys.clear()
    }
}