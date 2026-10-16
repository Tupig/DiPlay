# 2024 款零跑 C11 (8295P) TuPigPlay 安装完整指南 / Complete TuPigPlay installation guide for the 2024 Leapmotor C11 (8295P)

> **适用车型**：2024/2025 款零跑 C11 增程版/纯电版  
> **芯片**：高通 SA8295P  
> **屏幕**：14.6 英寸 2.5K (2560×1600) 中控 + 12.3 英寸仪表盘 + 副驾娱乐屏  
> **系统**：LeapOS 3.21.40 / 3.35.40+  
> **安装方式**：U 盘安装 + 白名单包名校验

> **Applicable vehicles**: 2024/2025 Leapmotor C11 EREV / BEV  
> **Chip**: Qualcomm SA8295P  
> **Screens**: 14.6-inch 2.5K (2560×1600) centre screen + 12.3-inch cluster + passenger display  
> **System**: LeapOS 3.21.40 / 3.35.40+  
> **Install method**: U-disk install + whitelist package-name check

---

## ⚠️ 核心限制（必读）/ Core limits (read this first)

| 限制项 | 说明 | 影响 |
|--------|------|------|
| **仅 U 盘安装** | 车机只识别 `应用 → U盘应用` 入口 | 无法通过 ADB、浏览器、微信传输安装 |
| **白名单校验** | 安装包包名必须在 21 个白名单内 | 必须使用白名单包名构建 APK |
| **签名校验不确定** | 3.35.40+ 可能校验签名证书 | 建议停留在 3.21.40 或更早版本 |
| **无 ADB** | 无法开启 USB 调试、无法抓包 | 所有 ADB 依赖功能不可用 |
| **无开发者选项** | 设置中无开发者模式入口 | 无法授予特殊权限（悬浮窗、辅助服务等） |

| Limit | Detail | Impact |
|-------|--------|--------|
| **U-disk install only** | The head unit only recognizes the `应用 → U盘应用` entry | No install through ADB, browser or WeChat transfer |
| **Whitelist check** | The APK package name must be within the 21-entry whitelist | The APK must be built with a whitelisted package name |
| **Signature check uncertain** | 3.35.40+ may verify the signing certificate | Staying on 3.21.40 or earlier is recommended |
| **No ADB** | USB debugging cannot be enabled; packet capture is impossible | Every ADB-dependent feature is unavailable |
| **No developer options** | Settings has no developer-mode entry | Special permissions (overlay, accessibility services, etc.) cannot be granted |

---

## 📦 白名单包名选择策略 / Whitelist package-name selection strategy

根据社区实测报告，推荐优先级：

Recommended priority, based on community-tested reports:

| 优先级 | 包名 | 应用名 | 实测版本 | 成功率 | 备注 |
|--------|------|--------|----------|--------|------|
| **1** | `com.ximalaya.ting.android.car` | 喜马拉雅车机版 | v4.7.2 | ⭐⭐⭐⭐⭐ | 报告最稳定，其他版本多失败 |
| **2** | `com.bilibili.bilithings` | 哔哩哔哩车机版 | v2.2.1 | ⭐⭐⭐⭐ | 需确认是车机版而非手机版 |
| **3** | `com.tencent.qqmusiccar` | QQ音乐车机版 | v2.6.1.1 | ⭐⭐⭐ | 后续报告称该版本已失效 |

| Priority | Package name | App | Tested version | Success | Notes |
|----------|--------------|-----|----------------|---------|-------|
| **1** | `com.ximalaya.ting.android.car` | 喜马拉雅 car build | v4.7.2 | ⭐⭐⭐⭐⭐ | Most stable in reports; most other versions fail |
| **2** | `com.bilibili.bilithings` | 哔哩哔哩 car build | v2.2.1 | ⭐⭐⭐⭐ | Confirm it is the car build, not the phone build |
| **3** | `com.tencent.qqmusiccar` | QQ音乐 car build | v2.6.1.1 | ⭐⭐⭐ | Later reports say this version stopped working |

> ⚠️ **重要**：`tv.danmaku.bili` 是 B 站**手机版**包名，**不可用**！必须用车机版包名。

> ⚠️ **Important**: `tv.danmaku.bili` is bilibili's **phone** package name and **does not work**! The car package name must be used.

---

## 🛠️ 完整构建流程 / Complete build procedure

### 环境准备 / Environment preparation

