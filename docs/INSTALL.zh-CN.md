# 安装与连接

[English](INSTALL.md) · **简体中文**

1. 停好车。从[官方发布页](https://github.com/shihabal3amri/DiPlay/releases/tag/v0.2.17)下载 `DiPlay-0.2.17.apk`，[官网](https://shihabal3amri.github.io/DiPlay/)上也有下载链接。官方应用要求 Android 7.1 或更高版本（API 25+）；Android 7.1–8.1 的支持仍需实车验证。
2. 使用车机支持的 APK 安装方式安装到 Android 车机上。不要装到 iPhone 上。就地升级时请使用相同的包名/变体和匹配的签名证书，这样才能保留设置和配对记录。各版本的构建与签名校验记录在 [validation](VALIDATION.md) 中；不要凭文件名推断升级兼容性。
3. 打开 DiPlay。为你所用到的功能授予权限：蓝牙/附近设备、较旧 Android 上的 Wi-Fi/位置权限，以及用于 Siri/通话的麦克风权限。允许通知以便使用连接控制。
4. 连接前先关闭其他手机投屏应用。

## 无线连接

车内自带热点是默认方式。打开 **Settings → Connection → Connection setup → Built-in car hotspot**，如果支持就打开 5 GHz 车内热点，并准确记下它的名称和密码。保持 iPhone 上的蓝牙和 Wi-Fi 处于开启状态，与车辆配对，然后点按 **Connect phone**。在兼容的固件上，蓝牙握手会自动下发热点凭据供手机加入；核心 CarPlay 并不依赖 ADB。如果 iPhone 没有自动加入，请记录手动加入是否能启动 CarPlay，并参阅单独确认过的[实验性加入修复](WIRELESS_HOTSPOT_JOIN.md)。**Choose iPhone** 用于更换已选中的配对设备。参见[设置指南](CONNECTION_SETUP.md)。

当车机实现了所需的固件 API 时，Wi-Fi Direct 是 Android 7.1+ 上的另一种选择。Android 7.1–9 使用生成的组凭据，无法校验请求的频段；Android 10+ 保留协商频段的校验。旧版的信道固定/清理限制见 [Android 9 Wi-Fi Direct](ANDROID9_WIFI_DIRECT.md)。**Existing Wi-Fi / Same LAN** 通过外部路由器或便携 Wi-Fi 连接：把车和 iPhone 接入同一个网络，关闭路由器的客户端隔离，并在 **Settings → Connection → Connection setup** 下保存该网络。保持蓝牙开启。DiPlay 不会替你加入网络或修改默认路由；参见[同一局域网设置](EXISTING_WIFI.md)。Local hotspot 选项已移除；原有的选择会迁移到内置热点。USB 依然可用。

在 **Settings → Connection → Connection setup → Wi-Fi Direct → Preferred channel** 中，Auto 保持自动选信道：在 5 GHz 接入点旁，它会先尝试符合条件的已保存/对齐信道以及显式的 2.4 GHz，之后才回退到其他 5 GHz/系统默认信道。这并不保证一定使用某个频段。**Auto · 5 GHz** 和 **Auto · 2.4 GHz** 会在设备支持的前提下把自动尝试限定在所选频段；旧版 Android 无法校验协商出的频段。你可以保存一个受支持的 2.4/5 GHz 信道供下次连接使用；已建立的会话会一直持续到重新连接。如果无线电拒绝该信道或创建了不同的信道，请改选 Auto 或其他信道后重新连接。地区/无线电的支持范围仍然适用，而且选择信道并不能证明确卡问题已修复。

**Settings → Connection** 下可选的自动启动内置热点默认关闭。在受支持的 BYD 固件上，显式开启后，DiPlay 会通过已授权的本地 ADB 为自身包名校验权限。它会保留车辆已保存的热点名称/密码；开机打开与打开即连接是另外两个独立设置。ADB 兜底方案要求每次请求时都存在可达且已授权的连接，并且固件要公开声明 Wi-Fi 热点命令；只有在观察到热点就绪后才会报告成功。这并不代表每个 DiLink 版本都支持。参见[连接设置](CONNECTION_SETUP.md)。

## USB

用支持数据传输的线缆把 iPhone 接到 USB **数据**口，并选择 **Connect with USB**。按提示批准 USB 访问、Trust/CarPlay 以及本地 VPN 权限。本地 VPN 承载的是 USB 网络链路，它不是上网用的 VPN 服务。仅供电的接口/线缆无法工作。如果有线 CarPlay 每隔几秒就断开、iPhone 又自动重连，可以先试 **Settings → Connection → USB connection → iPhone charging → Reduced**，再试 **Low**；代价是 iPhone 充电会变慢。

## 设置

首次启动时的 DiLink 设置向导可以跳过，也可以从 Settings 重新打开。DiPlay 每天检查一次 GitHub 官方更新，并在首页显示 **Update available**；**About** 页面点一下即可下载，并可关闭后台检查。APK 安装由 Android 系统处理。**App appearance** 用于选择 DiPlay 本身的浅色、深色或自动模式，与 CarPlay 的日间/夜间模式互不影响。

在 CarPlay 中按设置的手指数量下滑可打开快捷菜单，或返回主屏幕。在 **Settings → Vehicle → CarPlay controls** 中或快捷菜单内可选择两指、三指、四指或 **Off**；默认仍为三指。在 Back 或完整设置链接会丢弃尚未应用的更改之前，快捷菜单会先询问。图标/文字大小、分辨率和帧率在会话进行中使用 **Apply and reconnect** 生效。仅做出选择并不会应用；点 Cancel 会保留原设置。未连接时，**Save** 会在下次连接时生效。请遵循每项设置的说明：有的即时生效，有的则会显示 **Reconnect now** 并在下次连接时生效。

**Settings → Display** 包含图标/文字大小、分辨率、帧率、日/夜间外观、画面调节、Dock 位置、系统栏和 **Interface size**。Interface size 改变的是 DiPlay 自身的控件，而不是 CarPlay 的画面。建议先从 30 fps 和 Default 图标/文字大小开始；在 **Settings → Advanced → Video and audio** 中保持 **Efficient video**（HEVC）关闭。车机较慢时可尝试 80% 或 60% 的分辨率。部分 iPhone/车机组合仍会忽略图标/文字缩放。

**Settings → Audio** 包含媒体/导航音频路由和音乐缓冲选择。**Settings → Navigation** 包含位置上报，以及在受支持车机上的 BYD 导航卡片。**Settings → Vehicle** 包含 CarPlay 手势、方向盘按键和车机按键自定义。连接设置、自动连接和权限位于 **Settings → Connection**；诊断报告位于 **Settings → Diagnostics**。

车辆数据模式以及电量/轮速/驻车视频控制位于 **Settings → Advanced → Advanced vehicle data**。电量上报、轮速上报和驻车视频控制默认关闭。默认的 DiLink 5.0 模式仍为默认。旧模式为可选，使用有界的只读探测且需要已授权的网络 ADB；它只暴露已确认的字段。若出现替换既有已保存字段的提示，请仔细查看。这并不会开启 ADB、写入车辆设置，也不代表每一款较老的车机都受支持。

可选的方形画布旋转、分屏区域和侧边栏位于 **Settings → Advanced → Display (experimental)**，默认关闭。画布越大，解码器/GPU 的负担越重。Dock 位置仍在 **Settings → Display** 下。

**Smooth video (experimental)**、**Buffered music (experimental)**、**Call echo cancellation (experimental)** 和 **Clearer call voices (experimental)** 位于 **Settings → Advanced → Video and audio**，默认关闭。Smooth video 可能增加触控响应延迟，并使画面调节不可用。更改 Smooth video 或 Buffered music 会让进行中的会话重连；通话回声消除和人声过滤则在下次连接时生效。Buffered music 接受支持它的应用输出的 AAC-LC，不支持无损音频。同一卡片中的 **Low-latency decoding (experimental)** 和 **Direct video output (experimental)** 同样默认关闭。Low-latency decoding 在下次连接时生效；Direct video output 会让 CarPlay 重连并使画面调节不可用。如果画面出现花屏或卡顿，请关闭其中之一。**Settings → Diagnostics → FPS counter** 会显示每秒呈现与接收的帧数以及解码耗时。

独立的实验性 **CarPlay call keys** 和 **CarPlay calls on the dashboard** 仍默认关闭。它们出现在 **Settings → Navigation → BYD navigation** 下；若 BYD 导航卡片不可用，则出现在 **Settings → Advanced → Advanced vehicle data** 下。仪表盘卡片需要已授权的 ADB 和对应的固件。启用前请先阅读 [0.2.17 限制说明](RELEASE-NOTES-0.2.17.md)。

应用和发布网站提供七种语言，包括繁体中文（台湾）。Android 13+ 的应用语言选择与 Android 系统设置同步；Android 7.1–12 则保留已保存的上下文覆盖。香港/澳门用语和繁体使用台湾版应用翻译，并非独立的地区版本。

## 连接恢复与报告

如果重装后残留了旧的组，请先关闭其他投屏应用，再使用 **Settings → Connection → Wireless connection help → Reset CarPlay Wi-Fi**。在移除无法识别的 Wi-Fi Direct 组之前，DiPlay 会先询问。就地升级优于卸载重装。

请在 **0.2.17** 上复现尚未解决的问题并导出一份全新报告，即便你此前已经发送过旧日志。请从第一次连接尝试一直采集到失败发生；如果是开机/自启动问题，请重启后手动打开 DiPlay 再导出。使用 **Settings → Diagnostics → Save diagnostic report**。Android 10+ 通常保存到 **Downloads/DiPlay**；Android 7.1–9 会请求存储权限，同样保存在那里。**Choose save location** 也可用。如果文件选择器或 Downloads 存储不可用，兜底的 TXT 文件会保存在应用专属外部存储中，确认提示会显示其确切路径。若外部存储同样不可用，DiPlay 会把报告私密保存。两种兜底方式都提供 **View report** 和 **Share**；当没有可用的分享应用时，可在报告查看界面中选择/复制文本。请检查 `.txt` 文件，然后将其附加到你已有的 [GitHub issue](https://github.com/shihabal3amri/DiPlay/issues) 或[创建新 issue](https://github.com/shihabal3amri/DiPlay/issues/new/choose)，并附上车型/车机型号、确切的 DiLink/Android/固件版本、iPhone/iOS 版本、连接后端（USB、内置热点、Wi-Fi Direct 或同一局域网）、相关设置、期望与实际表现、复现步骤以及大致的失败时间。切勿包含你的热点密码。不会自动上传任何内容。

额外的无线/媒体/主题/退出自身应用记录有助于定位失败环节；但它们并不能证明 Qin Plus 启动、Wi-Fi Direct 卡顿、Siri、iOS 15 或日/夜间固件相关报告的问题已解决。参见 [0.2.17 发行说明](RELEASE-NOTES-0.2.17.md)。

APK 安装限制由你的车辆固件决定。ADB 是在你的车支持时的可选项，并非应用运行时的必要条件：

```sh
adb install -r DiPlay-0.2.17.apk
```

只使用受信任的电脑。使用不同的签名证书无法更新这个构建；在保存好你需要的报告之前，不要卸载。

只安装厂商认可包的固件需要先获得该批准。[Leapmotor 说明](LEAPMOTOR.md)记录了已公开的 零跑 C 系列白名单、U盘 安装流程，以及本仓库提供的实验性构建覆盖方式。这些都不构成兼容性承诺。

## BYD 导航

关于固件适用范围、地图元数据要求、设置项和清理行为，参见 [BYD 导航显示](BYD_NAVIGATION.md)。原生 DiLink 5 显示路由不需要外部 ADB 启动器。可选的 DiLink 3/4 集成有各自的固件与已授权 ADB 要求，详见该指南。
