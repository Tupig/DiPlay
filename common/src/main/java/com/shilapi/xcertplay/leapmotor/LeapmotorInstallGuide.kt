// SPDX-License-Identifier: AGPL-3.0-only
package com.shilapi.xcertplay

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import android.app.AlertDialog

/**
 * 零跑 C11 安装引导与首次配置向导
 * 
 * 功能：
 * 1. 首次启动检测车型信息，失败时引导用户手动配置
 * 2. 显示 U 盘安装步骤指引
 * 3. 白名单包名选择指引
 * 4. 常见问题排查入口
 */
object LeapmotorInstallGuide {
    private const val TAG = "DiPlay-LeapmotorInstallGuide"
    private const val PREFS = "diplay_leapmotor_install_guide"
    private const val KEY_GUIDE_COMPLETED = "guide_completed"
    private const val KEY_DISMISSED_COUNT = "dismissed_count"
    private const val MAX_DISMISSED = 3

    /** 检查是否需要显示安装引导 */
    fun shouldShowGuide(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val completed = prefs.getBoolean(KEY_GUIDE_COMPLETED, false)
        val dismissed = prefs.getInt(KEY_DISMISSED_COUNT, 0)
        return !completed && dismissed < MAX_DISMISSED
    }

    /** 标记引导已完成 */
    fun markGuideCompleted(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_GUIDE_COMPLETED, true)
            .apply()
    }

    /** 增加取消计数 */
    fun incrementDismissed(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putInt(KEY_DISMISSED_COUNT, prefs(context).getInt(KEY_DISMISSED_COUNT, 0) + 1)
            .apply()
    }

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    /** 显示安装引导对话框（入口） */
    fun showGuideDialog(context: Context, onDismiss: () -> Unit = {}) {
        if (!shouldShowGuide(context)) return

        val builder = AlertDialog.Builder(context)
        builder.setTitle("零跑 C11 TuPigPlay 安装向导")
        builder.setMessage(
            "欢迎使用 TuPigPlay！\n\n" +
            "检测到您使用的是零跑 C11 (8295P) 车机。\n" +
            "为了确保 CarPlay 正常工作，请完成以下配置："
        )
        builder.setPositiveButton("开始配置") { _, _ ->
            showStep1VehicleInfo(context)
        }
        builder.setNegativeButton("稍后配置") { _, _ ->
            incrementDismissed(context)
            onDismiss()
        }
        builder.setCancelable(false)
        builder.show()
    }

    /** 步骤 1：车型信息检测与手动配置 */
    private fun showStep1VehicleInfo(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("步骤 1/4：车型信息")
        
        // 尝试自动检测
        var detectedInfo: String = "未检测到"
        try {
            val info = LeapmotorVehicleInfo.getVehicleInfo(context)
            detectedInfo = info.toModelIdentifier()
        } catch (e: LeapmotorVehicleInfo.DetectionException) {
            detectedInfo = "自动检测失败：${e.message}"
        }

        val message = StringBuilder()
        message.appendLine("车型信息用于 CarPlay 连接时在 iPhone 端显示正确的车辆名称。")
        message.appendLine()
        message.appendLine("自动检测结果：")
        message.appendLine("  $detectedInfo")
        message.appendLine()
        message.appendLine("如果信息不正确，请点击「手动配置」修正。")

        builder.setMessage(message.toString())
        builder.setPositiveButton("信息正确，下一步") { _, _ ->
            showStep2InstallMethod(context)
        }
        builder.setNeutralButton("手动配置车型") { _, _ ->
            showManualVehicleConfig(context)
        }
        builder.setNegativeButton("取消") { _, _ ->
            incrementDismissed(context)
        }
        builder.setCancelable(false)
        builder.show()
    }

    /** 手动配置车型信息 */
    private fun showManualVehicleConfig(context: Context) {
        // 这里可以启动一个专门的 Activity 或 DialogFragment
        // 简化版：Toast 提示去设置页面
        Toast.makeText(context, "请前往 设置 → 零跑专用 → 车型信息 进行手动配置", Toast.LENGTH_LONG).show()
        
        // 尝试打开设置页面
        try {
            val intent = Intent(context, DiPlayActivity::class.java).apply {
                putExtra("page", "settings")
                putExtra("settings_category", "VEHICLE") // 需要在 SettingsCategory 中添加
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.w(TAG, "无法打开设置页面", e)
        }
    }

    /** 步骤 2：安装方式说明 */
    private fun showStep2InstallMethod(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("步骤 2/4：安装方式")
        builder.setMessage(
            "TuPigPlay 必须通过 U 盘安装到车机（白名单机制）。\n\n" +
            "关键点：\n" +
            "1. APK 必须使用白名单包名构建（喜马拉雅/B站/QQ音乐车机版）\n" +
            "2. U 盘格式化为 FAT32，16-32GB\n" +
            "3. APK 放在 U 盘根目录\n" +
            "4. 插入前排左侧数据口（非充电口）\n" +
            "5. 车机：应用 → U盘应用 → 安装"
        )
        builder.setPositiveButton("我知道了，下一步") { _, _ ->
            showStep3Connection(context)
        }
        builder.setNegativeButton("返回") { _, _ ->
            showStep1VehicleInfo(context)
        }
        builder.setCancelable(false)
        builder.show()
    }

    /** 步骤 3：连接配置 */
    private fun showStep3Connection(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("步骤 3/4：连接配置")
        builder.setMessage(
            "安装完成后，首次打开需配置连接：\n\n" +
            "1. 授权权限：蓝牙/附近设备、麦克风、通知、存储\n" +
            "2. 选择连接方式：\n" +
            "   • 内置车载热点（推荐，5GHz）\n" +
            "   • Wi-Fi Direct（备选）\n" +
            "3. iPhone 开启蓝牙+Wi-Fi，与车机配对\n" +
            "4. 点击「连接手机」"
        )
        builder.setPositiveButton("下一步") { _, _ ->
            showStep4Troubleshooting(context)
        }
        builder.setNegativeButton("返回") { _, _ ->
            showStep2InstallMethod(context)
        }
        builder.setCancelable(false)
        builder.show()
    }

    /** 步骤 4：常见问题 */
    private fun showStep4Troubleshooting(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("步骤 4/4：常见问题排查")
        builder.setMessage(
            "遇到问题请按顺序排查：\n\n" +
            "❌ 安装提示「非白名单应用」\n" +
            "   → 确认包名、U盘根目录、FAT32格式\n\n" +
            "❌ 安装提示「签名冲突」\n" +
            "   → 卸载同包名旧应用、或更换白名单包名重新构建\n\n" +
            "❌ 无法搜索到 iPhone\n" +
            "   → 重新配对蓝牙、开启 5GHz 热点、尝试 Wi-Fi Direct\n\n" +
            "❌ 连接后秒断/卡在「准备 CarPlay」\n" +
            "   → 核对热点密码、换信道、确认包名权限\n\n" +
            "📋 详细日志与反馈：设置 → 诊断 → 保存诊断报告\n" +
            "🐛 GitHub Issues: https://github.com/Tupig/DiPlay/issues"
        )
        builder.setPositiveButton("完成配置") { _, _ ->
            markGuideCompleted(context)
            Toast.makeText(context, "配置完成！如需修改请前往设置。", Toast.LENGTH_LONG).show()
        }
        builder.setNeutralButton("查看诊断报告") { _, _ ->
            // 打开诊断页面
            val intent = Intent(context, DiPlayActivity::class.java).apply {
                putExtra("page", "settings")
                putExtra("settings_category", "DIAGNOSTICS")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
        builder.setNegativeButton("返回") { _, _ ->
            showStep3Connection(context)
        }
        builder.setCancelable(false)
        builder.show()
    }

    /** 显示白名单包名选择指引 */
    fun showWhitelistPackageGuide(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("白名单包名选择")
        builder.setMessage(
            "零跑车机仅允许安装白名单内的 21 个应用。\n" +
            "TuPigPlay 必须使用以下包名之一构建：\n\n" +
            "1. 喜马拉雅车机版（推荐，成功率最高）\n" +
            "   com.ximalaya.ting.android.car\n\n" +
            "2. 哔哩哔哩车机版\n" +
            "   com.bilibili.bilithings\n\n" +
            "3. QQ音乐车机版\n" +
            "   com.tencent.qqmusiccar\n\n" +
            "⚠️ 注意：必须是车机版包名，手机版不可用！\n" +
            "构建命令示例：\n" +
            "./gradlew :mobile:assembleRelease \\\n" +
            "  -PdiplayApplicationId=com.ximalaya.ting.android.car"
        )
        builder.setPositiveButton("复制构建命令") { _, _ ->
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("build_command", 
                "./gradlew :mobile:assembleRelease -PdiplayApplicationId=com.ximalaya.ting.android.car")
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "构建命令已复制到剪贴板", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("关闭", null)
        builder.show()
    }

    /** 从 DiPlayActivity 调用的快捷入口 */
    fun showQuickGuide(context: Context) {
        showGuideDialog(context)
    }
}