```bash
# 1. 克隆项目 / Clone the project
git clone https://github.com/Tupig/DiPlay.git
cd TuPigPlay

# 2. 准备 JDK 17+ 和 Android SDK / Prepare JDK 17+ and the Android SDK
# 建议使用 Android Studio 自带 SDK / The Android Studio SDK is recommended

# 3.（可选）配置发布签名密钥（环境变量）/ (Optional) configure the release signing key (environment variables)
export ANDROID_KEYSTORE_PATH=/path/to/release.keystore
export ANDROID_KEYSTORE_PASSWORD=your_keystore_password
export ANDROID_KEY_ALIAS=your_key_alias
export ANDROID_KEY_PASSWORD=your_key_password
```

> **签名行为**：设置了 `ANDROID_KEYSTORE_PATH` 时，伪装包名构建直接产出已签名 APK；未设置时产出未签名 APK（`mobile-release-unsigned.apk`），需要自行签名（`zipalign` + `apksigner`）。零跑场景推荐 AOSP 公开平台测试密钥——与实测可用的 `DiPlay包名gallery.apk` 同签名，见 [LEAPMOTOR.md](LEAPMOTOR.md) 的 gallery3d 变体章节。

> **Signing behavior**: with `ANDROID_KEYSTORE_PATH` set, the disguised-package build outputs a signed APK directly; without it you get an unsigned APK (`mobile-release-unsigned.apk`) that you must sign yourself (`zipalign` + `apksigner`). For Leapmotor the public AOSP platform test key is recommended — the same signature as the verified `DiPlay包名gallery.apk`; see the gallery3d variant section in [LEAPMOTOR.md](LEAPMOTOR.md).

### 构建命令（核心：指定白名单包名）/ Build commands (core: specify the whitelist package name)

```bash
# 方案 A：喜马拉雅包名（推荐，成功率最高）
# Option A: Ximalaya package name (recommended, highest success rate)
./gradlew :mobile:assembleRelease \
  -PtupigplayApplicationId=com.ximalaya.ting.android.car

# 方案 B：B 站车机版包名 / Option B: bilibili car package name
./gradlew :mobile:assembleRelease \
  -PtupigplayApplicationId=com.bilibili.bilithings

# 方案 C：QQ音乐车机版包名 / Option C: QQ Music car package name
./gradlew :mobile:assembleRelease \
  -PtupigplayApplicationId=com.tencent.qqmusiccar
```

### 输出文件 / Output files

```
mobile/build/outputs/apk/release/
├── mobile-release.apk             # 配置了发布密钥时 / when a release key is configured
└── mobile-release-unsigned.apk    # 未配置时，需自行签名 / unsigned when not; sign it yourself
```

> **注意**：构建时会自动去掉 debug 的 `.hudtest` 后缀，APK 完全使用指定包名。

> **Note**: The build drops the debug `.hudtest` suffix automatically; the APK carries exactly the specified package name.

---

## 💾 U 盘准备与安装步骤 / U-disk preparation and installation steps

### U 盘要求 / U-disk requirements

- **格式**：FAT32（非 exFAT、NTFS）
- **容量**：16–32 GB（2GB 可能可用，>64GB 易失败）
- **品牌**：SanDisk、金士顿、三星等主流品牌
- **接口**：USB 2.0/3.0 均可

- **Format**: FAT32 (not exFAT or NTFS)
- **Capacity**: 16–32 GB (2 GB may work; >64 GB often fails)
- **Brand**: mainstream brands such as SanDisk, Kingston, Samsung
- **Port**: USB 2.0/3.0 both work

### 文件放置 / File placement

```
U盘根目录/
└── Ximalaya_Car_v4.7.2.apk   # 必须在根目录，子目录不识别 / must be in the root; subdirectories are not listed
```

> **命名建议**：用白名单应用原名 + 版本号，如 `Ximalaya_Car_v4.7.2.apk`

> **Naming suggestion**: whitelist app name + version, e.g. `Ximalaya_Car_v4.7.2.apk`

### 车机操作步骤 / Head-unit steps

1. **插入 U 盘** → 前排左侧**数据口**（中控台 USB 通常仅充电）
2. **打开**：`应用` → `U盘应用` → 等待扫描完成
3. **点击**：找到 `喜马拉雅` / `Ximalaya_Car_v4.7.2` → `安装`
4. **等待**：进度条跑完 → 提示"安装成功"
5. **启动**：在 `已装应用` 或主应用列表找到 `喜马拉雅` 图标打开
6. **授权**：首次启动依次允许
   - 蓝牙/附近设备（配对 iPhone）
   - 麦克风（Siri/通话）
   - 通知（连接状态栏）
   - 存储（诊断报告导出）

