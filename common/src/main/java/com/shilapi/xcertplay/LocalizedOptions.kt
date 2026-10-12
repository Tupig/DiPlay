// SPDX-License-Identifier: AGPL-3.0-only
package com.shilapi.xcertplay

import android.content.Context
import com.shilapi.xcertplay.airplay.CarPlaySize
import com.shilapi.xcertplay.host.R
import com.shilapi.xcertplay.orchestration.ManualHotspotValidation
import com.shilapi.xcertplay.transport.EvChargingConnectors

internal fun CarPlaySize.localizedLabel(context: Context): String = context.getString(when (this) {
    CarPlaySize.LARGE -> R.string.option_size_large
    CarPlaySize.MEDIUM -> R.string.option_size_medium
    CarPlaySize.SMALL -> R.string.option_size_small
})

internal fun DiLink51ClusterLayout.Theme.localizedLabel(context: Context): String = context.getString(when (this) {
    DiLink51ClusterLayout.Theme.SCENARIO -> R.string.option_theme_scenario
    DiLink51ClusterLayout.Theme.MAP -> R.string.option_theme_map
    DiLink51ClusterLayout.Theme.SIMPLE -> R.string.option_theme_simple
})

internal fun DiLink51ClusterLayout.Contrast.localizedLabel(context: Context): String = context.getString(when (this) {
    DiLink51ClusterLayout.Contrast.DEFAULT -> R.string.option_contrast_default
    DiLink51ClusterLayout.Contrast.LIGHT -> R.string.option_contrast_light
    DiLink51ClusterLayout.Contrast.DARK -> R.string.option_contrast_dark
})

/** 零跑仪表盘导航模式 */
enum class LeapmotorClusterNaviMode {
    OFF,
    TURN_ON_BY_NAVI,
    SMALL,
    FULL,
}

internal fun LeapmotorClusterNaviMode.localizedLabel(context: Context): String = context.getString(when (this) {
    LeapmotorClusterNaviMode.OFF -> R.string.navi_mode_off
    LeapmotorClusterNaviMode.TURN_ON_BY_NAVI -> R.string.navi_mode_turn_on_by_navi
    LeapmotorClusterNaviMode.SMALL -> R.string.navi_mode_small
    LeapmotorClusterNaviMode.FULL -> R.string.navi_mode_full
})

internal fun EvChargingConnectors.localizedLabel(context: Context): String = context.getString(when (this) {
    EvChargingConnectors.CCS2_TYPE2 -> R.string.connectors_ccs2_type2
    EvChargingConnectors.GB_T -> R.string.connectors_gb_t
    EvChargingConnectors.CCS1_J1772 -> R.string.connectors_ccs1_j1772
})

internal fun ManualHotspotValidation.Error.messageResource(): Int = when (this) {
    ManualHotspotValidation.Error.EMPTY_NAME -> R.string.hotspot_error_empty_name
    ManualHotspotValidation.Error.LONG_NAME -> R.string.hotspot_error_long_name
    ManualHotspotValidation.Error.INVALID_CHARACTER -> R.string.hotspot_error_invalid_character
    ManualHotspotValidation.Error.PASSWORD_LENGTH -> R.string.hotspot_error_password_length
}

/** 零跑原车仪表盘保持模式 */
enum class LeapmotorOemClusterHold {
    OFF,
    COMPONENT,
    PACKAGE,
}

internal fun LeapmotorOemClusterHold.localizedLabel(context: Context): String = context.getString(when (this) {
    LeapmotorOemClusterHold.OFF -> R.string.oem_cluster_hold_off
    LeapmotorOemClusterHold.COMPONENT -> R.string.oem_cluster_hold_component
    LeapmotorOemClusterHold.PACKAGE -> R.string.oem_cluster_hold_package
})

// 兼容旧代码
@Deprecated("使用 LeapmotorClusterNaviMode")
typealias BydClusterNaviMode = LeapmotorClusterNaviMode

@Deprecated("使用 LeapmotorOemClusterHold")
typealias BydOemClusterHold = LeapmotorOemClusterHold