// SPDX-License-Identifier: AGPL-3.0-only
package com.shilapi.xcertplay

import android.content.Context
import android.os.Build
import android.util.Log

/**
 * 零跑车辆访问层
 *
 * 零跑 C11 无公开 Car API（android.car 不在编译类路径，车机也未开放 CarPropertyManager）。
 * 车速、里程等车辆数据的读取在 Phase 5（#8）经 ADB 通道实现；当前提供编译可用的骨架
 * 与系统属性读取。
 */
object LeapmotorVehicleAccess {
    private const val TAG = "DiPlay-LeapmotorVehicleAccess"

    /** 车辆数据访问状态 */
    enum class Status {
        UNAVAILABLE,    // 车辆接口不可用
        READY,          // 可读取数据
        ERROR           // 错误状态
    }

    /** 车辆基础信息 */
    data class VehicleInfo(
        val vin: String? = null,
        val model: String? = null,
        val trim: String? = null,
        val year: Int? = null,
        val hardwareVersion: String? = null,
        val softwareVersion: String? = null,
    )

    /** 读取车辆基础信息（通过系统属性） */
    fun getVehicleInfo(context: Context): VehicleInfo {
        return try {
            VehicleInfo(
                vin = getSystemProperty("persist.leapmotor.vehicle.vin") ?: getSystemProperty("ro.leapmotor.vin"),
                model = getSystemProperty("persist.leapmotor.vehicle.model") ?: getSystemProperty("ro.leapmotor.model"),
                trim = getSystemProperty("persist.leapmotor.vehicle.trim") ?: getSystemProperty("ro.leapmotor.trim"),
                year = getSystemProperty("persist.leapmotor.vehicle.year")?.toIntOrNull()
                    ?: getSystemProperty("ro.leapmotor.year")?.toIntOrNull(),
                hardwareVersion = getSystemProperty("ro.hardware") ?: getSystemProperty("ro.product.device"),
                softwareVersion = getSystemProperty("ro.build.display.id") ?: Build.DISPLAY,
            )
        } catch (e: Exception) {
            Log.w(TAG, "读取车辆信息失败", e)
            VehicleInfo()
        }
    }

    /** 读取实时车速（km/h）；Phase 5（#8）经 ADB 通道实现。 */
    fun getVehicleSpeed(context: Context): Float? {
        Log.d(TAG, "车速读取待 Phase 5（#8）经 ADB 通道实现")
        return null
    }

    /** 读取里程（km）；Phase 5（#8）经 ADB 通道实现。 */
    fun getOdometer(context: Context): Long? = null

    /** 读取燃油/电量百分比；Phase 5（#8）经 ADB 通道实现。 */
    fun getFuelLevel(context: Context): Float? = null

    /** 读取档位；Phase 5（#8）经 ADB 通道实现。 */
    fun getGear(context: Context): Int? = null

    /** 检查车辆数据接口是否可用；Phase 5 接入 ADB 通道后返回实际状态。 */
    fun isAvailable(context: Context): Boolean = false

    /** 读取系统属性 */
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

    /** 获取访问状态描述 */
    fun getStatusDescription(context: Context): String {
        return if (isAvailable(context)) {
            "车辆数据接口可用"
        } else {
            "车辆数据接口待 Phase 5 接入（零跑经 ADB 读取）"
        }
    }
}
