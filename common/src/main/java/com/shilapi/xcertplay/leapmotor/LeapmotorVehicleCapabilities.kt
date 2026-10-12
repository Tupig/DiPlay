// SPDX-License-Identifier: AGPL-3.0-only
package com.shilapi.xcertplay

import android.content.Context

/**
 * 零跑车辆能力描述
 * 
 * 替代 BydVehicleCapabilities - 描述零跑车机支持的 CarPlay 功能
 * 基于零跑 C11 2024 (8295P) 硬件规格，无需 ADB 探测
 */
class LeapmotorVehicleCapabilities(
    /** 是否支持仪表盘地图投屏 */
    val clusterMap: Boolean = true,
    
    /** 是否支持副驾娱乐屏投屏 */
    val copilotDisplay: Boolean = true,
    
    /** 是否支持车辆数据读取（Car API） */
    val vehicleData: Boolean = true,
    
    /** 是否有 USB Host 端口 */
    val usbHostPort: Boolean = true,
    
    /** 是否支持 Wi-Fi Direct */
    val wifiDirect: Boolean = true,
    
    /** 是否支持 5GHz 热点 */
    val wifi5GHz: Boolean = true,
    
    /** 是否支持蓝牙电话音频路由 */
    val bluetoothPhoneAudio: Boolean = true,
    
    /** 是否支持 CarPlay 仪表盘投屏 */
    val carPlayClusterDisplay: Boolean = true,
    
    /** 是否支持 CarPlay 副驾屏投屏 */
    val carPlayCopilotDisplay: Boolean = true,
    
    /** 支持的最大视频分辨率宽度 */
    val maxVideoWidth: Int = 2560,
    
    /** 支持的最大视频分辨率高度 */
    val maxVideoHeight: Int = 1600,
    
    /** 支持的最大帧率 */
    val maxFps: Int = 60,
    
    /** 是否支持 HEVC 硬解 */
    val hevcHardwareDecoder: Boolean = true,
    
    /** 是否支持低延迟解码 */
    val lowLatencyDecoder: Boolean = true,
    
    /** 是否支持直接视频输出 */
    val directVideoOutput: Boolean = true,
    
    /** 车辆品牌 */
    val oem: String = "Leapmotor",
    
    /** 车型系列 */
    val modelLine: String = "C11",
    
    /** 年款 */
    val modelYear: Int = 2024,
    
    /** 芯片平台 */
    val soc: String = "SA8295P",
    
    /** 系统版本 */
    val systemVersion: String = "3.21+",
) {
    /** 转换为 CarCapabilities（兼容现有代码） */
    fun toCarCapabilities(): CarCapabilities {
        return CarCapabilities(
            clusterDisplay = clusterMap,
            copilotDisplay = copilotDisplay,
            vehicleData = vehicleData,
            usbHostPort = usbHostPort,
            wifiDirect = wifiDirect,
        )
    }

    /** 获取能力摘要（用于诊断报告） */
    fun getCapabilitiesSummary(): String {
        return buildString {
            appendLine("零跑 C11 2024 能力:")
            appendLine("  仪表盘地图: $clusterMap")
            appendLine("  副驾屏: $copilotDisplay")
            appendLine("  车辆数据: $vehicleData")
            appendLine("  USB Host: $usbHostPort")
            appendLine("  Wi-Fi Direct: $wifiDirect")
            appendLine("  5GHz热点: $wifi5GHz")
            appendLine("  蓝牙电话: $bluetoothPhoneAudio")
            appendLine("  CarPlay仪表盘: $carPlayClusterDisplay")
            appendLine("  CarPlay副驾: $carPlayCopilotDisplay")
            appendLine("  最大分辨率: ${maxVideoWidth}x${maxVideoHeight} @${maxFps}fps")
            appendLine("  HEVC硬解: $hevcHardwareDecoder")
            appendLine("  低延迟解码: $lowLatencyDecoder")
            appendLine("  直接视频输出: $directVideoOutput")
            appendLine("  芯片: $soc")
            appendLine("  系统: $systemVersion")
        }
    }

    /** 创建默认实例（2024 款 C11 标配） */
    companion object {
        fun createDefault(): LeapmotorVehicleCapabilities = LeapmotorVehicleCapabilities()
        
        /** 根据系统版本创建（未来扩展） */
        fun createForSystemVersion(systemVersion: String): LeapmotorVehicleCapabilities {
            return when {
                systemVersion.contains("3.35") -> LeapmotorVehicleCapabilities(
                    systemVersion = "3.35+",
                )
                systemVersion.contains("3.21") -> LeapmotorVehicleCapabilities(
                    systemVersion = "3.21+",
                )
                else -> LeapmotorVehicleCapabilities()
            }
        }
    }
}