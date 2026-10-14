# 已有 Wi-Fi / 同一局域网 / Existing Wi-Fi / Same LAN

在系统设置中把 Android 接收端和 iPhone 连接到同一个第三方路由器或便携 Wi-Fi。在 DiPlay → Connection setup → Wireless 中选择 **Existing Wi-Fi / Same LAN**，输入准确的 SSID 和 WPA2 密码并保存。开放式网络请留空密码。保持 Bluetooth 开启且 iPhone 与接收端已配对，然后照常启动无线 CarPlay。

Connect the Android receiver and iPhone to the same third-party router or portable Wi-Fi in system settings. In DiPlay → Connection setup → Wireless, select **Existing Wi-Fi / Same LAN**, enter the exact SSID and WPA2 password, and save. For an open network, leave the password empty. Keep Bluetooth enabled and the iPhone paired with the receiver, then start wireless CarPlay as usual.

请使用 WPA2-Personal 或 WPA2/WPA3 混合模式。仅 WPA3、企业级认证和强制门户（captive portal）目前不在该模式范围内。双方客户端必须能够互通；请允许组播 DNS 以进行服务发现。支持时优先使用 5 GHz。

Use WPA2-Personal or WPA2/WPA3 mixed mode. WPA3-only, enterprise authentication and captive portals are outside this mode's current scope. Both clients must be able to communicate; allow multicast DNS for service discovery. Prefer 5 GHz when supported.

DiPlay 直接附着到已有连接，不创建 AP、不加入其他网络，也不改变默认路由。Internet 访问由路由器提供。已有的 Wi-Fi 凭据与车机热点设置分开存储。Android 无法把已保存的密码暴露给普通应用，因此密码需要手动输入。可读取的实时 SSID 必须与已保存的名称一致。当网络能力快照隐藏了该名称时，管理器会尝试读取 station WifiInfo；如果两份快照都把它隐藏了，请在 Android 设置中核对手动配置。精确位置权限和已开启的系统定位可以让网络详情可读，但它们并不是启动该模式的必要条件，也不与可选的 GPS 上报绑定。

DiPlay attaches to the existing connection without creating an AP, joining another network or changing the default route. The router supplies Internet access. Existing Wi-Fi credentials are stored separately from car-hotspot settings. Android cannot expose saved passwords to ordinary apps, so the password is entered manually. A readable live SSID must match the saved name. The manager tries station WifiInfo when the network capabilities snapshot hides that name; if both snapshots hide it, verify the manual configuration in Android settings. Precise location permission and enabled system location can make network details readable, but are not required merely to start this mode or tied to optional GPS reporting.

## 实现 / Implementation

`ExistingWifiManager` 实现了无线后端契约。它选择唯一的非 VPN Wi-Fi Network，从 LinkProperties 取得其接口与地址，并从 WifiInfo 读取信道信息。局域网通信不要求 Internet 校验。含糊的 Wi-Fi 连接以及可观测的配置不一致会以配置错误失败。

`ExistingWifiManager` implements the wireless backend contract. It selects the single non-VPN Wi-Fi Network, obtains its interface and addresses from LinkProperties, and reads channel information from WifiInfo. Internet validation is not required for LAN communication. Ambiguous Wi-Fi connections and observable configuration mismatches fail with a configuration error.

引导端点优先使用带作用域的 link-local IPv6 地址，失败时回退到 IPv4。已有 Wi-Fi 模式还会保留另一可用地址族用于发现和 TCP。每个地址族都有一个 JmDNS 注册表和一个绑定到同一所选接口与端口的监听器。端口回退是共享的，部分绑定的监听器会被清理。发现探针使用匹配的源地址族和本地 IPv6 接口作用域。JmDNS 的 `getInetAddress()` 标识注册表所属的地址族；已弃用的 `getInterface()` 可能返回同一个 Android 接口的另一个地址。TXT 特性位与 AirPlay `/info` 使用相同的来源。

The bootstrap endpoint prefers a scoped link-local IPv6 address, falling back to IPv4. Existing Wi-Fi also retains the other usable address family for discovery and TCP. Each family has a JmDNS registry and a listener bound to the same selected interface and port. Port fallback is shared and partially bound listeners are cleaned up. Discovery probes use a matching source family and local IPv6 interface scope. JmDNS `getInetAddress()` identifies the registry family; deprecated `getInterface()` can return another address of the same Android interface. TXT feature bits share the same source as AirPlay `/info`.