1. **Insert the U disk** → front-left **data port** (the centre-console USB is usually charge-only)
2. **Open**: `应用` → `U盘应用` → wait for the scan to finish
3. **Tap**: find `喜马拉雅` / `Ximalaya_Car_v4.7.2` → `安装`
4. **Wait**: progress bar completes → "install succeeded"
5. **Launch**: open the `喜马拉雅` icon under `已装应用` or the main app list
6. **Grant**: on first launch allow, in turn
   - Bluetooth/Nearby devices (to pair the iPhone)
   - Microphone (Siri/calls)
   - Notifications (connection status)
   - Storage (diagnostic report export)

---

## 🔧 首次配置向导 / First-run setup wizard

### 1. 连接方式选择 / 1. Choosing the connection mode

```
设置 → 连接 → 连接设置 / Settings → Connection → Connection setup
├── 内置车载热点（推荐，5GHz 优先）/ Built-in car hotspot (recommended, 5 GHz first)
│   └── 开启车载热点 → 记录 SSID/密码 → 保存 / Turn on the hotspot → note SSID/password → Save
├── Wi-Fi Direct（备选）/ Wi-Fi Direct (alternative)
│   └── 无需热点，但可能回落 2.4GHz / No hotspot needed, may fall back to 2.4 GHz
└── 同局域网/现有 Wi-Fi（高级）/ Same LAN / Existing Wi-Fi (advanced)
    └── 需关闭路由器 AP 隔离 / Router AP isolation must be disabled
```

### 2. iPhone 配对 / 2. Pairing the iPhone

1. iPhone 开启 **蓝牙 + Wi-Fi** / Turn on **Bluetooth + Wi-Fi** on the iPhone
2. iPhone `设置 → 蓝牙` → 与车机配对 / iPhone `Settings → Bluetooth` → pair with the head unit
3. TuPigPlay 主页 → `选择 iPhone` → 选中已配对设备 / TuPigPlay home → `Choose iPhone` → select the paired device
4. 点击 `连接手机` → iPhone 弹窗允许 "CarPlay" "本地网络" "蓝牙" / Tap `Connect phone` → allow "CarPlay", "Local Network" and "Bluetooth" in the iPhone prompts

### 3. 分辨率/帧率调优（8295P 推荐）/ 3. Resolution/frame-rate tuning (recommended for 8295P)

```
设置 → 显示 / Settings → Display
├── 图标/文字大小：默认 / 110% / Icon/text size: Default / 110%
├── 分辨率：默认 (2560×1600) / 80% (2048×1280) / 60% / Resolution: Default (2560×1600) / 80% (2048×1280) / 60%
├── 帧率：60 fps（8295P 满血） / 30 fps（省电）/ Frame rate: 60 fps (full SA8295P) / 30 fps (power saving)
├── 高效视频 (HEVC)：关闭（兼容性优先）/ Efficient video (HEVC): off (compatibility first)
└── 界面大小：自动 / Interface size: Auto
```

### 4. 三屏显示配置 / 4. Three-screen display configuration

```
设置 → 车辆 → 仪表盘导航 / Settings → Vehicle → Cluster navigation
├── 仪表盘显示 CarPlay 地图：开启（需白名单包名权限）/ Cluster shows the CarPlay map: on (needs whitelist package permission)
├── 副驾屏显示：开启（副驾娱乐屏同步）/ Passenger screen: on (mirrors to the passenger display)
└── HUD 显示：关闭（C11 无 HUD）/ HUD: off (the C11 has no HUD)
```

---

## 🐛 常见故障排查 / Common troubleshooting

| 现象 | 可能原因 | 解决方案 |
|------|----------|----------|
| 提示"非白名单应用" | 包名错误/未在白名单 | 核对包名、重新构建、确认 U 盘根目录 |
| 提示"签名冲突/安装包解析错误" | 同包名应用已存在/签名不匹配 | 卸载旧版、或更换白名单包名重新构建 |
| 安装后点击图标闪退/白屏 | 系统版本不兼容/权限缺失 | 查看诊断报告、尝试降级系统、更换包名 |
| 无法搜索到 iPhone | 蓝牙未配对/热点未开/频段不匹配 | 重新配对蓝牙、开启 5GHz 热点、尝试 Wi-Fi Direct |
| 连接后秒断/卡在"准备 CarPlay" | 热点密码错误/信道干扰/白名单权限不足 | 核对密码、换信道、确认包名权限 |
| 仪表盘不显示地图 | 包名无仪表权限/系统版本限制 | 确认白名单包名、尝试 Wi-Fi Direct 模式 |
| 无声音/声音卡顿 | 音频路由错误/编码不兼容 | 设置→音频→媒体路由选"车载蓝牙"、关闭 HEVC |

