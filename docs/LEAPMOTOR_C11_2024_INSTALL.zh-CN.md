# 2024 款零跑 C11 (8295P) DiPlay 安装完整指南

> **适用车型**：2024/2025 款零跑 C11 增程版/纯电版  
> **芯片**：高通 SA8295P  
> **屏幕**：14.6 英寸 2.5K (2560×1600) 中控 + 12.3 英寸仪表盘 + 副驾娱乐屏  
> **系统**：LeapOS 3.21.40 / 3.35.40+  
> **安装方式**：U 盘安装 + 白名单包名校验

---

## ⚠️ 核心限制（必读）

| 限制项 | 说明 | 影响 |
|--------|------|------|
| **仅 U 盘安装** | 车机只识别 `应用 → U盘应用` 入口 | 无法通过 ADB、浏览器、微信传输安装 |
| **白名单校验** | 安装包包名必须在 21 个白名单内 | 必须使用白名单包名构建 APK |
| **签名校验不确定** | 3.35.40+ 可能校验签名证书 | 建议停留在 3.21.40 或更早版本 |
| **无 ADB** | 无法开启 USB 调试、无法抓包 | 所有 ADB 依赖功能不可用 |
| **无开发者选项** | 设置中无开发者模式入口 | 无法授予特殊权限（悬浮窗、辅助服务等） |

---

## 📦 白名单包名选择策略

根据社区实测报告，推荐优先级：

| 优先级 | 包名 | 应用名 | 实测版本 | 成功率 | 备注 |
|--------|------|--------|----------|--------|------|
| **1** | `com.ximalaya.ting.android.car` | 喜马拉雅车机版 | v4.7.2 | ⭐⭐⭐⭐⭐ | 报告最稳定，其他版本多失败 |
| **2** | `com.bilibili.bilithings` | 哔哩哔哩车机版 | v2.2.1 | ⭐⭐⭐⭐ | 需确认是车机版而非手机版 |
| **3** | `com.tencent.qqmusiccar` | QQ音乐车机版 | v2.6.1.1 | ⭐⭐⭐ | 后续报告称该版本已失效 |

> ⚠️ **重要**：`tv.danmaku.bili` 是 B 站**手机版**包名，**不可用**！必须用车机版包名。

---

## 🛠️ 完整构建流程

### 环境准备
```bash
# 1. 克隆项目
git clone https://github.com/Tupig/DiPlay.git
cd DiPlay

# 2. 准备 JDK 17+ 和 Android SDK
# 建议使用 Android Studio 自带 SDK

# 3. 配置签名密钥（local.properties）
cat > local.properties << 'EOF'
sdk.dir=/path/to/Android/Sdk
diplay.keystore=/path/to/release.keystore
diplay.keystore.password=your_keystore_password
diplay.key.alias=your_key_alias
diplay.key.password=your_key_password
EOF
```

### 构建命令（核心：指定白名单包名）

```bash
# 方案 A：喜马拉雅包名（推荐，成功率最高）
./gradlew :mobile:assembleRelease \
  -PdiplayApplicationId=com.ximalaya.ting.android.car

# 方案 B：B 站车机版包名
./gradlew :mobile:assembleRelease \
  -PdiplayApplicationId=com.bilibili.bilithings

# 方案 C：QQ音乐车机版包名
./gradlew :mobile:assembleRelease \
  -PdiplayApplicationId=com.tencent.qqmusiccar
```

### 输出文件
```
mobile/build/outputs/apk/release/
└── DiPlay-{version}-release.apk   # 重命名后拷入 U 盘
```

> **注意**：构建时会自动去掉 debug 的 `.hudtest` 后缀，APK 完全使用指定包名。

---

## 💾 U 盘准备与安装步骤

### U 盘要求
- **格式**：FAT32（非 exFAT、NTFS）
- **容量**：16–32 GB（2GB 可能可用，>64GB 易失败）
- **品牌**：SanDisk、金士顿、三星等主流品牌
- **接口**：USB 2.0/3.0 均可

### 文件放置
```
U盘根目录/
└── Ximalaya_Car_v4.7.2.apk   # 必须在根目录，子目录不识别
```

> **命名建议**：用白名单应用原名 + 版本号，如 `Ximalaya_Car_v4.7.2.apk`

### 车机操作步骤
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

---

## 🔧 首次配置向导

