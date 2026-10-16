# 隐私与诊断 / Privacy and diagnostics

TuPigPlay 的产品流程使用本地身份验证以及到 iPhone 的直连 USB/Wi-Fi。不使用账户、远程身份验证服务，也不进行自动诊断上传。iPhone 上的 CarPlay 应用有各自的联网与隐私行为。

TuPigPlay's product flow uses local authentication and a direct USB/Wi-Fi connection to the iPhone. No account, remote authentication service or automatic diagnostic upload is used. The iPhone's CarPlay apps have their own internet and privacy behavior.

车机在应用存储中保存应用偏好、已选配对设备、配对数据以及有界的诊断日志。身份验证与配对材料不进入 Android 备份。卸载会清除应用私有数据；导出到 Downloads 的报告会一直保留，直到你删除它们。

The head unit stores app preferences, paired-device selections, pairing data and bounded diagnostic logs in app storage. Authentication and pairing material are kept out of Android backup. Uninstalling removes app-private data; exported reports in Downloads remain until you delete them.

诊断导出由你主动发起。报告包含应用/设备版本、显示设置与协商、连接状态切换、Wi-Fi 频段/信道与状态，以及解码器恢复事件。导出器会过滤协议载荷、含凭据的行以及常见标识符。脱敏无法保证识别出每个厂商特有的字符串：公开发布前请先检查报告内容。GitHub issue 是公开的。

Diagnostic export is initiated by you. Reports include app/device versions, display settings and negotiation, connection transitions, Wi-Fi band/channel and state, and decoder recovery events. With the optional wheel key service on, they also list the last hardware keys it was given, as key and scan codes with what TuPigPlay did with each; a key that writes text is counted and never listed, and no input device name is recorded. The exporter filters protocol payloads, credential-bearing lines and common identifiers. Redaction cannot promise to recognize every vendor-specific string: review reports before posting them publicly. A GitHub issue is public.

在 Android 9 及更早版本上，**Save diagnostic report** 会请求存储权限并保存到 `Download/TuPigPlay`；如果你拒绝，则适用下述兜底方案。当文档选择器或 Downloads 存储不可用时，报告会保存在主外部存储的 `Android/data/<package>/files/diagnostic-reports/` 下，且无需请求存储权限。确认提示会显示实际的 TXT 文件路径。正式发布包名为 `com.tupig.tupigplay`；调试构建使用 `com.tupig.tupigplay.hudtest`。较新 Android 版本的文件管理器可能会限制对 `Android/data` 的访问；请改用 TuPigPlay 内的 **View** 或 **Share**。如果外部存储同样不可用，报告会保存在私有应用存储中。每个兜底位置保留最近的八次导出，卸载会将其一并移除。分享只会授予对所选报告的读取权限；不会自动发送任何内容。在 Android 9 及更早版本上，拥有存储权限的其他应用可能读取到这些外部报告。

On Android 9 and earlier, **Save diagnostic report** asks for storage access and saves to `Download/TuPigPlay`; if you decline, the fallbacks below apply. When the document picker or Downloads storage is unavailable, reports save under `Android/data/<package>/files/diagnostic-reports/` on primary external storage without requesting storage permission. The confirmation shows the actual TXT file path. The release package is `com.tupig.tupigplay`; debug builds use `com.tupig.tupigplay.hudtest`. File managers on newer Android versions may restrict access to `Android/data`; use **View** or **Share** in TuPigPlay instead. If external storage is also unavailable, reports save in private app storage. Each fallback location retains its newest eight exports, and uninstalling removes them. Sharing grants read access to the selected report only; nothing is sent automatically. On Android 9 and older, other apps with storage permission may be able to read the external reports.

麦克风权限用于支持 Siri 和通话。蓝牙/附近设备以及 Wi-Fi/位置权限用于设备发现与传输。可选的本地 VPN 权限用于承载 USB 链路；它不提供远程互联网 VPN。

Microphone access supports Siri and calls. Bluetooth/Nearby devices and Wi-Fi/Location permissions support discovery and transport. The optional local VPN permission supports the USB link; it does not provide a remote internet VPN.

已验证的 DiLink 5.1 仪表盘配置会可选地使用 Android 使用情况访问权限（Usage Access），以跟踪主题与小地图卡片的可见性。该权限会暴露应用活动历史。TuPigPlay 会把结果过滤到四个 BYD 原厂仪表盘活动，仅在本地处理，并且只记录推断出的仪表盘主题/可见性变化。无关的活动事件不会被保留或上传。自动模式需要主动启用；关闭后即停止这些查询。如 [BYD 导航](BYD_NAVIGATION.md#dilink-51-theme-profile)中所述，使用情况访问权限也可随时撤销。

The verified DiLink 5.1 cluster profile optionally uses Android Usage Access to follow theme and mini-map-card visibility. This permission exposes app-activity history. TuPigPlay filters the results to four stock BYD cluster activities, processes them locally, and logs only inferred cluster theme/visibility changes. Unrelated activity events are not retained or uploaded. Automatic mode is opt-in; disabling it stops these queries. Usage Access can also be revoked as described in [BYD navigation](BYD_NAVIGATION.md#dilink-51-theme-profile).

TuPigPlay 每天一次以及打开后不久，会检查 GitHub 的公开发布列表以获取更新。该请求的 user agent 中只携带 TuPigPlay 的版本号，不携带其他数据；GitHub 会看到车机的 IP 地址。未经你点按，不会下载或安装任何内容，且 **About** 页面可关闭后台检查。下载的更新会与其公布的校验和进行核对；在 Android 10 及以上，它还会被复制到 `Download/TuPigPlay`。

TuPigPlay checks GitHub's public release list for updates once a day and shortly after it opens. The request carries TuPigPlay's version in its user agent and no other data, and GitHub sees the head unit's IP address. Nothing is downloaded or installed without your tap, and **About** turns the background check off. A downloaded update is verified against its published checksum; on Android 10 and later it is also copied to `Download/TuPigPlay`.

静态网站没有统计脚本，也没有账户。当你使用 GitHub Pages、GitHub 和 Telegram 时，它们各自适用其自身的政策。

The static website has no analytics script or account. GitHub Pages, GitHub and Telegram apply their own policies when you use those services.
