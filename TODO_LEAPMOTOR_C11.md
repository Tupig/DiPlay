# 零跑 C11 2024 (8295P) 适配任务清单

## 📋 总体进度
- [x] Phase 1: 核心构建系统与白名单包名支持
- [x] Phase 2: 零跑专用配置与诊断系统
- [x] Phase 3: 方向盘按键完全适配（核心需求）
- [x] Phase 7: 移除比亚迪专用代码，替换为零跑实现 (核心替换完成，引用更新中)
- [ ] Phase 4: 三屏显示适配（中控+仪表+副驾）
- [ ] Phase 5: 无 ADB 环境功能降级与优化
- [ ] Phase 6: 文档合并为中英双语单文件
- [ ] Phase 8: 构建验证与测试

---

## Phase 1: 核心构建系统与白名单包名支持
- [x] 1.1 修改 `mobile/build.gradle.kts` 支持 `-PdiplayApplicationId` 参数
- [x] 1.2 验证白名单包名构建：喜马拉雅、B站、QQ音乐三选一
- [x] 1.3 添加构建脚本 `build_leapmotor.sh` 一键构建
- [x] 1.4 验证 APK 包名、签名、安装流程
- [x] 1.5 确认图标资源全用项目内资源（应用图标 ic_dp_navigation、CarPlay 回车图标 ic_car_home、界面图标 ic_carplay）
- [x] 1.6 完善 AndroidManifest.xml 权限（蓝牙、位置、麦克风、相机、存储、USB、网络、系统级、通知、媒体、通话等全套权限）
- [x] 1.7 版本号自动递增机制（version.properties + version_increment.sh，四段式版本号）
- [x] 1.8 APK 参考文件入库（PaopaoDesktop、DiPlay包名gallery）

---

## Phase 2: 零跑专用配置与诊断系统
- [x] 2.1 创建 `LeapmotorC11Settings.kt` 核心配置类
- [x] 2.2 创建 `LeapmotorVehicleInfo.kt` 动态车型检测（系统属性 > Build字段 > 零跑专有属性 > 用户设置，全失败抛异常）
- [x] 2.3 创建 `LeapmotorDiagnostics.kt` 诊断上报
- [x] 2.4 创建 `LeapmotorInstallGuide.kt` 安装引导
- [x] 2.5 创建 `LeapmotorVehicleAccess.kt` 车辆访问层（替代 BydAdbAccess）
- [x] 2.6 创建 `LeapmotorClusterBridge.kt` 仪表盘桥接（替代 BydHudBridge）
- [x] 2.7 创建 `LeapmotorVehicleCapabilities.kt` 车辆能力描述（替代 BydVehicleCapabilities）
- [x] 2.8 创建 `LeapmotorVehicleDataStore.kt` 车辆数据存储（替代 BydVehicleFieldStore）
- [x] 2.9 创建 `LeapmotorVehicleField.kt` 车辆数据字段（替代 BydVehicleField）
- [x] 2.10 创建 `LeapmotorCarPlayCall.kt` 通话处理（替代 BydCarPlayCall）
- [x] 2.11 创建 `LeapmotorOutputSettings.kt` 输出设置（替代 BydOutputSettings）
- [x] 2.12 集成到 `CarPlayHostActivity.onCreate()` 读取车型并用于 IAP2 标识和 AirPlayConfig
- [ ] 2.13 添加零跑专用设置入口（车型信息查看/手动覆盖）
- [ ] 2.14 车型检测失败时的用户提示与设置引导

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
- [ ] 4.2 仪表盘：12.3" 投屏适配（ClusterMapPresentation 适配零跑）
- [ ] 4.3 副驾屏：娱乐屏同步显示（SidePanel / 分屏模式）
- [ ] 4.4 安全区域适配（圆角、挖孔、仪表遮挡）
- [ ] 4.5 分屏/画中画模式适配

---

## Phase 5: 无 ADB 环境功能降级
- [ ] 5.1 移除所有 `require ADB` 的功能依赖
- [ ] 5.2 热点管理：仅支持内置热点 + Wi-Fi Direct
- [ ] 5.3 USB 自动确认：改用系统原生权限弹窗
- [ ] 5.4 仪表盘速度计：改用公开车辆总线 API（如有）
- [ ] 5.5 设置备份：仅二维码/Web 方式

---

## Phase 6: 文档合并为中英双语单文件
- [ ] 6.1 合并 `LEAPMOTOR.md` + `LEAPMOTOR.zh-CN.md` → `LEAPMOTOR.md`（双语）
- [ ] 6.2 合并 `INSTALL.md` + `INSTALL.zh-CN.md` → `INSTALL.md`（双语）
- [ ] 6.3 合并 `LEAPMOTOR_C11_2024_INSTALL.zh-CN.md` → `LEAPMOTOR_C11_2024_INSTALL.md`（双语）
- [ ] 6.4 删除所有 `.zh-CN.md` 文件
- [ ] 6.5 更新导航链接

---

