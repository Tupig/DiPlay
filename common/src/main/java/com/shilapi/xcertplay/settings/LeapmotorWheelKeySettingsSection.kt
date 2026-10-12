// SPDX-License-Identifier: AGPL-3.0-only
package com.shilapi.xcertplay.settings

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.shilapi.xcertplay.LeapmotorWheelKeys
import com.shilapi.xcertplay.LeapmotorWheelSettings
import com.shilapi.xcertplay.LeapmotorC11Settings
import com.shilapi.xcertplay.WheelKeyService
import com.shilapi.xcertplay.WheelZoomSettings
import com.shilapi.xcertplay.host.R
import kotlin.math.roundToInt

/**
 * 零跑 C11 方向盘按键设置界面
 * 
 * 提供：
 * 1. 预设选择（默认/备选）
 * 2. 按键学习模式
 * 3. 当前映射显示
 * 4. 功能开关（缩放、摇杆、Siri）
 * 5. 检测到的按键日志
 */
object LeapmotorWheelKeySettingsSection {

    private const val TAG = "DiPlay-LeapmotorWheelKeySettings"

    /** 创建零跑方向盘按键设置分类 */
    fun createLeapmotorWheelKeySection(
        context: Context,
        parent: LinearLayout,
        theme: SettingsTheme,
        onSettingsChanged: () -> Unit,
    ) {
        // 标题
        parent.addView(SettingsWidgets.createCategoryHeader(
            context, context.getString(R.string.leapmotor_wheel_keys_title), theme
        ))

        // 1. 预设选择
        addPresetSelector(context, parent, theme, onSettingsChanged)

        // 2. 功能开关
        addFeatureSwitches(context, parent, theme, onSettingsChanged)

        // 3. 当前按键映射显示
        addKeyMappingDisplay(context, parent, theme)

        // 4. 按键学习按钮
        addKeyLearningButtons(context, parent, theme, onSettingsChanged)

        // 5. 检测到的按键日志
        addDetectedKeysLog(context, parent, theme)

        // 6. 重置按钮
        addResetButton(context, parent, theme, onSettingsChanged)
    }

    /** 1. 预设选择器 */
    private fun addPresetSelector(
        context: Context,
        parent: LinearLayout,
        theme: SettingsTheme,
        onSettingsChanged: () -> Unit,
    ) {
        val currentPreset = LeapmotorWheelSettings.getPreset(context)
        val presets = LeapmotorWheelSettings.Preset.values()

        val result = SettingsWidgets.createChoiceRow(
            context,
            options = presets.map { it.name },
            labels = presets.map { it.name },
            checked = currentPreset.ordinal,
            theme = theme,
            onClick = { index ->
                val newPreset = presets[index]
                LeapmotorWheelSettings.setPreset(context, newPreset)
                LeapmotorWheelSettings.applyPresetToWheelZoomSettings(context)
                WheelKeyService.settingsChanged()
                onSettingsChanged()
                Log.i(TAG, "切换预设: ${newPreset.name}")
            },
        )
        parent.addView(result.container)

        // 预设说明
        val desc = TextView(context).apply {
            text = when (currentPreset) {
                LeapmotorWheelSettings.Preset.DEFAULT -> "默认预设：适配大部分配置的零跑 C11 方向盘"
                LeapmotorWheelSettings.Preset.ALTERNATIVE -> "备选预设：部分车型方向盘键值不同，如确认键为 DPAD_CENTER"
            }
            textSize = 13f
            setTextColor(theme.textSecondary)
            setPadding(theme.dp(context, 16), theme.dp(context, 4), theme.dp(context, 16), theme.dp(context, 12))
        }
        parent.addView(desc)
    }