| Symptom | Possible cause | Fix |
|---------|----------------|-----|
| "Not a whitelisted app" | Wrong package name / not whitelisted | Check the package name, rebuild, confirm the U-disk root |
| "Signature conflict / package parse error" | Same-name app already installed / signature mismatch | Uninstall the old build, or rebuild with another whitelist package name |
| Icon crash/white screen after install | Incompatible system / missing permissions | Check the diagnostic report, try downgrading the system, change the package name |
| iPhone not found | Bluetooth unpaired / hotspot off / band mismatch | Re-pair Bluetooth, enable the 5 GHz hotspot, try Wi-Fi Direct |
| Drops at once / stuck on "Preparing CarPlay" | Wrong hotspot password / channel interference / insufficient whitelist permission | Check the password, change channel, confirm the package-name permission |
| Cluster shows no map | Package name lacks cluster permission / system limit | Confirm the whitelist package name, try Wi-Fi Direct |
| No audio / stuttering | Wrong audio routing / incompatible codec | Settings → Audio → media routing "car Bluetooth"; turn HEVC off |

---

## 📊 诊断报告导出（出问题必做）/ Diagnostic report export (do this when something fails)

```
设置 → 诊断 → 保存诊断报告 / Settings → Diagnostics → Save diagnostic report
├── 选择保存位置：下载/文件/U盘 / Choose save location: Downloads / Files / U disk
├── 文件名：TuPigPlay_diagnostic_yyyyMMdd_HHmmss.txt / File name: TuPigPlay_diagnostic_yyyyMMdd_HHmmss.txt
└── 反馈时必附：此文件 + 车型年款 + 系统版本 + 安装包名 + 复现步骤
    Always attach: this file + model year + system version + install package name + reproduction steps
```

---

## 🔄 版本更新 / Version updates

```bash
# 拉取最新代码 / Pull the latest code
git pull origin main

# 重新构建（保持相同包名和签名以覆盖安装）/ Rebuild (keep the same package name and signature to update in place)
./gradlew :mobile:assembleRelease \
  -PtupigplayApplicationId=com.ximalaya.ting.android.car

# U 盘覆盖安装（设置和配对记录保留）/ Update from the U disk (settings and pairing records are kept)
```

> 覆盖安装要求新旧版本**签名一致**：上一版用什么密钥签名，这一版就用同一个密钥；换了签名需先卸载旧版（设置与配对记录会丢失）。

> An in-place update requires the **same signature** on the old and new builds: sign with the same key you used last time; a different key requires uninstalling first (settings and pairing records are lost).

---

## ⚖️ 保修与风险提示 / Warranty and risk notice

> **零跑官方保修条款**：因未经认可的软件改动导致的故障，不在保修范围内。  
> **缓解建议**：
> 1. 安装前记录"恢复出厂设置"路径
> 2. 出现车机异常先恢复出厂设置再进售后
> 3. 不要在保修期内大张旗鼓展示第三方应用

> **Leapmotor's official warranty terms**: faults caused by unauthorized software changes are not covered.  
> **Mitigation suggestions**:
> 1. Record the "factory reset" path before installing
> 2. If the head unit misbehaves, factory-reset it before visiting service
> 3. Do not openly showcase third-party apps during the warranty period

---

## 📚 参考资料 / References

- [TuPigPlay 官方仓库 / TuPigPlay repository](https://github.com/Tupig/DiPlay)
- [零跑 C 系列白名单详情 / Leapmotor C-series whitelist details](https://www.yeyulingfeng.com/222080.html)
- [U 盘安装视频教程 / U-disk install video tutorial](https://v.douyin.com/q-UifwDixxA/)
- [社区讨论：汽车之家 C11 论坛 / Community: Autohome C11 forum](https://club.autohome.com.cn/bbs/thread/7d089e78e6743966/109027712-1.html)

---

## 📝 更新日志 / Changelog

| 版本 | 日期 | 变更 |
|------|------|------|
| 1.0 | 2026-10-12 | 首版：2024 款 C11 完整安装指南 |

| Version | Date | Change |
|---------|------|--------|
| 1.0 | 2026-10-12 | First edition: complete install guide for the 2024 C11 |
