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
import com.shilapi.xcertplay.wheel.LeapmotorWheelKeys
import com.shilapi.xcertplay.LeapmotorWheelSettings
import com.shilapi.xcertplay.leapmotor.LeapmotorC11Settings
import com.shilapi.xcertplay.wheel.WheelKeyService
import com.shilapi.xcertplay.WheelZoomSettings
import com.shilapi.xcertplay.host.R
import kotlin.math.roundToInt

/**
 * 闆惰窇 C11 鏂瑰悜鐩樻寜閿缃晫闈? * 
 * 鎻愪緵锛? * 1. 棰勮閫夋嫨锛堥粯璁?澶囬€夛級
 * 2. 鎸夐敭瀛︿範妯″紡
 * 3. 褰撳墠鏄犲皠鏄剧ず
 * 4. 鍔熻兘寮€鍏筹紙缂╂斁銆佹憞鏉嗐€丼iri锛? * 5. 妫€娴嬪埌鐨勬寜閿棩蹇? */
object LeapmotorWheelKeySettingsSection {

    private const val TAG = "DiPlay-LeapmotorWheelKeySettings"

    /** 鍒涘缓闆惰窇鏂瑰悜鐩樻寜閿缃垎绫?*/
    fun createLeapmotorWheelKeySection(
        context: Context,
        parent: LinearLayout,
        theme: SettingsTheme,
        onSettingsChanged: () -> Unit,
    ) {
        // 鏍囬
        parent.addView(SettingsWidgets.createCategoryHeader(
            context, context.getString(R.string.leapmotor_wheel_keys_title), theme
        ))

        // 1. 棰勮閫夋嫨
        addPresetSelector(context, parent, theme, onSettingsChanged)

        // 2. 鍔熻兘寮€鍏?        addFeatureSwitches(context, parent, theme, onSettingsChanged)

        // 3. 褰撳墠鎸夐敭鏄犲皠鏄剧ず
        addKeyMappingDisplay(context, parent, theme)

        // 4. 鎸夐敭瀛︿範鎸夐挳
        addKeyLearningButtons(context, parent, theme, onSettingsChanged)

        // 5. 妫€娴嬪埌鐨勬寜閿棩蹇?        addDetectedKeysLog(context, parent, theme)

        // 6. 閲嶇疆鎸夐挳
        addResetButton(context, parent, theme, onSettingsChanged)
    }

