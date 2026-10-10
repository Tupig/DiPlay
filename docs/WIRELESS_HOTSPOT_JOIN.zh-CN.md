# 自动加入 CarPlay 热点

[English](WIRELESS_HOTSPOT_JOIN.md) · **简体中文**

iPhone 可能收到了可用的热点凭据，却无法自动加入。
如果它离开了当前的 Wi-Fi 网络，但手动选择接收端的热点时
能不经密码提示就启动 CarPlay，那么要把"关联失败"与
"Wi-Fi 链路已建立之后的失败"区分开来。

先按
[Apple 的 CarPlay 故障排查指引](https://support.apple.com/en-us/105109)
检查 CarPlay 网络的 Bluetooth、Wi-Fi 和"自动加入"。
请在停车状态下复现问题，并附上一份经过审阅的 DiPlay 诊断报告，
其中包含接收端固件/Android 版本、手机/iOS 版本、连接方式以及手动
选择是否成功。密码、完整的网络抓包、地址和私有设备备份
都不要出现在公开报告里。

## 文档记载的要求

[Apple 的 WWDC 2017 无线 CarPlay 会议](https://developer.apple.com/videos/play/wwdc2017/717/)
说明了凭据交换后的自动加入，并要求接入点的广播中包含 Apple Device
与 Interworking 信息元素。仅通过 iAP2 发送 Wi-Fi 凭据
并不能证明该 AP 确实广播了这些元素。

[Apple 的 WWDC 2023 连接性讨论](https://developer.apple.com/videos/play/wwdc2023/10150/)
指出简化版端点交换自 iOS 14 起支持。目前没有任何经过审阅的公开 Apple
通告把下述失败认定为 iOS 14.0.1 独有的缺陷。
这些公开演讲既没有对 DiPlay 做认证，也没有涵盖私有配件规范的
全部细节。

## 受控互操作性观察

一次受控对比使用了如下接收端与手机：

- DiPlay **0.2.12**，包名 `com.shihab.diplay`，原始安装版本。
- iPhone **8 Plus / iOS 14.0.1**，热点"自动加入"已启用。
- OnePlus **7T Pro / Android 13 (API 33)**，WPA2 热点，实际工作在 **5220 MHz /
  信道 44**。这是 DiPlay 的 BYD 支持范围之外的一次接收端互操作性观察；
  它并不意味着增加了 OnePlus 支持。
- DiPlay 广播的信道为 **0**，因为其应用无法读出有效信道。
  Android 存储的信道同样是 0（自动选择）。
- 手动加入无需输入密码即可成功。重命名热点可以复现自动加入失败，
  而手动加入依然可用。

全新的 Windows Native Wi-Fi BSS 扫描显示存在 Interworking IE **107**，
但没有 Apple 厂商元素。原本的厂商元素是 WMM（`00:50:f2`，类型 2）。
这次对比使用了相同的接收端、手机、发布版本和已保存的热点配置：

| AP 广播 | 观察到的自动加入结果 | 接收端证据 |
| --- | --- | --- |
| 加入 Apple 元素 | 自动加入成功；CarPlay 启动 | 一个 AP 客户端；IPv6 TCP 已接受；会话活跃 |
| 恢复原始配置，移除该元素 | 离开了家庭 Wi-Fi；未加入 | 零个 AP 客户端；30 秒后首次 TCP 超时 |
| 重新加入同一元素 | 自动加入成功；CarPlay 再次启动 | 一个 AP 客户端；IPv6 TCP 已接受；会话活跃 |

实验采用 5 GHz 厂商 IE `dd0800a0400000020021`（ID 221，
OUI `00:a0:40`，类型 0），遵循公开的
[LIVI 固定版本实现](https://github.com/f-io/LIVI/blob/a9562234429fc9d19d9f9804b6b288f9d435a922/native/livi-helperd/crates/livi-wifi/src/server.rs#L408)。
这些字节只是用来标识所测试的元素，并不是一份通用的、经过认证的
CarPlay 配置配方。

事先对 Android 热点的完整私有配置做了备份。特权 shell API 复制了
该配置，只改动其中的厂商元素列表。移除元素可完整还原原始配置。重新
加入后，从副本中清除新增的列表，得到的配置与原始配置完全一致，
从而验证了凭据、安全设置、频段及其他已保存字段均被原样保留。
每次广播变更都需要重启热点。全新扫描确认了元素的有无以及信道 44；
重启可能改变 AP 的 BSSID，因此这不是固定 BSSID 的对比。

Windows 通过
[WLAN_BSS_ENTRY](https://learn.microsoft.com/en-us/windows/win32/api/wlanapi/ns-wlanapi-wlan_bss_entry)
返回收到的 beacon/probe-response IE。
这并不是对 iPhone 自身关联交换过程的抓包。检查扫描的新鲜度，
以免把一条陈旧的 BSS 缓存条目当作当前的 AP。

## 结论与实现边界

移除/重新加入的对比表明，在该测试环境下，缺失的 Apple 广播元素就是
可复现的原因。只要元素存在，原始版本上信道 0 和 IPv6 都能成功。
无需改动信道，也无需切换 IPv4 优先。

这并不能确立一个 iOS 版本相关的缺陷，也不能说明是 0.2.12 引入的
回归。其他 iOS 版本、BYD 固件、2.4 GHz、Wi-Fi Direct、重启后重连
以及长时间会话的可靠性，都还没有经过该对比的测试。

Android 13 通过特权系统 API 而非普通应用权限来暴露厂商元素；参见
[AOSP SoftApConfiguration](https://android.googlesource.com/platform/packages/modules/Wifi/+/refs/heads/android13-release/framework/java/android/net/wifi/SoftApConfiguration.java)。
更早的 Android 与厂商固件需要单独验证能力与权限。在这台测试机上被接受的
特权写入，不足以作为通用应用内修复的证据。

## 自选式应用内修复

**Connection setup → Built-in car hotspot → Check hotspot repair**
会运行 DiPlay 授权的本地 ADB/app_process 辅助程序。打开该界面、
应用恢复前台或启动 CarPlay 都不会运行这一修复。Check 会读取已保存的
配置并准备一份私有的完整回滚快照；它不会改动热点。
检查成功后，**Add automatic-join information** 需要单独一次确认。
这是基于能力的实验性 BYD 工作，而不是对新车辆品牌的支持。

该辅助程序要求 Android 13/API 33 或更高版本、固件提供完整的
`SoftApConfiguration` getter/setter 与厂商元素/复制 API、
权威的热点 5 GHz 能力（含所支持的信道），以及一份已保存的、
**仅 5 GHz** 的频段配置（且该配置已被框架标记为用户已配置）。
它会拒绝 2.4 GHz、自动/混合频段、桥接 AP、
6/60 GHz，以及含糊或冲突的 Apple Device 元素。它不改动频段、凭据、
安全设置、信道、地址或 Interworking IE 107。已有的厂商元素按原顺序保留；
若已存在完全相同的 Apple 元素，则保持原样，不声称拥有其回滚权。
任何写入之前，都会先将副本与原始配置做比对。

对于默认/非用户配置，在备份或写入之前就会被拒绝。Android 13 的
[Wi-Fi 配置存储](https://android.googlesource.com/platform/packages/modules/Wifi/+/refs/heads/android13-release/service/java/com/android/server/wifi/WifiApConfigStore.java#L179)
在保存配置时总会把 user-configured 标志强制置为 true，因此原始的
false 值无法通过该 API 逐字还原。Apply 会重复同样的闸门检查，
包括针对早先准备好的快照。其余每个字段的相等性检查依然严格；
若出现意外的固件规范化或配置漂移，则保留恢复状态，
而不是把它当作成功保全。

在许多构建上，包括 WRITE_SETTINGS 在内的普通应用权限都不足以调用该
API。LocalAdb **仅支持明文 localhost:5555 ADB**。
它没有实现 Android 的 TLS 无线调试配对协议。Check 可以请求 ADB 授权；
Apply/Restore 绝不会在后台请求授权。
只能通过电脑的已配对 TLS ADB 连接访问的接收端，对该应用路径并不
自动可见。整个过程不会执行任何 root 命令、adbd 重配置或 TLS 降级。

在 Apply 或 Restore 之前，请停车、断开 CarPlay，并在车机设置中
关闭热点。这两次写入都要求一份新鲜、权威且处于**已禁用**状态的 AP
状态；启动中/停止中/失败/隐藏状态都会被拒绝。DiPlay 绝不会停止或
重启热点。之后请自行打开热点以更新广播，然后验证全新扫描与自动加入。
已保存配置的回读，并不能证明某个 OEM HAL 真的广播了该元素，也不能
证明 CarPlay 加入成功。

### 回滚与不确定结果

该 shell 辅助程序会把完整的原始配置与目标配置保存在一份带版本号、
带完整性校验的原子日志中，位置在
`/data/local/tmp/diplay-hotspot-join-<package>/`。目录权限为 0700，新建
文件为 0600，归属执行它的 shell/root UID。已存在的外来、
对其他人/组可访问或符号链接的条目都会被拒绝。快照文件以及目录重命名
与其父目录条目，都会在变更前完成同步并回读。
备份中包含凭据：绝不要把它们复制进报告、源码压缩包或 GitHub。它们
位于应用诊断导出、FileProvider 和 Android 应用备份存储之外。

另一个未完结的原始配置绝不会被另一次 Check 覆盖。Check 会在进程/应用
重建之后恢复回滚动作。**Restore saved hotspot** 需要确认，
且要求相同的框架/厂商固件标识，并且当前配置必须等于原始配置或本次
事务中明确的目标配置。配置漂移会被拒绝，而不是被覆盖。固件标识包括
框架/厂商构建属性以及 Wi-Fi 框架模块的摘要，该模块的更新可能独立于
Android 构建发生。标识缺失/不可读时按失败关闭处理。在解码旧的 Android
parcel 之前会先校验身份；不兼容或损坏的备份会原样保留，供私下排查。

被拒绝/异常的写入，只有当新鲜回读证明已保存的配置仍是原始值或我们的
目标值，且恢复是安全的，才会做补偿。回读或传输丢失、写入中断、
完成标记失败以及外部编辑，都会保留待处理日志并报告恢复情况/不确定性。
一次被取消的网络调用并不能证明辅助程序没有做出改动：请关闭热点并
重新 Check。取消标记与有界辅助程序时限共同把关；
已经发起的 Binder 调用可能仍会完成，之后会尝试经过验证的补偿。
进程与文件锁会拒绝并发的修复调用。Android 没有针对独立的
OEM/设置写入方的 compare-and-set 事务：新的检查可以缩小该竞争窗口，
但意外的变化会被暴露出来以便恢复。

日志能在应用进程死亡后存活，卸载后也可能残留。
请保持相同的包名与固件以便恢复。
不要删除未完结的备份。一次新的 Check 可以替换一份未被使用的已准备
快照或一次已完成的回滚；未完结的 pending/applied 快照会被保留。
本次修复绝不会移除先前已存在的元素。辅助程序不会打印任何凭据、备份
内容、私有固件标识或异常负载，也不会把它们加入诊断报告。

### 验证边界

一系列 JVM/Robolectric 专项测试覆盖了合并、平台复制/parcel 保留、
完整回滚、API/频段/冲突拒绝、持久化存储失败、配置与固件漂移、
取消、回读丢失、部分写入、重叠锁、框架 setter 规范化以及独立的 UI
确认。其中规范化回归测试使用了真实的 Android 13 配置 parcel，并配合
生产适配器、日志与事务：默认配置与早先准备的快照在写入前即被拒绝；
符合条件的用户配置可以应用、恢复并对失败/取消的写入做补偿，
且不放宽漂移检测。构建/lint 结果记录在
[hotspot repair validation](HOTSPOT_JOIN_VALIDATION.md) 中。

上文那次受控手机实验使用的是原始 APK 和一个独立的特权辅助程序，
并未走这条新的应用内路径。BYD 固件、应用发起的授权、广播刷新、
自动加入、重启持久性与长时间会话仍需真机验收。没有这些 API 的
Android 9/10 BYD 接收端依然不被本修复支持；普通的 CarPlay
连接方式则继续照常工作。
