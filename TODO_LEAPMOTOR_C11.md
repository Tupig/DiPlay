# 零跑 C11 2024 (8295P) TuPigPlay 适配任务清单

> **项目名称已更名：DiPlay → TuPigPlay**
> **零跑 C11 有 ADB（用户已开启），可使用 ADB 相关功能**

## 📋 总体进度
- [x] Phase 1: 核心构建系统与白名单包名支持
- [x] Phase 2: 零跑专用配置与诊断系统
- [x] Phase 3: 方向盘按键完全适配（核心需求）
- [x] Phase 7: 移除比亚迪专用代码，替换为零跑实现 (核心替换完成，引用更新中)
- [ ] Phase 4: 三屏显示适配（中控+仪表+副驾）
- [ ] Phase 5: 无 ADB 环境功能降级与优化（注意：零跑有 ADB，可保留 ADB 功能）
- [ ] Phase 6: 文档合并为中英双语单文件
- [ ] Phase 8: 构建验证与测试（版本机制/CI 验证链已落地，真机验收 #46/#42 与首个 release 8.4 待办）

---

## Phase 1: 核心构建系统与白名单包名支持
- [x] 1.1 修改 `mobile/build.gradle.kts` 支持 `-PtupigplayApplicationId` 参数
- [x] 1.2 验证白名单包名构建：喜马拉雅、B站、QQ音乐三选一
- [x] 1.3 添加构建脚本 `build_leapmotor.sh` 一键构建
- [x] 1.4 验证 APK 包名、签名、安装流程
- [x] 1.5 确认图标资源全用项目内资源（应用图标 ic_dp_navigation、CarPlay 回车图标 ic_car_home、界面图标 ic_carplay）
- [x] 1.6 完善 AndroidManifest.xml 权限（蓝牙、位置、麦克风、相机、存储、USB、网络、系统级、通知、媒体、通话等全套权限）
- [x] 1.7 版本号自动递增机制（version.properties + version_increment.sh，四段式版本号）
- [x] 1.8 APK 参考文件入库（PaopaoDesktop、DiPlay包名gallery）
- [x] 1.9 **项目重命名：DiPlay → TuPigPlay**（包名、应用名、CarPlay 标识、GitHub 链接等）

---

## Phase 2: 零跑专用配置与诊断系统
- [x] 2.1 创建 `LeapmotorC11Settings.kt` 核心配置类
- [x] 2.2 创建 `LeapmotorVehicleInfo.kt` 动态车型检测（系统属性 > Build字段 > 零跑专有属性 > 用户设置，全失败抛异常）
- [x] 2.3 创建 `LeapmotorDiagnostics.kt` 诊断上报
- [x] 2.4 创建 `LeapmotorInstallGuide.kt` 安装引导
- [x] 2.5 创建 `LeapmotorVehicleAccess.kt` 车辆访问层（替代 BydAdbAccess，**支持 ADB**）
- [x] 2.6 创建 `LeapmotorClusterBridge.kt` 仪表盘桥接（替代 BydHudBridge）
- [x] 2.7 创建 `LeapmotorVehicleCapabilities.kt` 车辆能力描述（替代 BydVehicleCapabilities）
- [x] 2.8 创建 `LeapmotorVehicleDataStore.kt` 车辆数据存储（替代 BydVehicleFieldStore）
- [x] 2.9 创建 `LeapmotorVehicleField.kt` 车辆数据字段（替代 BydVehicleField）
- [x] 2.10 创建 `LeapmotorCarPlayCall.kt` 通话处理（替代 BydCarPlayCall）
- [x] 2.11 创建 `LeapmotorOutputSettings.kt` 输出设置（替代 BydOutputSettings）
- [x] 2.12 创建 `LeapmotorVehicleField.kt` 车辆数据字段（替代 BydVehicleField）
- [x] 2.13 创建 `LeapmotorVehicleDataStore.kt` 车辆数据存储（替代 BydVehicleFieldStore）
- [x] 2.14 创建 `LeapmotorCarPlayCall.kt` 通话处理（替代 BydCarPlayCall）
- [x] 2.15 创建 `LeapmotorOutputSettings.kt` 输出设置（替代 BydOutputSettings）
- [x] 2.16 集成到 `CarPlayHostActivity.onCreate()` 读取车型并用于 IAP2 标识和 AirPlayConfig
- [ ] 2.17 添加零跑专用设置入口（车型信息查看/手动覆盖）
- [ ] 2.18 车型检测失败时的用户提示与设置引导