    /** 1. 棰勮閫夋嫨鍣?*/
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
                Log.i(TAG, "鍒囨崲棰勮: ${newPreset.name}")
            },
        )
        parent.addView(result.container)

        // 棰勮璇存槑
        val desc = TextView(context).apply {
            text = when (currentPreset) {
                LeapmotorWheelSettings.Preset.DEFAULT -> "榛樿棰勮锛氶€傞厤澶ч儴鍒嗛厤缃殑闆惰窇 C11 鏂瑰悜鐩?
                LeapmotorWheelSettings.Preset.ALTERNATIVE -> "澶囬€夐璁撅細閮ㄥ垎杞﹀瀷鏂瑰悜鐩橀敭鍊间笉鍚岋紝濡傜‘璁ら敭涓?DPAD_CENTER"
            }
            textSize = 13f
            setTextColor(theme.textSecondary)
            setPadding(theme.dp(context, 16), theme.dp(context, 4), theme.dp(context, 16), theme.dp(context, 12))
        }
        parent.addView(desc)
    }

    /** 2. 鍔熻兘寮€鍏?*/
    private fun addFeatureSwitches(
        context: Context,
        parent: LinearLayout,
        theme: SettingsTheme,
        onSettingsChanged: () -> Unit,
    ) {
        // 缂╂斁鍔熻兘
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

        // 缂╂斁琛屼负
        addChoiceRow(context, parent, theme,
            title = context.getString(R.string.leapmotor_wheel_zoom_behaviour_title),
            summary = context.getString(R.string.leapmotor_wheel_zoom_behaviour_summary),
            options = arrayOf("鍒囨崲妯″紡", "瀹氭椂妯″紡"),
            labels = arrayOf("鎸夋ā寮忛敭鍒囨崲寮€鍏?, "寮€鍚悗 5 绉掓棤鎿嶄綔鑷姩鍏抽棴"),
            checked = WheelZoomSettings.behaviour(context).ordinal,
            onChange = { index ->
                WheelZoomSettings.setBehaviour(context, WheelZoomSettings.Behaviour.values()[index])
                WheelKeyService.settingsChanged()
                onSettingsChanged()
            }
        )

        // 鎽囨潌鍔熻兘
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

        // 鎽囨潌鑷姩鍏抽棴
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

        // Siri 鎸夐敭
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

    /** 3. 褰撳墠鎸夐敭鏄犲皠鏄剧ず */
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

    /** 4. 鎸夐敭瀛︿範鎸夐挳 */
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

        // 瀛︿範瑙掕壊鐨勬寜閽?        val roles = arrayOf(
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
                text = "瀛︿範: $label",
                theme = theme,
                onClick = {
                    startLearning(context, role, label, onSettingsChanged)
                },
            )
            container.addView(btn)
        }

        // 鍙栨秷瀛︿範鎸夐挳
        val cancelBtn = SettingsWidgets.createButton(
            context,
            text = context.getString(R.string.leapmotor_wheel_cancel_learning),
            theme = theme,
            onClick = {
                WheelKeyService.cancelLearning()
                Toast.makeText(context, "宸插彇娑堝涔?, Toast.LENGTH_SHORT).show()
            },
        )
        container.addView(cancelBtn)

        parent.addView(container)
    }

    /** 寮€濮嬪涔犳寚瀹氳鑹茬殑鎸夐敭 */
    private fun startLearning(
        context: Context,
        role: WheelZoomSettings.Role,
        label: String,
        onSettingsChanged: () -> Unit,
    ) {
        val success = WheelKeyService.learn(
            role = role,
            cancelled = { Log.i(TAG, "瀛︿範鍙栨秷: $label") },
            refused = { takenRole ->
                Toast.makeText(context, "璇ユ寜閿凡琚?$takenRole 鍗犵敤", Toast.LENGTH_SHORT).show()
            },
            done = { assignedRole, key ->
                runOnUiThread {
                    Toast.makeText(context, "$label 宸茶缃负: ${key.code}/${key.scanCode}", Toast.LENGTH_LONG).show()
                    onSettingsChanged()
                }
            },
        )

        if (success) {
            Toast.makeText(context, "璇峰湪 10 绉掑唴鎸変笅鏂瑰悜鐩樹笂瑕佽涓恒€?label銆嶇殑鎸夐敭", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "鏃犳硶寮€濮嬪涔狅細鏈嶅姟鏈繍琛屾垨鍔熻兘鏈惎鐢?, Toast.LENGTH_SHORT).show()
        }
    }

    /** 5. 妫€娴嬪埌鐨勬寜閿棩蹇?*/
    private fun addDetectedKeysLog(
        context: Context,
        parent: LinearLayout,
        theme: SettingsTheme,
    ) {
        val header = SettingsWidgets.createCategoryHeader(
            context, context.getString(R.string.leapmotor_wheel_detected_keys), theme
        )
        parent.addView(header)

        // 杩欓噷鏄剧ず鏈€杩戞娴嬪埌鐨勬寜閿紙闇€瑕佸湪 WheelKeyService 涓褰曪級
        // 绠€鍖栫増锛氭樉绀鸿鏄?        val info = TextView(context).apply {
            text = "鏂瑰悜鐩樻寜閿寜涓嬫椂浼氬湪姝よ褰?KeyCode/ScanCode锛岀敤浜庣‘璁ゆ槧灏勫叧绯汇€俓n" +
                   "璇峰湪銆岃缃?鈫?璇婃柇 鈫?淇濆瓨璇婃柇鎶ュ憡銆嶆煡鐪嬪畬鏁存棩蹇椼€?
            textSize = 13f
            setTextColor(theme.textSecondary)
            setPadding(theme.dp(context, 16), theme.dp(context, 8), theme.dp(context, 16), theme.dp(context, 12))
        }
        parent.addView(info)
    }

    /** 6. 閲嶇疆鎸夐挳 */
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
                    .setMessage("灏嗛噸缃墍鏈夋柟鍚戠洏鎸夐敭鏄犲皠涓哄綋鍓嶉璁鹃粯璁ゅ€硷紝纭畾鍚楋紵")
                    .setPositiveButton("纭畾") { _, _ ->
                        LeapmotorWheelSettings.resetToDefault(context)
                        WheelKeyService.settingsChanged()
                        onSettingsChanged()
                        Toast.makeText(context, "宸查噸缃负榛樿棰勮", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("鍙栨秷", null)
                    .show()
            },
        )
        parent.addView(btn)
    }

    // ===== 杈呭姪鏂规硶 =====

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

    /** 杩愯鍦?UI 绾跨▼ */
    private fun runOnUiThread(action: () -> Unit) {
        if (Thread.currentThread().isInstanceOf<android.os.Looper>()) {
            action()
        } else {
            android.os.Handler(android.os.Looper.getMainLooper()).post(action)
        }
    }
}