已有的 Bluetooth/iAP2 序列被复用：0x5703 携带网络凭据，0x4301 携带所选端点（不带本地作用域后缀）。可读取的 AP BSSID 是 0x5703 的一个可选提示，与接收端的 AirPlay 身份相互独立。未知、占位、零值、组播或格式错误的 AP 地址会被省略。P2P 与车机热点后端保持其既有的单地址默认值与引导行为。

The existing Bluetooth/iAP2 sequence is reused: 0x5703 carries network credentials, and 0x4301 carries the selected endpoint without a local scope suffix. A readable AP BSSID is an optional 0x5703 hint, separate from the receiver's AirPlay identity. Unknown, placeholder, zero, multicast or malformed AP addresses are omitted. The P2P and car hotspot backends retain their existing single-address defaults and bootstrap behavior.

网络丢失或任一所选地址发生变化都会重启无线会话。关闭后端会注销其回调，而不会断开 Wi-Fi。已保存的"自动启动车机热点"仅适用于车机热点模式。

Network loss or a change to either selected address restarts the wireless session. Closing the backend unregisters its callback without disconnecting Wi-Fi. Saved automatic car-hotspot startup applies only to car-hotspot mode.

相关工作：[PR #22](https://github.com/shihabal3amri/DiPlay/pull/22) 也在一个 Android 7 移植中实现了外部 Wi-Fi。本功能使用当前架构，没有引入该移植或其系统网络修改。

Related work: [PR #22](https://github.com/shihabal3amri/DiPlay/pull/22) also implements external Wi-Fi within an Android 7 port. This feature uses the current architecture without importing that port or its system network modifications.

## 验证 / Validation

测试覆盖 station 选择、脱敏后的网络信息、手动配置、地址作用域与地址族选择、回调清理、设置持久化、双地址族发布/监听、端口回退、模式切换以及引导编码。既有的 P2P 与热点测试与它们并行运行。

Tests cover station selection, redacted network information, manual configuration, address scope and family selection, callback cleanup, settings persistence, dual-family publication/listening, port fallback, mode transitions and bootstrap encoding. Existing P2P and hotspot tests run alongside them.

贡献者报告，在一台 2022 款 BYD 汉 DM-i（DiLink 4.0 / Android 10 (API 29)，固件 21.1.21.2401160.1）上完成最终实现的全新安装一切成功：先前的测试应用被卸载，独立测试 APK 正常安装并配置，Same LAN 顺利进入 CarPlay，使用过程中未发现问题。这验证了该配置流程，但并不能确立与每一种接收端、iOS 版本或路由器的兼容性。开放式网络与 WPA2/WPA3 混合网络尚未在真机上单独验证。

The contributor reports a successful clean install of the final implementation on a 2022 BYD Han DM-i with DiLink 4.0 / Android 10 (API 29), firmware 21.1.21.2401160.1: the previous test app was uninstalled, the standalone test APK was installed and configured normally, and Same LAN entered CarPlay with no issues noticed during use. This validates that setup; it does not establish compatibility with every receiver, iOS version or router. Open and WPA2/WPA3 mixed networks have not been separately validated on hardware.

设备检查时，请确认两台设备都保持着已有的 Wi-Fi 连接、CarPlay 能启动，且 iPhone 可以使用在线服务。遇到失败时，请保存一份诊断报告，其中包含尝试时间以及 iPhone 实际的 Wi-Fi 连接情况。仅仅缺少 Bonjour 控制服务事件，并不能证明 CarPlay 发现失败。

For a device check, confirm that both devices keep the existing Wi-Fi connection, CarPlay starts, and the iPhone can use online services. For failures, save one diagnostic report with the attempt time and the iPhone's actual Wi-Fi connection. An absence of Bonjour control-service events alone does not prove that CarPlay discovery failed.

## 构建 / Builds

仅源码的 debug APK 会省略运行时配件认证。请配合 [BUILD.md](BUILD.md) 中所述的显式外部资源使用既有的 `:mobile:assembleStandaloneDebug` 任务，并遵循 [SECURITY.md](../SECURITY.md)。认证输入与 Android 签名材料必须留在 Git 之外。应用签名密钥与 CarPlay 配件身份是彼此独立的。

A source-only debug APK omits runtime accessory authentication. Use the existing `:mobile:assembleStandaloneDebug` task with the explicit external assets described in [BUILD.md](BUILD.md), following [SECURITY.md](../SECURITY.md). Authentication inputs and Android signing material must remain outside Git. The app signing key is separate from the CarPlay accessory identity.
