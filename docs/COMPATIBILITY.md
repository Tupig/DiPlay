# 兼容性 / Compatibility

本公开预览版是一个独立接收端，并非 Apple 认证的 CarPlay 配件。实验性的随附配件身份可被提取，其未来的可用性不保证。

This public preview is an independent receiver, not an Apple-certified CarPlay accessory. The experimental bundled accessory identity is extractable and its future acceptance is not guaranteed.

| 方面 | 当前范围 |
| --- | --- |
| 车机 | Android 7.1+（API 25+）APK；Android 7.1–8.1 的支持尚未在实车上确认，Android 7.x 没有仅本机热点（请使用车辆热点、Wi-Fi Direct 或 Existing Wi-Fi），并且会拒绝应用打开车辆热点的请求，因此在非 BYD 车机上需要驾驶员在车辆设置中自行开启；Android 7.0 及更早版本不支持；Wi-Fi Direct 在旧版 Android 7.1–9 上有一条依赖固件的路径，无法校验请求的频段，Android 10+ 则使用现代校验频段 |
| 手机 | 已启用 CarPlay 的标准未越狱 iPhone；设备/iOS 兼容性因机型而异 |
| 实机证据 | 此前的私有构建：在开发车辆上使用 iPhone XS / iOS 18.7.10 确认了有线与无线的画面、触控和音频 |
| 其他车型 | 跨 DiLink 代际的社区反馈好坏参差；这不是一份经认证的车型支持列表 |
| 0.2.14 证据 | 自动化源码/构建校验与署名的贡献者测试；不主张新的完整发布实车测试或全车型支持。此前 DiLink 5.1 HUD/热点的结果仍属历史证据。 |
| Wi-Fi | Auto 使用符合条件的已保存/对齐信道；在 5 GHz 接入点旁，显式的 2.4 GHz 优先于其他 5 GHz/未固定默认回退。手动信道保持显式；不保证频段/性能。 |
| 视频 | 默认 H.264 / 30 fps；60 fps 与 HEVC 会提高对设备的特定要求 |

| Area | Current scope |
| --- | --- |
| Head unit | Android 7.1+ (API 25+) APK; Android 7.1–8.1 support is not yet confirmed on a vehicle, and Android 7.x has no local-only hotspot (use the car hotspot, Wi-Fi Direct or Existing Wi-Fi) and refuses an app's request to turn the car hotspot on, so on head units that are not BYD the driver turns it on in the car settings; Android 7.0 and older are unsupported; Wi-Fi Direct has a firmware-dependent legacy Android 7.1–9 path with unverified requested frequency, and modern verified frequency on Android 10+ |
| Phone | Standard, non-jailbroken iPhone with CarPlay enabled; device/iOS compatibility varies |
| Physical evidence | Previous private builds: wired and wireless picture, touch and audio confirmed on the development car with iPhone XS / iOS 18.7.10 |
| Other cars | Mixed community reports across DiLink generations; not a certified model support list |
| 0.2.14 evidence | Automated source/build validation and attributed contributor tests; no new complete-release vehicle test or universal model support is claimed. Earlier DiLink5.1 HUD/hotspot results remain historical evidence. |
| Wi-Fi | Auto uses eligible saved/aligned channels; beside a 5 GHz station, explicit 2.4 GHz precedes other 5 GHz/unpinned default fallbacks. Manual channels stay explicit; no band/performance guarantee. |
| Video | Default H.264 / 30 fps; 60 fps and HEVC increase device-specific demands |

## BYD HUD 与车辆热点 / BYD HUD and car hotspot

确切的已验证固件与生命周期限制参见 [BYD 导航](BYD_NAVIGATION.md)。此前的车辆热点构建在开发车辆上通过限定作用域的 IPv6 启动了 CarPlay。当前接入点端点优先使用可用的 IPv4，并保留限定作用域的 IPv6 回退。手机必须加入所配置的车辆热点。两者的结果都不保证在每款固件上都受支持。

See [BYD navigation](BYD_NAVIGATION.md) for the exact verified firmware and lifecycle limits. Earlier car-hotspot builds started CarPlay on the development car using scoped IPv6. Current access-point endpoints prefer usable IPv4 and retain scoped IPv6 fallback. The phone must join the configured car hotspot. Neither result guarantees support on every firmware.

## Geely / Lynk & Co steering-wheel keys

