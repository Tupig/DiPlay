# 车内自带热点设置 / Built-in car hotspot setup

DiAuto 与 DiPlay —— 车内自带热点测试构建
2026 年 9 月 28 日

DiAuto-connection-setup-test.apk：Android 手机 / Android Auto
DiPlay-connection-setup-test.apk：iPhone / Apple CarPlay
把 APK 装在车上，不要装在手机上。在不卸载的前提下更新对应的已有测试应用，以保留设置。

DiAuto & DiPlay — Built-in car hotspot test builds
28 September 2026

DiAuto-connection-setup-test.apk: Android phone / Android Auto
DiPlay-connection-setup-test.apk: iPhone / Apple CarPlay
Install the APK on the car, not on your phone. Update the matching existing test app without uninstalling to preserve settings.

这些构建移除了 Local hotspot 选项。车内自带热点成为默认方式；Wi-Fi Direct 和 USB 依然可用。此前对 Local hotspot 的选择会切换到内置热点。连接前请核对并保存车辆真实的热点信息。

These builds remove the Local hotspot option. Built-in car hotspot is the default; Wi-Fi Direct and USB remain available. Previous Local hotspot selections switch to built-in hotspot. Check and save the car's real hotspot details before connecting.

## 会话内设置与身份验证 / In-session settings and authentication — DIPLAY

在 CarPlay 中，按配置的手指数量（2、3 或 4；默认 3）下滑即可打开隐藏设置菜单。打开或取消该菜单不会影响健康的会话连接。Save and reconnect 会应用修改；Back 或 X 则丢弃修改。菜单打开期间若连接中断，关闭菜单后会自动恢复，除非 Wi-Fi 需要执行既有的手动重置操作。

In CarPlay, swipe down with the configured number of fingers (2, 3 or 4; default 3) to open the hidden settings menu. Opening or cancelling the menu keeps a healthy session connected. Save and reconnect applies the edits; Back or X discards them. A connection lost while the menu is open recovers on closing, unless Wi-Fi requires the existing manual reset action.

隐藏菜单提供 Local offline 和 USB/CH341 两种身份验证方式。Local 为默认方式，首次连接需要已配置的身份标识。选择 USB/CH341 并保存后，本次及后续连接都会使用所配置的 CH341 桥接，且不会安装或加载本地身份文件，即便这些文件已经存在。请允许 Android 的 USB 权限提示。桥接缺失时会等待硬件就绪，而不会回退到本地身份验证。连接 CH341 桥接不会把无线 CarPlay 变成有线模式。切回 Local 时会在保存前先校验身份；失败则菜单保持打开，并保留此前保存的身份验证选择。

The hidden menu offers Local offline and USB/CH341 authentication. Local is the default and requires a provisioned identity for the first connection. Selecting USB/CH341 and saving uses the configured CH341 bridge for this and subsequent connections, without installing or loading local identity files, even if they already exist. Allow Android's USB permission prompt. A missing bridge waits for hardware; it does not fall back to local authentication. Connecting the CH341 bridge does not change wireless CarPlay to wired mode. Switching back to Local validates the identity before saving; failure keeps the menu open and preserves the previously saved authentication choice.

## 内置热点设置 —— 两个应用通用 / Built-in hotspot setup — both apps

1. 在车辆设置中打开其内置 Wi-Fi 热点。若支持请选择 5 GHz。准确记下热点名称和密码。
2. 在车上打开 DiAuto 或 DiPlay。进入 Settings → Connection setup（若显示 Open connection setup 则点按它）。
3. 选择 Built-in car hotspot。点按 Save hotspot details and use this mode（或 Edit saved hotspot），填入车辆的热点名称和密码后保存。需要时可使用 Hide keyboard。保持车辆热点处于开启状态。
4. 在手机上打开蓝牙和 Wi-Fi，并与车辆蓝牙配对。允许应用在车上请求的权限。
5. 返回应用并点按 Connect phone。被询问时选择你的手机。在 DiPlay 中，如需改用其他手机，可点按 Choose iPhone。
6. 在手机上接受 Android Auto 或 CarPlay 的提示。

1. In the car's settings, turn on its built-in Wi-Fi hotspot. Select 5 GHz if available. Note the hotspot name and password exactly.
2. Open DiAuto or DiPlay on the car. Go to Settings → Connection setup (tap Open connection setup if shown).
3. Select Built-in car hotspot. Tap Save hotspot details and use this mode (or Edit saved hotspot), enter the car's hotspot name and password, and save. Use Hide keyboard if needed. Leave the car hotspot on.
4. Turn on Bluetooth and Wi-Fi on your phone. Pair it with the car's Bluetooth. Allow the app permissions requested on the car.
5. Return to the app and tap Connect phone. Select your phone when asked. In DiPlay, use Choose iPhone if you need to select a different phone.
6. Accept the Android Auto or CarPlay prompts on your phone.

