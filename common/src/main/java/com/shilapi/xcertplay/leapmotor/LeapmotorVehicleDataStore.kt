// SPDX-License-Identifier: AGPL-3.0-only
package com.shilapi.xcertplay

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * 零跑车辆数据存储
 * 
 * 替代 BydVehicleFieldStore - 存储车辆配置、用户偏好、校准数据
 * 零跑无需探测，直接存储用户配置和校准参数
 */
class LeapmotorVehicleDataStore(context: Context) {
    private const val TAG = "DiPlay-LeapmotorVehicleDataStore"
    private const val PREFS = "diplay_leapmotor_vehicle_data"
    private const val KEY_CALIBRATION = "calibration"
    private const val KEY_USER_PREFERENCES = "user_preferences"
    private const val KEY_VEHICLE_CONFIG = "vehicle_config"
    private const val KEY_LAST_SYNC = "last_sync"

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    private val gson = Gson()

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
                gson.fromJson(json, CalibrationData::class.java)
            } catch (e: Exception) {
                Log.w(TAG, "解析校准数据失败", e)
                CalibrationData()
            }
        } else CalibrationData()
    }

    /** 保存校准数据 */
    private fun saveCalibration(calibration: CalibrationData) {
        prefs.edit()
            .putString(KEY_CALIBRATION, gson.toJson(calibration))
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
                gson.fromJson(json, UserPreferences::class.java)
            } catch (e: Exception) {
                Log.w(TAG, "解析用户偏好失败", e)
                UserPreferences()
            }
        } else UserPreferences()
    }

    /** 保存用户偏好 */
    fun saveUserPreferences(preferences: UserPreferences) {
        prefs.edit()
            .putString(KEY_USER_PREFERENCES, gson.toJson(preferences))
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
                gson.fromJson(json, VehicleConfig::class.java)
            } catch (e: Exception) {
                Log.w(TAG, "解析车辆配置失败", e)
                VehicleConfig()
            }
        } else VehicleConfig()
    }

    /** 保存车辆配置 */
    fun saveVehicleConfig(config: VehicleConfig) {
        prefs.edit()
            .putString(KEY_VEHICLE_CONFIG, gson.toJson(config))
            .apply()
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
            val map = gson.fromJson(json, object : TypeToken<Map<String, Any>>() {}.type)
            prefs.edit().apply {
                map["calibration"]?.let { putString(KEY_CALIBRATION, gson.toJson(it)) }
                map["userPreferences"]?.let { putString(KEY_USER_PREFERENCES, gson.toJson(it)) }
                map["vehicleConfig"]?.let { putString(KEY_VEHICLE_CONFIG, gson.toJson(it)) }
                map["lastSync"]?.let { putLong(KEY_LAST_SYNC, it.toString().toLong()) }
                apply()
            }
            Log.i(TAG, "车辆数据导入成功")
            true
        } catch (e: Exception) {
            Log.e(TAG, "车辆数据导入失败", e)
            false
        }
    }
}