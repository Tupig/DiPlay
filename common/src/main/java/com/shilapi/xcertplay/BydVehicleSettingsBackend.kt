// SPDX-License-Identifier: AGPL-3.0-only
package com.shilapi.xcertplay

import android.content.Context

/**
 * 零跑车辆设置后端接口
 * 替代 BydVehicleSettingsBackend - 零跑无 ADB，通过系统 API 访问车辆数据
 */
internal interface LeapmotorVehicleSettingsBackend {
    fun check(context: Context, mayAsk: Boolean): LeapmotorVehicleAccess.Status
    fun checkState(context: Context, mayAsk: Boolean): LeapmotorVehicleAccess.State
    fun probe(context: Context, persist: Boolean): LeapmotorVehicleProbeOutcome
    fun execute(name: String, block: () -> Unit)
}

internal object AndroidLeapmotorVehicleSettingsBackend : LeapmotorVehicleSettingsBackend {
    override fun check(context: Context, mayAsk: Boolean) = LeapmotorVehicleAccess.getStatus(context, mayAsk)
    
    override fun checkState(context: Context, mayAsk: Boolean) = LeapmotorVehicleAccess.getState(context, mayAsk)
    
    override fun probe(context: Context, persist: Boolean) =
        LeapmotorVehicleCapabilityProbe.probe(context, persist)
    
    override fun execute(name: String, block: () -> Unit) {
        Thread(block, name).start()
    }
}

/** Replaced only by Robolectric tests; production always uses [AndroidLeapmotorVehicleSettingsBackend]. */
internal object LeapmotorVehicleSettingsBackendProvider {
    @Volatile var current: LeapmotorVehicleSettingsBackend = AndroidLeapmotorVehicleSettingsBackend

    fun reset() {
        current = AndroidLeapmotorVehicleSettingsBackend
    }
}

// 兼容旧代码
@Deprecated("使用 LeapmotorVehicleSettingsBackend")
typealias BydVehicleSettingsBackend = LeapmotorVehicleSettingsBackend
@Deprecated("使用 AndroidLeapmotorVehicleSettingsBackend")
typealias AndroidBydVehicleSettingsBackend = AndroidLeapmotorVehicleSettingsBackend
@Deprecated("使用 LeapmotorVehicleSettingsBackendProvider")
typealias BydVehicleSettingsBackendProvider = LeapmotorVehicleSettingsBackendProvider