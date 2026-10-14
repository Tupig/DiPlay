# DiPlay（TuPigPlay）

[![Build](https://github.com/Tupig/DiPlay/actions/workflows/diplay.yml/badge.svg)](https://github.com/Tupig/DiPlay/actions/workflows/diplay.yml)
[![Release](https://img.shields.io/github/v/release/Tupig/DiPlay)](https://github.com/Tupig/DiPlay/releases/latest)
[![License](https://img.shields.io/github/license/Tupig/DiPlay)](LICENSE)
[![Issues](https://img.shields.io/github/issues/Tupig/DiPlay)](https://github.com/Tupig/DiPlay/issues)

> **为兼容的比亚迪（BYD）安卓车机提供有线及无线 CarPlay，采用熟悉的 DiAuto 风格界面。** DiPlay 是独立应用，正式版包名为 `com.shihab.diplay`。
>
> **CarPlay for compatible BYD Android head units.** Wired and wireless, with the familiar DiAuto interface. DiPlay is an independent app: `com.shihab.diplay`.

> ⚠️ **注意：** 这些项目专注于比亚迪汽车。它们可能在其他品牌上运行，但其他品牌不在支持范围内，也没有增加支持或修复其品牌特定兼容性问题的计划。
>
> **Note:** These projects focus on BYD cars. They may work on other brands, but other brands are unsupported and there are no plans to add support or fix brand-specific incompatibilities.

[下载与官网 / Download & website](https://shihabal3amri.github.io/DiPlay/) · [0.2.19 版本 / Release v0.2.19](https://github.com/shihabal3amri/DiPlay/releases/tag/v0.2.19) · [报告问题 / Report a problem](https://github.com/Tupig/DiPlay/issues/new/choose) · [繁體中文指南 / Traditional Chinese guide](README.zh-TW.md)

![DiPlay home](site/assets/home.png)

## 目录 / Table of Contents

- [功能特性 / Features](#-功能特性--features)
- [快速开始 / Quick Start](#-快速开始--quick-start)
- [安装 / Installation](#-安装--installation)
- [适配车型 / Supported Vehicles](#️-适配车型--supported-vehicles)
- [构建 / Building](#️-构建--building)
- [常见问题 / FAQ](#-常见问题--faq)
- [贡献 / Contributing](#-贡献--contributing)
- [许可证 / License](#-许可证--license)
- [致谢 / Acknowledgments](#-致谢--acknowledgments)

## ✨ 功能特性 / Features

| 功能 | 说明 |
| --- | --- |
| ✅ 有线 USB 与无线 CarPlay | 使用本地认证；无需越狱、转接盒、Mac、账户或认证服务器 |
| ✅ BYD HUD 导航 | 在已验证固件上提供挡风玻璃导航箭头、距离与路名 |
| ✅ 车机热点 | 支持车机热点、改进的音频缓冲与可保存的接收端诊断 |
| ✅ 自动地址发现 | 固定信道 Wi-Fi 回退，并记住成功的配置 |
| ✅ 显示调节 | 图标／文字大小、分辨率和帧率可调；应用显示改动会重连 CarPlay |
| ✅ 诊断报告 | 本地导出诊断报告，只有你主动选择才会分享 |
| ✅ 与 DiAuto 并存 | 可与 DiAuto 并存安装，同一时间只运行一个投屏应用 |

| Feature | Description |
| --- | --- |
| ✅ Wired USB and wireless CarPlay | With local authentication; no jailbreak, dongle, Mac, account or authentication server is required |
| ✅ BYD HUD navigation | Arrows, distance and street names on the windshield HUD on verified firmware |
| ✅ Car hotspot | Car hotspot support, improved audio buffering and saved receive diagnostics |
| ✅ Automatic address discovery | Fixed-channel Wi-Fi fallbacks and successful-configuration memory |
| ✅ Display adjustments | Icon/text size, resolution and frame rate; applying a display change reconnects CarPlay |
| ✅ Diagnostic reports | Local diagnostic export; reports are sent only if you choose to share them |
| ✅ Separate installation alongside DiAuto | Run one projection app at a time |

0.2.19 的主要变化：许多车机上有线 CarPlay 启动快了几秒；通话、Siri 与导航语音的时序修正，CarPlay 在后台时音乐不再断音；DiLink 4 仪表地图开启时不再首次触摸即重连、保留状态栏时倒车影像后画面不再缩小；新增日语、补齐繁体中文与更多阿拉伯语；“设置 → 诊断 → 在手机上取得报告”可通过二维码或地址验证码在手机上下载报告；CarPlay 内设置菜单重设计；可选仪表地图专辑封面卡片。完整列表见 [CHANGELOG.md](CHANGELOG.md) 与 [0.2.19 发行说明](docs/RELEASE-NOTES-0.2.19.md)。

What's new in 0.2.19: wired CarPlay starts several seconds sooner on many head units; calls, Siri and navigation prompts keep the right timing, and background music no longer underruns; with the DiLink 4 dashboard map on, CarPlay no longer restarts at the first touch or stays scaled down after the reversing camera; Japanese added, Traditional Chinese complete and more Arabic translated; **Settings → Diagnostics → Get the report on a phone** downloads the diagnostic report by QR code or a typed address and code; a redesigned settings menu inside CarPlay; an optional album-artwork card on the dashboard map. See [CHANGELOG.md](CHANGELOG.md) and [0.2.19 release notes](docs/RELEASE-NOTES-0.2.19.md).

## 🚀 快速开始 / Quick Start

1. 停好车。从[官方发布页](https://github.com/shihabal3amri/DiPlay/releases/tag/v0.2.19)下载 `DiPlay-0.2.19.apk`（[官网](https://shihabal3amri.github.io/DiPlay/)亦有链接）。本 fork 的 `TuPigPlay-0.0.0.1.apk` 需从源码自行构建，见 [BUILD.md](docs/BUILD.md)。
2. 将 APK 安装到**车机**上，而不是 iPhone 上。车机须允许安装 APK，系统为 Android 7.1+（API 25+）。
3. 打开 DiPlay，按需授予权限：蓝牙／附近设备、麦克风（Siri／通话）、通知；连接前先关闭其他投屏应用。
4. 默认使用车内自带热点：在车机设置中开启热点（支持则选 5 GHz），在 **Settings → Connection → Connection setup → Built-in car hotspot** 中保存热点名称与密码；iPhone 保持蓝牙与 Wi-Fi 开启并与车辆配对，然后点按 **Connect phone**。

1. Park the car. Download `DiPlay-0.2.19.apk` from the [official release](https://github.com/shihabal3amri/DiPlay/releases/tag/v0.2.19) (also linked on the [website](https://shihabal3amri.github.io/DiPlay/)). This fork's `TuPigPlay-0.0.0.1.apk` is built from source; see [BUILD.md](docs/BUILD.md).
2. Install the APK on the **car head unit**, not the iPhone. The head unit must permit APK installation and run Android 7.1+ (API 25+).
3. Open DiPlay and grant the permissions you need: Bluetooth/Nearby devices, microphone (Siri/calls), notifications. Close other phone-projection apps before connecting.
4. The built-in car hotspot is the default: turn on the car hotspot (5 GHz if available) in the car's settings, save its exact name and password under **Settings → Connection → Connection setup → Built-in car hotspot**, keep Bluetooth and Wi-Fi enabled on the iPhone, pair it with the car, then tap **Connect phone**.

安装命令 / Install command:

```sh
adb install -r DiPlay-0.2.19.apk
```

> 💡 提示：ADB 只是车机支持时的可选安装方式，核心 CarPlay 连接不要求 ADB。
> Tip: ADB is an optional installation method where the car supports it; core CarPlay does not require ADB.

完整安装、无线模式、USB 与连接恢复见 [INSTALL.md](docs/INSTALL.md)。
See [INSTALL.md](docs/INSTALL.md) for full installation, wireless modes, USB and connection recovery.

## 📦 安装 / Installation

> ⚠️ **注意：请安装在车机上，而不是 iPhone 上。** 无需越狱、转接盒、Mac、账户或认证服务器。核心 CarPlay 不需要 ADB；可选的仪表盘、电量、轮速和驻车视频功能需要支持的固件与已授权的网络 ADB。车机必须允许安装 APK；APK 支持 Android 7.1+（API 25），Android 7.1–8.1 尚未经实车验证。
>
> **Install on the car, not the iPhone.** No jailbreak, dongle, Mac, account or authentication server is required. Core CarPlay does not require ADB; optional dashboard, battery, wheel-speed and parked-video features do (supported firmware and approved network ADB). Your head unit must permit APK installation. The APK supports Android 7.1+ (API 25); Android 7.1–8.1 support is not yet confirmed on a vehicle.

> ⚠️ **注意：本产品未经 Apple 认证。** APK 内置的是从公开 Carlinkit 固件中恢复的实验性配件身份，并非为 DiPlay 新签发的 MFi 身份；其中的私钥可被提取。未来 iOS 版本是否继续接受、各车机上的可靠性，以及该身份是否适合公开分发，均未确定。此版本用于社区测试，不保证普遍兼容。
>
> **This is not an Apple-certified product.** The APK bundles an experimental accessory identity recovered from public Carlinkit firmware, not a newly provisioned MFi identity for DiPlay. A bundled private key is extractable. Acceptance after future iOS updates, reliability across head units and suitability of that identity for general distribution are unresolved. This release invites community testing; it is not a guarantee of universal compatibility.

> 💡 **提示：** 无线支持 Wi-Fi Direct、车机自带热点或现有 Wi-Fi／同一局域网。Android 7.1–9 的 Wi-Fi Direct 使用依赖固件的旧版建组路径（生成的组凭据、请求频率未经验证），见 [Android 9 Wi-Fi Direct](docs/ANDROID9_WIFI_DIRECT.md)；Android 10+ 会校验协商出的组频率。
>
> Wireless supports Wi-Fi Direct, the car's built-in hotspot or Existing Wi-Fi / Same LAN. Android 7.1–9 Wi-Fi Direct uses a firmware-dependent legacy path with generated group credentials and unverified requested frequency; see [Android 9 Wi-Fi Direct](docs/ANDROID9_WIFI_DIRECT.md). Android 10+ verifies its negotiated group frequency.

就地升级时，使用相同的包名/变体和匹配的签名证书才能保留设置和配对记录；各版本构建与签名校验记录见 [VALIDATION.md](docs/VALIDATION.md)，不要凭文件名推断升级兼容性。

For an in-place update that preserves settings and pairing records, use the same package/variant and a matching signing certificate. Release-specific build and signing checks are recorded in [validation](docs/VALIDATION.md); do not infer update compatibility from the filename.

### 📚 文档 / Documentation

| 主题 / Topic | 指南 / Guide |
| --- | --- |
| 安装与连接 / Install and connect | [INSTALL.md](docs/INSTALL.md) |
| 零跑安装说明 / Leapmotor install notes | [LEAPMOTOR.md](docs/LEAPMOTOR.md) · [LEAPMOTOR_C11_2024_INSTALL.md](docs/LEAPMOTOR_C11_2024_INSTALL.md) |
| 兼容性与故障排查 / Compatibility and troubleshooting | [COMPATIBILITY.md](docs/COMPATIBILITY.md) |
| 流畅的无线 CarPlay / Smooth wireless CarPlay | [SMOOTH_WIRELESS.md](docs/SMOOTH_WIRELESS.md) |
| 自动加入热点 / Automatic hotspot joining | [WIRELESS_HOTSPOT_JOIN.md](docs/WIRELESS_HOTSPOT_JOIN.md) |
| 现有 Wi-Fi／同一局域网 / Existing Wi-Fi / Same LAN | [EXISTING_WIFI.md](docs/EXISTING_WIFI.md) |
| 连接设置 / Connection setup | [CONNECTION_SETUP.md](docs/CONNECTION_SETUP.md) |
| 隐私与诊断报告 / Privacy and diagnostic reports | [PRIVACY.md](docs/PRIVACY.md) |
| 从源码构建 / Build from source | [BUILD.md](docs/BUILD.md) |
| 校验记录 / Validation | [VALIDATION.md](docs/VALIDATION.md) |
| 版本说明 / Release notes | [CHANGELOG.md](CHANGELOG.md) |
| 第三方声明与许可 / Credits and licenses | [THIRD_PARTY_NOTICES.md](docs/THIRD_PARTY_NOTICES.md) |
| 繁體中文指南 / Traditional Chinese guide | [README.zh-TW.md](README.zh-TW.md) |

## 🖥️ 适配车型 / Supported Vehicles

| 车型／车机 | 平台・系统 | 屏幕 | 状态／证据 |
| --- | --- | --- | --- |
| BYD DiLink 5.1（开发用车） | — | — | 实车验证：挡风玻璃实时导航与路名可用，车机热点可启动 CarPlay，Wi-Fi Direct 性能明显提升 |
| BYD 唐 Tang 2024（DiLink 5.0） | — | 2560×1440 | 流畅无线参数实测车型；方向盘速度与视频功能贡献在唐（DiLink 5.0）＋ iPhone 15 Pro / iOS 27 上测试，隧道内轮速推算未验证 |
| BYD 汉 Han DM-i 2022（DiLink 4.0 / Android 10，固件 21.1.21.2401160.1） | — | — | 现有 Wi-Fi／同一局域网全新安装验证，Same LAN 顺利进入 CarPlay |
| BYD Seal 2022（DiLink 4） | — | — | DiLink 4 投屏／画面修复所测试的车型 |
| BYD DiLink 3 | — | 1280×480 投屏表面 | 仪表盘地图路径已识别该表面，实际地图输出仍需实车复测 |
| BOS Mini A1 车机 | Android 9 | — | 软件 Opus 麦克风回退已被接受（iPhone 12 / iOS 27） |
| Geely／领克 `geely_fx11`（CS11_A4，DesaySV） | Android 11 | — | 方向盘按键经厂商输入服务转发（仅在 CarPlay 会话期间订阅） |
| 零跑 Leapmotor C11 2024 增程（LeapOS 3.21.40） | 高通 SA8295P | 14.6 英寸 2560×1600 中控 + 12.3 英寸仪表盘 + 副驾娱乐屏 | 未实车验证；U 盘安装与白名单资料见 [LEAPMOTOR_C11_2024_INSTALL.md](docs/LEAPMOTOR_C11_2024_INSTALL.md) |

| Car / head unit | Platform・OS | Screen | Status / evidence |
| --- | --- | --- | --- |
| BYD DiLink 5.1 (development car) | — | — | Verified on a real car: live windshield guidance and street names work, the car hotspot starts CarPlay, Wi-Fi Direct performance is substantially improved |
| BYD Tang 2024 (DiLink 5.0) | — | 2560×1440 | Smooth-wireless measurements were taken on this car; wheel-speed and video contributions were tested on a BYD Tang with DiLink 5.0 and an iPhone 15 Pro on iOS 27; wheel-speed dead reckoning in tunnels remains unverified |
| BYD Han DM-i 2022 (DiLink 4.0 / Android 10, firmware 21.1.21.2401160.1) | — | — | Clean-install validation for Existing Wi-Fi / Same LAN; Same LAN entered CarPlay with no issues noticed |
| BYD Seal 2022 (DiLink 4) | — | — | The car for which the DiLink 4 casting/picture fixes describe the tested setup |
| BYD DiLink 3 | — | 1280×480 projection surface | The surface is recognized by the cluster-map path; actual map output still needs an on-car retest |
| BOS Mini A1 head unit | Android 9 | — | Software Opus microphone fallback accepted (iPhone 12 on iOS 27) |
| Geely/Lynk & Co `geely_fx11` (CS11_A4, DesaySV) | Android 11 | — | Steering-wheel keys forwarded through the vendor input service (subscribed only while a CarPlay session is active) |
| Leapmotor C11 2024 EREV (LeapOS 3.21.40) | Qualcomm SA8295P | 14.6-inch 2560×1600 centre screen + 12.3-inch cluster + passenger display | Unvalidated; U-disk install and whitelist facts are collected in [LEAPMOTOR_C11_2024_INSTALL.md](docs/LEAPMOTOR_C11_2024_INSTALL.md) |

更广泛的车机与 iOS 兼容性不作保证；HUD 固件范围与清理限制见 [BYD 导航](docs/BYD_NAVIGATION.md)。历史版本说明见 [CHANGELOG.md](CHANGELOG.md)。

Broader head-unit and iOS compatibility is not guaranteed. The HUD firmware scope and cleanup limits are documented in [BYD navigation](docs/BYD_NAVIGATION.md). Historical release notes are linked from [CHANGELOG.md](CHANGELOG.md).

## 🛠️ 构建 / Building

完整构建指南（模块选择、环境准备、独立车载测试 APK、release 打包与故障排查）见 [BUILD.md](docs/BUILD.md)。

For the full build guide (module selection, environment setup, the standalone car-test APK, release packaging and troubleshooting), see [BUILD.md](docs/BUILD.md).

| 项 / Item | 要求 / Requirement |
| --- | --- |
| JDK | 25 |
| Android SDK | Platform 37（`platforms;android-37.0`）、Build-Tools 36.0.0、Platform-Tools |
| Android NDK | 28.2.13676358 |
| 主应用 / Main app | Gradle 模块 `:mobile` → `mobile/build/outputs/apk/debug/mobile-debug.apk` |

主应用调试构建 / Main app debug build:

```sh
./gradlew :mobile:assembleDebug
```

与 CI 相同的检查 / The same checks CI runs:

```sh
./gradlew :shared:testDebugUnitTest :common:testDebugUnitTest :mobile:lintDebug :mobile:assembleDebug
```

> 💡 提示：Debug 应用 ID 为 `com.shihab.diplay.hudtest`，Release 为 `com.shihab.diplay`；可用 `-PdiplayApplicationId=` 覆盖应用 ID，详见 [BUILD.md](docs/BUILD.md)。
> Tip: the debug application ID is `com.shihab.diplay.hudtest` and the release one is `com.shihab.diplay`; the application ID can be overridden with `-PdiplayApplicationId=` — see [BUILD.md](docs/BUILD.md).

## ❓ 常见问题 / FAQ

**问：DiPlay 应该装在哪里？**

DiPlay 安装在**车机**上，而不是 iPhone 上。iPhone 只需启用 CarPlay，通过蓝牙／Wi-Fi／USB 连接。

**Q: Where is DiPlay installed?**

DiPlay is installed on the **car head unit**, not on the iPhone. The iPhone only needs CarPlay enabled; it connects over Bluetooth/Wi-Fi/USB.

**问：使用 DiPlay 需要 ADB 吗？**

核心 CarPlay（有线与无线连接）不需要 ADB。可选的仪表盘地图、电量、轮速和驻车视频等 BYD 功能需要支持的固件与已授权的网络 ADB。

**Q: Does DiPlay need ADB?**

Core CarPlay (wired and wireless) does not require ADB. Optional BYD features such as the dashboard map, battery, wheel speed and parked video need supported firmware and approved network ADB.

**问：无线连接有哪几种方式？卡顿怎么办？**

车机自带热点（推荐）、Wi-Fi Direct、现有 Wi-Fi／同一局域网，另有有线 USB。卡顿时优先使用车机热点、Default 图标、30 fps 和较低分辨率，保持 HEVC 关闭，然后附上诊断报告；信道与画面尺寸的实测见 [SMOOTH_WIRELESS.md](docs/SMOOTH_WIRELESS.md)。

**Q: Which wireless modes exist, and what about stutter?**

Built-in car hotspot (recommended), Wi-Fi Direct, Existing Wi-Fi / Same LAN, plus wired USB. For stutter, prefer the car hotspot, Default icons, 30 fps and a lower resolution with HEVC off, then attach a diagnostic report; see [SMOOTH_WIRELESS.md](docs/SMOOTH_WIRELESS.md) for measured channel and picture-size results.

**问：如何报告问题？**

在 **0.2.19** 上复现问题并记录发生时间；打开 **Settings → Diagnostics → Save diagnostic report**（Android 10+ 通常保存到 **Downloads/DiPlay**；Android 7.1–9 会请求存储权限）。检查 `.txt` 并删除隐私信息，再附到匹配的[已有 issue](https://github.com/Tupig/DiPlay/issues) 或[新建 issue](https://github.com/Tupig/DiPlay/issues/new/choose)，并注明车型／车机、DiLink/Android/完整固件、iPhone/iOS、连接方式、相关设置、复现步骤与失败时间。报告不会自动上传；切勿公开热点密码。

**Q: How do I report a problem?**

Reproduce it on **0.2.19** and note the failure time, then use **Settings → Diagnostics → Save diagnostic report** (Android 10+ normally saves to **Downloads/DiPlay**; Android 7.1–9 asks for storage access). Review the `.txt`, remove private information, and attach it to a matching [existing issue](https://github.com/Tupig/DiPlay/issues) or [create one](https://github.com/Tupig/DiPlay/issues/new/choose), with car/head-unit model, DiLink/Android/full firmware, iPhone/iOS, connection mode, relevant settings, reproduction steps and the failure time. Reports are shared only when you choose; never post your hotspot password.

**问：为什么导航小组件或悬浮地图不显示？**

标准导航小组件需要支持 Android 小组件的启动器；比亚迪内置主页不接受任意小组件。悬浮地图和嵌入地图需要先启用“CarPlay 仪表地图”。

**Q: Why does the navigation widget or the floating map not appear?**

The standard navigation widget needs a launcher that supports Android widgets; the BYD built-in home does not accept arbitrary widgets. The floating and embedded maps require the CarPlay dashboard map to be enabled first.

**问：DiPlay 支持哪些语言？**

应用与网站源码包含英语、阿拉伯语、俄语、乌克兰语、西班牙语、简体中文、繁体中文（台湾）和日语。Android 13+ 的应用语言与系统按应用语言设置同步；香港／澳门及 Hant 选择使用台湾译文。繁体指南见 [README.zh-TW.md](README.zh-TW.md)。

**Q: Which languages does DiPlay support?**

The app and website source include English, Arabic, Russian, Ukrainian, Spanish, Simplified Chinese, Traditional Chinese (Taiwan) and Japanese. On Android 13+ the app language stays synchronized with Android's per-app language setting; Hong Kong/Macao and Hant selections use the Taiwan translation. See the [Traditional Chinese guide](README.zh-TW.md).

**问：DiPlay 与 DiAuto 是什么关系？**

两者可并存安装，但同一时间只运行一个投屏应用。

**Q: How does DiPlay relate to DiAuto?**

They install side by side, but run only one projection app at a time.

## 🤝 贡献 / Contributing

欢迎通过 [issue 模板](https://github.com/Tupig/DiPlay/issues/new/choose) 报告缺陷、连接问题或提出需求；连接类问题请附诊断日志。提交 PR 时请关联对应 issue（`Closes #N`），并按 PR 模板列出自测清单。

Bug reports, connection problems and feature requests are welcome via the [issue templates](https://github.com/Tupig/DiPlay/issues/new/choose); connection reports need diagnostic logs. Reference the corresponding issue in your PR (`Closes #N`) and fill in the PR template's self-check list.

## 📄 许可证 / License

本项目基于 [xcertplay](https://github.com/shilapi/xcertplay)（GPL-3.0），许可证见 [LICENSE](LICENSE)。主界面／设置界面与网站改编自 [DiAuto](https://github.com/shihabal3amri/DiAuto)（AGPL-3.0），该许可证位于 `docs/licenses`；分发修改版时请保留这些声明（见 [THIRD_PARTY_NOTICES.md](docs/THIRD_PARTY_NOTICES.md)）。CarPlay 及其图标属 Apple Inc.；本项目不代表 Apple 或比亚迪的认可。

Based on [xcertplay](https://github.com/shilapi/xcertplay), GPL-3.0; see [LICENSE](LICENSE). The home/settings UI and website adapt [DiAuto](https://github.com/shihabal3amri/DiAuto), AGPL-3.0; that license is included in `docs/licenses`. Preserve those notices when distributing modifications (see [THIRD_PARTY_NOTICES.md](docs/THIRD_PARTY_NOTICES.md)). CarPlay and its icon belong to Apple Inc.; no Apple or BYD affiliation or endorsement is implied.

本仓库以一份干净的公开源码快照为起点：本地研究、测试者报告与发布签名密钥不在仓库内；与 APK 对应的完整源码随每个版本提供。凭据与发布打包策略见 [SECURITY.md](SECURITY.md)。

This repository starts with a clean public source snapshot: local research, tester reports and release-signing secrets are excluded; the complete source corresponding to the APK is provided with every release. See [SECURITY.md](SECURITY.md) for the credentials and release-packaging policy.

## 🙏 致谢 / Acknowledgments

- 上游项目 [shihabal3amri/DiPlay](https://github.com/shihabal3amri/DiPlay)：本仓库是其 fork；上游的发布、issue 与网站由上游项目托管，本 fork 的改动记录在其提交历史中。在依赖某个改动之前，请先在自己的车机上重新验证。
- [xcertplay](https://github.com/shilapi/xcertplay)（GPL-3.0）：原始 CarPlay 接收端，其 README 保留于 [docs/UPSTREAM-README.md](docs/UPSTREAM-README.md)。
- [DiAuto](https://github.com/shihabal3amri/DiAuto)（AGPL-3.0）：主界面与设置界面的基础。
- [LIVI](https://github.com/f-io/LIVI) 与 [showcase](https://github.com/amineross/showcase)：xcertplay 的重要参考（见 [docs/UPSTREAM-README.md](docs/UPSTREAM-README.md)）。
- 所有测试者与贡献者：诊断报告与实车反馈让每一步改进成为可能。

- Upstream [shihabal3amri/DiPlay](https://github.com/shihabal3amri/DiPlay): this repository is a fork of it; releases, upstream issues and the website are hosted by the upstream project, and this fork's changes are recorded in its commit history. Re-test on your own head unit before relying on a change.
- [xcertplay](https://github.com/shilapi/xcertplay) (GPL-3.0): the original CarPlay receiver; its README is preserved at [docs/UPSTREAM-README.md](docs/UPSTREAM-README.md).
- [DiAuto](https://github.com/shihabal3amri/DiAuto) (AGPL-3.0): the basis of the home and settings UI.
- [LIVI](https://github.com/f-io/LIVI) and [showcase](https://github.com/amineross/showcase): important references for xcertplay (see [docs/UPSTREAM-README.md](docs/UPSTREAM-README.md)).
- All testers and contributors: diagnostic reports and real-car feedback make every improvement possible.