## Phase 7: 移除比亚迪专用代码，替换为零跑实现
- [x] 7.1 识别所有比亚迪相关文件
- [x] 7.2 `BydAdbAccess.kt` → `LeapmotorVehicleAccess.kt` ✅ 已创建
- [x] 7.3 `BydVehicleCapabilities.kt` → `LeapmotorVehicleCapabilities.kt` ✅ 已创建
- [x] 7.4 `BydNavigationOutputs.kt` → `LeapmotorNavigationOutputs.kt` ✅ 已创建
- [x] 7.5 `BydHudBridge.kt` → `LeapmotorClusterBridge.kt` ✅ 已创建
- [x] 7.6 `BydVehicleFieldStore.kt` → `LeapmotorVehicleDataStore.kt` ✅ 已创建
- [x] 7.7 `BydCarPlayCall.kt` → `LeapmotorCarPlayCall.kt` ✅ 已创建
- [x] 7.8 创建 `LeapmotorVehicleField.kt` 替代 `BydVehicleField.kt`
- [x] 7.9 创建 `LeapmotorVehicleDataStore.kt` 替代 `BydVehicleFieldStore.kt`
- [x] 7.10 创建 `LeapmotorOutputSettings.kt` 替代 `BydOutputSettings.kt`
- [x] 7.11 创建 `LeapmotorCarPlayCall.kt` 替代 `BydCarPlayCall.kt`
- [ ] 7.12 更新所有引用处（CarPlayCallKeys, CarPlayVideo, DiPlayActivity, CarPlayMediaKeys, CarHotspotSetup, BydVehicleSettingsBackend, LocalizedOptions 等）
- [ ] 7.13 删除原 BYD 文件（BydAdbAccess, BydVehicleCapabilities, BydNavigationOutputs, BydVehicleFieldStore, BydVehicleProbeOutcome, BydVehicleField, BydOutputSettings, BydAdbAccessState, BydHudBridge, BydCarPlayCall, BydClusterSpeedReader, BydOutputSettings）
- [ ] 7.14 处理 `CarPlayCallKeyPolicy.kt` 中的 BYD 逻辑
- [ ] 7.15 处理 `BydVehicleSettingsBackend.kt` 重写为零跑版本
- [ ] 7.16 更新文档引用（BYD_NAVIGATION.md 等）

---

## Phase 8: 构建验证与测试
- [ ] 8.1 完整构建 Release APK（自动版本号递增）
- [ ] 8.2 验证包名、签名、安装
- [ ] 8.3 模拟器/真机测试核心流程
- [ ] 8.4 生成最终交付物
- [ ] 8.5 推送到主库（自动版本号递增）

---

## 🔍 已识别的比亚迪相关文件（需替换/移除）

### core 模块
- [ ] `common/src/main/java/com/shilapi/xcertplay/BydAdbAccess.kt` → 已替换为 LeapmotorVehicleAccess
- [ ] `common/src/main/java/com/shilapi/xcertplay/BydVehicleCapabilities.kt` → 已替换为 LeapmotorVehicleCapabilities
- [ ] `common/src/main/java/com/shilapi/xcertplay/BydNavigationOutputs.kt` → 已替换为 LeapmotorNavigationOutputs
- [ ] `common/src/main/java/com/shilapi/xcertplay/BydVehicleFieldStore.kt` → 已替换为 LeapmotorVehicleDataStore
- [ ] `common/src/main/java/com/shilapi/xcertplay/BydVehicleProbeOutcome.kt` → 移除（零跑无探测）
- [ ] `common/src/main/java/com/shilapi/xcertplay/BydVehicleField.kt` → 已替换为 LeapmotorVehicleField
- [ ] `common/src/main/java/com/shilapi/xcertplay/BydOutputSettings.kt` → 已替换为 LeapmotorOutputSettings
- [ ] `common/src/main/java/com/shilapi/xcertplay/BydAdbAccessState.kt` → 移除

### shared 模块
- [ ] `shared/src/main/java/com/shilapi/xcertplay/hud/BydHudBridge.kt` → 已替换为 LeapmotorClusterBridge
- [ ] `shared/src/main/java/com/shilapi/xcertplay/hud/BydCarPlayCall.kt` → 已替换为 LeapmotorCarPlayCall
- [ ] `shared/src/main/java/com/shilapi/xcertplay/hud/BydClusterSpeedReader.kt` → 移除（零跑无 ADB 速度读取）
- [ ] `shared/src/main/java/com/shilapi/xcertplay/hud/BydOutputSettings.kt` → 已替换为 LeapmotorOutputSettings
- [ ] `shared/src/main/java/com/shilapi/xcertplay/hud/CarPlayCallKeyPolicy.kt` → 清理 BYD 逻辑

### 需更新引用的文件
- [x] `CarHotspotSetup.kt` - 已更新为 LeapmotorOutputSettings
- [x] `CarPlayMediaKeys.kt` - 已更新为 LeapmotorOutputSettings
- [ ] `CarPlayCallKeys.kt` - 需替换 BydNavigationOutputs/BydOutputSettings
- [ ] `CarPlayVideo.kt` - 需替换 BydNavigationOutputs
- [ ] `DiPlayActivity.kt` - 需替换多个 Byd* 导入
- [ ] `CarPlayVideo.kt` - 需替换 BydNavigationOutputs
- [ ] `BydVehicleSettingsBackend.kt` - 重写为零跑版本
- [ ] `LocalizedOptions.kt` - 需替换 BydClusterNaviMode

### 文档
- [ ] `docs/BYD_NAVIGATION.md` / `BYD_NAVIGATION.zh-CN.md` → 重写为零跑版本或删除
- [ ] `docs/LEAPMOTOR.md` / `LEAPMOTOR.zh-CN.md` → 合并为双语单文件

---

## 📝 备注
- 所有任务完成前不得从列表移除
- 每完成一项在对应 `- [ ]` 标记为 `- [x]`
- 方向盘按键适配为最高优先级（用户核心需求）
- 版本号自动递增：每次构建/推送自动 +1，四段式版本号（major.minor.patch.build），后三位最多到100进位