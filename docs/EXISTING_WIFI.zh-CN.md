# 已有 Wi-Fi / 同一局域网

[English](EXISTING_WIFI.md) · **简体中文**

在系统设置中把 Android 接收端和 iPhone 连接到同一个第三方路由器或便携
Wi-Fi。在 DiPlay → Connection setup → Wireless 中选择
**Existing Wi-Fi / Same LAN**，输入准确的 SSID 和 WPA2 密码并保存。
开放式网络请留空密码。保持 Bluetooth 开启且 iPhone 与接收端已配对，
然后照常启动无线 CarPlay。

请使用 WPA2-Personal 或 WPA2/WPA3 混合模式。仅 WPA3、企业级认证和
强制门户（captive portal）目前不在该模式范围内。双方客户端必须能够
互通；请允许组播 DNS 以进行服务发现。支持时优先使用 5 GHz。

DiPlay 直接附着到已有连接，不创建 AP、不加入其他网络，也不改变默认
路由。Internet 访问由路由器提供。已有的 Wi-Fi 凭据与车机热点设置分开
存储。Android 无法把已保存的密码暴露给普通应用，因此密码需要手动
输入。可读取的实时 SSID 必须与已保存的名称一致。当网络能力快照隐藏了
该名称时，管理器会尝试读取 station WifiInfo；如果两份快照都把它隐藏
了，请在 Android 设置中核对手动配置。精确位置权限和已开启的系统定位
可以让网络详情可读，但它们并不是启动该模式的必要条件，也不与可选的
GPS 上报绑定。

## 实现

`ExistingWifiManager` 实现了无线后端契约。它选择唯一的非 VPN Wi-Fi
Network，从 LinkProperties 取得其接口与地址，并从 WifiInfo 读取信道
信息。局域网通信不要求 Internet 校验。含糊的 Wi-Fi 连接以及可观测的
配置不一致会以配置错误失败。

引导端点优先使用带作用域的 link-local IPv6 地址，失败时回退到 IPv4。
已有 Wi-Fi 模式还会保留另一可用地址族用于发现和 TCP。每个地址族都有
一个 JmDNS 注册表和一个绑定到同一所选接口与端口的监听器。端口回退是
共享的，部分绑定的监听器会被清理。发现探针使用匹配的源地址族和本地
IPv6 接口作用域。JmDNS 的 `getInetAddress()` 标识注册表所属的地址族；
已弃用的 `getInterface()` 可能返回同一个 Android 接口的另一个地址。
TXT 特性位与 AirPlay `/info` 使用相同的来源。

已有的 Bluetooth/iAP2 序列被复用：0x5703 携带网络凭据，
0x4301 携带所选端点（不带本地作用域后缀）。可读取的 AP BSSID 是
0x5703 的一个可选提示，与接收端的 AirPlay 身份相互独立。未知、占位、
零值、组播或格式错误的 AP 地址会被省略。P2P 与车机热点后端保持其既有
的单地址默认值与引导行为。

网络丢失或任一所选地址发生变化都会重启无线会话。关闭后端会注销其
回调，而不会断开 Wi-Fi。已保存的"自动启动车机热点"仅适用于车机热点
模式。

相关工作：[PR #22](https://github.com/shihabal3amri/DiPlay/pull/22) 也在一个
Android 7 移植中实现了外部 Wi-Fi。本功能使用当前架构，没有引入该移植
或其系统网络修改。

## 验证

测试覆盖 station 选择、脱敏后的网络信息、手动配置、地址作用域与地址族
选择、回调清理、设置持久化、双地址族发布/监听、端口回退、模式切换以及
引导编码。既有的 P2P 与热点测试与它们并行运行。

贡献者报告，在一台 2022 款 BYD 汉 DM-i（DiLink 4.0 / Android 10
(API 29)，固件 21.1.21.2401160.1）上完成最终实现的全新安装一切成功：
先前的测试应用被卸载，独立测试 APK 正常安装并配置，Same LAN 顺利进入
CarPlay，使用过程中未发现问题。这验证了该配置流程，但并不能确立与每
一种接收端、iOS 版本或路由器的兼容性。开放式网络与 WPA2/WPA3 混合
网络尚未在真机上单独验证。
设备检查时，请确认两台设备都保持着已有的 Wi-Fi 连接、CarPlay 能启动，
且 iPhone 可以使用在线服务。遇到失败时，请保存一份诊断报告，其中包含
尝试时间以及 iPhone 实际的 Wi-Fi 连接情况。仅仅缺少 Bonjour 控制服务
事件，并不能证明 CarPlay 发现失败。

## 构建

仅源码的 debug APK 会省略运行时配件认证。请配合 [BUILD.md](BUILD.md) 中
所述的显式外部资源使用既有的 `:mobile:assembleStandaloneDebug` 任务，
并遵循 [SECURITY.md](../SECURITY.md)。认证输入与 Android 签名材料必须
留在 Git 之外。应用签名密钥与 CarPlay 配件身份是彼此独立的。
