// SPDX-License-Identifier: AGPL-3.0-only
package com.shilapi.xcertplay

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import org.json.JSONObject

/**
 * 零跑车辆数据存储
 * 
 * 替代 BydVehicleFieldStore - 存储车辆配置、用户偏好、校准数据
 * 零跑无需探测，直接存储用户配置和校准参数
 */
class LeapmotorVehicleDataStore(context: Context) {

    companion object {
        private const val TAG = "DiPlay-LeapmotorVehicleDataStore"
        private const val PREFS = "diplay_leapmotor_vehicle_data"
        private const val KEY_CALIBRATION = "calibration"
        private const val KEY_USER_PREFERENCES = "user_preferences"
        private const val KEY_VEHICLE_CONFIG = "vehicle_config"
        private const val KEY_ADB_SPEED_COMMAND = "adb_speed_command"
        private const val KEY_LAST_SYNC = "last_sync"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    /** 车辆校准数据 */
    data class CalibrationData(
        val clusterSafeArea: ClusterSafeArea? = null,
        val displayOffsetX: Int = 0,
        val displayOffsetY: Int = 0,
        val displayScale: Float = 1.0f,
        val lastCalibrated: Long = 0,
    )

    /** 仪表盘安全区域 */
    data class ClusterSafeArea(
        val left: Int,
        val top: Int,
        val right: Int,
        val bottom: Int,
        val timestamp: Long = System.currentTimeMillis(),
    )

    /** 用户偏好设置 */
    data class UserPreferences(
        val preferredClusterContent: String = "MAP",  // MAP, TURN_GUIDANCE, MEDIA, SPEEDOMETER
        val clusterBrightness: Int = 80,              // 0-100
        val enableSpeedometer: Boolean = false,
        val enableTurnGuidance: Boolean = true,
        val enableMediaInfo: Boolean = true,
        val enableCallDisplay: Boolean = true,
    )

    /** 车辆配置 */
    data class VehicleConfig(
        val vin: String? = null,
        val model: String = "C11",
        val trim: String? = null,
        val year: Int = 2024,
        val hardwareVersion: String = "SA8295P",
        val softwareVersion: String = "3.21+",
    )

    // ========== 校准数据 ==========

    /** 保存仪表盘安全区域校准 */
    fun saveClusterSafeArea(safeArea: ClusterSafeArea) {
        val calibration = getCalibration().copy(
            clusterSafeArea = safeArea,
            lastCalibrated = System.currentTimeMillis()
        )
        saveCalibration(calibration)
    }

    /** 获取仪表盘安全区域 */
    fun getClusterSafeArea(): ClusterSafeArea? = getCalibration().clusterSafeArea

    /** 获取校准数据 */
    fun getCalibration(): CalibrationData {
        val json = prefs.getString(KEY_CALIBRATION, null)
        return if (json != null) {
            try {
                calibrationFromJson(json)
            } catch (e: Exception) {
                Log.w(TAG, "解析校准数据失败", e)
                CalibrationData()
            }
        } else CalibrationData()
    }

    /** 保存校准数据 */
    private fun saveCalibration(calibration: CalibrationData) {
        prefs.edit()
            .putString(KEY_CALIBRATION, calibrationToJson(calibration))
            .apply()
    }

    /** 保存显示偏移 */
    fun saveDisplayOffset(offsetX: Int, offsetY: Int) {
        val calibration = getCalibration().copy(
            displayOffsetX = offsetX,
            displayOffsetY = offsetY,
            lastCalibrated = System.currentTimeMillis()
        )
        saveCalibration(calibration)
    }

    /** 获取显示偏移 */
    fun getDisplayOffset(): Pair<Int, Int> {
        val cal = getCalibration()
        return cal.displayOffsetX to cal.displayOffsetY
    }

    /** 保存显示缩放 */
    fun saveDisplayScale(scale: Float) {
        val calibration = getCalibration().copy(
            displayScale = scale,
            lastCalibrated = System.currentTimeMillis()
        )
        saveCalibration(calibration)
    }

    /** 获取显示缩放 */
    fun getDisplayScale(): Float = getCalibration().displayScale

    // ========== 用户偏好 ==========

    /** 获取用户偏好 */
    fun getUserPreferences(): UserPreferences {
        val json = prefs.getString(KEY_USER_PREFERENCES, null)
        return if (json != null) {
            try {
                userPreferencesFromJson(json)
            } catch (e: Exception) {
                Log.w(TAG, "解析用户偏好失败", e)
                UserPreferences()
            }
        } else UserPreferences()
    }

    /** 保存用户偏好 */
    fun saveUserPreferences(preferences: UserPreferences) {
        prefs.edit()
            .putString(KEY_USER_PREFERENCES, userPreferencesToJson(preferences))
            .apply()
    }

    /** 更新单个偏好项 */
    fun updateUserPreference(block: UserPreferences.() -> Unit) {
        val prefs = getUserPreferences()
        prefs.block()
        saveUserPreferences(prefs)
    }

    // ========== 车辆配置 ==========

    /** 获取车辆配置 */
    fun getVehicleConfig(): VehicleConfig {
        val json = prefs.getString(KEY_VEHICLE_CONFIG, null)
        return if (json != null) {
            try {
                vehicleConfigFromJson(json)
            } catch (e: Exception) {
                Log.w(TAG, "解析车辆配置失败", e)
                VehicleConfig()
            }
        } else VehicleConfig()
    }

    /** 保存车辆配置 */
    fun saveVehicleConfig(config: VehicleConfig) {
        prefs.edit()
            .putString(KEY_VEHICLE_CONFIG, vehicleConfigToJson(config))
            .apply()
    }

    // ========== ADB 车速命令 ==========

    /**
     * 读取车速的 ADB shell 命令（#8）。
     * 零跑车速的 service call 参数需真机标定（见 docs/LEAPMOTOR.md），未标定时返回 null，
     * 上层以 null 优雅降级。
     */
    fun getAdbSpeedCommand(): String? = prefs.getString(KEY_ADB_SPEED_COMMAND, null)?.takeIf { it.isNotBlank() }

    /** 保存车速命令；传 null 或空白即清除标定（恢复降级）。 */
    fun saveAdbSpeedCommand(command: String?) {
        if (command.isNullOrBlank()) {
            prefs.edit().remove(KEY_ADB_SPEED_COMMAND).apply()
        } else {
            prefs.edit().putString(KEY_ADB_SPEED_COMMAND, command.trim()).apply()
        }
    }

    // ========== 同步时间戳 ==========

    /** 更新最后同步时间 */
    fun updateLastSync() {
        prefs.edit()
            .putLong(KEY_LAST_SYNC, System.currentTimeMillis())
            .apply()
    }

    /** 获取最后同步时间 */
    fun getLastSync(): Long = prefs.getLong(KEY_LAST_SYNC, 0)

    // ========== 清除数据 ==========

    /** 清除所有数据（恢复出厂设置时调用） */
    fun clearAll() {
        prefs.edit().clear().apply()
        Log.i(TAG, "车辆数据已清除")
    }

    /** 导出数据（用于备份） */
    fun exportData(): String {
        return buildString {
            appendLine("{")
            appendLine("  \"calibration\": ${prefs.getString(KEY_CALIBRATION, "{}")},")
            appendLine("  \"userPreferences\": ${prefs.getString(KEY_USER_PREFERENCES, "{}")},")
            appendLine("  \"vehicleConfig\": ${prefs.getString(KEY_VEHICLE_CONFIG, "{}")},")
            appendLine("  \"lastSync\": ${prefs.getLong(KEY_LAST_SYNC, 0)}")
            appendLine("}")
        }
    }

    /** 导入数据（从备份恢复） */
    fun importData(json: String): Boolean {
        return try {
            val root = JSONObject(json)
            val editor = prefs.edit()
            root.optJSONObject("calibration")?.let { editor.putString(KEY_CALIBRATION, it.toString()) }
            root.optJSONObject("userPreferences")?.let { editor.putString(KEY_USER_PREFERENCES, it.toString()) }
            root.optJSONObject("vehicleConfig")?.let { editor.putString(KEY_VEHICLE_CONFIG, it.toString()) }
            if (root.has("lastSync")) editor.putLong(KEY_LAST_SYNC, root.getLong("lastSync"))
            editor.apply()
            Log.i(TAG, "车辆数据导入成功")
            true
        } catch (e: Exception) {
            Log.e(TAG, "车辆数据导入失败", e)
            false
        }
    }

    // ========== JSON 序列化（org.json） ==========

    private fun calibrationToJson(calibration: CalibrationData): String {
        val json = JSONObject()
        json.put("displayOffsetX", calibration.displayOffsetX)
        json.put("displayOffsetY", calibration.displayOffsetY)
        json.put("displayScale", calibration.displayScale.toDouble())
        json.put("lastCalibrated", calibration.lastCalibrated)
        calibration.clusterSafeArea?.let { safeArea ->
            val safeAreaJson = JSONObject()
            safeAreaJson.put("left", safeArea.left)
            safeAreaJson.put("top", safeArea.top)
            safeAreaJson.put("right", safeArea.right)
            safeAreaJson.put("bottom", safeArea.bottom)
            safeAreaJson.put("timestamp", safeArea.timestamp)
            json.put("clusterSafeArea", safeAreaJson)
        }
        return json.toString()
    }

    private fun calibrationFromJson(json: String): CalibrationData {
        val obj = JSONObject(json)
        val safeAreaJson = obj.optJSONObject("clusterSafeArea")
        return CalibrationData(
            clusterSafeArea = safeAreaJson?.let {
                ClusterSafeArea(
                    left = it.optInt("left", 0),
                    top = it.optInt("top", 0),
                    right = it.optInt("right", 0),
                    bottom = it.optInt("bottom", 0),
                    timestamp = it.optLong("timestamp", 0),
                )
            },
            displayOffsetX = obj.optInt("displayOffsetX", 0),
            displayOffsetY = obj.optInt("displayOffsetY", 0),
            displayScale = obj.optDouble("displayScale", 1.0).toFloat(),
            lastCalibrated = obj.optLong("lastCalibrated", 0),
        )
    }

    private fun userPreferencesToJson(preferences: UserPreferences): String {
        val json = JSONObject()
        json.put("preferredClusterContent", preferences.preferredClusterContent)
        json.put("clusterBrightness", preferences.clusterBrightness)
        json.put("enableSpeedometer", preferences.enableSpeedometer)
        json.put("enableTurnGuidance", preferences.enableTurnGuidance)
        json.put("enableMediaInfo", preferences.enableMediaInfo)
        json.put("enableCallDisplay", preferences.enableCallDisplay)
        return json.toString()
    }

    private fun userPreferencesFromJson(json: String): UserPreferences {
        val obj = JSONObject(json)
        return UserPreferences(
            preferredClusterContent = obj.optString("preferredClusterContent", "MAP"),
            clusterBrightness = obj.optInt("clusterBrightness", 80),
            enableSpeedometer = obj.optBoolean("enableSpeedometer", false),
            enableTurnGuidance = obj.optBoolean("enableTurnGuidance", true),
            enableMediaInfo = obj.optBoolean("enableMediaInfo", true),
            enableCallDisplay = obj.optBoolean("enableCallDisplay", true),
        )
    }

    private fun vehicleConfigToJson(config: VehicleConfig): String {
        val json = JSONObject()
        config.vin?.let { json.put("vin", it) }
        json.put("model", config.model)
        config.trim?.let { json.put("trim", it) }
        json.put("year", config.year)
        json.put("hardwareVersion", config.hardwareVersion)
        json.put("softwareVersion", config.softwareVersion)
        return json.toString()
    }

    private fun vehicleConfigFromJson(json: String): VehicleConfig {
        val obj = JSONObject(json)
        return VehicleConfig(
            vin = obj.optString("vin").ifEmpty { null },
            model = obj.optString("model", "C11"),
            trim = obj.optString("trim").ifEmpty { null },
            year = obj.optInt("year", 2024),
            hardwareVersion = obj.optString("hardwareVersion", "SA8295P"),
            softwareVersion = obj.optString("softwareVersion", "3.21+"),
        )
    }
}