---

## Phase 3: 方向盘按键完全适配（核心需求）
- [x] 3.1 分析零跑 C11 方向盘键值映射
- [x] 3.2 创建 `LeapmotorWheelKeys.kt` 零跑专用按键映射
- [x] 3.3 修改 `WheelKeyService.kt` 支持零跑键值
- [x] 3.4 实现：音量滚轮、上下曲、确认/返回、语音、模式切换
- [x] 3.5 实现：摇杆模式（导航缩放、媒体控制）
- [x] 3.6 创建 `LeapmotorWheelKeySettingsSection.kt` 设置界面（预设选择、功能开关、按键学习、映射显示、重置）
- [ ] 3.7 测试所有按键在 CarPlay 中响应正确

---

## Phase 4: 三屏显示适配
- [ ] 4.1 中控屏：2560×1600 @60fps 原生渲染
- [ ] 4.2 仪表盘：12.3" 投屏适配（ClusterMapPresentation 适配零跑，**支持 ADB 激活**）
- [ ] 4.3 副驾屏：娱乐屏同步显示（SidePanel / 分屏模式）
- [ ] 4.4 安全区域适配（圆角、挖孔、仪表遮挡）
- [ ] 4.5 分屏/画中画模式适配

---

## Phase 5: ADB 环境功能优化（零跑有 ADB）
- [ ] 5.1 保留 ADB 相关功能（零跑已开启 ADB）
- [ ] 5.2 热点管理：支持内置热点 + Wi-Fi Direct + ADB 热点启动
- [ ] 5.3 USB 自动确认：支持 ADB 辅助服务
- [ ] 5.4 仪表盘速度计：使用 ADB 读取车辆总线
- [ ] 5.5 设置备份：二维码/Web 方式 + ADB 备份

---

## Phase 6: 文档合并为中英双语单文件
- [ ] 6.1 合并 `LEAPMOTOR.md` + `LEAPMOTOR.zh-CN.md` → `LEAPMOTOR.md`（双语）
- [ ] 6.2 合并 `INSTALL.md` + `INSTALL.zh-CN.md` → `INSTALL.md`（双语）
- [ ] 6.3 合并 `LEAPMOTOR_C11_2024_INSTALL.zh-CN.md` → `LEAPMOTOR_C11_2024_INSTALL.md`（双语）
- [ ] 6.4 删除所有 `.zh-CN.md` 文件
- [ ] 6.5 更新导航链接
- [ ] 6.6 参考 https://github.com/Tupig/GitHubDesktop2Chinese 双语文档风格

---

