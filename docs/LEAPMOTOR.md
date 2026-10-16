# Leapmotor（零跑）说明 / Leapmotor notes

**未经验证。** 目前还没有 Leapmotor 车机运行过 TuPigPlay。本页汇总了在为 2024 款 Leapmotor C11 增程版（LeapOS 3.21.40）做准备期间收集到的、已公开的零跑 C 系列安装事实。下文每一条都来自 OEM 或社区渠道，均不构成 TuPigPlay 兼容性承诺。在依赖任何一步之前，请先在车上逐项确认，并先阅读[保修警告](#保修--warranty)。

**Unvalidated.** No Leapmotor head unit has run TuPigPlay. This page collects published Leapmotor C-series install facts gathered while preparing a 2024 Leapmotor C11 EREV on LeapOS 3.21.40. Every item below is OEM- or community-sourced and none of it is a TuPigPlay compatibility claim. Confirm each step on the car before relying on it, and read the [warranty warning](#保修--warranty) first.

## 应用白名单 / The application whitelist

Leapmotor 车机只从 **应用 → U盘应用** 安装第三方 APK，且该包必须通过 Leapmotor 的白名单校验。独立报道显示，该校验会访问 Leapmotor 的后端服务器，而 2024 年的一次 OTA 将该请求重定向到社区服务器的抓包绕过方案已被关闭。据报告，只有在该 OTA 之前完成越狱的车辆才保留了这一能力。

Leapmotor head units install third-party APKs only from **应用 → U盘应用**, and only when the package passes a Leapmotor whitelist. Independent reporting says the check reached Leapmotor's backend, and that a 2024 OTA closed the packet-capture workaround which redirected that request to a community server. Only vehicles jailbroken before that OTA are reported to retain it.

当前校验究竟验证了什么**尚未确定**。有三种观察互相矛盾：

What the current check validates is **not established**. Three observations conflict:

| 观察 | 意味着 |
| --- | --- |
| 有报道称安装结果对版本敏感——喜马拉雅 v4.7.2 能安装，而同一包的其他版本失败 | 校验的不只是包名 |
| 据报道，携带白名单包名的重新打包第三方应用仍然能安装 | 只校验包名 |
| 一条关于 3.35.40 系统的 2026 年 6 月投诉称只有“内部白名单签名包”能安装 | 校验签名证书 |

| Observation | Implies |
| --- | --- |
| Reported installs are version-sensitive — 喜马拉雅 v4.7.2 installs while other versions of the same package fail | More than the package name is checked |
| Repackaged third-party apps carrying a whitelisted package name reportedly still install | The package name alone is checked |
| A June 2026 complaint about system 3.35.40 says only "内部白名单签名包" install | The signing certificate is checked |

LeapOS 3.21.40 处于上述报道之间，其行为尚未被摸清。不要预设以上任何一种。

LeapOS 3.21.40 sits between those reports and has not been characterized. Do not assume any of the three.

## 白名单应用（21 个）/ Whitelisted applications (21)

两个独立来源——一位车主转述的 Leapmotor 客服回复，以及 C10 车主论坛的一个帖子——列出了相同的 21 个应用：

Two independent sources — a Leapmotor customer-service reply relayed by an owner, and a C10 owners-forum post — list the same 21 applications:

> bilibili、QQ音乐、宝宝巴士、喜马拉雅、懒人听书、抖音、搜狐视频、欢乐斗地主、WPS office、今日头条、好看视频、微博、悟空识字、汽水音乐、潮汐、番茄免费小说、番茄畅听、百度、百度网盘、红果免费短剧、贝乐虎儿歌

论坛帖子称，需要特定系统版本才会开放完整 21 个：**纯电 3.10.16 / 增程 3.20.16**。导航应用是预装的，不能通过 U盘安装。

The forum post requires a specific system build before the list opens to 21: **纯电 3.10.16 / 增程 3.20.16**. Navigation applications are preinstalled, not U盘-installable.

## 有报告称可安装的版本 / Versions reported to install

在 C 系列车型上有相关报告；未在 3.21.40 上重新测试。

Reported on C-series cars; not re-tested on 3.21.40.

| 应用 | 版本 |
| --- | --- |
| QQ音乐 | v2.6.1.1（后续报告称该版本已无法安装，改用车载官方版可用） |
| 喜马拉雅 | v4.7.2（“其他版本全部失败”） |
| 哔哩哔哩 | v2.2.1 |
| 宝宝巴士故事 | v1.3.8 |
| 懒人听书 | v2.5.0 |
| 搜狐视频 | v1.0.03 |

| Application | Version |
| --- | --- |
| QQ音乐 | v2.6.1.1 (a later report says this build stopped installing and the current official car build works instead) |
| 喜马拉雅 | v4.7.2 ("other versions all failed") |
| 哔哩哔哩 | v2.2.1 |
| 宝宝巴士故事 | v1.3.8 |
| 懒人听书 | v2.5.0 |
| 搜狐视频 | v1.0.03 |

## U盘安装步骤 / U盘 install procedure

1. 将 U盘格式化为 **FAT32**。容量 16 GB–32 GB 是最常报告可用的范围；有一份报告称 2 GB 的 U盘也能装，另一份则称更大的 U盘会失败。
2. 将 APK 放在 U盘**根目录**。子目录中的文件不会被列出。
3. 插入**数据**口，而非仅充电口。在 C11/C10 上，数据口位于前排左侧，而不是中控台 USB。
4. 打开 **应用 → U盘应用**，等待扫描完成，然后点按 **安装**。
5. 已安装的应用会出现在 **已装应用** 以及常规应用列表中。

1. Format the drive **FAT32**. Capacity 16 GB–32 GB is the most commonly reported working range; one report installed from a 2 GB drive, another reports larger drives failing.
2. Put the APK in the **root** of the drive. Files in subdirectories are not listed.
3. Plug into a **data** port, not a charge-only port. On C11/C10 the data port is on the front/left rather than the centre-console USB.
4. Open **应用 → U盘应用**, wait for the scan, then tap **安装**.
5. Installed applications appear under **已装应用** and in the normal app list.

## 包名 / Package names

针对下文的[包名实验](#本-fork-中的包名实验--package-name-experiment-in-this-fork)，三个白名单车载版的报告包名如下：

For the [package-name experiment](#本-fork-中的包名实验--package-name-experiment-in-this-fork) below, the reported package names of three whitelisted car builds are:

| 应用 | 包名 |
| --- | --- |
| 喜马拉雅车机版 | `com.ximalaya.ting.android.car` |
| 哔哩哔哩车机版 | `com.bilibili.bilithings` |
| QQ音乐车机版 | `com.tencent.qqmusiccar` |

| Application | Package name |
| --- | --- |
| 喜马拉雅车机版 | `com.ximalaya.ting.android.car` |
| 哔哩哔哩车机版 | `com.bilibili.bilithings` |
| QQ音乐车机版 | `com.tencent.qqmusiccar` |

这些信息来自 APK 目录和发行方下载页，并非来自 Leapmotor 设备。`tv.danmaku.bili` 是 bilibili 的**手机版**，不是车机版。请务必针对你实际下载的 APK 进行确认：

These come from APK directories and publisher download pages, not from a Leapmotor device. `tv.danmaku.bili` is the **phone** build of bilibili, not the car build. Always confirm against the APK you actually download:

```sh
apkanalyzer manifest application-id path/to/app.apk
```

## 本 fork 中的包名实验 / Package-name experiment in this fork

`mobile` 接受一个可选的 Gradle 属性，用于替换最终的应用 ID。CI 的检查命令（单测、lint、普通 debug 构建）不设置它，因此不受影响；`build-apk` job 仅在构建 gallery3d 变体时设置它（见下节）。设置后，debug 的 `.hudtest` 后缀会被去掉，APK 将完全使用你所要求的包名。

`mobile` accepts an optional Gradle property that replaces the final application ID. The CI checks (unit tests, lint, ordinary debug builds) never set it and are unaffected; the `build-apk` job sets it only when building the gallery3d variant (next section). When it is set the debug `.hudtest` suffix is dropped, so the APK carries exactly the requested package name.

```sh
TUPIGPLAY_AUTH_ASSETS_DIR=/absolute/path/to/runtime-assets \
  ./gradlew :mobile:assembleDebug -PtupigplayApplicationId=com.example.app
```

该实验用于判断白名单是否以包名为键：

The experiment decides whether the whitelist keys on the package name:

1. 用默认 ID 构建，从 U盘安装，并记录确切的拒绝文案。这是基线。
2. 用一个**尚未安装在**车机上的白名单包名构建，再次安装。
3. 如果第 2 步能装上，说明在此系统版本上校验以包名为依据。如果被拒绝，或以不同信息（例如签名冲突）拒绝，则说明校验不只是针对包名。

1. Build with the default ID, install from U盘, and record the exact rejection text. This is the baseline.
2. Build with a whitelisted package name that is **not already installed** on the head unit, and install again.
3. If step 2 installs, the check is package-name based on this build. If it is refused, or refused with a different message such as a signature conflict, the check is not package-name only.

每次尝试的失败文案就是证据。如果基线提示“需要白名单”，而第 2 步出现签名错误，说明系统级安装路径确实执行了，并且比较了签名。

The failure text of each attempt is the evidence. "需要白名单" at baseline and a signature error in step 2 means the system-level install path ran and the signature was compared.

在安装之前，若实验成功需知的后果：

Consequences if it works, before you install:

- TuPigPlay 会以其他厂商的包名安装，在应用列表中与该应用无法区分。
- 在类似的车机上，被认可的包会被视为系统应用而无法卸载；需要恢复出厂设置才能移除。
- 只要白名单发生变化，或 OTA 加入证书校验（至少一个 2026 年的系统版本已经这样做），该对应关系就会失效。

- TuPigPlay is installed under another vendor's package name and is indistinguishable from that application in the app list.
- On comparable head units an approved package is treated as a system application and cannot be uninstalled; a factory reset is required to remove it.
- The mapping is invalidated whenever the whitelist changes or the OTA adds certificate checking, which at least one 2026 build has done.

## gallery3d 变体（CI 构建伪装包）/ The gallery3d variant (CI-built disguise package)

用户实测可用包（`DiPlay包名gallery.apk`，装上零跑 C11 跑过 CarPlay）实证了这条路：包名改为白名单内的 `com.android.gallery3d`（Android 系统图库），并用 AOSP 公开平台测试密钥（`CN=Android, O=Android`）签名，LeapOS 3.x 接受该签名。本 fork 把它产品化为 CI 产物。

A user-verified package (`DiPlay包名gallery.apk`, installed and running CarPlay on a Leapmotor C11) proved the path: rename the package to the whitelisted `com.android.gallery3d` (Android's stock gallery) and sign with the public AOSP platform test key (`CN=Android, O=Android`); LeapOS 3.x accepts that signature. This fork turns it into a CI artifact.

每次推送到 main 或 PR，`build-apk` job 会额外产出：

Every push to main and every PR additionally produces in the `build-apk` job:

- 产物名：`TuPigPlay-<version>-gallery3d-<短SHA>.apk`（Actions artifact，保留 7 天）
- 包名 `com.android.gallery3d`，release 构建（非 debuggable），`versionCode`/`versionName` 来自 `version.properties`
- 签名：AOSP 公开平台测试密钥（证书 SHA-256 `a40da80a…a71bf5dc`，与上述实测可用包的签名指纹一致）
- **无 MFi 认证资产**：MFi 资产被 `scripts/check_public_tree.py` 禁入仓库，CI 产物只能验证构建与安装，**不能连接 CarPlay**

- Artifact: `TuPigPlay-<version>-gallery3d-<shortSHA>.apk` (Actions artifact, 7-day retention)
- Package `com.android.gallery3d`, release build (not debuggable); `versionCode`/`versionName` come from `version.properties`
- Signed with the public AOSP platform test key (certificate SHA-256 `a40da80a…a71bf5dc`, identical to the verified package above)
- **No MFi authentication assets**: blocked from the repository by `scripts/check_public_tree.py`, so the CI artifact verifies build and installability only and **cannot connect CarPlay**

### 本地构建可连车的 gallery3d 包 / Building a connectable gallery3d package locally

MFi 运行时资产只在本地存在。带资产的可连车变体在本地构建：

MFi runtime assets exist only locally. Build the connectable variant locally with them:

```sh
export JAVA_HOME=<JDK 路径 / path to a JDK>
export TUPIGPLAY_AUTH_ASSETS_DIR=/absolute/path/to/runtime-assets  # 含 offline-mfi/identity.pk8 与 certificate.p7b
./gradlew :mobile:assembleRelease -PtupigplayApplicationId=com.android.gallery3d
```

产物为 `mobile/build/outputs/apk/release/mobile-release-unsigned.apk`。用 AOSP 测试密钥签名（密钥不入库，从 AOSP 获取 `build/target/product/security/testkey.pk8` 与 `testkey.x509.pem`）：

The output is `mobile/build/outputs/apk/release/mobile-release-unsigned.apk`. Sign it with the AOSP test key (not stored in this repository; obtain `build/target/product/security/testkey.pk8` and `testkey.x509.pem` from AOSP):

```sh
BT="$ANDROID_HOME/build-tools/36.0.0"
"$BT/zipalign" -f -p 4 mobile/build/outputs/apk/release/mobile-release-unsigned.apk gallery3d-aligned.apk
"$BT/apksigner" sign --key testkey.pk8 --cert testkey.x509.pem --out TuPigPlay-gallery3d.apk gallery3d-aligned.apk
"$BT/apksigner" verify --print-certs TuPigPlay-gallery3d.apk   # 应含 CN=Android, OU=Android, O=Android
```

### 冲突与升级 / Conflicts and upgrades

- **同签名同包名**：本变体与实测可用包的签名指纹一致，可在其上原地升级；但旧实验包 `versionCode` 为 40030，高于本 fork 的 39，直接安装会被系统拒绝降级。先卸载旧包（设置内卸载或 `adb uninstall com.android.gallery3d`），再安装新包。
- **系统图库冲突**：若车机自带 `com.android.gallery3d` 系统应用且签名不同，安装会以 `INSTALL_FAILED_UPDATE_INCOMPATIBLE` 失败。实测的 2024 款 C11（LeapOS 3.x）无此冲突；其他车型安装前先自查。
- **失效风险**：白名单是系统行为，OTA 随时可能加入证书校验（至少一个 2026 年的系统版本已这样做）。届时本变体与所有包名伪装路径一并失效。

- **Same signature, same package**: this variant's signing fingerprint matches the verified package, so it upgrades in place; however the old experimental package has `versionCode` 40030, higher than this fork's 39, so a direct install is rejected as a downgrade. Uninstall the old package first (from Settings or `adb uninstall com.android.gallery3d`), then install.
- **System gallery conflict**: if the head unit ships its own `com.android.gallery3d` system app with a different signature, installation fails with `INSTALL_FAILED_UPDATE_INCOMPATIBLE`. The verified 2024 C11 (LeapOS 3.x) has no such conflict; check your own model first.
- **Invalidation risk**: the whitelist is system behavior and an OTA can add certificate checking at any time (at least one 2026 build already has). This variant then fails together with every package-disguise path.

## 已关闭的路径 / Paths that are closed

| 路径 | 状态 |
| --- | --- |
| 虚拟机（**虚拟机设置 → USB导入**，`LPOS` 文件夹） | 大约在 2023 年加入，用于在白名单外导入应用；据报告当前系统（包括 3.21.40）中已不存在。 |
| 将白名单请求抓包重定向到社区服务器 | 已被 2024 年的 OTA 关闭。不要尝试冒充 Leapmotor 后端。 |
| 开发者选项与 USB 调试 | 在本车型的 **设置 → 关于** 中不存在。连续点按版本/构建字段七次是否会显示，尚未得到确认。 |
| T03 零听 → 讯飞输入法 → 手机助手 → 自动装 路径进入原生 Android 设置 | 为旧系统上的 T03 记录过。在未确认存在相同入口之前，不适用于 C 系列。 |

| Path | Status |
| --- | --- |
| Virtual machine (**虚拟机设置 → USB导入**, `LPOS` folder) | Added around 2023 to import apps outside the whitelist; reported absent from current systems, including 3.21.40. |
| Packet-capture redirect of the whitelist request to a community server | Closed by a 2024 OTA. Do not attempt to impersonate Leapmotor's backend. |
| Developer options and USB debugging | Not present in **Settings → About** on this vehicle. Tapping the build/version field seven times has not been confirmed to reveal it. |
| T03 零听 → 讯飞输入法 → 手机助手 → 自动装 route to stock Android settings | Documented for T03 on an older system. Not applicable to C-series without confirming the same entry points exist. |

## ADB

运行 TuPigPlay 不需要 ADB。它只对无法满足白名单的车机有意义。

ADB is not required to run TuPigPlay. It matters only for head units whose whitelist cannot be satisfied.

历史观察（更早的系统版本）：iPhone 热点网段 `172.20.10.0/28` 上有一个主机在 5555 端口响应 `unauthorized`，且从未出现过授权提示。这更像是遗留可达的 Leapmotor 服务网 ADB，而不是用户可启用的调试端口。

Earlier observation (on an earlier system build): the iPhone hotspot range `172.20.10.0/28` exposes a host on port 5555 answering `unauthorized` with no authorization prompt ever shown. That is consistent with a Leapmotor service-network ADB left reachable, not with a user-enableable debug port.

当前目标车辆已启用 ADB（2026-10 确认），TuPigPlay 的零跑 ADB 功能——热点自动开启、车速读取（#8）——在这辆车上可用。此前「公开渠道没有已知方法可在 LeapOS 3.x 上启用 ADB」的结论对当前车辆不再成立；若你的车机没有 ADB，这些功能保持降级，不影响 CarPlay 主功能。

The current target vehicle has ADB enabled (confirmed 2026-10), and TuPigPlay's Leapmotor ADB features — automatic hotspot and vehicle-speed reading (#8) — work on it. The earlier conclusion that no public method enables ADB on LeapOS 3.x no longer holds for this vehicle; on a head unit without ADB those features stay degraded and CarPlay itself is unaffected.

## 车速命令标定 / Calibrating the speed command

零跑没有公开的车辆数据接口，车速经车机本机 ADB（`127.0.0.1:5555`）执行一条 shell 命令并读取其输出（#8）。命令模板保存在 `LeapmotorVehicleDataStore`；未标定时 `LeapmotorVehicleAccess.getVehicleSpeed()` 返回 null，功能降级，不崩溃。

Leapmotor exposes no vehicle-data API, so speed is read over the head unit's own ADB (`127.0.0.1:5555`) by running one shell command (#8). The template lives in `LeapmotorVehicleDataStore`; until it is calibrated, `LeapmotorVehicleAccess.getVehicleSpeed()` returns null and the feature stays degraded without crashing.

标定步骤（需要可用的 ADB）：

1. `adb shell service list` — 找车辆服务候选（名称含 car / vehicle / leapmotor 字样）。
2. `adb shell dumpsys <候选服务>`、`adb shell getprop | grep -i -e car -e vehicle -e speed` — 找一个随车速变化的数值。
3. 行驶中（由副驾操作）多次采样，确认数值与仪表车速一致、单位为 km/h。
4. 把确定的 shell 命令写入车速命令存储，验证 `getVehicleSpeed()` 返回实际车速。

Calibration steps (requires working ADB):

1. `adb shell service list` — shortlist vehicle services (names containing car / vehicle / leapmotor).
2. `adb shell dumpsys <candidate>`, `adb shell getprop | grep -i -e car -e vehicle -e speed` — find a value that changes with the speed.
3. While driving (operated by the passenger), sample repeatedly and confirm the value matches the cluster speedometer in km/h.
4. Store the resulting shell command in the speed-command store and verify `getVehicleSpeed()` returns the live speed.

读取端把命令输出中的第一个数字当作 km/h（`LeapmotorVehicleAccess.parseSpeed`），因此命令应让车速是输出中的第一个数字，或在命令内用 `grep` / `awk` 先行提取。

The reader takes the first number in the command output as km/h (`LeapmotorVehicleAccess.parseSpeed`), so write the command so the speed is the first number in the output, or extract it first with `grep` / `awk`.

## 保修 / Warranty

有车主报告侧载应用可能影响保修索赔：保修手册排除由未经认可的软件改动导致的故障，并且售后会被指示检查这一点。开始前先保存恢复出厂设置的路径，是常见的缓解办法。

Owners report that sideloading can affect warranty claims: the warranty booklet excludes faults caused by unauthorised software changes, and service is instructed to check for it. Saving a factory-reset path before starting is the usual mitigation.

## 来源 / Sources

- 白名单的存在、服务端校验与 2024 年 OTA 关闭：[虎嗅，2025 年 11 月 11 日](https://www.huxiu.com/article/4803348.html)；转载见[脖子哥](https://mp.weixin.qq.com/s/2MPyaQ72M39GXQClJReySw)与 [tojoyun，2025 年 11 月 13 日](https://tojoyun.com/news/zixun/6915063097ed1f5ac6a0c5ff)。
- 3.35.40 系统上的“白名单签名包”：[车质网投诉，2026 年 6 月 1 日](https://www.12365auto.com/zlts/20260601/1672822.shtml)。
- 21 个应用列表及报告版本：[夜雨聆风](https://www.yeyulingfeng.com/222080.html)（客服回复加车主实测版本）；相同的 21 个名称及所需系统版本见[汽车之家 C10 论坛](https://club.autohome.com.cn/bbs/thread/e52d158a2b4efd06/108728290-1.html)。
- U盘安装步骤：[新出行](https://www.xchuxing.com/article/124125)；[汽车之家 C11 论坛](https://club.autohome.com.cn/bbs/thread/7d089e78e6743966/109027712-1.html)。
- 虚拟机导入：[易车，2023 年 3 月 2 日](https://hao.yiche.com/wenzhang/78905749/)；[沙发管家](http://www.xmxgame.com/articles/Ei68qp5Tey54lIOz.html)。
- 经销商白名单讲解视频：[零跑中心迎宾大道总店，2023 年 5 月 10 日](https://www.bilibili.com/video/BV17o4y147wA/)。
- T03 原生设置路径：[博客园，2024 年 7 月 25 日](https://www.cnblogs.com/Sendige/p/18320192)。
- 保修立场：[Leapmotor Forum](https://www.leapmotorforum.com/threads/adding-apps-to-t03.294/)。
- 同类品牌的服务模式 ADB：[小鹏服务模式笔记](https://www.cnblogs.com/ahuangawu/articles/19128583)。

- Whitelist existence, server-side check and the 2024 OTA closure: [虎嗅, 11 Nov 2025](https://www.huxiu.com/article/4803348.html); syndicated as [脖子哥](https://mp.weixin.qq.com/s/2MPyaQ72M39GXQClJReySw) and [tojoyun, 13 Nov 2025](https://tojoyun.com/news/zixun/6915063097ed1f5ac6a0c5ff).
- "白名单签名包" on system 3.35.40: [车质网 complaint, 1 Jun 2026](https://www.12365auto.com/zlts/20260601/1672822.shtml).
- The 21-application list and reported versions: [夜雨聆风](https://www.yeyulingfeng.com/222080.html) (customer-service reply plus owner-tested versions); the same 21 names and the required system builds on [汽车之家 C10 论坛](https://club.autohome.com.cn/bbs/thread/e52d158a2b4efd06/108728290-1.html).
- U盘 procedure: [新出行](https://www.xchuxing.com/article/124125); [汽车之家 C11 论坛](https://club.autohome.com.cn/bbs/thread/7d089e78e6743966/109027712-1.html).
- Virtual-machine import: [易车, 2 Mar 2023](https://hao.yiche.com/wenzhang/78905749/); [沙发管家](http://www.xmxgame.com/articles/Ei68qp5Tey54lIOz.html).
- Dealer whitelist overview video: [零跑中心迎宾大道总店, 10 May 2023](https://www.bilibili.com/video/BV17o4y147wA/).
- T03 stock-settings route: [博客园, 25 Jul 2024](https://www.cnblogs.com/Sendige/p/18320192).
- Warranty position: [Leapmotor Forum](https://www.leapmotorforum.com/threads/adding-apps-to-t03.294/).
- Service-mode ADB on a comparable brand: [Xiaopeng service-mode notes](https://www.cnblogs.com/ahuangawu/articles/19128583).
