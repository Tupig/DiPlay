// SPDX-License-Identifier: AGPL-3.0-only
package com.shilapi.xcertplay

import android.content.Context
import android.util.Log

/**
 * 零跑 C11 仪表盘/导航输出
 * 
 * 替代 BydNavigationOutputs，适配零跑车机
 * 零跑无 ADB，仪表盘显示通过系统原生投屏 API 实现
 */
object LeapmotorNavigationOutputs {
    private const val TAG = "DiPlay-LeapmotorNavOut"

    /** 仪表盘是否有活跃路线 */
    var dashboardRouteActive = false
        private set

    /** 仪表盘显示的备注文本 */
    var dashboardNote: String? = null
        private set

    /** 在仪表盘显示简短提示 */
    fun dashboardNote(context: Context, text: String, source: Int = 0) {
        dashboardNote = text
        dashboardRouteActive = true
        Log.i(TAG, "仪表盘提示: $text")
        
        // 零跑通过系统投屏显示，无需 ADB
        // 实际显示由 CarPlay 仪表盘投屏处理
        try {
            // 尝试通过系统 API 发送到仪表盘
            // 这里留作扩展接口，实际实现依赖 CarPlay 仪表盘投屏
        } catch (e: Exception) {
            Log.w(TAG, "仪表盘提示发送失败", e)
        }
    }

    /** 清除仪表盘提示 */
    fun clearDashboardNote() {
        dashboardNote = null
        dashboardRouteActive = false
    }

    /** CarPlay 通话状态（用于方向盘按键判断） */
    fun carPlayCall(): Any? {
        // 通过 CarPlay 会话获取通话状态
        return CarPlayBackgroundSession.snapshot()?.controller?.let {
            // 这里需要根据实际 CarPlay 控制器 API 实现
            null
        }
    }
}