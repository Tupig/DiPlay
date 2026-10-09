# Leapmotor (零跑) notes

**Unvalidated.** No Leapmotor head unit has run DiPlay. This page collects published Leapmotor C-series install facts gathered while preparing a 2024 Leapmotor C11 EREV on LeapOS 3.21.40. Every item below is OEM- or community-sourced and none of it is a DiPlay compatibility claim. Confirm each step on the car before relying on it, and read the [warranty warning](#warranty) first.

## The application whitelist

Leapmotor head units install third-party APKs only from **应用 → U盘应用**, and only when the package passes a Leapmotor whitelist. Independent reporting says the check reached Leapmotor's backend, and that a 2024 OTA closed the packet-capture workaround which redirected that request to a community server. Only vehicles jailbroken before that OTA are reported to retain it.

What the current check validates is **not established**. Three observations conflict:

| Observation | Implies |
| --- | --- |
| Reported installs are version-sensitive — 喜马拉雅 v4.7.2 installs while other versions of the same package fail | More than the package name is checked |
| Repackaged third-party apps carrying a whitelisted package name reportedly still install | The package name alone is checked |
| A June 2026 complaint about system 3.35.40 says only "内部白名单签名包" install | The signing certificate is checked |

LeapOS 3.21.40 sits between those reports and has not been characterized. Do not assume any of the three.

## Whitelisted applications (21)

Two independent sources — a Leapmotor customer-service reply relayed by an owner, and a C10 owners-forum post — list the same 21 applications:

> bilibili、QQ音乐、宝宝巴士、喜马拉雅、懒人听书、抖音、搜狐视频、欢乐斗地主、WPS office、今日头条、好看视频、微博、悟空识字、汽水音乐、潮汐、番茄免费小说、番茄畅听、百度、百度网盘、红果免费短剧、贝乐虎儿歌

The forum post requires a specific system build before the list opens to 21: **纯电 3.10.16 / 增程 3.20.16**. Navigation applications are preinstalled, not U盘-installable.

## Versions reported to install

Reported on C-series cars; not re-tested on 3.21.40.

| Application | Version |
| --- | --- |
| QQ音乐 | v2.6.1.1 (a later report says this build stopped installing and the current official car build works instead) |
| 喜马拉雅 | v4.7.2 ("other versions all failed") |
| 哔哩哔哩 | v2.2.1 |
| 宝宝巴士故事 | v1.3.8 |
| 懒人听书 | v2.5.0 |
| 搜狐视频 | v1.0.03 |

## U盘 install procedure

1. Format the drive **FAT32**. Capacity 16 GB–32 GB is the most commonly reported working range; one report installed from a 2 GB drive, another reports larger drives failing.
2. Put the APK in the **root** of the drive. Files in subdirectories are not listed.
3. Plug into a **data** port, not a charge-only port. On C11/C10 the data port is on the front/left rather than the centre-console USB.
4. Open **应用 → U盘应用**, wait for the scan, then tap **安装**.
5. Installed applications appear under **已装应用** and in the normal app list.

## Package names

For the [package-name experiment](#package-name-experiment-in-this-fork) below, the reported package names of three whitelisted car builds are:

| Application | Package name |
| --- | --- |
| 喜马拉雅车机版 | `com.ximalaya.ting.android.car` |
| 哔哩哔哩车机版 | `com.bilibili.bilithings` |
| QQ音乐车机版 | `com.tencent.qqmusiccar` |

These come from APK directories and publisher download pages, not from a Leapmotor device. `tv.danmaku.bili` is the **phone** build of bilibili, not the car build. Always confirm against the APK you actually download:

```sh
apkanalyzer manifest application-id path/to/app.apk
```

## Package-name experiment in this fork

`mobile` accepts an optional Gradle property that replaces the final application ID. It is unset in CI, so ordinary builds and every test in the CI command are unaffected. When it is set the debug `.hudtest` suffix is dropped, so the APK carries exactly the requested package name.

```sh
DIPLAY_AUTH_ASSETS_DIR=/absolute/path/to/runtime-assets \
  ./gradlew :mobile:assembleDebug -PdiplayApplicationId=com.example.app
```

The experiment decides whether the whitelist keys on the package name:

1. Build with the default ID, install from U盘, and record the exact rejection text. This is the baseline.
2. Build with a whitelisted package name that is **not already installed** on the head unit, and install again.
3. If step 2 installs, the check is package-name based on this build. If it is refused, or refused with a different message such as a signature conflict, the check is not package-name only.

The failure text of each attempt is the evidence. "需要白名单" at baseline and a signature error in step 2 means the system-level install path ran and the signature was compared.

Consequences if it works, before you install:

- DiPlay is installed under another vendor's package name and is indistinguishable from that application in the app list.
- On comparable head units an approved package is treated as a system application and cannot be uninstalled; a factory reset is required to remove it.
- The mapping is invalidated whenever the whitelist changes or the OTA adds certificate checking, which at least one 2026 build has done.

## Paths that are closed

| Path | Status |
| --- | --- |
| Virtual machine (**虚拟机设置 → USB导入**, `LPOS` folder) | Added around 2023 to import apps outside the whitelist; reported absent from current systems, including 3.21.40. |
| Packet-capture redirect of the whitelist request to a community server | Closed by a 2024 OTA. Do not attempt to impersonate Leapmotor's backend. |
| Developer options and USB debugging | Not present in **Settings → About** on this vehicle. Tapping the build/version field seven times has not been confirmed to reveal it. |
| T03 零听 → 讯飞输入法 → 手机助手 → 自动装 route to stock Android settings | Documented for T03 on an older system. Not applicable to C-series without confirming the same entry points exist. |

## ADB

ADB is not required to run DiPlay. It matters only for head units whose whitelist cannot be satisfied.

Observed on the target vehicle: the iPhone hotspot range `172.20.10.0/28` exposes a host on port 5555 answering `unauthorized` with no authorization prompt ever shown. That is consistent with a Leapmotor service-network ADB left reachable, not with a user-enableable debug port, and it cannot be used as-is.

No public method enables ADB on LeapOS 3.x. Comparable brands expose one: BYD keeps ADB open, and Xiaopeng units are documented as opening **维修模式 → 检查模式 → 常用命令合集** from an authorised service visit before `setprop service.adb.tcp.port 5050`. No equivalent Leapmotor procedure is published.

## Warranty

Owners report that sideloading can affect warranty claims: the warranty booklet excludes faults caused by unauthorised software changes, and service is instructed to check for it. Saving a factory-reset path before starting is the usual mitigation.

## Sources

- Whitelist existence, server-side check and the 2024 OTA closure: [虎嗅, 11 Nov 2025](https://www.huxiu.com/article/4803348.html); syndicated as [脖子哥](https://mp.weixin.qq.com/s/2MPyaQ72M39GXQClJReySw) and [tojoyun, 13 Nov 2025](https://tojoyun.com/news/zixun/6915063097ed1f5ac6a0c5ff).
- "白名单签名包" on system 3.35.40: [车质网 complaint, 1 Jun 2026](https://www.12365auto.com/zlts/20260601/1672822.shtml).
- The 21-application list and reported versions: [夜雨聆风](https://www.yeyulingfeng.com/222080.html) (customer-service reply plus owner-tested versions); the same 21 names and the required system builds on [汽车之家 C10 论坛](https://club.autohome.com.cn/bbs/thread/e52d158a2b4efd06/108728290-1.html).
- U盘 procedure: [新出行](https://www.xchuxing.com/article/124125); [汽车之家 C11 论坛](https://club.autohome.com.cn/bbs/thread/7d089e78e6743966/109027712-1.html).
- Virtual-machine import: [易车, 2 Mar 2023](https://hao.yiche.com/wenzhang/78905749/); [沙发管家](http://www.xmxgame.com/articles/Ei68qp5Tey54lIOz.html).
- Dealer whitelist overview video: [零跑中心迎宾大道总店, 10 May 2023](https://www.bilibili.com/video/BV17o4y147wA/).
- T03 stock-settings route: [博客园, 25 Jul 2024](https://www.cnblogs.com/Sendige/p/18320192).
- Warranty position: [Leapmotor Forum](https://www.leapmotorforum.com/threads/adding-apps-to-t03.294/).
- Service-mode ADB on a comparable brand: [Xiaopeng service-mode notes](https://www.cnblogs.com/ahuangawu/articles/19128583).