On the Geely/Lynk & Co head unit (`geely_fx11`, model `CS11_A4`, DesaySV, Android 11) the steering wheel does not produce standard `KEYCODE_MEDIA_*` events, so the media session and the accessibility path never see it. TuPigPlay detects this head unit and subscribes to the wheel's vendor key codes on the car's own input service (`com.geely.inputservice`), then forwards play/pause and skip to the active CarPlay session, and the microphone key's long press to Siri. The subscription exists only while a CarPlay session is active, and on every other head unit nothing is bound and behaviour is unchanged.

Two limits come from the car, not from TuPigPlay:

- The microphone key's long press is answered by the car's HiCar app, which registers for the same code. Disable it over ADB (`adb shell pm disable-user --user 0 com.geely.hicar`; `pm enable` undoes it) if Siri should answer instead. TuPigPlay never changes that itself, and a system update may need the command again. A short press stays the car's own voice assistant, which cannot be intercepted from an app.
- This firmware opens the microphone only for the voice-communication audio source, so TuPigPlay uses it for Siri on this head unit. Voice-recognition input elsewhere is unchanged.

When the car's own music app is playing as well, the wheel reaches both it and CarPlay: the head unit routes the key to its current audio source before any app sees it.

Tested on the car named above; other Geely/Lynk firmware is untested.

## Android 9 Wi-Fi Direct 与实验性功能 / Android 9 Wi-Fi Direct and experimental features

Android 9 路径使用公开的旧版组 API 创建或复用一个持久系统配置，读取生成的凭据，并在可用时使用固件的信道设置接口。它对固定/取消做了串行化，避开他方拥有的组，并只移除自己拥有的活跃组；持久配置文件不会被删除。未知的设置结果会中止启动；固定/清理不可靠时应改用车辆热点手动连接。参见[旧版限制](ANDROID9_WIFI_DIRECT.md)；贡献者的 Redmi K20 Pro 结果并不能验证每一款 BYD Android 9 固件。

The Android 9 path creates or reuses a persistent system profile with the public legacy group API, reads generated credentials and uses the firmware channel setter where available. It serializes pinning/cancellation, avoids foreign groups and removes only its owned active group; persistent profiles are not deleted. Unknown setter outcomes stop startup, and unreliable pinning/cleanup should use the manual car hotspot. See [legacy limits](ANDROID9_WIFI_DIRECT.md); the contributor's Redmi K20 Pro result does not validate every BYD Android 9 firmware.

DiLink 3 通话控制/仪表盘卡片与 AAC-LC 缓冲音乐是相互独立、默认关闭的实验功能。它们出现在 **Settings → Navigation → BYD navigation** 下（该卡片不可用时出现在 **Advanced → Advanced vehicle data** 下），缓冲音乐则在 **Advanced → Video and audio** 下。其修正后的源码已有回归测试覆盖；完整的通话/音频/麦克风/恢复以及音乐中断验收仍是设备层面的工作。DiLink 4 投屏/画面修复描述的是所测试的 2022 款 Seal 设置，并非对 Qin/Seal 全系的保证。Android 13+ 的热点加入辅助功能要求严格的用户配置/5 GHz/API/ADB 门槛以及显式确认。参见 [0.2.14 发行说明](RELEASE-NOTES-0.2.14.md)。

DiLink 3 call controls/dashboard cards and AAC-LC buffered music are independent default-off experiments. They appear in **Settings → Navigation → BYD navigation** (or **Advanced → Advanced vehicle data** when that card is unavailable), and **Advanced → Video and audio** for buffered music. Their corrected source has regression coverage; full call/audio/microphone/restoration and music-interruption acceptance remains device work. DiLink 4 casting/picture fixes describe the tested 2022 Seal setup, not a Qin/Seal-wide guarantee. The Android 13+ hotspot join helper requires strict user-configuration/5 GHz/API/ADB gates and explicit confirmation. See [0.2.14 release notes](RELEASE-NOTES-0.2.14.md).

## 当前设置与启用限制 / Current settings and opt-in limits

首次启动且没有已保存的 iPhone 时会打开 **Setup guide**；它可以跳过，也可从 **Settings → Overview** 重新打开。它会从系统构建名称中读取 DiLink 版本（例如 `DiLink3.0`），允许驾驶员自行更正，并且只提供适用于该代际的功能，标注为 `Tested on some cars` 或 `Experimental`，必要时还会带 ADB 徽标。在 DiLink 3 上，它会隐藏 DiLink 4 仪表盘路由并建议关闭，因为该路由会中断 DiLink 3 的仪表盘地图。这些标签是对上述反馈的概括，并非一份经认证的支持列表。

