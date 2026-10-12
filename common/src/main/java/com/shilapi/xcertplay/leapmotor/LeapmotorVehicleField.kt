// SPDX-License-Identifier: AGPL-3.0-only
package com.shilapi.xcertplay

/**
 * 零跑车辆数据字段定义
 * 
 * 替代 BydVehicleField - 定义零跑车辆可读取的数据字段
 * 基于 Android Car API (Vehicle Property IDs) 标准
 */
object LeapmotorVehicleField {
    /** 字段类型 */
    enum class Type {
        INT32, INT64, FLOAT, BOOLEAN, STRING, BYTES
    }

    /** 车辆数据字段 */
    data class Field(
        val id: Int,                    // CarPropertyIds 常量值
        val name: String,               // 字段名称
        val type: Type,                 // 数据类型
        val unit: String,               // 单位
        val description: String,        // 描述
        val minValue: Number? = null,   // 最小值
        val maxValue: Number? = null,   // 最大值
    )

    /** 零跑支持的标准车辆字段（基于 Android Car API） */
    val SUPPORTED_FIELDS = listOf(
        // 车速相关
        Field(android.car.hardware.property.CarPropertyIds.PERF_VEHICLE_SPEED, "vehicle_speed", Type.FLOAT, "km/h", "车辆速度"),
        Field(android.car.hardware.property.CarPropertyIds.PERF_ODOMETER, "odometer", Type.INT64, "km", "总里程"),
        
        // 燃油/电量
        Field(android.car.hardware.property.CarPropertyIds.FUEL_LEVEL_LOW, "fuel_level", Type.FLOAT, "%", "燃油/电量百分比"),
        Field(android.car.hardware.property.CarPropertyIds.FUEL_CAPACITY, "fuel_capacity", Type.FLOAT, "L", "油箱/电池容量"),
        Field(android.car.hardware.property.CarPropertyIds.FUEL_TYPE, "fuel_type", Type.INT32, "", "燃料类型(0=汽油,1=柴油,2=电,3=混动)"),
        
        // 车门状态
        Field(android.car.hardware.property.CarPropertyIds.DOOR_LOCK, "door_lock", Type.BOOLEAN, "", "车门锁状态"),
        Field(android.car.hardware.property.CarPropertyIds.DRIVER_DOOR_OPEN, "driver_door", Type.BOOLEAN, "", "驾驶员车门"),
        Field(android.car.hardware.property.CarPropertyIds.PASSENGER_DOOR_OPEN, "passenger_door", Type.BOOLEAN, "", "副驾车门"),
        Field(android.car.hardware.property.CarPropertyIds.REAR_LEFT_DOOR_OPEN, "rear_left_door", Type.BOOLEAN, "", "后左车门"),
        Field(android.car.hardware.property.CarPropertyIds.REAR_RIGHT_DOOR_OPEN, "rear_right_door", Type.BOOLEAN, "", "后右车门"),
        Field(android.car.hardware.property.CarPropertyIds.HOOD_OPEN, "hood_open", Type.BOOLEAN, "", "引擎盖"),
        Field(android.car.hardware.property.CarPropertyIds.TRUNK_OPEN, "trunk_open", Type.BOOLEAN, "", "后备箱"),
        
        // 窗户状态
        Field(android.car.hardware.property.CarPropertyIds.WINDOW_POS_DRIVER, "window_driver", Type.INT32, "%", "驾驶员车窗位置"),
        Field(android.car.hardware.property.CarPropertyIds.WINDOW_POS_PASSENGER, "window_passenger", Type.INT32, "%", "副驾车窗位置"),
        Field(android.car.hardware.property.CarPropertyIds.WINDOW_POS_REAR_LEFT, "window_rear_left", Type.INT32, "%", "后左车窗位置"),
        Field(android.car.hardware.property.CarPropertyIds.WINDOW_POS_REAR_RIGHT, "window_rear_right", Type.INT32, "%", "后右车窗位置"),
        
        // 齿轮/档位
        Field(android.car.hardware.property.CarPropertyIds.GEAR_SELECTION, "gear", Type.INT32, "", "当前档位(0=P,1=R,2=N,3=D,4=S,5=L)"),
        Field(android.car.hardware.property.CarPropertyIds.CURRENT_GEAR, "current_gear", Type.INT32, "", "当前挡位详细"),
        
        // 转向/车轮
        Field(android.car.hardware.property.CarPropertyIds.STEERING_ANGLE, "steering_angle", Type.FLOAT, "度", "方向盘转角"),
        Field(android.car.hardware.property.CarPropertyIds.WHEEL_TICK_FRONT_LEFT, "wheel_tick_fl", Type.INT64, "", "前左轮脉冲"),
        Field(android.car.hardware.property.CarPropertyIds.WHEEL_TICK_FRONT_RIGHT, "wheel_tick_fr", Type.INT64, "", "前右轮脉冲"),
        Field(android.car.hardware.property.CarPropertyIds.WHEEL_TICK_REAR_LEFT, "wheel_tick_rl", Type.INT64, "", "后左轮脉冲"),
        Field(android.car.hardware.property.CarPropertyIds.WHEEL_TICK_REAR_RIGHT, "wheel_tick_rr", Type.INT64, "", "后右轮脉冲"),
        
        // 环境信息
        Field(android.car.hardware.property.CarPropertyIds.ENV_OUTSIDE_TEMPERATURE, "outside_temp", Type.FLOAT, "℃", "外部温度"),
        Field(android.car.hardware.property.CarPropertyIds.HVAC_TEMPERATURE_SET, "hvac_temp_set", Type.FLOAT, "℃", "空调设定温度"),
        Field(android.car.hardware.property.CarPropertyIds.HVAC_FAN_SPEED, "hvac_fan_speed", Type.INT32, "", "风扇速度"),
        Field(android.car.hardware.property.CarPropertyIds.HVAC_AC_ON, "hvac_ac_on", Type.BOOLEAN, "", "空调开关"),
        Field(android.car.hardware.property.CarPropertyIds.HVAC_AUTO_ON, "hvac_auto", Type.BOOLEAN, "", "自动空调"),
        Field(android.car.hardware.property.CarPropertyIds.HVAC_DUAL_ON, "hvac_dual", Type.BOOLEAN, "", "双区空调"),
        
        // 灯光
        Field(android.car.hardware.property.CarPropertyIds.HEADLIGHTS_STATE, "headlights", Type.INT32, "", "大灯状态(0=关,1=近光,2=远光,3=自动)"),
        Field(android.car.hardware.property.CarPropertyIds.HIGH_BEAM_ON, "high_beam", Type.BOOLEAN, "", "远光灯"),
        Field(android.car.hardware.property.CarPropertyIds.FOG_LIGHTS_FRONT, "fog_front", Type.BOOLEAN, "", "前雾灯"),
        Field(android.car.hardware.property.CarPropertyIds.FOG_LIGHTS_REAR, "fog_rear", Type.BOOLEAN, "", "后雾灯"),
        Field(android.car.hardware.property.CarPropertyIds.DAYTIME_RUNNING_LIGHTS, "drl", Type.BOOLEAN, "", "日行灯"),
        Field(android.car.hardware.property.CarPropertyIds.TURN_SIGNAL_STATE, "turn_signal", Type.INT32, "", "转向灯(0=关,1=左,2=右,3=危险报警)"),
        Field(android.car.hardware.property.CarPropertyIds.BRAKE_LIGHTS_ON, "brake_lights", Type.BOOLEAN, "", "刹车灯"),
        Field(android.car.hardware.property.CarPropertyIds.HAZARD_LIGHTS_ON, "hazard_lights", Type.BOOLEAN, "", "危险报警灯"),
        
        // 雨刮/除雾
        Field(android.car.hardware.property.CarPropertyIds.WIPER_STATUS, "wiper_status", Type.INT32, "", "雨刮状态"),
        Field(android.car.hardware.property.CarPropertyIds.DEFROSTER_FRONT, "defroster_front", Type.BOOLEAN, "", "前窗除雾"),
        Field(android.car.hardware.property.CarPropertyIds.DEFROSTER_REAR, "defroster_rear", Type.BOOLEAN, "", "后窗除雾"),
        
        // 电池/高压系统（电动车特有）
        Field(android.car.hardware.property.CarPropertyIds.EV_BATTERY_LEVEL, "ev_battery_level", Type.FLOAT, "%", "高压电池电量"),
        Field(android.car.hardware.property.CarPropertyIds.EV_BATTERY_CAPACITY, "ev_battery_capacity", Type.FLOAT, "kWh", "电池容量"),
        Field(android.car.hardware.property.CarPropertyIds.EV_RANGE, "ev_range", Type.FLOAT, "km", "剩余续航"),
        Field(android.car.hardware.property.CarPropertyIds.EV_CHARGING_STATUS, "ev_charging_status", Type.INT32, "", "充电状态(0=未充,1=充电中,2=充满,3=错误)"),
        Field(android.car.hardware.property.CarPropertyIds.EV_CHARGE_PORT_OPEN, "ev_charge_port", Type.BOOLEAN, "", "充电口盖"),
        Field(android.car.hardware.property.CarPropertyIds.EV_PLUGGED_IN, "ev_plugged_in", Type.BOOLEAN, "", "已插枪"),
        
        // TPMS 胎压监测
        Field(android.car.hardware.property.CarPropertyIds.TIRE_PRESSURE_FRONT_LEFT, "tire_fl", Type.FLOAT, "kPa", "前左胎压"),
        Field(android.car.hardware.property.CarPropertyIds.TIRE_PRESSURE_FRONT_RIGHT, "tire_fr", Type.FLOAT, "kPa", "前右胎压"),
        Field(android.car.hardware.property.CarPropertyIds.TIRE_PRESSURE_REAR_LEFT, "tire_rl", Type.FLOAT, "kPa", "后左胎压"),
        Field(android.car.hardware.property.CarPropertyIds.TIRE_PRESSURE_REAR_RIGHT, "tire_rr", Type.FLOAT, "kPa", "后右胎压"),
        
        // 里程/行程
        Field(android.car.hardware.property.CarPropertyIds.TRIP_METER, "trip_meter", Type.FLOAT, "km", "行程里程"),
        Field(android.car.hardware.property.CarPropertyIds.TRIP_METER_B, "trip_meter_b", Type.FLOAT, "km", "行程B"),
    )

    /** 获取字段 ID 列表（用于 CarPropertyManager 批量注册） */
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