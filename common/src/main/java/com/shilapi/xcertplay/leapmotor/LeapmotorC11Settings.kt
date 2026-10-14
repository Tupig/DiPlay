// SPDX-License-Identifier: AGPL-3.0-only
package com.shilapi.xcertplay

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import java.util.Date
import java.util.Locale

/**
 * 2024 款零跑 C11 (8295P) 专用配置
 * 
 * 硬件：SA8295P + 14.6" 2560×1600 2.5K 屏 + 仪表盘 + 副驾屏
 * 系统：LeapOS 3.21.40+
 * 安装：U 盘安装 + 白名单包名校验
 * 限制：无 ADB、无开发者选项、无侧载通道
 */
object LeapmotorC11Settings {

    // ========== 白名单包名策略 ==========
    // 报告安装成功率最高的 3 个车载版包名（按推荐顺序）
    enum class WhitelistPackage(val packageName: String, val displayName: String, val knownWorkingVersion: String) {
        XIMALAYA("com.ximalaya.ting.android.car", "喜马拉雅车机版", "v4.7.2"),
        BILIBILI("com.bilibili.bilithings", "哔哩哔哩车机版", "v2.2.1"),
        QQ_MUSIC("com.tencent.qqmusiccar", "QQ音乐车机版", "v2.6.1.1")
    }

    /** 当前构建使用的白名单包名（通过 -PdiplayApplicationId 指定） */
    val CURRENT_WHITELIST_PACKAGE: String = WhitelistPackage.XIMALAYA.packageName

    /** 判断当前包名是否为白名单包名 */
    fun isWhitelisted(packageName: String): Boolean = WhitelistPackage.values().any { it.packageName == packageName }

    /** 获取推荐的白名单包名（优先级排序） */
    fun recommendedWhitelistPackages(): List<WhitelistPackage> = WhitelistPackage.values().toList()

    // ========== 硬件能力常量 ==========
    object Hardware {
        const val SOC = "SA8295P"
        const val PANEL_WIDTH = 2560
        const val PANEL_HEIGHT = 1600
        const val PANEL_DIAGONAL_INCHES = 14.6f
        const val PANEL_REFRESH_HZ = 60
        const val PANEL_DPI = 216  // 计算值
        const val CLUSTER_AVAILABLE = true
        const val COPILOT_AVAILABLE = true
        const val USB_HOST_PORT = true
        const val WIFI_5GHZ = true
        const val WIFI_DIRECT = true
    }

    // ========== 系统版本识别 ==========
    object SystemVersion {
        const val LEAPOS_3_21_40 = "3.21.40"
        const val LEAPOS_3_35_40 = "3.35.40"  // 已知仅支持白名单签名包

        /** 检测是否为 2024 款 C11 系统 */
        fun isLeapmotorC11_2024(context: Context): Boolean {
            val display = Build.DISPLAY.lowercase()
            val manufacturer = Build.MANUFACTURER.lowercase()
            val brand = Build.BRAND.lowercase()
            val model = Build.MODEL.lowercase()
            
            return (display.contains("3.21") || display.contains("3.35")) &&
                   (manufacturer.contains("leapmotor") || brand.contains("leapmotor") || model.contains("c11"))
        }

        /** 获取当前系统版本字符串 */
        fun getCurrentVersion(): String = Build.DISPLAY
    }

    // ========== 安装引导 ==========
    object InstallGuide {
        /** U 盘安装步骤（中文） */
        val USB_INSTALL_STEPS_ZH = """
            【零跑 C11 2024 款 U 盘安装 DiPlay 完整步骤】
            
            ⚠️ 重要前提：
            1. 必须使用白名单包名构建 APK（见下方构建命令）
            2. 系统版本需为 LeapOS 3.21.40 或更早（3.35.40+ 可能仅支持白名单签名包）
            3. 请先备份恢复出厂设置路径（设置 → 系统 → 重置）
            
            📋 准备工作：
            • U 盘：16-32GB，FAT32 格式（推荐 SanDisk/金士顿）
            • APK：使用白名单包名构建的 DiPlay-{版本}-leapmotor.apk
            
            🔧 构建命令（在电脑上执行）：
            ./gradlew :mobile:assembleRelease \\
              -PdiplayApplicationId=com.ximalaya.ting.android.car
            
            📥 安装步骤：
            1. 将 APK 重命名为 Ximalaya_Car_v4.7.2.apk 放入 U 盘根目录
            2. 插入【前排左侧数据口】（中控台 USB 仅充电）
            3. 车机打开：应用 → U盘应用 → 等待扫描 → 点击安装
            4. 安装成功后在"已装应用"中找到"喜马拉雅"图标启动
            5. 首次启动授权：蓝牙/附近设备、麦克风、通知、存储
            
            🔍 故障排查：
            • 提示"非白名单应用" → 确认包名正确、U盘根目录、FAT32格式
            • 提示"签名冲突" → 卸载同包名旧应用、或更换白名单包名重试
            • 安装后闪退 → 查看日志、尝试降级系统版本或更换包名
            • 无法连接 CarPlay → 检查蓝牙配对、热点开启、位置权限
            
            📞 反馈渠道：
            GitHub Issues: https://github.com/Tupig/DiPlay/issues
            请附带：车型年款、系统版本、安装包名、报错截图、诊断报告
        """.trimIndent()

