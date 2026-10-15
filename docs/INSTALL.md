# 安装与连接 / Install and connect

1. 停好车。从[官方发布页](https://github.com/Tupig/DiPlay/releases)下载 `TuPigPlay-0.0.0.1.apk`，[官网](https://tupig.github.io/DiPlay/)上也有下载链接。官方应用要求 Android 7.1 或更高版本（API 25+）；Android 7.1–8.1 的支持仍需实车验证。
2. 使用车机支持的 APK 安装方式安装到 Android 车机上。不要装到 iPhone 上。就地升级时请使用相同的包名/变体和匹配的签名证书，这样才能保留设置和配对记录。各版本的构建与签名校验记录在 [validation](VALIDATION.md) 中；不要凭文件名推断升级兼容性。
3. 打开 TuPigPlay。为你所用到的功能授予权限：蓝牙/附近设备、较旧 Android 上的 Wi-Fi/位置权限，以及用于 Siri/通话的麦克风权限。允许通知以便使用连接控制。
4. 连接前先关闭其他手机投屏应用。

1. Park the car. Download `TuPigPlay-0.0.0.1.apk` from the [official release](https://github.com/Tupig/DiPlay/releases), also linked on the [website](https://tupig.github.io/DiPlay/). The official app requires Android 7.1 or newer (API 25+); Android 7.1–8.1 support still needs vehicle testing.
2. Install on the Android head unit using its supported APK installation method. Do not install on the iPhone. Use the same package/variant and a matching signing certificate for an in-place update that preserves settings and pairing records. Release-specific build and signing checks are recorded in [validation](VALIDATION.md); do not infer update compatibility from the filename.
3. Open TuPigPlay. Grant the permissions requested for the features you use: Bluetooth/Nearby devices, Wi-Fi/Location on older Android, and microphone for Siri/calls. Allow notifications for connection controls.
4. Close other phone-projection apps before connecting.

## 无线连接 / Wireless

车内自带热点是默认方式。打开 **Settings → Connection → Connection setup → Built-in car hotspot**，如果支持就打开 5 GHz 车内热点，并准确记下它的名称和密码。保持 iPhone 上的蓝牙和 Wi-Fi 处于开启状态，与车辆配对，然后点按 **Connect phone**。在兼容的固件上，蓝牙握手会自动下发热点凭据供手机加入；核心 CarPlay 并不依赖 ADB。如果 iPhone 没有自动加入，请记录手动加入是否能启动 CarPlay，并参阅单独确认过的[实验性加入修复](WIRELESS_HOTSPOT_JOIN.md)。**Choose iPhone** 用于更换已选中的配对设备。参见[设置指南](CONNECTION_SETUP.md)。

Built-in car hotspot is the default. Open **Settings → Connection → Connection setup → Built-in car hotspot**, turn on the car hotspot at 5 GHz if available, and save its exact name and password. Keep Bluetooth and Wi-Fi enabled on the iPhone, pair it with the car, then tap **Connect phone**. On compatible firmware, the Bluetooth handshake supplies hotspot credentials for automatic joining; core CarPlay does not require ADB. If the iPhone does not join automatically, record whether manual joining starts CarPlay and see the separately confirmed [experimental join repair](WIRELESS_HOTSPOT_JOIN.md). **Choose iPhone** changes the selected paired device. See [the setup guide](CONNECTION_SETUP.md).

当车机实现了所需的固件 API 时，Wi-Fi Direct 是 Android 7.1+ 上的另一种选择。Android 7.1–9 使用生成的组凭据，无法校验请求的频段；Android 10+ 保留协商频段的校验。旧版的信道固定/清理限制见 [Android 9 Wi-Fi Direct](ANDROID9_WIFI_DIRECT.md)。**Existing Wi-Fi / Same LAN** 通过外部路由器或便携 Wi-Fi 连接：把车和 iPhone 接入同一个网络，关闭路由器的客户端隔离，并在 **Settings → Connection → Connection setup** 下保存该网络。保持蓝牙开启。TuPigPlay 不会替你加入网络或修改默认路由；参见[同一局域网设置](EXISTING_WIFI.md)。Local hotspot 选项已移除；原有的选择会迁移到内置热点。USB 依然可用。

Wi-Fi Direct is an alternative on Android 7.1+ when the head unit implements the needed firmware APIs. Android 7.1–9 uses generated group credentials and cannot verify the requested frequency; Android 10+ retains negotiated-frequency verification. Legacy pinning/cleanup limits are in [Android 9 Wi-Fi Direct](ANDROID9_WIFI_DIRECT.md). **Existing Wi-Fi / Same LAN** connects through an external router or portable Wi-Fi: connect the car and iPhone to the same network, disable router client isolation, and save that network under **Settings → Connection → Connection setup**. Keep Bluetooth enabled. TuPigPlay does not join the network or change the default route for you; see [Same LAN setup](EXISTING_WIFI.md). The Local hotspot option has been removed; existing selections migrate to built-in hotspot. USB remains available.

在 **Settings → Connection → Connection setup → Wi-Fi Direct → Preferred channel** 中，Auto 保持自动选信道：在 5 GHz 接入点旁，它会先尝试符合条件的已保存/对齐信道以及显式的 2.4 GHz，之后才回退到其他 5 GHz/系统默认信道。这并不保证一定使用某个频段。**Auto · 5 GHz** 和 **Auto · 2.4 GHz** 会在设备支持的前提下把自动尝试限定在所选频段；旧版 Android 无法校验协商出的频段。你可以保存一个受支持的 2.4/5 GHz 信道供下次连接使用；已建立的会话会一直持续到重新连接。如果无线电拒绝该信道或创建了不同的信道，请改选 Auto 或其他信道后重新连接。地区/无线电的支持范围仍然适用，而且选择信道并不能证明确卡问题已修复。

In **Settings → Connection → Connection setup → Wi-Fi Direct → Preferred channel**, Auto keeps automatic selection: beside a 5 GHz station it tries eligible saved/aligned channels and explicit 2.4 GHz before other 5 GHz/system-default fallbacks. It does not guarantee a band. **Auto · 5 GHz** and **Auto · 2.4 GHz** constrain automatic attempts to the chosen band where supported; legacy Android cannot verify the negotiated frequency. You may save a supported 2.4/5 GHz channel for the next connection; an active session continues until reconnect. If the radio rejects it or creates a different channel, choose Auto or another channel and reconnect. Regional/radio support still applies, and channel choice does not establish stutter as fixed.

**Settings → Connection** 下可选的自动启动内置热点默认关闭。在受支持的 BYD 固件上，显式开启后，TuPigPlay 会通过已授权的本地 ADB 为自身包名校验权限。它会保留车辆已保存的热点名称/密码；开机打开与打开即连接是另外两个独立设置。ADB 兜底方案要求每次请求时都存在可达且已授权的连接，并且固件要公开声明 Wi-Fi 热点命令；只有在观察到热点就绪后才会报告成功。这并不代表每个 DiLink 版本都支持。参见[连接设置](CONNECTION_SETUP.md)。

Optional automatic built-in-hotspot startup under **Settings → Connection** is off by default. On supported BYD firmware, explicitly enabling it verifies permissions for TuPigPlay's own package through already-authorized local ADB. It preserves the car's saved hotspot name/password; boot-open and connect-on-open are separate settings. The ADB fallback requires a reachable, already-authorized connection on each request and a firmware-advertised Wi-Fi tethering command; it reports success only after observing hotspot readiness. This does not establish support on every DiLink version. See [connection setup](CONNECTION_SETUP.md).

## USB

用支持数据传输的线缆把 iPhone 接到 USB **数据**口，并选择 **Connect with USB**。按提示批准 USB 访问、Trust/CarPlay 以及本地 VPN 权限。本地 VPN 承载的是 USB 网络链路，它不是上网用的 VPN 服务。仅供电的接口/线缆无法工作。如果有线 CarPlay 每隔几秒就断开、iPhone 又自动重连，可以先试 **Settings → Connection → USB connection → iPhone charging → Reduced**，再试 **Low**；代价是 iPhone 充电会变慢。

Connect the iPhone to a USB **data** port with a data-capable cable and choose **Connect with USB**. Approve USB access, Trust/CarPlay and the local VPN permission if requested. The local VPN carries the USB network link; it is not an internet VPN service. Charge-only ports/cables cannot work. If wired CarPlay drops every few seconds and the iPhone reconnects by itself, try **Settings → Connection → USB connection → iPhone charging → Reduced**, then **Low**; the iPhone then charges more slowly.

## 设置 / Settings

首次启动时的 DiLink 设置向导可以跳过，也可以从 Settings 重新打开。TuPigPlay 每天检查一次 GitHub 官方更新，并在首页显示 **Update available**；**About** 页面点一下即可下载，并可关闭后台检查。APK 安装由 Android 系统处理。**App appearance** 用于选择 TuPigPlay 本身的浅色、深色或自动模式，与 CarPlay 的日间/夜间模式互不影响。

The first-launch DiLink setup guide can be skipped or reopened from Settings. TuPigPlay checks for official GitHub updates once a day and shows **Update available** on Home; **About** downloads it on your tap and can turn the background check off. Android handles APK installation. **App appearance** selects Light, Dark or Auto for TuPigPlay itself, separately from CarPlay day/night mode.

在 CarPlay 中按设置的手指数量下滑可打开快捷菜单，或返回主屏幕。在 **Settings → Vehicle → CarPlay controls** 中或快捷菜单内可选择两指、三指、四指或 **Off**；默认仍为三指。在 **CarPlay 快捷菜单 → Display → Fullscreen** 下可启用 **Temporary system bars (5 seconds)**：用相同手指数**上滑**可短暂显示被隐藏的车机状态栏/导航栏（用于空调控制等车辆功能），下滑仍打开快捷菜单。该选项默认关闭，在手势禁用、没有隐藏的系统栏、应用处于多窗口模式或哈曼厂商专属全屏模式激活时不可用。不支持的固件可能忽略该系统栏请求。它不会改变已保存的系统栏配置，也不会重连 CarPlay。在 Back 或完整设置链接会丢弃尚未应用的更改之前，快捷菜单会先询问。图标/文字大小、分辨率和帧率在会话进行中使用 **Apply and reconnect** 生效。仅做出选择并不会应用；点 Cancel 会保留原设置。未连接时，**Save** 会在下次连接时生效。请遵循每项设置的说明：有的即时生效，有的则会显示 **Reconnect now** 并在下次连接时生效。

Swipe down with the configured finger count in CarPlay to open the quick menu, or return to the home screen. Choose two, three or four fingers, or **Off**, under **Settings → Vehicle → CarPlay controls** or in the quick menu; three remains the default. Under **CarPlay quick menu → Display → Fullscreen**, opt into **Temporary system bars (5 seconds)** to swipe **up** with the same number of fingers and briefly reveal any hidden head-unit status/navigation bars (for climate controls and similar car functions). Swipe down still opens the quick menu. This is off by default and unavailable when the gesture is disabled, no bars are hidden, the app is in multi-window mode or Harman's vendor-specific full-screen mode is active. The OS may ignore the bar request on unsupported firmware. It does not change the saved bar configuration or reconnect CarPlay. The quick menu asks before Back or the full-settings link discards changes you have not applied. Icon/text size, resolution and frame rate use **Apply and reconnect** during an active session. A selection alone does not apply; Cancel preserves the old setting. When disconnected, **Save** applies to the next connection. Follow each setting's description: some apply live, while others show **Reconnect now** and take effect at the next connection.

**Settings → Display** 包含图标/文字大小、分辨率、帧率、日/夜间外观、画面调节、Dock 位置、系统栏和 **Interface size**。Interface size 改变的是 TuPigPlay 自身的控件，而不是 CarPlay 的画面。建议先从 30 fps 和 Default 图标/文字大小开始；在 **Settings → Advanced → Video and audio** 中保持 **Efficient video**（HEVC）关闭。车机较慢时可尝试 80% 或 60% 的分辨率。部分 iPhone/车机组合仍会忽略图标/文字缩放。

**Settings → Display** contains icon/text size, resolution, frame rate, day/night appearance, picture adjustments, dock position, system bars and **Interface size**. Interface size changes TuPigPlay's own controls, not CarPlay's picture. Start with 30 fps and Default icon/text size; leave **Efficient video** (HEVC) off under **Settings → Advanced → Video and audio**. Try 80% or 60% resolution for a slower head unit. Some iPhone/head-unit combinations still ignore icon/text scaling.

**Settings → Audio** 包含媒体/导航音频路由和音乐缓冲选择。**Settings → Navigation** 包含位置上报，以及在受支持车机上的 BYD 导航卡片。**Settings → Vehicle** 包含 CarPlay 手势、方向盘按键和车机按键自定义。连接设置、自动连接和权限位于 **Settings → Connection**；诊断报告位于 **Settings → Diagnostics**。

**Settings → Audio** contains media/navigation audio routing and the music-buffer choice. **Settings → Navigation** contains location reporting and the BYD navigation card on supported head units. **Settings → Vehicle** contains CarPlay gestures, wheel keys and car-button customization. Connection setup, automatic connection and permissions are under **Settings → Connection**; reports are under **Settings → Diagnostics**.

车辆数据模式以及电量/轮速/驻车视频控制位于 **Settings → Advanced → Advanced vehicle data**。电量上报、轮速上报和驻车视频控制默认关闭。默认的 DiLink 5.0 模式仍为默认。旧模式为可选，使用有界的只读探测且需要已授权的网络 ADB；它只暴露已确认的字段。若出现替换既有已保存字段的提示，请仔细查看。这并不会开启 ADB、写入车辆设置，也不代表每一款较老的车机都受支持。

要将闲置方向盘按键映射为 CarPlay 音乐播放/暂停（#815），请打开 **Settings → Vehicle → Wheel keys**，启用 **Play/pause key on the wheel**，然后点按 **Assign: Play/pause** 并按下物理按键。已配置的按键可在 TuPigPlay 设置界面可见时学习；车辆保留按键需要可选的方向盘按键无障碍服务（CarPlay 在后台时同样可用）。该映射默认关闭，不会替换 Siri/地图缩放/摇杆键的分配，也不会在通话进行中或 CarPlay 未连接时接管按键。**Fire TV、Google TV、Android TV 及其他 HID 遥控器保持既有的方向键、OK/选择、返回、菜单、主屏、音量、助手和专用媒体按钮行为。** 这些标准或 OEM 保留键码不能被重新分配为播放/暂停（包括此前保存的分配）；媒体控制继续使用既有的 Android MediaSession 路由。闲置按键仍可分配。这是 Android 按键兼容性说明，不代表每款专有电视遥控器都能与每台车机配对。部分 OEM 固件会在 TuPigPlay 看到按键之前将其拦截；当按键从未出现在诊断报告中时，没有固件特定集成的情况下 TuPigPlay 无法重新映射它。

To map a spare steering-wheel button to CarPlay music play/pause (#815), open **Settings → Vehicle → Wheel keys**, enable **Play/pause key on the wheel**, then tap **Assign: Play/pause** and press the physical key. A configured key can be learned while TuPigPlay's settings screen is visible; vehicle-reserved keys need the optional wheel-key accessibility service (which can also work with CarPlay in the background). The remap is disabled by default, does not replace Siri/map-zoom/joystick key assignments and does not take over keys while a call is active or CarPlay is disconnected. **Fire TV, Google TV, Android TV and other HID remotes keep their existing D-pad, OK/Select, Back, Menu, Home, volume, assistant and dedicated media-button actions.** These standard or OEM-reserved codes cannot be reassigned to play/pause, including previously saved assignments; media controls continue to use the existing Android MediaSession route. Spare keys can still be assigned. This is Android-key compatibility, not a promise that every proprietary TV remote can pair with every head unit. Some OEM firmware intercepts keys before TuPigPlay can see them; when the key never appears in the diagnostic report, TuPigPlay cannot remap it without a firmware-specific integration.

Vehicle-data mode and battery/wheel-speed/parked-video controls are under **Settings → Advanced → Advanced vehicle data**. Battery reporting, wheel-speed reporting and parked-video controls are off by default. Default DiLink 5.0 mode remains the default. Legacy mode is optional, uses bounded read-only detection and needs authorized network ADB; it exposes only confirmed fields. Review any prompt to replace previously saved fields. This does not enable ADB, write vehicle settings or establish support for every older head unit.

可选的方形画布旋转、分屏区域和侧边栏位于 **Settings → Advanced → Display (experimental)**，默认关闭。画布越大，解码器/GPU 的负担越重。Dock 位置仍在 **Settings → Display** 下。

Optional square-canvas rotation, split-screen areas and the side panel are under **Settings → Advanced → Display (experimental)** and are off by default. Larger canvases increase decoder/GPU work. Dock position remains under **Settings → Display**.

**Smooth video (experimental)**、**Buffered music (experimental)**、**Call echo cancellation (experimental)** 和 **Clearer call voices (experimental)** 位于 **Settings → Advanced → Video and audio**，默认关闭。Smooth video 可能增加触控响应延迟，并使画面调节不可用。更改 Smooth video 或 Buffered music 会让进行中的会话重连；通话回声消除和人声过滤则在下次连接时生效。Buffered music 接受支持它的应用输出的 AAC-LC，不支持无损音频。同一卡片中的 **Low-latency decoding (experimental)** 和 **Direct video output (experimental)** 同样默认关闭。Low-latency decoding 在下次连接时生效；Direct video output 会让 CarPlay 重连并使画面调节不可用。如果画面出现花屏或卡顿，请关闭其中之一。**Settings → Diagnostics → FPS counter** 会显示每秒呈现与接收的帧数以及解码耗时。

**Smooth video (experimental)**, **Buffered music (experimental)**, **Call echo cancellation (experimental)** and **Clearer call voices (experimental)** are under **Settings → Advanced → Video and audio** and are off by default. Smooth video can add touch-response delay and makes picture adjustments unavailable. Changing Smooth video or Buffered music reconnects an active session; call echo cancellation and voice filtering apply at the next connection. Buffered music accepts AAC-LC from supporting apps, not lossless audio. **Low-latency decoding (experimental)** and **Direct video output (experimental)** in the same card are also off by default. Low-latency decoding applies at the next connection; Direct video output reconnects CarPlay and makes picture adjustments unavailable. Turn either off if the picture shows artifacts or freezes. **Settings → Diagnostics → FPS counter** shows frames shown and received per second and the decode time.

独立的实验性 **CarPlay call keys** 和 **CarPlay calls on the dashboard** 仍默认关闭。它们出现在 **Settings → Navigation → BYD navigation** 下；若 BYD 导航卡片不可用，则出现在 **Settings → Advanced → Advanced vehicle data** 下。仪表盘卡片需要已授权的 ADB 和对应的固件。启用前请先阅读 [0.2.18 限制说明](RELEASE-NOTES-0.2.18.md)。

Independent experimental **CarPlay call keys** and **CarPlay calls on the dashboard** remain off by default. They appear under **Settings → Navigation → BYD navigation**, or under **Settings → Advanced → Advanced vehicle data** when the BYD navigation card is unavailable. The dashboard card needs authorized ADB and target firmware. Review [0.2.18 limits](RELEASE-NOTES-0.2.18.md) before opting in.

应用和发布网站源码提供八种语言，包括日语和繁体中文（台湾）。Android 13+ 的应用语言选择与 Android 系统设置同步；Android 7.1–12 则保留已保存的上下文覆盖。香港/澳门用语和繁体使用台湾版应用翻译，并非独立的地区版本。参见[繁体中文安装指南](INSTALL.zh-TW.md)。

The app and website source include eight languages, including Japanese and Traditional Chinese (Taiwan). Android 13+ app-language selection is synchronized with Android settings; Android 7.1–12 retain a saved context override. Hong Kong/Macao and Hant use the Taiwan app translation, not separate regional editions. See the [Traditional Chinese installation guide](INSTALL.zh-TW.md).

## 连接恢复与报告 / Connection recovery and reports

如果重装后残留了旧的组，请先关闭其他投屏应用，再使用 **Settings → Connection → Wireless connection help → Reset CarPlay Wi-Fi**。在移除无法识别的 Wi-Fi Direct 组之前，TuPigPlay 会先询问。就地升级优于卸载重装。

If the head unit's wallpaper shows through CarPlay's rounded picture corners (#635), open the in-CarPlay settings menu and choose **Safe area → Black rounded corners**. The default is **Off**. A manual 12–48 dp radius previews immediately; pick the smallest radius that covers the wallpaper. **iPhone mask (experimental)** instead negotiates CarPlay's `cornerMasks` feature at the **next connection**, accepts the iPhone's `topLeftCornerMask` grayscale PNG, and mirrors its per-pixel coverage to all four corners. This is intended to reproduce the iPhone's actual contour rather than guess a circular radius. The feature is **unverified on a real TuPigPlay head unit** and depends on iOS offering a mask. It drops the main display's safe-area declaration (required by iOS for this feature), which may change UI placement. If the mask is absent or invalid, the app retains a local 24 dp approximation instead. Both modes fill the corners black above TextureView or SurfaceView output without intercepting touches. Save the setting and reconnect to apply the native mode; turn it Off if the picture or layout is wrong. **Square corners, no safe area** remains the established alternative for a rectangular picture.

If reinstalling left an old group, close other projection apps, then use **Settings → Connection → Wireless connection help → Reset CarPlay Wi-Fi**. TuPigPlay asks before removing an unrecognized Wi-Fi Direct group. Updating in place is preferable to uninstalling.

请在 **0.0.0.1** 上复现尚未解决的问题并导出一份全新报告，即便你此前已经发送过旧日志。请从第一次连接尝试一直采集到失败发生；如果是开机/自启动问题，请重启后手动打开 TuPigPlay 再导出。使用 **Settings → Diagnostics → Save diagnostic report**。Android 10+ 通常保存到 **Downloads/TuPigPlay**；Android 7.1–9 会请求存储权限，同样保存在那里。**Choose save location** 也可用。如果文件选择器或 Downloads 存储不可用，兜底的 TXT 文件会保存在应用专属外部存储中，确认提示会显示其确切路径。若外部存储同样不可用，TuPigPlay 会把报告私密保存。两种兜底方式都提供 **View report** 和 **Share**；当没有可用的分享应用时，可在报告查看界面中选择/复制文本。请检查 `.txt` 文件，然后将其附加到你已有的 [GitHub issue](https://github.com/Tupig/DiPlay/issues) 或[创建新 issue](https://github.com/Tupig/DiPlay/issues/new/choose)，并附上车型/车机型号、确切的 DiLink/Android/固件版本、iPhone/iOS 版本、连接后端（USB、内置热点、Wi-Fi Direct 或同一局域网）、相关设置、期望与实际表现、复现步骤以及大致的失败时间。切勿包含你的热点密码。不会自动上传任何内容。

Please reproduce unresolved problems on **0.0.0.1** and export a fresh report, even if you already sent older logs. Capture from the first connection attempt through the failure; for boot/auto-start problems, reboot and then open TuPigPlay manually to export. Use **Settings → Diagnostics → Save diagnostic report**. Android 10+ normally saves to **Downloads/TuPigPlay**; Android 7.1–9 asks for storage access and saves there too. **Choose save location** is also available. If the picker or Downloads storage is unavailable, the fallback TXT file is saved in app-specific external storage; the confirmation shows its exact path. If external storage is also unavailable, TuPigPlay saves the report privately. Both fallbacks offer **View report** and **Share**; select/copy text in the report view when no sharing app is available. Review the `.txt` file, then attach it to your existing [GitHub issue](https://github.com/Tupig/DiPlay/issues) or [create a new issue](https://github.com/Tupig/DiPlay/issues/new/choose), with car/head-unit model, exact DiLink/Android/firmware versions, iPhone/iOS, connection backend (USB, built-in hotspot, Wi-Fi Direct or Same LAN), relevant settings, expected/actual behavior, reproduction steps and approximate failure time. Never include your hotspot password. Nothing is uploaded automatically.

额外的无线/媒体/主题/退出自身应用记录有助于定位失败环节；但它们并不能证明 Qin Plus 启动、Wi-Fi Direct 卡顿、Siri、iOS 15 或日/夜间固件相关报告的问题已解决。参见 [0.2.20 发行说明](RELEASE-NOTES-0.2.20.md)。

The additional wireless/media/theme/own-app-exit records help identify the failing stage; they do not establish Qin Plus startup, Wi-Fi Direct stutter, Siri, iOS 15 or day/night firmware reports as resolved. See [0.2.20 release notes](RELEASE-NOTES-0.2.20.md).

APK 安装限制由你的车辆固件决定。ADB 是在你的车支持时的可选项，并非应用运行时的必要条件：

APK installation restrictions are controlled by your car's firmware. ADB is optional if your car supports it, not an app runtime requirement:

```sh
adb install -r TuPigPlay-0.0.0.1.apk
```

只使用受信任的电脑。使用不同的签名证书无法更新这个构建；在保存好你需要的报告之前，不要卸载。

Only use a trusted computer. A different signing certificate cannot update this build; do not uninstall until you have saved any reports you need.

只安装厂商认可包的固件需要先获得该批准。[Leapmotor 说明](LEAPMOTOR.md)记录了已公开的 零跑 C 系列白名单、U盘 安装流程，以及本仓库提供的实验性构建覆盖方式。这些都不构成兼容性承诺。

## BYD 导航 / BYD navigation

关于固件适用范围、地图元数据要求、设置项和清理行为，参见 [BYD 导航显示](BYD_NAVIGATION.md)。原生 DiLink 5 显示路由不需要外部 ADB 启动器。可选的 DiLink 3/4 集成有各自的固件与已授权 ADB 要求，详见该指南。

See [BYD navigation displays](BYD_NAVIGATION.md) for the firmware scope, map metadata requirements, settings and cleanup behavior. Native DiLink 5 display routing needs no external ADB starter. Optional DiLink 3/4 integrations have separate firmware and authorized-ADB requirements described in the guide.
