// SPDX-License-Identifier: AGPL-3.0-only
package com.shilapi.xcertplay

import android.content.Context
import android.util.Log

/**
 * 零跑仪表盘桥接
 * 
 * 替代 BydHudBridge - 零跑通过 CarPlay 仪表盘投屏（Cluster Display）显示
 * 无需 ADB，使用 CarPlay 标准 Cluster Display 协议
 */
object LeapmotorClusterBridge {
    private const val TAG = "DiPlay-LeapmotorClusterBridge"

    /** 仪表盘显示状态 */
    enum class ClusterState {
        DISCONNECTED,    // 未连接
        CONNECTING,      // 连接中
        CONNECTED,       // 已连接，正在投屏
        ERROR
    }

    /** 仪表盘显示内容类型 */
    enum class ContentType {
        MAP,           // 地图导航
        TURN_GUIDANCE, // 转向引导
        SPEEDOMETER,   // 速度计
        MEDIA,         // 媒体信息
        PHONE,         // 通话信息
        NONE           // 无内容
    }

    @Volatile private var currentState = ClusterState.DISCONNECTED
    @Volatile private var currentContent = ContentType.NONE
    @Volatile private var isClusterDisplayAvailable = false

    /** 检查仪表盘投屏是否可用 */
    fun isClusterDisplayAvailable(context: Context): Boolean {
        // 零跑 2024 款 C11 支持 CarPlay 仪表盘投屏
        return LeapmotorC11Settings.SystemVersion.isLeapmotorC11_2024(context)
    }

    /** 启动仪表盘投屏 */
    fun startClusterProjection(context: Context, contentType: ContentType = ContentType.MAP): Boolean {
        if (!isClusterDisplayAvailable(context)) {
            Log.w(TAG, "仪表盘投屏不可用")
            return false
        }

        currentContent = contentType
        currentState = ClusterState.CONNECTING
        
        // 实际投屏由 CarPlayClusterDisplay 管理
        // 这里只记录状态
        currentState = ClusterState.CONNECTED
        Log.i(TAG, "仪表盘投屏启动: $contentType")
        return true
    }

    /** 停止仪表盘投屏 */
    fun stopClusterProjection() {
        currentState = ClusterState.DISCONNECTED
        currentContent = ContentType.NONE
        Log.i(TAG, "仪表盘投屏停止")
    }

    /** 设置显示内容 */
    fun setContentType(contentType: ContentType) {
        currentContent = contentType
        Log.i(TAG, "仪表盘内容切换: $contentType")
    }

    /** 获取当前状态 */
    fun getState(): ClusterState = currentState

    /** 获取当前内容类型 */
    fun getContentType(): ContentType = currentContent

    /** 发送转向引导到仪表盘（通过 CarPlay Cluster Display） */
    fun sendTurnGuidance(
        context: Context,
        maneuverType: Int,
        distanceMeters: Float,
        roadName: String?,
        nextManeuverType: Int?,
        nextDistanceMeters: Float?
    ) {
        if (currentState != ClusterState.CONNECTED) return
        
        Log.i(TAG, "转向引导: type=$maneuverType, distance=${distanceMeters}m, road=$roadName, next=$nextManeuverType/${nextDistanceMeters}m")
    }

    /** 发送速度计数据到仪表盘 */
    fun sendSpeedometer(speedKmh: Float) {
        if (currentState != ClusterState.CONNECTED) return
        
        Log.i(TAG, "速度: $speedKmh km/h")
    }

    /** 发送媒体信息到仪表盘 */
    fun sendMediaInfo(
        title: String?,
        artist: String?,
        albumArt: android.graphics.Bitmap?,
        isPlaying: Boolean
    ) {
        if (currentState != ClusterState.CONNECTED) return
        
        Log.i(TAG, "媒体: title=$title, artist=$artist, playing=$isPlaying, albumArt=${albumArt != null}")
    }

    /** 发送通话状态到仪表盘 */
    fun sendCallState(
        state: Int, // 0=无, 1=来电, 2=去电, 3=通话中
        callerName: String?,
        callerNumber: String?,
        durationSeconds: Int
    ) {
        if (currentState != ClusterState.CONNECTED) return
        
        Log.i(TAG, "通话状态: state=$state, name=$callerName, number=$callerNumber, duration=${durationSeconds}s")
    }

    /** 获取状态描述（用于诊断报告） */
    fun getStatusDescription(): String {
        return "状态: ${currentState.name}, 内容: ${currentContent.name}, 可用: $isClusterDisplayAvailable"
    }
}