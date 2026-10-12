// SPDX-License-Identifier: AGPL-3.0-only
package com.shilapi.xcertplay

/**
 * 零跑车辆数据字段定义
 *
 * 替代 BydVehicleField——定义零跑车辆可读取的数据字段。
 * 零跑 C11 无公开 Car API（android.car 不在编译类路径），字段清单待 Phase 5（#8）
 * 真机 ADB 调试时按实际可读地址填充；当前提供编译可用的结构。
 */
object LeapmotorVehicleField {
    /** 字段类型 */
    enum class Type {
        INT32, INT64, FLOAT, BOOLEAN, STRING, BYTES
    }

    /** 车辆数据字段 */
    data class Field(
        val id: Int,                    // 字段 ID（ADB 读取地址）
        val name: String,               // 字段名称
        val type: Type,                 // 数据类型
        val unit: String,               // 单位
        val description: String,        // 描述
        val minValue: Number? = null,   // 最小值
        val maxValue: Number? = null,   // 最大值
    )

    /** 零跑支持的车辆字段；Phase 5（#8）真机 ADB 调试后填充。 */
    val SUPPORTED_FIELDS: List<Field> = emptyList()

    /** 获取字段 ID 列表（用于批量注册/读取） */
    val SUPPORTED_FIELD_IDS: List<Int> = SUPPORTED_FIELDS.map { it.id }

    /** 根据 ID 查找字段 */
    fun findById(id: Int): Field? = SUPPORTED_FIELDS.firstOrNull { it.id == id }

    /** 根据名称查找字段 */
    fun findByName(name: String): Field? = SUPPORTED_FIELDS.firstOrNull { it.name == name }

    /** 获取字段描述（用于诊断报告） */
    fun getFieldDescription(id: Int): String {
        return findById(id)?.let { "${it.name} (${it.unit}) - ${it.description}" } ?: "未知字段 ID: $id"
    }
}
