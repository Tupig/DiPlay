// SPDX-License-Identifier: AGPL-3.0-only
package com.shilapi.xcertplay

import android.content.Context
import android.os.Build
import android.util.Log

/**
 * 零跑车辆访问层
 *
 * 零跑 C11 无公开 Car API（android.car 不在编译类路径，车机也未开放 CarPropertyManager）。
 * 车速等车辆数据经车机本机 ADB 通道读取（#8，Phase 5）：命令模板由真机标定后存入
 * [LeapmotorVehicleDataStore]，未标定或通道不可用时一律返回 null 优雅降级。
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

    /** 车速经本机 ADB 通道读取（#8）；adbd 不可达时 30 s 退避、行驶中永不弹授权框。 */
    private val adbShell = LeapmotorAdbShell(TAG)

    @Volatile private var speedCommandMissingLogged = false

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

    /**
     * 读取实时车速（km/h），经本机 ADB 通道（#8）。
     * 车速命令需真机标定（[LeapmotorVehicleDataStore.saveAdbSpeedCommand]，探测步骤见
     * docs/LEAPMOTOR.md）：未标定、adbd 不可达或输出无法解析时返回 null，上层优雅降级。
     */
    fun getVehicleSpeed(context: Context): Float? {
        val command = LeapmotorVehicleDataStore(context).getAdbSpeedCommand()
        if (command == null) {
            if (!speedCommandMissingLogged) {
                speedCommandMissingLogged = true
                Log.i(TAG, "车速命令未标定，车速读取降级为不可用（标定步骤见 docs/LEAPMOTOR.md）")
            }
            return null
        }
        return parseSpeed(adbShell.run(context, command))
    }

    /** 从车速命令输出中取第一个数字；无法解析返回 null。 */
    internal fun parseSpeed(output: String?): Float? =
        output?.trim()?.let { Regex("-?\\d+(?:\\.\\d+)?").find(it)?.value?.toFloatOrNull() }

    /** 读取里程（km）；待真机标定车速命令后按同一范式扩展（#8）。 */
    fun getOdometer(context: Context): Long? = null

    /** 读取燃油/电量百分比；待真机标定后按同一范式扩展（#8）。 */
    fun getFuelLevel(context: Context): Float? = null

    /** 读取档位；待真机标定后按同一范式扩展（#8）。 */
    fun getGear(context: Context): Int? = null

    /** 车速命令已标定即视为车辆数据接口可用（#8）。 */
    fun isAvailable(context: Context): Boolean =
        LeapmotorVehicleDataStore(context).getAdbSpeedCommand() != null

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
            "车辆数据接口可用（车速命令已标定，经 ADB 通道读取）"
        } else {
            "车速命令未标定（探测步骤见 docs/LEAPMOTOR.md），车速等数据降级为不可用"
        }
    }
}