点按 Connect phone 之前，你不需要在手机上手动加入热点。应用会通过蓝牙发送热点信息，手机即可自动加入。请使用车辆的热点，而不是手机的个人热点。此连接设置不需要 ADB。也不需要车辆流量套餐；手机能否上网取决于手机自身的网络设置。如果你更改了车辆热点的名称或密码，也请在应用中同步更新。每次只测试一个投屏应用。

You do not need to join the hotspot manually on your phone before tapping Connect phone. The app sends its details over Bluetooth so the phone can join automatically. Use the car's hotspot, not your phone's Personal Hotspot. ADB is not required for this connection setup. A car internet plan is not required; phone internet availability depends on its network settings. If you change the car hotspot name or password, update it in the app too. Test one projection app at a time.

## 已有 Wi-Fi / 同一局域网 —— DIPLAY / Existing Wi-Fi / same LAN — DIPLAY

在系统设置中，把车辆和 iPhone 接入同一个外部路由器或便携 Wi-Fi。在 DiPlay 的 Connection setup 中选择 Existing Wi-Fi / Same LAN，并保存该网络的确切名称和 WPA2 密码。保持蓝牙开启，然后照常连接。不会创建车辆热点或 Wi-Fi Direct 组。请关闭路由器的客户端隔离。构建要求、网络限制和设备验证参见 [Existing Wi-Fi](EXISTING_WIFI.md)。

Connect the car and iPhone to the same external router or portable Wi-Fi in system settings. In DiPlay Connection setup, select Existing Wi-Fi / Same LAN and save that network's exact name and WPA2 password. Keep Bluetooth enabled, then connect as usual. No car hotspot or Wi-Fi Direct group is created. Disable router client isolation. See [Existing Wi-Fi](EXISTING_WIFI.md) for build requirements, network limitations and device validation.

## Wi-Fi Direct 信道 —— DIPLAY / Wi-Fi Direct channel — DIPLAY

在 Settings → Connection setup 中选择 Wi-Fi Direct，然后选择 Preferred channel。Auto 是默认值，保持 DiPlay 的自动选信道逻辑。你可以选择一个 5 GHz 信道（36、40、44、48、149、153、157、161 或 165），或一个 2.4 GHz 信道（1–11）。车辆及其所在地区的 Wi-Fi 设置必须支持所选信道。保存会把该选择应用于下一次 Wi-Fi Direct 连接；已有的连接会持续到你断开/重连。如果车辆拒绝该信道或创建了不同的信道，DiPlay 会报告错误。请改选 Auto 或其他信道后重新连接。切换到内置热点会保留这个选择，但不会把它应用到车辆热点上。

In Settings → Connection setup, choose Wi-Fi Direct, then Preferred channel. Auto is the default and keeps DiPlay's automatic channel selection. You can choose a 5 GHz channel (36, 40, 44, 48, 149, 153, 157, 161 or 165), or a 2.4 GHz channel (1–11). The car and its regional Wi-Fi settings must support the selected channel. Save applies the choice to the next Wi-Fi Direct connection; an existing connection continues until you disconnect/reconnect. If the car rejects the channel or creates a different one, DiPlay reports an error. Choose Auto or another channel and reconnect. Switching to the built-in hotspot preserves this choice without applying it to the car hotspot.

## 可选：需要 ADB 的 DIPLAY BYD 功能 / Optional: DiPlay BYD features requiring ADB

Settings → BYD features · needs ADB 仅在 BYD 导航服务或原厂 BYD 车辆设置应用存在，且传统的网络 ADB 在 127.0.0.1:5555 可达时才会出现。该热点检查也覆盖缺少受支持导航服务的较老 QUALCOMM/qti 车机；但它不会在这些车机上开启导航输出类功能。未经批准的 ADB 密钥仍会显示该设置入口；不支持 TLS 配对。初始检查从不要求批准，也不读取车辆数据。

Settings → BYD features · needs ADB appears only when BYD navigation services or the factory BYD car settings app are present, and traditional network ADB is reachable at 127.0.0.1:5555. The hotspot check also covers older QUALCOMM/qti head units without the supported navigation services; it does not enable navigation-output features on those units. An unapproved ADB key still shows the setup entry; TLS pairing is not supported. The initial check never requests approval or reads vehicle data.

BYD ADB 部分包含电量上报（含充电接口与低电量提醒）、隧道用轮速、驻车时的视频以及仪表盘歌曲。这些通过 ADB 使用 autoservice，而不是 AMap 导航接收器。Check ADB access 和 Apply and reconnect 也在该部分中。仪表盘地图流开关沿用既有的显示与固件检查。导航箭头、HUD 和地图显示设置在 BYD 导航中保持既有的能力检查。已保存的选择和车辆读取行为均无变化。

The BYD ADB section contains battery reporting (with charging connectors and low-charge warning), wheel speed for tunnels, video while parked, and the dashboard song. These use autoservice through ADB, not the AMap navigation receiver. Check ADB access and Apply and reconnect are in this section too. The cluster-map stream switch retains its existing display and firmware checks. Navigation arrows, HUD, and map-display settings keep their existing capability checks in BYD navigation. Saved choices and vehicle-reading behavior are unchanged.