### 1. 连接方式选择
```
设置 → 连接 → 连接设置
├── 内置车载热点（推荐，5GHz 优先）
│   └── 开启车载热点 → 记录 SSID/密码 → 保存
├── Wi-Fi Direct（备选）
│   └── 无需热点，但可能回落 2.4GHz
└── 同局域网/现有 Wi-Fi（高级）
    └── 需关闭路由器 AP 隔离
```

### 2. iPhone 配对
1. iPhone 开启 **蓝牙 + Wi-Fi**
2. iPhone `设置 → 蓝牙` → 与车机配对
3. DiPlay 主页 → `选择 iPhone` → 选中已配对设备
4. 点击 `连接手机` → iPhone 弹窗允许 "CarPlay" "本地网络" "蓝牙"

### 3. 分辨率/帧率调优（8295P 推荐）
```
设置 → 显示
├── 图标/文字大小：默认 / 110%
├── 分辨率：默认 (2560×1600) / 80% (2048×1280) / 60%
├── 帧率：60 fps（8295P 满血） / 30 fps（省电）
├── 高效视频 (HEVC)：关闭（兼容性优先）
└── 界面大小：自动
```

### 4. 三屏显示配置
```
设置 → 车辆 → 仪表盘导航
├── 仪表盘显示 CarPlay 地图：开启（需白名单包名权限）
├── 副驾屏显示：开启（副驾娱乐屏同步）
└── HUD 显示：关闭（C11 无 HUD）
```

---

## 🐛 常见故障排查

| 现象 | 可能原因 | 解决方案 |
|------|----------|----------|
| 提示"非白名单应用" | 包名错误/未在白名单 | 核对包名、重新构建、确认 U 盘根目录 |
| 提示"签名冲突/安装包解析错误" | 同包名应用已存在/签名不匹配 | 卸载旧版、或更换白名单包名重新构建 |
| 安装后点击图标闪退/白屏 | 系统版本不兼容/权限缺失 | 查看诊断报告、尝试降级系统、更换包名 |
| 无法搜索到 iPhone | 蓝牙未配对/热点未开/频段不匹配 | 重新配对蓝牙、开启 5GHz 热点、尝试 Wi-Fi Direct |
| 连接后秒断/卡在"准备 CarPlay" | 热点密码错误/信道干扰/白名单权限不足 | 核对密码、换信道、确认包名权限 |
| 仪表盘不显示地图 | 包名无仪表权限/系统版本限制 | 确认白名单包名、尝试 Wi-Fi Direct 模式 |
| 无声音/声音卡顿 | 音频路由错误/编码不兼容 | 设置→音频→媒体路由选"车载蓝牙"、关闭 HEVC |

---

## 📊 诊断报告导出（出问题必做）

```
设置 → 诊断 → 保存诊断报告
├── 选择保存位置：下载/文件/U盘
├── 文件名：DiPlay_diagnostic_yyyyMMdd_HHmmss.txt
└── 反馈时必附：此文件 + 车型年款 + 系统版本 + 安装包名 + 复现步骤
```

---

## 🔄 版本更新

```bash
# 拉取最新代码
git pull origin main

# 重新构建（保持相同包名和签名以覆盖安装）
./gradlew :mobile:assembleRelease \
  -PdiplayApplicationId=com.ximalaya.ting.android.car

# U 盘覆盖安装（设置和配对记录保留）
```

---

## ⚖️ 保修与风险提示

> **零跑官方保修条款**：因未经认可的软件改动导致的故障，不在保修范围内。  
> **缓解建议**：
> 1. 安装前记录"恢复出厂设置"路径
> 2. 出现车机异常先恢复出厂设置再进售后
> 3. 不要在保修期内大张旗鼓展示第三方应用

---

## 📚 参考资料

- [DiPlay 官方仓库](https://github.com/Tupig/DiPlay)
- [零跑 C 系列白名单详情](https://www.yeyulingfeng.com/222080.html)
- [U 盘安装视频教程](https://v.douyin.com/q-UifwDixxA/)
- [社区讨论：汽车之家 C11 论坛](https://club.autohome.com.cn/bbs/thread/7d089e78e6743966/109027712-1.html)

---

## 📝 更新日志

| 版本 | 日期 | 变更 |
|------|------|------|
| 1.0 | 2026-10-12 | 首版：2024 款 C11 完整安装指南 |