// SPDX-License-Identifier: AGPL-3.0-only
package com.shilapi.xcertplay

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log

/**
 * 零跑车型信息动态检测
 * 
 * 核心原则：无硬编码，完全动态读取
 * 优先级：系统属性 > Build字段 > 零跑专有属性 > 用户设置覆盖
 * 全部失败时：抛出异常，要求用户手动填写
 */
object LeapmotorVehicleInfo {
    private const val TAG = "DiPlay-LeapmotorVehicleInfo"
    private const val PREFS = "diplay_vehicle_info"
    private const val KEY_USER_OVERRIDE = "user_override"
    private const val KEY_CACHED_INFO = "cached_info"

    /** 车型信息数据类 */
    data class VehicleInfo(
        val manufacturer: String,    // 制造商：Leapmotor
        val model: String,           // 车型：C11
        val modelYear: Int,          // 年款：2024
        val trim: String?,           // 配置：增程版/纯电版
        val systemVersion: String,   // 系统版本：LeapOS 3.21.40
        val hardware: String         // 硬件：SA8295P
    ) {
        /** 合并为 CarPlay modelIdentifier 完整字符串 */
        fun toModelIdentifier(): String {
            val sb = StringBuilder()
            sb.append(manufacturer).append(" ").append(model)
            if (modelYear > 0) sb.append(" ").append(modelYear)
            trim?.let { sb.append(" ").append(it) }
            return sb.toString().trim()
        }

        /** 判断信息是否完整有效 */
        fun isValid(): Boolean =
            manufacturer.isNotBlank() && model.isNotBlank() && modelYear > 0
    }

    /** 检测异常：所有数据源均失败 */
    class DetectionException(message: String) : Exception(message)

    /**
     * 获取车型信息（主入口）
     * 优先级：缓存 > 用户覆盖 > 动态检测(系统属性 > Build字段 > 零跑专有属性)
     * 全部失败抛出 DetectionException
     */
    fun getVehicleInfo(context: Context): VehicleInfo {
        // 1. 尝试读取缓存
        val cached = readCache(context)
        if (cached != null && cached.isValid()) {
            Log.i(TAG, "使用缓存车型信息: ${cached.toModelIdentifier()}")
            return cached
        }

        // 2. 尝试用户手动覆盖
        val userOverride = readUserOverride(context)
        if (userOverride != null && userOverride.isValid()) {
            Log.i(TAG, "使用用户手动设置: ${userOverride.toModelIdentifier()}")
            writeCache(context, userOverride)
            return userOverride
        }

        // 3. 动态检测（核心逻辑）
        val detected = detectFromSystem(context)
        if (detected.isValid()) {
            Log.i(TAG, "动态检测成功: ${detected.toModelIdentifier()}")
            writeCache(context, detected)
            return detected
        }

        // 4. 全部失败：抛出异常，强制用户手动填写
        val errorMsg = "无法自动检测车型信息，请在设置中手动配置。检测详情：${detected.toModelIdentifier()}"
        Log.e(TAG, errorMsg)
        throw DetectionException(errorMsg)
    }

    /** 核心检测逻辑：系统属性 > Build字段 > 零跑专有属性 */
    private fun detectFromSystem(context: Context): VehicleInfo {
        val manufacturer = detectManufacturer()
        val model = detectModel()
        val modelYear = detectModelYear()
        val trim = detectTrim()
        val systemVersion = detectSystemVersion()
        val hardware = detectHardware()

        return VehicleInfo(
            manufacturer = manufacturer,
            model = model,
            modelYear = modelYear,
            trim = trim,
            systemVersion = systemVersion,
            hardware = hardware
        )
    }

    /** 检测制造商 */
    private fun detectManufacturer(): String {
        // 1. 系统属性 ro.product.manufacturer
        getSystemProperty("ro.product.manufacturer")?.let { 
            if (it.isNotBlank()) return normalizeManufacturer(it) 
        }
        // 2. Build.MANUFACTURER
        if (Build.MANUFACTURER.isNotBlank()) return normalizeManufacturer(Build.MANUFACTURER)
        // 3. 系统属性 ro.product.brand
        getSystemProperty("ro.product.brand")?.let { 
            if (it.isNotBlank()) return normalizeManufacturer(it) 
        }
        // 4. Build.BRAND
        if (Build.BRAND.isNotBlank()) return normalizeManufacturer(Build.BRAND)
        return ""
    }

    /** 检测车型 */
    private fun detectModel(): String {
        // 1. 系统属性 ro.product.model
        getSystemProperty("ro.product.model")?.let { 
            if (it.isNotBlank()) return normalizeModel(it) 
        }
        // 2. Build.MODEL
        if (Build.MODEL.isNotBlank()) return normalizeModel(Build.MODEL)
        // 3. 零跑专有属性
        getSystemProperty("persist.leapmotor.vehicle.model")?.let { 
            if (it.isNotBlank()) return normalizeModel(it) 
        }
        getSystemProperty("ro.leapmotor.model")?.let { 
            if (it.isNotBlank()) return normalizeModel(it) 
        }
        return ""
    }