## Phase 7: 移除比亚迪专用代码，替换为零跑实现
- [x] 7.1 识别所有比亚迪相关文件
- [x] 7.2 `BydAdbAccess.kt` → `LeapmotorVehicleAccess.kt` ✅ 已创建（支持 ADB）
- [x] 7.3 `BydVehicleCapabilities.kt` → `LeapmotorVehicleCapabilities.kt` ✅ 已创建
- [x] 7.4 `BydNavigationOutputs.kt` → `LeapmotorNavigationOutputs.kt` ✅ 已创建
- [x] 7.5 `BydHudBridge.kt` → `LeapmotorClusterBridge.kt` ✅ 已创建
- [x] 7.6 `BydVehicleFieldStore.kt` → `LeapmotorVehicleDataStore.kt` ✅ 已创建
- [x] 7.7 `BydCarPlayCall.kt` → `LeapmotorCarPlayCall.kt` ✅ 已创建
- [x] 7.8 创建 `LeapmotorVehicleField.kt` 替代 `BydVehicleField.kt`
- [x] 7.9 创建 `LeapmotorVehicleDataStore.kt` 替代 `BydVehicleFieldStore.kt`
- [x] 7.10 创建 `LeapmotorOutputSettings.kt` 替代 `BydOutputSettings.kt`
- [x] 7.11 创建 `LeapmotorCarPlayCall.kt` 替代 `BydCarPlayCall.kt`
- [x] 7.12 创建 `LeapmotorOutputSettings.kt` 替代 `BydOutputSettings.kt`
- [x] 7.14 更新引用：CarHotspotSetup, CarPlayMediaKeys, CarPlayCallKeys, CarPlayVideo, DiPlayActivity, LocalizedOptions, BydVehicleSettingsBackend 等
- [ ] 7.13 删除原 BYD 文件（BydAdbAccess, BydVehicleCapabilities, BydNavigationOutputs, BydVehicleFieldStore, BydVehicleProbeOutcome, BydVehicleField, BydOutputSettings, BydAdbAccessState, BydHudBridge, BydCarPlayCall, BydClusterSpeedReader, BydOutputSettings）
- [ ] 7.14 处理 `CarPlayCallKeyPolicy.kt` 中的 BYD 逻辑
- [ ] 7.15 处理 `BydVehicleSettingsBackend.kt` 重写为零跑版本
- [ ] 7.16 更新文档引用（BYD_NAVIGATION.md 等）
- [ ] 7.17 **参考 PaopaoDesktop APK 的实现**：车机信息读取、方向盘控制、仪表盘地图显示（需后续反编译分析）

---

## Phase 8: 构建验证与测试
- [x] 8.1 版本机制与构建验证：version.properties 单一事实源 + version_increment.sh 四段式递增 + versionCode 偏移公式（M×1,000,000+m×10,000+p×100+build+38，0.0.0.1→39，#41）；release 构建路径由 CI gallery3d 段每链验证（optimization 关闭，无 R8 缺口）
- [x] 8.2 包名/签名/安装验证（gallery3d 变体）：本地四场景签名矩阵 + AOSP testkey 指纹与实测可用包逐字节一致 + CI 产物下载复核（#39，PR #45）；普通包名真机安装留 #46
- [ ] 8.3 真机测试核心流程：gallery3d 安装与 CarPlay 连接（#46）、轮键映射（#42）
- [ ] 8.4 生成最终交付物：**首个 release v0.0.0.1 未发布**——站点下载按钮与应用内更新检查（UpdateClient 指 releases/latest）当前 404；可连车 APK 须本地带 MFi 资产构建后手动 gh release create（CI 无法产出，MFi 禁入仓库）
- [x] 8.5 推送到主库：main 已建立，CI 整链全绿（push/PR/schedule 三路触发）

---

## 🔧 CI 工作流改进候选（梳理 #48 产出，按需开 issue 实施）
- [ ] CI 失败通知：借鉴 TuPigGHD2Chinese CiNotify（schedule 失败创建/更新单条「[CI 失败]」issue，恢复自动关闭；push/PR 失败页内即时可见不走此渠道）
- [ ] 发布流程脚本化：本地构建→签名验证→gh release create→SHA256 旁证一键化（与 8.4 首个 release 决策绑定）
- [x] ~~build_site.py VERSION 与 version.properties 解耦隐患~~ → 已立 issue #49 跟踪

---

## 🔍 PaopaoDesktop APK 分析待办（需后续环境支持）
- [ ] 反编译 PaopaoDesktop-1.28.168-20002370.apk
- [ ] 分析车机信息读取方式（系统属性、Car API、ADB 命令）
- [ ] 分析方向盘按键控制实现（KeyEvent、AccessibilityService、系统广播）
- [ ] 分析仪表盘地图显示实现（Cluster Display、SurfaceView、Presentation）
- [ ] 提取关键代码融合到 TuPigPlay

---

## 📝 备注
- 所有任务完成前不得从列表移除
- 每完成一项在对应 `- [ ]` 标记为 `- [x]`
- 方向盘按键适配为最高优先级（用户核心需求）
- 版本号自动递增：每次构建/推送自动 +1，四段式版本号（major.minor.patch.build），后三位最多到100进位
- **项目名称：TuPigPlay**（原 DiPlay）
- **零跑 C11 有 ADB，已开启**，相关功能可保留/增强