A first launch with no saved iPhone opens the **Setup guide**; it can be skipped and reopened from **Settings → Overview**. It reads the DiLink version from the system build name (for example `DiLink3.0`), lets the driver correct it, and only offers features that apply to that generation, labelled `Tested on some cars` or `Experimental` and with an ADB badge where needed. On DiLink 3 it hides the DiLink 4 cluster route and offers to turn it off, because that route stops the DiLink 3 dashboard map. The labels summarise the reports above; they are not a certified support list.

**Settings → Display** 包含分辨率、帧率、图标/文字大小、外观和 Interface size；Interface size 改变的是 TuPigPlay 控件，而不是 CarPlay 的几何布局。**Audio** 包含音频路由和常规的音乐缓冲选择。**Navigation** 包含位置/BYD 导航，**Vehicle** 包含手势/方向盘按键/车机按键控制，**Connection** 包含传输/启动/权限，**Advanced** 包含实验性显示/媒体与车辆数据，**Diagnostics** 用于导出报告。

**Settings → Display** holds resolution, frame rate, icon/text size, appearance and Interface size; Interface size changes TuPigPlay controls, not CarPlay geometry. **Audio** holds routing and the ordinary music-buffer choice. **Navigation** holds location/BYD guidance, **Vehicle** holds gestures/wheel keys/car-button controls, **Connection** holds transport/startup/permissions, **Advanced** holds experimental display/media and vehicle data, and **Diagnostics** exports reports.

**Smooth video (experimental)**、**Call echo cancellation (experimental)** 和 **Clearer call voices (experimental)** 在 **Settings → Advanced → Video and audio** 下默认关闭。Smooth video 会增加自适应节拍延迟，可能拖慢触控响应，并使用没有画面调节的 SurfaceView 路径。更改它会让运行中的会话重连。通话处理相关控件在下次连接时生效；源码/JNI 测试并不能证明车上的实际声学效果。可选的旋转、分屏区域和侧边栏在 **Advanced → Display (experimental)** 下默认关闭。已有的已启用设置会在就地升级中保留。

**Smooth video (experimental)**, **Call echo cancellation (experimental)** and **Clearer call voices (experimental)** are off by default under **Settings → Advanced → Video and audio**. Smooth video adds adaptive pacing delay, can delay touch response, and uses a SurfaceView path without picture adjustments. Changing it reconnects a running session. The call-processing controls apply at the next connection; source/JNI tests do not establish acoustic quality on a car. Optional rotation, split-screen areas and side panel are off by default under **Advanced → Display (experimental)**. Existing opted-in settings are retained during an in-place update.

此前专门观察到的 1280×480 DiLink 3 投屏表面现已被既有的仪表盘地图路径识别。实际的地图输出仍需在车上复测；实测的 1920×720 校准并不会应用到那块更小的屏幕上。窄幅 USB 取景/队列修复与蓝牙接收修复已有回归测试覆盖，但它们并不能证明每一份连接反馈都已修复。仅充电的 USB 口无法提供数据传输。

The specifically observed 1280×480 DiLink 3 projection surface is now recognized by the existing cluster-map path. Actual map output still needs an on-car retest; the measured 1920×720 calibration is not applied to that smaller panel. Narrow USB framing/queue repairs and Bluetooth receive fixes have regression coverage, but they do not establish a fix for every connection report. A charging-only USB port cannot supply the data transport.

## 已知限制 / Known limitations

