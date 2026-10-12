// SPDX-License-Identifier: AGPL-3.0-only
package com.shilapi.xcertplay

import android.content.Context
import android.util.Log

/**
 * 零跑 CarPlay 通话处理
 * 
 * 替代 BydCarPlayCall - 处理 CarPlay 通话状态在仪表盘/HUD 的显示
 * 零跑通过 CarPlay Cluster Display 标准协议显示，无需 ADB
 */
object LeapmotorCarPlayCall {
    private const val TAG = "DiPlay-LeapmotorCarPlayCall"

    /** 通话状态 */
    enum class CallState(
        val carPlayState: Int,  // 对应 CarPlay 通话状态
        val displayName: String
    ) {
        NONE(0, "无通话"),
        INCOMING(1, "来电"),
        OUTGOING(2, "去电"),
        ACTIVE(3, "通话中"),
        HOLD(4, "保持"),
        DISCONNECTED(5, "已挂断"),
        FAILED(6, "失败"),
    }

    @Volatile private var currentState = CallState.NONE
    @Volatile private var callerName: String? = null
    @Volatile private var callerNumber: String? = null
    @Volatile private var callDurationSeconds = 0
    @Volatile private var isClusterDisplayEnabled = true

    /** 更新通话状态（由 CarPlay 会话回调） */
    fun updateCallState(
        state: CallState,
        name: String? = null,
        number: String? = null,
        duration: Int = 0,
    ) {
        currentState = state
        callerName = name
        callerNumber = number
        callDurationSeconds = duration
        
        Log.i(TAG, "通话状态更新: $state, 名称: $name, 号码: $number, 时长: ${duration}s")
        
        // 同步到仪表盘显示
        if (isClusterDisplayEnabled && LeapmotorClusterBridge.getState() == LeapmotorClusterBridge.ClusterState.CONNECTED) {
            LeapmotorClusterBridge.sendCallState(
                state = state.carPlayState,
                callerName = name,
                callerNumber = number,
                durationSeconds = duration
            )
        }
    }

    /** 获取当前通话状态 */
    fun getCurrentState(): CallState = currentState

    /** 获取来电号码/名称 */
    fun getCallerInfo(): Pair<String?, String?> = callerName to callerNumber

    /** 获取通话时长 */
    fun getCallDuration(): Int = callDurationSeconds

    /** 设置仪表盘通话显示开关 */
    fun setClusterDisplayEnabled(enabled: Boolean) {
        isClusterDisplayEnabled = enabled
        Log.i(TAG, "仪表盘通话显示: ${if (enabled) "开启" else "关闭"}")
    }

    /** 检查是否有活跃通话 */
    fun hasActiveCall(): Boolean = currentState != CallState.NONE && currentState != CallState.DISCONNECTED

    /** 格式化通话时长 */
    fun formatDuration(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", mins, secs)
    }

    /** 获取状态描述（用于诊断报告） */
    fun getStatusDescription(): String {
        return "通话: ${currentState.displayName}, 来电: ${callerName ?: callerNumber ?: "未知"}, 时长: ${formatDuration(callDurationSeconds)}, 仪表显示: ${if (isClusterDisplayEnabled) "开" else "关"}"
    }

    /** 重置通话状态 */
    fun reset() {
        currentState = CallState.NONE
        callerName = null
        callerNumber = null
        callDurationSeconds = 0
    }
}