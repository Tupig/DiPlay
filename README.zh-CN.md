# DiPlay

[English](README.md) · [简体中文](README.zh-CN.md) · [繁體中文](README.zh-TW.md)

为兼容的比亚迪安卓车机提供有线及无线 CarPlay，采用 DiAuto 风格界面。

> 这些项目专注于比亚迪汽车。它们可能在其他品牌上运行，但其他品牌不在支持范围内，也没有增加支持或修复其品牌特定兼容性问题的计划。

[下载与中文网站](https://shihabal3amri.github.io/DiPlay/zh-Hans/) · [0.2.19 版本](https://github.com/shihabal3amri/DiPlay/releases/tag/v0.2.19) · [完整说明](README.md) · [报告问题](https://github.com/shihabal3amri/DiPlay/issues/new/choose)

## 0.2.19 — 公开预览版

请安装在允许 APK 安装的 Android 7.1+（API 25+）车机上；Android 7.1–8.1 尚未经实车验证。无需越狱、转接盒、账户或认证服务器。有线及无线核心连接不要求 ADB；可选车辆数据及车辆控制需要支持的固件和已授权网络 ADB。

Wi-Fi Direct 现支持 Android 7.1–9 的旧版建组路径，使用系统返回的真实凭据。首选信道依赖固件 API；Android 7.1–9 无法读回协商频率，所以请求信道在诊断中标为未经验证，系统默认为信道 0。Android 10+ 保留频率验证。也可使用车机内置热点、USB 或[现有 Wi-Fi／同一局域网](docs/EXISTING_WIFI.md)；同一局域网模式由车机和 iPhone 自行连接外部路由器，DiPlay 不替你修改默认路由。[Android 9 Wi-Fi Direct 限制](docs/ANDROID9_WIFI_DIRECT.md)说明清理及持久配置边界。

- 有线 USB 与无线 CarPlay，使用本地认证。
- 在已验证固件上提供比亚迪 HUD 导航箭头、距离和路名。
- 支持车机热点、改进的音频缓冲与可保存的接收端诊断。
- 自动地址发现、固定信道 Wi-Fi 回退，并记住成功的配置。
- 图标／文字大小、分辨率和帧率可调；应用显示改动会重连 CarPlay。
- 本地导出诊断报告，只有你主动选择才会分享。
- 可与 DiAuto 并存安装，同一时间只运行一个投屏应用。

> **本产品未经 Apple 认证。** APK 内置的是从公开 Carlinkit 固件中恢复的实验性配件身份，并非为 DiPlay 新签发的 MFi 身份。其中的私钥可被提取。未来 iOS 版本是否继续接受、各车机上的可靠性，以及该身份是否适合公开分发，均未确定。此版本用于社区测试，不保证普遍兼容。

### 已在实车验证的内容

早期版本在开发用 DiLink 5.1 车辆上测试过：挡风玻璃实时导航与路名可用，车机热点现在可以启动 CarPlay，Wi-Fi Direct 性能有明显提升。偶发的音频中断仍然存在，推迟到后续版本处理。悬浮地图测试版也安装在开发用 DiLink 5.1 车辆上，收到的反馈促成了 0.2.9 的缩放修正。

更早的方向盘速度和视频功能贡献在比亚迪唐（DiLink 5.0）配 iPhone 15 Pro / iOS 27 上测试过；隧道内的轮速推算仍未验证。更广泛的车机与 iOS 兼容性不作保证。HUD 固件范围与清理限制见 [BYD 导航文档](docs/BYD_NAVIGATION.md)。

## 新增与修正

- **有线连接**：许多车机上有线 CarPlay 的启动快了几秒；iOS 26 的 iPhone 先提供不含 CarPlay 的 USB 模式时也能启动；固件删除了 USB 授权页面的 Android 7.1–9 车机，在已授权网络 ADB 时也能使用有线连接。
- **通话、Siri 与音乐**：16 kHz 格式的通话、Siri 和导航语音不再以三倍速度向 iPhone 报告播放位置；麦克风首次读取失败时会自动重启；CarPlay 在后台时音乐不再每隔几秒断音。
- **仪表地图与倒车影像**：DiLink 4 开启仪表地图时，CarPlay 不再在第一次触摸屏幕时重新连接；保留状态栏时，倒车影像后 CarPlay 画面不再缩小。
- **语言**：新增日语，繁体中文已完整，并补充了 67 条阿拉伯语。
- **诊断报告**：“**设置 → 诊断 → 在手机上取得报告**”可通过二维码或输入地址和验证码，在手机上下载诊断报告；连接界面会针对每种失败给出提示。
- **其他**：重新设计的 CarPlay 内设置菜单，以及可选的仪表地图专辑封面卡片。

[0.2.19 完整说明](docs/RELEASE-NOTES-0.2.19.md)包含贡献链接及功能限制；构建和验证信息见[验证记录](docs/VALIDATION.md)。Android 7.1–8.1 尚需实车验证，不宣称所有车型的连接、音频或 Siri 问题均已解决。可选功能请停车后测试。

### 请提供 0.2.19 的新诊断报告

1. 更新到 **0.2.19**，复现问题并记录发生时间。开机／自动启动问题发生后，可手动打开 DiPlay 导出。
2. 打开“**设置 → 诊断 → 保存诊断报告**”。Android 10+ 通常保存到 **Downloads/DiPlay**；Android 7.1–9 会请求存储权限并同样保存到该位置，也可点“选择保存位置”。如选择器或公共存储不可用，应用会使用专用外部或私有目录，并在确认中说明目的地。
3. 使用确认中的**查看报告／分享**；没有分享应用时，可在报告视图中选择并复制文本。检查 `.txt` 并删除隐私信息，再附到匹配的[现有问题](https://github.com/shihabal3amri/DiPlay/issues)，或[新建问题](https://github.com/shihabal3amri/DiPlay/issues/new/choose)。报告不会自动上传，请勿公开热点密码或私有认证文件。
4. 注明车型／车机、DiLink/Android/完整固件版本、iPhone/iOS、USB／车机热点／Wi-Fi Direct／同一局域网、相关设置、复现步骤、预期与实际结果及故障时间。

1. 打开“**设置 → 诊断 → 保存诊断报告**”。
2. Android 10+ 通常保存到 **Downloads/DiPlay**；Android 9 使用文件选择器，也可点“选择保存位置”。
3. 如选择器或公共存储不可用，应用会使用专用外部或私有目录，并在确认中说明目的地；也可用确认中的**查看报告／分享**。
4. 检查 `.txt` 并删除隐私信息，再附到匹配的[现有问题](https://github.com/shihabal3amri/DiPlay/issues)，或[新建问题](https://github.com/shihabal3amri/DiPlay/issues/new/choose)。

历史记录：[0.2.17](docs/RELEASE-NOTES-0.2.17.md)、[0.2.16](docs/RELEASE-NOTES-0.2.16.md)、[0.2.15](docs/RELEASE-NOTES-0.2.15.md)、[0.2.14](docs/RELEASE-NOTES-0.2.14.md)、[0.2.13](docs/RELEASE-NOTES-0.2.13.md)、[0.2.12](docs/RELEASE-NOTES-0.2.12.md)、[0.2.11](docs/RELEASE-NOTES-0.2.11.md)、[安装与连接](docs/INSTALL.md)。

## 构建与校验

标准导航小组件需要支持 Android 小组件的启动器；比亚迪内置主页不接受任意小组件。悬浮地图和嵌入地图需要启用“CarPlay 仪表地图”。应用及网站源代码包含英语、简体中文、繁体中文（台湾）、阿拉伯语、俄语、乌克兰语、西班牙语和日语。应用的香港／澳门及 Hant 选择使用台湾译文，不宣称提供独立地区翻译。[繁体中文说明](README.zh-TW.md)提供安装、设置、隐私和构建指南。源代码、构建说明及许可证随版本提供。
