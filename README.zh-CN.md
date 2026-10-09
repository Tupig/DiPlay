# DiPlay

[English](README.md) · **简体中文**

为兼容的比亚迪安卓车机提供有线及无线 CarPlay，采用 DiAuto 风格界面。独立应用：`com.shihab.diplay`（正式版）/ `com.shihab.diplay.hudtest`（测试版）。

> 这些项目专注于比亚迪汽车。它们可能在其他品牌上运行，但其他品牌不在支持范围内，也没有增加支持或修复其品牌特定兼容性问题的计划。

[下载与中文网站](https://shihabal3amri.github.io/DiPlay/zh-Hans/) · [0.2.14 版本](https://github.com/shihabal3amri/DiPlay/releases/tag/v0.2.14) · [报告问题](https://github.com/shihabal3amri/DiPlay/issues/new/choose)

![DiPlay 首页](site/assets/home.png)

## 目录

- [0.2.14 — 公开预览版](#0214--公开预览版)
- [新增与修正](#新增与修正)
- [报告问题](#报告问题)
- [构建与校验](#构建与校验)
- [文档索引](#文档索引)
- [来源与致谢](#来源与致谢)
- [本地发布打包](#本地发布打包)

## 0.2.14 — 公开预览版

请安装在**车机**上，而不是 iPhone 上。无需越狱、转接盒、Mac、账户或认证服务器。有线及无线核心连接不要求 ADB；可选的车辆数据、车辆控制和停车播放视频需要支持的固件和已授权网络 ADB。你的车机必须允许安装 APK。

APK 支持 Android 9+（API 28+）。无线连接支持 Wi-Fi Direct、车机自带热点，或[现有 Wi-Fi／同一局域网](docs/EXISTING_WIFI.md)。Android 9 的 Wi-Fi Direct 使用依赖固件的旧版建组路径，凭据由系统生成且请求频率未经验证，详见 [Android 9 Wi-Fi Direct 限制](docs/ANDROID9_WIFI_DIRECT.md)；Android 10+ 会校验协商出的频率。

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

- **设置与界面**：新增分类、搜索、快捷控制和重连提示，改进小屏幕及阿拉伯语从右到左布局。界面大小可选自动或 100–200%，只调整 DiPlay 自身控件，CarPlay 画面尺寸仍单独设置。
- **日常使用**：自动连接可选择上次使用、无线或 USB；支持按车机本地时间安排昼夜模式；改进车辆按钮自定义图片选择。
- **连接与画面**：针对已捕获故障修正 USB／蓝牙恢复，完善热点地址发现与无线交接。无硬件加速的窗口使用兼容的视频输出。实验性平滑视频默认关闭，可能增加触摸延迟，且使用时不能调整画面颜色。
- **Siri 与通话**：可配置方向盘 Siri 键，增加麦克风源回退及可选音频焦点处理，修正 BYD 通话观察进程。实验性通话回声消除和语音过滤均默认关闭，修改在下次连接生效。
- **仪表与布局**：保留非方形专辑封面比例，旋转／分屏计算考虑系统栏，识别已观察到的 DiLink 3 仪表投射尺寸；该尺寸的实际地图输出仍需车辆复测。

[0.2.14 完整说明](docs/RELEASE-NOTES-0.2.14.md)包含贡献链接及功能限制；构建和验证信息见[验证记录](docs/VALIDATION.md)。Android 9 仍是最低支持版本，不宣称所有车型的 USB、卡顿或 Siri 问题均已解决。可选功能请停车后测试。历史记录：[0.2.13](docs/RELEASE-NOTES-0.2.13.md)、[0.2.12](docs/RELEASE-NOTES-0.2.12.md)、[0.2.11](docs/RELEASE-NOTES-0.2.11.md)、[安装与连接](docs/INSTALL.md)。

## 报告问题

如果问题仍然存在，请在 **0.2.14** 上复现，然后附上一份新的诊断报告。

1. 打开“**设置 → 诊断 → 保存诊断报告**”。
2. Android 10+ 通常保存到 **Downloads/DiPlay**；Android 9 使用文件选择器，也可点“选择保存位置”。
3. 如选择器或公共存储不可用，应用会使用专用外部或私有目录，并在确认中说明目的地；也可用确认中的**查看报告／分享**。
4. 检查 `.txt` 并删除隐私信息，再附到匹配的[现有问题](https://github.com/shihabal3amri/DiPlay/issues)，或[新建问题](https://github.com/shihabal3amri/DiPlay/issues/new/choose)。

请注明车型／车机、DiLink/Android/完整固件版本、iPhone/iOS、USB／车机热点／Wi-Fi Direct／同一局域网、相关设置、复现步骤、预期与实际结果及故障时间。报告不会自动上传，**请勿公开热点密码**。

## 构建与校验

完整的环境准备、模块选择与发布签名见[从源码构建](docs/BUILD.md)。主应用请选择 `mobile` 模块；`maphost` 是地图演示应用，`home` 是可选启动器。

环境要求：JDK 25、Android SDK Platform 37、Build-Tools、Platform-Tools 和 NDK 28.2.13676358。设置 `JAVA_HOME`，并设置 `ANDROID_HOME` 或 `local.properties`。

运行与 CI 相同的检查：

```sh
./gradlew :shared:testDebugUnitTest :common:testDebugUnitTest :home:testDebugUnitTest \
  :mobile:lintDebug :home:lintDebug :maphost:lintDebug \
  :mobile:assembleDebug :home:assembleDebug :maphost:assembleDebug
```

构建车机测试用独立 APK（源码构建不包含配件身份，无法自行启动 CarPlay）：

```sh
DIPLAY_AUTH_ASSETS_DIR=/absolute/path/to/runtime-assets ./gradlew :mobile:assembleStandaloneDebug
```

产物路径：`mobile/build/outputs/apk/debug/mobile-debug.apk`。目录结构与校验步骤见[从源码构建](docs/BUILD.md)。

## 文档索引

| 主题 | 文档 |
| --- | --- |
| 安装与连接 | [INSTALL.md](docs/INSTALL.md) |
| 零跑（Leapmotor）安装说明 | [LEAPMOTOR.md](docs/LEAPMOTOR.md) |
| 兼容性与故障排查 | [COMPATIBILITY.md](docs/COMPATIBILITY.md) |
| 流畅的无线 CarPlay | [SMOOTH_WIRELESS.md](docs/SMOOTH_WIRELESS.md) |
| 现有 Wi-Fi／同一局域网 | [EXISTING_WIFI.md](docs/EXISTING_WIFI.md) |
| 隐私与诊断报告 | [PRIVACY.md](docs/PRIVACY.md) |
| 从源码构建 | [BUILD.md](docs/BUILD.md) |
| 验证记录 | [VALIDATION.md](docs/VALIDATION.md) |
| 版本说明 | [CHANGELOG.md](CHANGELOG.md) |
| 第三方许可与致谢 | [THIRD_PARTY_NOTICES.md](docs/THIRD_PARTY_NOTICES.md) |

标准导航小组件需要支持 Android 小组件的启动器；比亚迪内置主页不接受任意小组件。悬浮地图和嵌入地图需要启用“CarPlay 仪表地图”。部分车机仍可能卡顿或无法应用图标大小设置。

应用及发布网站支持英语、阿拉伯语、俄语、乌克兰语、西班牙语、简体中文和繁体中文（台湾）。繁体中文使用台湾用语；应用的香港／澳门及 Hant 选择同样使用台湾译文，不宣称提供独立地区翻译。应用语言在设置中选择；Android 13+ 上会与系统的按应用语言设置保持同步。源代码、构建说明及许可证随版本提供。

## 来源与致谢

基于 [xcertplay](https://github.com/shilapi/xcertplay)，GPL-3.0。主界面／设置与网站改写自 [DiAuto](https://github.com/shihabal3amri/DiAuto)，AGPL-3.0；该许可证包含在 `docs/licenses` 中。分发修改版本时请保留这些声明。CarPlay 及其图标归 Apple Inc. 所有；不代表与 Apple 或比亚迪存在关联或背书。

xcertplay 原始 README 保存在 [docs/UPSTREAM-README.md](docs/UPSTREAM-README.md)。

本仓库从一份干净的公开源码快照开始。本地研究、测试者报告与发布签名密钥均已排除。与 APK 对应的完整源码随每个版本提供；实验性运行时身份资产另行在构建说明与声明中描述。

### 本分支

本仓库是 [shihabal3amri/DiPlay](https://github.com/shihabal3amri/DiPlay) 的一个 fork。版本发布、上游问题与网站均由上游项目托管；本地改动记录在本分支的提交历史中。依赖某项改动前，请在自己的车机上重新测试。

## 本地发布打包

正式版 APK 有意包含实验性配件身份。Git 仓库与源码压缩包排除全部配件身份与 Android 签名密钥；测试在运行时生成合成身份。源码／CI 构建默认不含运行时身份资产，本地发布构建需显式指定外部资产目录。发布 APK 会使其中的身份可被提取；仅在本地构建并不能保护该身份的机密性。