- 如果 iPhone 离开了当前的 Wi-Fi 网络却没有加入车辆热点，请检查该热点是否启用了自动加入。如果手动选择它时无需输入密码就启动了 CarPlay，请在诊断报告中记录这一区别。即便凭据以及随后的 CarPlay 传输都正常，AP 广播信息元素也可能影响自动加入。Apple 官方文档所述的要求与一次受控对比参见[自动加入热点](WIRELESS_HOTSPOT_JOIN.md)；这并不代表对每款车机或 iOS 版本都有受支持的修复。
- 部分车机会卡顿，尤其是在视频负载较高时。仅有 2.4 GHz 链路并不能证明原因：干扰、固件和解码器停顿都可能是因素。请先尝试 Default 图标、30 fps 和较低分辨率，然后附上一份报告。
- 关于在 BYD Tang 上最流畅所用的 Wi-Fi 信道与画面尺寸及其原因，参见[流畅的无线 CarPlay](SMOOTH_WIRELESS.md)。
- 当车辆自身的 Wi-Fi 客户端在搜索网络时，Wi-Fi Direct 可能每隔几秒就卡顿一次。车辆只有一套无线电，每次搜索都会让它离开 CarPlay 信道，期间发出的音频包便会丢失；更大的音乐缓冲也无法补回这些包。来自 DiLink 3、4.0 和 5.0 的反馈显示：当客户端未加入网络时每 10 s 搜索一次，在某些其他固件上或已加入网络时则每 20–30 s 搜索一次。车内自带热点在客户端关闭时运行，是推荐的连接方式。在 Android 7.1 及以上，若网络 ADB 已获批准，TuPigPlay 会在 Wi-Fi Direct 会话期间暂停 Android 的自动加入，从而停止这种搜索；在 CarPlay 结束之前，车辆不会加入已保存的网络。控制器租约共享同一个串行 worker，一个持久标记位于暂停之前，恢复失败时在应用运行期间每 30 s 重试一次，应用启动时会恢复被打断的恢复过程，且车辆重启后 Android 会自行重置该设置。未获批准时不会有任何变化，报告中会写明 `reason=adb-not-approved` 或 `reason=adb-off`。其他应用发起的搜索不会被暂停。Android 11+ 路径已在一辆 DiLink 5.1 车上手工确认；应用内路径仍需停车复测。
- 部分 iPhone/车机组合不会明显应用图标和文字大小。重连功能已经实现；但这并不保证 iPhone 会选择所请求的布局。
- 支持加入 5 GHz 网络的无线电仍可能拒绝 5 GHz 的 Wi-Fi Direct 组。该能力标志仅用于诊断，并非组所有者支持的证明。
- 自动启动取决于车辆固件和启动权限。
- USB 需要数据口以及正确的主机/从机角色行为。
- 通话、Siri、后台重连、长途出行以及未来的 iOS 版本需要更广泛的测试。