Automatically turn on the car hotspot（自动打开车辆热点）仅在选中 Built-in car hotspot 时，才以一个开关加一段说明的形式出现在同一部分。选择 Wi-Fi Direct 会隐藏它，但不会改变已保存的选择，也不会改变其他 BYD ADB 选项。该开关默认关闭。打开后会自动通过 ADB 申请缺失的 WRITE_SETTINGS，不需要单独的设置按钮或 DiPlay 确认。如有需要，请批准车辆系统的 ADB 提示。只有在所需权限校验通过后该开关才会启用；授权失败会使其保持关闭并显示一条消息。授权之后，允许应用直接发起热点请求的固件可以在不使用 ADB 的情况下打开已保存的热点。阻止该请求的固件则需要在每次开机或连接尝试时都有一个已授权且可达的传统网络 ADB 连接。该兜底方案只使用该固件服务帮助中声明的"启动已保存热点"命令，会检查其结果，并等待观察到 AP 已启用状态。这些命令并非标准 Android 命令；这并不代表每个 DiLink 版本都受支持。如果两条路径都不支持，请使用车辆自身的热点设置。关闭 ADB 会隐藏此设置，但保留选择；需再次开启 ADB 才能修改。USB、Wi-Fi Direct、断开连接、退出以及关闭此选项都不会停止热点。不支持的固件、缺失的权限和启动失败都会上报；车辆自身的热点设置仍可用于手动配置。

Automatically turn on the car hotspot appears as one switch and description in the same section only when Built-in car hotspot is selected. Wi-Fi Direct hides it without changing the saved choice or the other BYD ADB options. The switch is off by default. Turning it on automatically requests missing WRITE_SETTINGS through ADB, without a separate setup button or DiPlay confirmation. Approve the car's system ADB prompt if needed. The switch is enabled only after the required permissions are verified; a failed grant leaves it off and shows a message. After the grant, firmware that allows the app's direct hotspot request can turn on the saved hotspot without ADB. Firmware that blocks that request needs an already-authorized, reachable traditional network ADB connection at each startup or connection attempt. The fallback uses only a saved-hotspot start command advertised by that firmware's service help, checks its result, and waits for an observed AP enabled state. These commands are not standard Android commands; this does not establish support for every DiLink version. If neither path is supported, use the car's own hotspot settings. Turning ADB off hides this setting but preserves the choice; enable ADB again to change it. USB, Wi-Fi Direct, disconnecting, exiting, and turning this option off do not stop the hotspot. Unsupported firmware, missing permission, and startup failures are reported; the car's own hotspot settings remain available for manual setup.

若希望开机后自动启动，还需启用 Open after the car starts。两个选项同时选中时，开关启用后还会一并申请缺失的 SYSTEM_ALERT_WINDOW 以便开机启动。这不会开启悬浮地图，也不会自动打开开机选项。车机可能还需要它自身的自启动权限。Connect when DiPlay opens 仍是一个独立选项：应用可以在不连接 iPhone 的情况下启动热点。

For startup after boot, also enable Open after the car starts. When both options are selected, the switch being enabled also requests missing SYSTEM_ALERT_WINDOW for boot launch. This does not enable a floating map or turn on the boot option automatically. The head unit may also require its own auto-start permission. Connect when DiPlay opens remains a separate choice: the app can start the hotspot without connecting to an iPhone.

## 可选：DIPLAY 自动仪表盘地图 / Optional: DiPlay automatic instrument map

在受支持的 DiLink 5.1 固件上，打开 Settings → BYD navigation → Automatic map setup · ADB。按照屏幕上的一次性电脑设置流程操作，然后点按 Check and enable。打开仪表盘的地图卡片或选择 Map theme。该权限用于自动检测仪表盘主题/卡片，与热点连接无关。指南中说明了针对已安装应用的确切命令。

On the supported DiLink 5.1 firmware, open Settings → BYD navigation → Automatic map setup · ADB. Follow the displayed one-time computer setup, then tap Check and enable. Open the cluster's map card or select Map theme. This permission is for automatic cluster theme/card detection, not hotspot connection. The guide explains the exact command for the installed app.

## 测试/反馈要点 / What to test and report

请检查首次连接、重启应用/车辆后的重连、地图、音乐/音频，以及你的车所支持的仪表盘地图功能。如有失败，请记录时间与步骤、车型、DiLink/Android 版本、手机型号/系统，以及你使用的是哪个应用。通过 Settings → Diagnostics → Save diagnostic report 导出诊断报告。报告保存在 Downloads/DiAuto 或 Downloads/DiPlay 下。请把报告连同你的测试反馈一起分享；不要包含你的热点密码。

Check first connection, reconnect after restarting the app/car, maps, music/audio, and any instrument-map features supported by your car. If something fails, note the time and steps, car model, DiLink/Android version, phone model/OS, and which app you used. Export a diagnostic report from Settings → Diagnostics → Save diagnostic report. Reports are saved under Downloads/DiAuto or Downloads/DiPlay. Share the report with your test feedback; do not include your hotspot password.