    /** 2. 功能开关 */
    private fun addFeatureSwitches(
        context: Context,
        parent: LinearLayout,
        theme: SettingsTheme,
        onSettingsChanged: () -> Unit,
    ) {
        // 缩放功能
        addSwitchRow(context, parent, theme,
            title = context.getString(R.string.leapmotor_wheel_zoom_title),
            summary = context.getString(R.string.leapmotor_wheel_zoom_summary),
            checked = WheelZoomSettings.enabled(context),
            onChange = { checked ->
                WheelZoomSettings.setEnabled(context, checked)
                WheelKeyService.settingsChanged()
                onSettingsChanged()
            }
        )

        // 缩放行为
        addChoiceRow(context, parent, theme,
            title = context.getString(R.string.leapmotor_wheel_zoom_behaviour_title),
            summary = context.getString(R.string.leapmotor_wheel_zoom_behaviour_summary),
            options = arrayOf("切换模式", "定时模式"),
            labels = arrayOf("按模式键切换开关", "开启后 5 秒无操作自动关闭"),
            checked = WheelZoomSettings.behaviour(context).ordinal,
            onChange = { index ->
                WheelZoomSettings.setBehaviour(context, WheelZoomSettings.Behaviour.values()[index])
                WheelKeyService.settingsChanged()
                onSettingsChanged()
            }
        )

        // 摇杆功能
        addSwitchRow(context, parent, theme,
            title = context.getString(R.string.leapmotor_wheel_joystick_title),
            summary = context.getString(R.string.leapmotor_wheel_joystick_summary),
            checked = WheelZoomSettings.joystick(context),
            onChange = { checked ->
                WheelZoomSettings.setJoystick(context, checked)
                WheelKeyService.settingsChanged()
                onSettingsChanged()
            }
        )

        // 摇杆自动关闭
        addSwitchRow(context, parent, theme,
            title = context.getString(R.string.leapmotor_wheel_joystick_auto_off_title),
            summary = context.getString(R.string.leapmotor_wheel_joystick_auto_off_summary),
            checked = WheelZoomSettings.joystickAutoOff(context),
            onChange = { checked ->
                WheelZoomSettings.setJoystickAutoOff(context, checked)
                WheelKeyService.settingsChanged()
                onSettingsChanged()
            }
        )

        // Siri 按键
        addSwitchRow(context, parent, theme,
            title = context.getString(R.string.leapmotor_wheel_siri_title),
            summary = context.getString(R.string.leapmotor_wheel_siri_summary),
            checked = WheelZoomSettings.siriKey(context),
            onChange = { checked ->
                WheelZoomSettings.setSiriKey(context, checked)
                WheelKeyService.settingsChanged()
                onSettingsChanged()
            }
        )
    }

    /** 3. 当前按键映射显示 */
    private fun addKeyMappingDisplay(
        context: Context,
        parent: LinearLayout,
        theme: SettingsTheme,
    ) {
        val keyMap = LeapmotorWheelSettings.getKeyMap(context)

        val header = SettingsWidgets.createCategoryHeader(
            context, context.getString(R.string.leapmotor_wheel_current_mapping), theme
        )
        parent.addView(header)

        keyMap.forEach { (leapKey, wheelKey) ->
            val row = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(theme.dp(context, 16), theme.dp(context, 12), theme.dp(context, 16), theme.dp(context, 12))
            }

            val label = TextView(context).apply {
                text = leapKey.description
                textSize = 15f
                setTextColor(theme.textPrimary)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val value = TextView(context).apply {
                text = "KeyCode: ${wheelKey.code} / ScanCode: ${wheelKey.scanCode}"
                textSize = 13f
                setTextColor(theme.textSecondary)
                gravity = Gravity.END
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            }

            row.addView(label)
            row.addView(value)
            parent.addView(row)
        }
    }

    /** 4. 按键学习按钮 */
    private fun addKeyLearningButtons(
        context: Context,
        parent: LinearLayout,
        theme: SettingsTheme,
        onSettingsChanged: () -> Unit,
    ) {
        val header = SettingsWidgets.createCategoryHeader(
            context, context.getString(R.string.leapmotor_wheel_learning_title), theme
        )
        parent.addView(header)

        // 学习角色的按钮
        val roles = arrayOf(
            WheelZoomSettings.Role.MODE to context.getString(R.string.leapmotor_wheel_role_mode),
            WheelZoomSettings.Role.ZOOM_IN to context.getString(R.string.leapmotor_wheel_role_zoom_in),
            WheelZoomSettings.Role.ZOOM_OUT to context.getString(R.string.leapmotor_wheel_role_zoom_out),
            WheelZoomSettings.Role.JOYSTICK to context.getString(R.string.leapmotor_wheel_role_joystick),
            WheelZoomSettings.Role.PREVIOUS to context.getString(R.string.leapmotor_wheel_role_previous),
            WheelZoomSettings.Role.NEXT to context.getString(R.string.leapmotor_wheel_role_next),
            WheelZoomSettings.Role.SELECT to context.getString(R.string.leapmotor_wheel_role_select),
            WheelZoomSettings.Role.SIRI to context.getString(R.string.leapmotor_wheel_role_siri),
        )

        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(theme.dp(context, 16), theme.dp(context, 8), theme.dp(context, 16), theme.dp(context, 8))
        }

        roles.forEach { (role, label) ->
            val btn = SettingsWidgets.createButton(
                context,
                text = "学习: $label",
                theme = theme,
                onClick = {
                    startLearning(context, role, label, onSettingsChanged)
                },
            )
            container.addView(btn)
        }

