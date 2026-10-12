// SPDX-License-Identifier: AGPL-3.0-only
package com.shilapi.xcertplay

import android.content.Context
import android.util.Log

/**
 * 零跑车辆访问层
 * 
 * 替代 BydAdbAccess - 零跑无 ADB，通过系统 API / 车辆总线只读接口访问
 * 所有操作均不需要 ADB 权限
 */
object LeapmotorVehicleAccess {
    private const val TAG = "DiPlay-LeapmotorVehicleAccess"

    /** 车辆数据访问状态 */
    enum class Status {
        UNAVAILABLE,    // 车辆接口不可用
        READY,          // 可读取数据
        ERROR(msg: String) // 错误状态
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

    /** 读取实时车速（km/h） - 通过 Vehicle HAL / CarPropertyManager */
    fun getVehicleSpeed(context: Context): Float? {
        return try {
            // 尝试通过 CarPropertyManager 读取（需要 CAR_POWER 或 CAR_INFO 权限）
            val carPropertyManager = context.getSystemService(Context.CAR_SERVICE) as? android.car.Car
            carPropertyManager?.let { car ->
                val propertyManager = car.getCarPropertyManager()
                val speed = propertyManager?.getProperty(android.car.hardware.property.CarPropertyIds.PERF_VEHICLE_SPEED)
                speed?.let { it.value as Float / 100.0f } // 转换为 km/h
            }
        } catch (e: Exception) {
            Log.d(TAG, "车速读取不可用（需 CAR 权限或 Vehicle HAL 支持）", e)
            null
        }
    }

    /** 读取里程（km） */
    fun getOdometer(context: Context): Long? {
        return try {
            val carPropertyManager = context.getSystemService(Context.CAR_SERVICE) as? android.car.Car
            carPropertyManager?.let { car ->
                val propertyManager = car.getCarPropertyManager()
                propertyManager?.getProperty(android.car.hardware.property.CarPropertyIds.PERF_ODOMETER)?.value as? Long
            }
        } catch (e: Exception) {
            Log.d(TAG, "里程读取不可用", e)
            null
        }
    }

    /** 读取燃油/电量百分比 */
    fun getFuelLevel(context: Context): Float? {
        return try {
            val carPropertyManager = context.getSystemService(Context.CAR_SERVICE) as? android.car.Car
            carPropertyManager?.let { car ->
                val propertyManager = car.getCarPropertyManager()
                propertyManager?.getProperty(android.car.hardware.property.CarPropertyIds.FUEL_LEVEL_LOW)?.value as? Float
            }
        } catch (e: Exception) {
            null
        }
    }

    /** 读取档位 */
    fun getGear(context: Context): Int? {
        return try {
            val carPropertyManager = context.getSystemService(Context.CAR_SERVICE) as? android.car.Car
            carPropertyManager?.let { car ->
                val propertyManager = car.getCarPropertyManager()
                propertyManager?.getProperty(android.car.hardware.property.CarPropertyIds.GEAR_SELECTION)?.value as? Int
            }
        } catch (e: Exception) {
            null
        }
    }

    /** 检查车辆数据接口是否可用 */
    fun isAvailable(context: Context): Boolean {
        return try {
            val car = context.getSystemService(Context.CAR_SERVICE) as? android.car.Car
            car != null
        } catch (e: Exception) {
            false
        }
    }

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
            "车辆数据接口可用（Car API）"
        } else {
            "车辆数据接口不可用（零跑车机未开放 Car API 或无权限）"
        }
    }
}