        /** 获取安装指引文本 */
        fun getInstallGuide(context: Context): String = USB_INSTALL_STEPS_ZH
    }

    // ========== 运行时能力检测 ==========
    object RuntimeCapabilities {
        /** 检测当前环境能力 */
        fun detect(context: Context): CapabilityReport {
            val pm = context.packageManager
            val isLeapmotor = SystemVersion.isLeapmotorC11_2024(context)
            val currentPkg = context.packageName
            val isWhitelisted = isWhitelisted(currentPkg)
            
            return CapabilityReport(
                isLeapmotorC11_2024 = isLeapmotor,
                currentPackageName = currentPkg,
                isWhitelistedPackage = isWhitelisted,
                hasAdb = false,  // 零跑无 ADB
                hasDeveloperOptions = false,
                canInstallApk = true,  // 通过 U 盘
                systemVersion = SystemVersion.getCurrentVersion(),
                screenWidth = Hardware.PANEL_WIDTH,
                screenHeight = Hardware.PANEL_HEIGHT,
                recommendedFps = 60
            )
        }

        data class CapabilityReport(
            val isLeapmotorC11_2024: Boolean,
            val currentPackageName: String,
            val isWhitelistedPackage: Boolean,
            val hasAdb: Boolean,
            val hasDeveloperOptions: Boolean,
            val canInstallApk: Boolean,
            val systemVersion: String,
            val screenWidth: Int,
            val screenHeight: Int,
            val recommendedFps: Int
        )
    }

    // ========== 功能开关（针对零跑环境优化） ==========
    object FeatureFlags {
        // 零跑环境下默认关闭的实验性功能（需 ADB 或 root）
        const val CLUSTER_SPEEDOMETER = false      // 需 ADB
        const val HARMAN_FULLSCREEN = false        // 非哈曼车机
        const val FCE_CLUSTER_GUIDANCE = false     // 非 FCE 平台
        const val GEELY_WHEEL_KEYS = false         // 非吉利
        const val BYD_AMBIENT_LIGHT = false        // 非比亚迪
        const val BYD_NAVIGATION = false           // 非比亚迪
        const val USB_AUTO_CONFIRM = false         // 需辅助服务
        const val ADB_CLUSTER_ACTIVITY = false     // 需 ADB
        
        // 零跑环境下可用的功能
        const val WIRELESS_HOTSPOT_BUILTIN = true  // 内置热点
        const val WIRELESS_WIFI_DIRECT = true      // Wi-Fi Direct
        const val USB_CARPLAY = true               // 标准 USB CarPlay
        const val SETTINGS_BACKUP_QR = true        // 二维码备份
        const val DIAGNOSTIC_EXPORT = true         // 诊断导出
        const val CUSTOM_RESOLUTION = true         // 自定义分辨率
        const val FPS_COUNTER = true               // FPS 计数器
        const val SMOOTH_VIDEO = true              // 流畅视频（GPU 侧）
        const val BUFFERED_AUDIO = true            // 缓冲音乐
        const val CALL_ECHO_CANCEL = true          // 通话回声消除
        const val LOW_LATENCY_DECODER = true       // 低延迟解码
    }

    // ========== 诊断上报 ==========
    object Diagnostics {
        /** 生成零跑专用诊断报告 */
        fun generateLeapmotorReport(context: Context): String {
            val cap = RuntimeCapabilities.detect(context)
            val sb = StringBuilder()
            sb.appendLine("=== 零跑 C11 2024 诊断报告 ===")
            sb.appendLine("时间: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}")
            sb.appendLine("包名: ${cap.currentPackageName}")
            sb.appendLine("白名单包名: ${cap.isWhitelistedPackage}")
            sb.appendLine("系统版本: ${cap.systemVersion}")
            sb.appendLine("屏幕: ${cap.screenWidth}×${cap.screenHeight} @${cap.recommendedFps}fps")
            sb.appendLine("芯片: ${Hardware.SOC}")
            sb.appendLine("ADB可用: ${cap.hasAdb}")
            sb.appendLine("开发者选项: ${cap.hasDeveloperOptions}")
            sb.appendLine("U盘安装: ${cap.canInstallApk}")
            sb.appendLine("=== 功能可用性 ===")
            sb.appendLine("内置热点: ${FeatureFlags.WIRELESS_HOTSPOT_BUILTIN}")
            sb.appendLine("Wi-Fi Direct: ${FeatureFlags.WIRELESS_WIFI_DIRECT}")
            sb.appendLine("USB CarPlay: ${FeatureFlags.USB_CARPLAY}")
            sb.appendLine("设置备份: ${FeatureFlags.SETTINGS_BACKUP_QR}")
            sb.appendLine("诊断导出: ${FeatureFlags.DIAGNOSTIC_EXPORT}")
            sb.appendLine("自定义分辨率: ${FeatureFlags.CUSTOM_RESOLUTION}")
            sb.appendLine("FPS计数器: ${FeatureFlags.FPS_COUNTER}")
            sb.appendLine("实验性视频优化: ${FeatureFlags.SMOOTH_VIDEO}")
            sb.appendLine("缓冲音乐: ${FeatureFlags.BUFFERED_AUDIO}")
            return sb.toString()
        }
    }
}