        // 取消学习按钮
        val cancelBtn = SettingsWidgets.createButton(
            context,
            text = context.getString(R.string.leapmotor_wheel_cancel_learning),
            theme = theme,
            onClick = {
                WheelKeyService.cancelLearning()
                Toast.makeText(context, "已取消学习", Toast.LENGTH_SHORT).show()
            },
        )
        container.addView(cancelBtn)

        parent.addView(container)
    }

    /** 开始学习指定角色的按键 */
    private fun startLearning(
        context: Context,
        role: WheelZoomSettings.Role,
        label: String,
        onSettingsChanged: () -> Unit,
    ) {
        val success = WheelKeyService.learn(
            role = role,
            cancelled = { Log.i(TAG, "学习取消: $label") },
            refused = { takenRole ->
                Toast.makeText(context, "该按键已被 $takenRole 占用", Toast.LENGTH_SHORT).show()
            },
            done = { assignedRole, key ->
                runOnUiThread {
                    Toast.makeText(context, "$label 已设置为: ${key.code}/${key.scanCode}", Toast.LENGTH_LONG).show()
                    onSettingsChanged()
                }
            },
        )

        if (success) {
            Toast.makeText(context, "请在 10 秒内按下方向盘上要设为「$label」的按键", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "无法开始学习：服务未运行或功能未启用", Toast.LENGTH_SHORT).show()
        }
    }

    /** 5. 检测到的按键日志 */
    private fun addDetectedKeysLog(
        context: Context,
        parent: LinearLayout,
        theme: SettingsTheme,
    ) {
        val header = SettingsWidgets.createCategoryHeader(
            context, context.getString(R.string.leapmotor_wheel_detected_keys), theme
        )
        parent.addView(header)

        // 这里显示最近检测到的按键（需要在 WheelKeyService 中记录）
        // 简化版：显示说明
        val info = TextView(context).apply {
            text = "方向盘按键按下时会在此记录 KeyCode/ScanCode，用于确认映射关系。\n" +
                   "请在「设置 → 诊断 → 保存诊断报告」查看完整日志。"
            textSize = 13f
            setTextColor(theme.textSecondary)
            setPadding(theme.dp(context, 16), theme.dp(context, 8), theme.dp(context, 16), theme.dp(context, 12))
        }
        parent.addView(info)
    }

    /** 6. 重置按钮 */
    private fun addResetButton(
        context: Context,
        parent: LinearLayout,
        theme: SettingsTheme,
        onSettingsChanged: () -> Unit,
    ) {
        val btn = SettingsWidgets.createButton(
            context,
            text = context.getString(R.string.leapmotor_wheel_reset_title),
            theme = theme,
            onClick = {
                new AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.leapmotor_wheel_reset_title))
                    .setMessage("将重置所有方向盘按键映射为当前预设默认值，确定吗？")
                    .setPositiveButton("确定") { _, _ ->
                        LeapmotorWheelSettings.resetToDefault(context)
                        WheelKeyService.settingsChanged()
                        onSettingsChanged()
                        Toast.makeText(context, "已重置为默认预设", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("取消", null)
                    .show()
            },
        )
        parent.addView(btn)
    }

    // ===== 辅助方法 =====

    private fun addSwitchRow(
        context: Context,
        parent: LinearLayout,
        theme: SettingsTheme,
        title: String,
        summary: String,
        checked: Boolean,
        onChange: (Boolean) -> Unit,
    ) {
        val result = SettingsWidgets.createSwitchRow(
            context,
            title = title,
            summary = summary,
            checked = checked,
            theme = theme,
            onChange = onChange,
        )
        parent.addView(result.rowView)
    }

    private fun addChoiceRow(
        context: Context,
        parent: LinearLayout,
        theme: SettingsTheme,
        title: String,
        summary: String,
        options: Array<String>,
        labels: Array<String>,
        checked: Int,
        onChange: (Int) -> Unit,
    ) {
        val result = SettingsWidgets.createChoiceRow(
            context,
            options = options,
            labels = labels,
            checked = checked,
            theme = theme,
            onClick = onChange,
        )
        parent.addView(result.container)

        val desc = TextView(context).apply {
            text = labels[checked]
            textSize = 13f
            setTextColor(theme.textSecondary)
            setPadding(theme.dp(context, 16), theme.dp(context, 4), theme.dp(context, 16), theme.dp(context, 12))
        }
        parent.addView(desc)
    }

    /** 运行在 UI 线程 */
    private fun runOnUiThread(action: () -> Unit) {
        if (Thread.currentThread().isInstanceOf<android.os.Looper>()) {
            action()
        } else {
            android.os.Handler(android.os.Looper.getMainLooper()).post(action)
        }
    }
}