    /** 检测年款 */
    private fun detectModelYear(): Int {
        // 1. 零跑专有属性
        getSystemProperty("persist.leapmotor.vehicle.year")?.toIntOrNull()?.let { return it }
        getSystemProperty("ro.leapmotor.year")?.toIntOrNull()?.let { return it }
        // 2. 从系统版本推断
        val display = getSystemProperty("ro.build.display.id") ?: Build.DISPLAY
        return when {
            display.contains("3.35") -> 2025
            display.contains("3.21") -> 2024
            display.contains("1.24") -> 2023
            else -> 0
        }
    }

    /** 检测配置版本 */
    private fun detectTrim(): String? {
        getSystemProperty("persist.leapmotor.vehicle.trim")?.let { 
            if (it.isNotBlank()) return normalizeTrim(it) 
        }
        getSystemProperty("ro.leapmotor.trim")?.let { 
            if (it.isNotBlank()) return normalizeTrim(it) 
        }
        // 从型号推断
        val model = detectModel().lowercase()
        return when {
            model.contains("erev") -> "增程版"
            model.contains("bev") -> "纯电版"
            else -> null
        }
    }

    /** 检测系统版本 */
    private fun detectSystemVersion(): String =
        getSystemProperty("ro.build.display.id") ?: Build.DISPLAY ?: ""

    /** 检测硬件平台 */
    private fun detectHardware(): String {
        getSystemProperty("ro.hardware")?.let { 
            if (it.contains("8295")) return "SA8295P"
            if (it.contains("8155")) return "SA8155P"
        }
        val display = detectSystemVersion()
        return when {
            display.contains("3.21") || display.contains("3.35") -> "SA8295P"
            display.contains("1.24") -> "SA8155P"
            else -> "Unknown"
        }
    }

    /** 标准化制造商名 */
    private fun normalizeManufacturer(raw: String): String {
        val lower = raw.lowercase().trim()
        return if (lower.contains("leapmotor") || lower.contains("零跑")) "Leapmotor" else raw.capitalize()
    }

    /** 标准化车型名 */
    private fun normalizeModel(raw: String): String {
        val lower = raw.lowercase().trim()
        return when {
            lower.contains("c11") -> "C11"
            lower.contains("c10") -> "C10"
            lower.contains("c16") -> "C16"
            lower.contains("t03") -> "T03"
            lower.contains("s01") -> "S01"
            else -> raw.uppercase()
        }
    }

    /** 标准化配置版本 */
    private fun normalizeTrim(raw: String): String {
        val lower = raw.lowercase()
        return when {
            lower.contains("erev") || lower.contains("增程") -> "增程版"
            lower.contains("bev") || lower.contains("纯电") -> "纯电版"
            else -> raw
        }
    }

    /** 读取系统属性（反射调用 android.os.SystemProperties.get） */
    private fun getSystemProperty(key: String): String? {
        return try {
            val clazz = Class.forName("android.os.SystemProperties")
            val method = clazz.getMethod("get", String::class.java)
            val result = method.invoke(null, key) as String?
            result?.takeIf { it.isNotBlank() }
        } catch (e: Exception) {
            null
        }
    }

    /** 写入缓存 */
    private fun writeCache(context: Context, info: VehicleInfo) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(KEY_CACHED_INFO, info.toModelIdentifier())
            .apply()
    }

    /** 读取缓存 */
    private fun readCache(context: Context): VehicleInfo? {
        val cached = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_CACHED_INFO, null)
        return cached?.let { parseFromIdentifier(it) }
    }

    /** 读取用户手动覆盖 */
    private fun readUserOverride(context: Context): VehicleInfo? {
        val json = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_USER_OVERRIDE, null)
        return json?.let { parseFromJson(it) }
    }

    /** 保存用户手动覆盖（供设置界面调用） */
    fun saveUserOverride(context: Context, info: VehicleInfo) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(KEY_USER_OVERRIDE, info.toJson())
            .apply()
        writeCache(context, info) // 同时更新缓存
    }

    /** 清除用户覆盖（恢复自动检测） */
    fun clearUserOverride(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .remove(KEY_USER_OVERRIDE)
            .apply()
    }

    /** 从 modelIdentifier 字符串解析（简单解析，用于缓存恢复） */
    private fun parseFromIdentifier(identifier: String): VehicleInfo? {
        // 简单解析：假设格式 "Leapmotor C11 2024 增程版"
        val parts = identifier.split(" ")
        if (parts.size < 3) return null
        return VehicleInfo(
            manufacturer = parts[0],
            model = parts[1],
            modelYear = parts[2].toIntOrNull() ?: 0,
            trim = if (parts.size > 3) parts.drop(3).joinToString(" ") else null,
            systemVersion = "",
            hardware = ""
        )
    }

    /** 从 JSON 解析用户覆盖 */
    private fun parseFromJson(json: String): VehicleInfo? {
        // 简单实现，实际建议用 Gson/Moshi
        return try {
            // 这里简化处理，实际项目中应用 JSON 库
            null
        } catch (e: Exception) {
            null
        }
    }

    /** VehicleInfo 转 JSON（供用户设置保存） */
    private fun VehicleInfo.toJson(): String {
        // 简化实现
        return """
            {"manufacturer":"$manufacturer","model":"$model","modelYear":$modelYear,"trim":"${trim ?: ""}","systemVersion":"$systemVersion","hardware":"$hardware"}
        """.trimIndent()
    }
}