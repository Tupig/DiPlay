# 隐私与诊断

[English](PRIVACY.md) · **简体中文**

DiPlay 的产品流程使用本地身份验证以及到 iPhone 的直连 USB/Wi-Fi。不使用账户、远程身份验证服务，也不进行自动诊断上传。iPhone 上的 CarPlay 应用有各自的联网与隐私行为。

车机在应用存储中保存应用偏好、已选配对设备、配对数据以及有界的诊断日志。身份验证与配对材料不进入 Android 备份。卸载会清除应用私有数据；导出到 Downloads 的报告会一直保留，直到你删除它们。

诊断导出由你主动发起。报告包含应用/设备版本、显示设置与协商、连接状态切换、Wi-Fi 频段/信道与状态，以及解码器恢复事件。导出器会过滤协议载荷、含凭据的行以及常见标识符。脱敏无法保证识别出每个厂商特有的字符串：公开发布前请先检查报告内容。GitHub issue 是公开的。

在 Android 9 及更早版本上，**Save diagnostic report** 会请求存储权限并保存到 `Download/DiPlay`；如果你拒绝，则适用下述兜底方案。当文档选择器或 Downloads 存储不可用时，报告会保存在主外部存储的 `Android/data/<package>/files/diagnostic-reports/` 下，且无需请求存储权限。确认提示会显示实际的 TXT 文件路径。正式发布包名为 `com.shihab.diplay`；调试构建使用 `com.shihab.diplay.hudtest`。较新 Android 版本的文件管理器可能会限制对 `Android/data` 的访问；请改用 DiPlay 内的 **View** 或 **Share**。如果外部存储同样不可用，报告会保存在私有应用存储中。每个兜底位置保留最近的八次导出，卸载会将其一并移除。分享只会授予对所选报告的读取权限；不会自动发送任何内容。在 Android 9 及更早版本上，拥有存储权限的其他应用可能读取到这些外部报告。

麦克风权限用于支持 Siri 和通话。蓝牙/附近设备以及 Wi-Fi/位置权限用于设备发现与传输。可选的本地 VPN 权限用于承载 USB 链路；它不提供远程互联网 VPN。

已验证的 DiLink 5.1 仪表盘配置会可选地使用 Android 使用情况访问权限（Usage Access），以跟踪主题与小地图卡片的可见性。该权限会暴露应用活动历史。DiPlay 会把结果过滤到四个 BYD 原厂仪表盘活动，仅在本地处理，并且只记录推断出的仪表盘主题/可见性变化。无关的活动事件不会被保留或上传。自动模式需要主动启用；关闭后即停止这些查询。如 [BYD 导航](BYD_NAVIGATION.md#dilink-51-theme-profile)中所述，使用情况访问权限也可随时撤销。

DiPlay 每天一次以及打开后不久，会检查 GitHub 的公开发布列表以获取更新。该请求的 user agent 中只携带 DiPlay 的版本号，不携带其他数据；GitHub 会看到车机的 IP 地址。未经你点按，不会下载或安装任何内容，且 **About** 页面可关闭后台检查。下载的更新会与其公布的校验和进行核对；在 Android 10 及以上，它还会被复制到 `Download/DiPlay`。

静态网站没有统计脚本，也没有账户。当你使用 GitHub Pages、GitHub 和 Telegram 时，它们各自适用其自身的政策。