- If the iPhone leaves its current Wi-Fi network but does not join the car hotspot, check that Auto-Join is enabled for that hotspot. If selecting it manually starts CarPlay without a password prompt, record that distinction in the diagnostic report. AP broadcast information elements can affect automatic joining even when credentials and the subsequent CarPlay transport work. See [automatic hotspot joining](WIRELESS_HOTSPOT_JOIN.md) for Apple's documented requirements and a controlled comparison; this does not establish a supported fix for every head unit or iOS version.
- Some units stutter, particularly under higher video load. A 2.4 GHz link alone does not prove the cause: interference, firmware and decoder stalls can all contribute. Try Default icons, 30 fps and a lower resolution, then attach a report.
- For the Wi-Fi channel and the picture size that gave the smoothest result on a BYD Tang, and why, see [Smooth wireless CarPlay](SMOOTH_WIRELESS.md).
- Wi-Fi Direct can stutter every few seconds while the car's own Wi-Fi client searches for networks. The car has one radio, so each search takes it off the CarPlay channel and audio packets sent meanwhile are lost; a larger music buffer cannot replace them. Reports from DiLink 3, 4.0 and 5.0 show the search every 10 s when the client is not joined to a network, and every 20–30 s on some other firmware or while it is joined. The built-in car hotspot runs with the client off and is the recommended connection. With network ADB already approved, TuPigPlay pauses Android's automatic joining, which stops this search, during Wi-Fi Direct sessions on Android 7.1 and later; the car does not join saved networks until CarPlay ends. Controller leases share one serial worker, a durable marker precedes the pause, failed restores retry every 30 s while the app runs, app startup recovers an interrupted restore, and Android resets the setting itself when the car restarts. Without approval nothing changes and the report says `reason=adb-not-approved` or `reason=adb-off`. Searches requested by other apps are not paused. The Android 11+ path was confirmed by hand on one DiLink 5.1 car; the in-app path needs a parked vehicle retest.
- Wireless CarPlay cannot start on a head unit that keeps calls and music on the maker's own Bluetooth module, because Android's Bluetooth there accepts connections that reach no phone. A common sign: the iPhone is paired in the maker's own Bluetooth or Phone app, not in Android's Bluetooth settings. Since 0.2.18 TuPigPlay tests for this and stops with the cause instead of retrying ([#590](https://github.com/shihabal3amri/DiPlay/pull/590)). Confirmed on one Allwinner T8 with TopWay firmware; [#330](https://github.com/shihabal3amri/DiPlay/issues/330) and [#354](https://github.com/shihabal3amri/DiPlay/issues/354) show the same symptom. USB is the only route left to try on such a unit.
- Wireless CarPlay also cannot start on a head unit whose Bluetooth service gives apps no socket manager: every RFCOMM connect fails inside Android within a few milliseconds, while the car's own calls and music keep working. Seen on a Changan CS35 Plus with an Allwinner T7 on Android 9 ([#709](https://github.com/shihabal3amri/DiPlay/issues/709)). TuPigPlay stops with that cause instead of retrying. Use USB on such a unit.
- A head unit whose Android has no Bluetooth adapter at all is told so, when the driver chooses an iPhone and when a connection is tried, instead of being asked to turn Bluetooth on. Where the adapter exists but its switch will not stay on, as on the GAC unit in [#581](https://github.com/shihabal3amri/DiPlay/issues/581), the request to turn Bluetooth on adds that this means wireless CarPlay cannot work there once the driver has been to Bluetooth settings and come back with it still off.
- Some iOS/head-unit combinations do not visibly apply icon and text size. Reconnection is implemented; that does not guarantee the iPhone chooses the requested layout.
- A radio that supports joining a 5 GHz network may still reject a 5 GHz Wi-Fi Direct group. The capability flag is diagnostic, not proof of group-owner support.
- Automatic startup depends on the car's firmware and startup permissions.
- USB requires a data port and correct host/device-role behavior.
- Calls, Siri, background reconnection, long journeys and future iOS releases need broader testing.

如需最新反馈，请在 **0.2.14** 上复现问题，然后打开 **Settings → Diagnostics → Save diagnostic report**。Android 10+ 通常保存到 **Downloads/TuPigPlay**；Android 9 使用文档选择器。确认提示会标明兜底存储位置，并提供 **View report** 和 **Share**。请把检查过的 `.txt` 附加到对应的[已有 issue](https://github.com/Tupig/DiPlay/issues)，或[创建 issue](https://github.com/Tupig/DiPlay/issues/new/choose)。请包含车型/车机、Android/DiLink/固件、iPhone/iOS、连接模式、步骤以及失败时间。不会自动上传任何内容。

For a current report, reproduce the problem on **0.2.14**, then open **Settings → Diagnostics → Save diagnostic report**. Android 10+ normally saves to **Downloads/TuPigPlay**; Android 9 uses a document picker. The confirmation identifies fallback storage and offers **View report** and **Share**. Attach the reviewed `.txt` to a matching [existing issue](https://github.com/Tupig/DiPlay/issues), or [create an issue](https://github.com/Tupig/DiPlay/issues/new/choose). Include car/head-unit, Android/DiLink/firmware, iPhone/iOS, connection mode, steps and failure time. Nothing uploads automatically.

报告会记录请求的频段、接入点关联状态、回退失败以及记忆配置事件。Android 10+ 可以校验实际的组频段；Android 9 会把其被接受的信道请求记为未校验，把系统默认记为信道 0。Wi-Fi 凭据与协议载荷已被排除。热点成功本身并不等于 CarPlay 会话成功。

Reports record requested frequencies, station association state, fallback failures and remembered-configuration events. Android 10+ can verify the actual group frequency; Android 9 reports its accepted channel request as unverified and system-default as channel 0. Wi-Fi credentials and protocol payloads are excluded. A successful hotspot is not itself a successful CarPlay session.

Android 参考：[SupplicantState](https://developer.android.com/reference/android/net/wifi/SupplicantState)、[explicit P2P operating frequency](https://developer.android.com/reference/android/net/wifi/p2p/WifiP2pConfig.Builder#setGroupOperatingFrequency(int))。

Android references: [SupplicantState](https://developer.android.com/reference/android/net/wifi/SupplicantState), [explicit P2P operating frequency](https://developer.android.com/reference/android/net/wifi/p2p/WifiP2pConfig.Builder#setGroupOperatingFrequency(int)).
