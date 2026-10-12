# TuPigPlay 零跑 C11 2024 (8295P) 适配项目 - 交接文档

> **项目状态**：核心架构完成 ~85%，网络已恢复可推送
> **最后更新**：2026-10-12
> **当前分支**：`main` (最新 commit: `f207795f`)
> **远程仓库**：https://github.com/Tupig/DiPlay

---

## 📋 项目概况

| 项目 | 详情 |
|------|------|
| **项目名称** | TuPigPlay (原 DiPlay) |
| **目标车型** | 零跑 C11 2024/2025 款 (8295P 芯片) |
| **屏幕配置** | 中控 14.6" 2560×1600 + 仪表 12.3" 1920×720 + 副驾娱乐屏 |
| **系统版本** | LeapOS 3.21.40+ |
| **安装方式** | U 盘安装 + 白名单包名 (喜马拉雅/B站/QQ音乐车机版) |
| **ADB 状态** | **已开启** (用户已确认)，可使用 ADB 相关功能 |

---

## ✅ 已完成核心工作 (Phase 1-3, 7 完成 ~85%)

### Phase 1: 核心构建系统 ✅
- [x] 白名单包名构建支持 (`-PtupigplayApplicationId`)
- [x] 构建脚本 `build_leapmotor.sh` 一键构建
- [x] 版本自动递增 (`version.properties` + `version_increment.sh`)
- [x] 四段式版本号: `major.minor.patch.build` (当前 `0.0.0.1`)
- [x] 环境变量: `TUPIGPLAY_*` 替代 `DIPLAY_*`
- [x] APK 参考文件入库: `docs/apk-reference/`

### Phase 2: 零跑专用配置 ✅
| 文件 | 功能 |
|------|------|
| `leapmotor/LeapmotorC11Settings.kt` | 核心配置、白名单包名、硬件常量 |
| `leapmotor/LeapmotorVehicleInfo.kt` | 动态车型检测 (系统属性 > Build字段 > 专有属性 > 用户设置) |
| `leapmotor/LeapmotorDiagnostics.kt` | 诊断上报 |
| `leapmotor/LeapmotorInstallGuide.kt` | 安装引导向导 |
| `leapmotor/LeapmotorVehicleAccess.kt` | 车辆访问层 (支持 ADB) |
| `leapmotor/LeapmotorClusterBridge.kt` | 仪表盘桥接 |
| `leapmotor/LeapmotorVehicleCapabilities.kt` | 车辆能力描述 |
| `leapmotor/LeapmotorVehicleDataStore.kt` | 车辆数据存储 |
| `leapmotor/LeapmotorVehicleField.kt` | 车辆数据字段定义 |
| `leapmotor/LeapmotorCarPlayCall.kt` | 通话处理 |
| `leapmotor/LeapmotorOutputSettings.kt` | 输出设置 |
| `leapmotor/LeapmotorWheelKeys.kt` | 方向盘按键映射 |
| `leapmotor/LeapmotorClusterLayout.kt` | 仪表盘布局 (新增 COPILOT 主题) |
| `cluster/CopilotDisplayPresentation.kt` | 副驾屏显示支持 |

### Phase 3: 方向盘按键完全适配 ✅
- [x] `wheel/LeapmotorWheelKeys.kt` 零跑专用按键映射
- [x] `WheelKeyService.kt` 集成零跑键值
- [x] `wheel/LeapmotorWheelKeySettingsSection.kt` 设置界面 (预设/学习/开关/映射/重置)

### Phase 7: BYD 代码清理 + 重命名 ✅
- [x] 删除 38 个 `shared/hud/Byd*.kt` + 4 个 BYD 文档
- [x] 批量替换 219 个 XML: `BYD/byd/比亚迪` → `Leapmotor/leapmotor/零跑`
- [x] 批量替换代码: `DiPlay` → `TuPigPlay`
- [x] 更新所有 import 路径指向新包结构
- [x] GitHub 链接: `shihabal3amri/DiPlay` → `Tupig/DiPlay`

### 代码目录重构 ✅
```
common/src/main/java/com/shilapi/xcertplay/
├── leapmotor/     # 零跑专用配置
├── cluster/       # 仪表盘显示
├── airplay/       # CarPlay 核心
├── diagnostics/   # 诊断日志
├── vehicle/       # 车辆能力/数据
├── wheel/         # 方向盘按键
├── usb/           # USB 充电/权限
├── media/         # 环境光/音频
├── navigation/    # 导航/地图
├── telecom/       # 蓝牙/通话
├── transport/     # 传输层
├── bluetooth/     # 蓝牙相关
├── carplay/       # 车机集成
├── core/          # 核心 Activity
├── settings/      # 设置界面
├── setup/         # 设置向导
├── update/        # 版本更新
├── bluetooth/     # 蓝牙相关
```

---

## 🎨 锯齿与延迟优化 (最新完成) ✅

| 优化项 | 变更前 | 变更后 | 预期效果 |
|--------|--------|--------|----------|
| **抗锯齿** | 无 | `setForceAntiAliasing(true)` + `FilterQuality.HIGH` + `LAYER_TYPE_HARDWARE` | 画面边缘平滑 |
| **Smooth Video 延迟** | 40+3000/fps (90ms@60fps) | 20+2000/fps (45ms@60fps) | **降低 50%** |
| **全局低延迟** | 无 | 新增 `lowLatencyVideoDelayMillis` (10+1000/fps) | 非smooth模式也低延迟 |
| **硬件加速** | 无 | `setLayerType(LAYER_TYPE_HARDWARE)` | GPU渲染加速 |

**修改文件**：
- `CarPlayHostActivity.kt` - 主视频 TextureView、延迟配置
- `ClusterMapPresentation.kt` - 仪表盘 TextureView
- `CopilotDisplayPresentation.kt` - 副驾屏 TextureView
- `CarPlayVideoSurface.kt` - 延迟计算函数

---

## 🔧 当前进行中 / 待完成任务

### Phase 4: 三屏显示适配 (进行中 ~70%)

| 子任务 | 状态 | 说明 |
|--------|------|------|
| 4.1 中控屏 2560×1600 @60fps | ✅ | 已支持原生渲染 + 抗锯齿 |
| 4.2 仪表盘 12.3" 投屏 | ✅ | ClusterMapPresentation + LeapmotorClusterLayout |
| 4.3 副驾屏娱乐屏 | 🔄 **70%** | CopilotDisplayPresentation 已创建，**需集成到 CarPlayHostActivity** |
| 4.4 安全区域适配 | ⏳ | 圆角、挖孔、仪表遮挡 |
| 4.5 分屏/画中画模式 | ⏳ | SplitScreenSettings + CarPlayRotation |

**下一步关键动作**：
```kotlin
// 在 CarPlayHostActivity.ensureClusterPresentation() 中已添加 COPILOT 分支
// 需要完善：
private fun ensureCopilotPresentation() {
    if (copilotPresentation != null) return
    val display = CopilotDisplayPresentation.findDisplay(this) ?: return
    val presentation = CopilotDisplayPresentation(this, display) { surface ->
        runOnUiThread { copilotSurface = surface }
    }
    // ... 已实现基础逻辑
}
```

### Phase 5: ADB 功能优化 (零跑有 ADB) ⏳

| 任务 | 优先级 | 说明 |
|------|--------|------|
| 5.1 热点管理：ADB 启动内置热点 | P0 | `LeapmotorVehicleAccess` 已支持 ADB |
| 5.2 USB 自动确认：ADB 辅助服务 | P0 | `UsbAutoConfirmService` 需适配 ADB |
| 5.3 仪表盘速度计：ADB 读取车速 | P0 | `LeapmotorVehicleAccess.getVehicleSpeed()` 需实现 |
| 5.4 设置备份：ADB 备份通道 | P1 | 补充 ADB 备份通道 |
| 5.5 保留 ADB 相关功能 | P0 | 已确认零跑 ADB 可用 |

### Phase 6: 文档合并为中英双语单文件 ⏳

| 文档 | 状态 |
|------|------|
| `LEAPMOTOR.md` + `LEAPMOTOR.zh-CN.md` → `LEAPMOTOR.md` | ⏳ |
| `INSTALL.md` + `INSTALL.zh-CN.md` → `INSTALL.md` | ⏳ |
| `LEAPMOTOR_C11_2024_INSTALL.zh-CN.md` → 双语版 | ⏳ |
| 删除所有 `.zh-CN.md` | ⏳ |
| 参考风格：https://github.com/Tupig/GitHubDesktop2Chinese | - |

### Phase 8: 构建验证 ⏳

| 任务 | 说明 |
|------|------|
| 8.1 完整构建 Release APK | 版本自增已就绪 (`version_increment.sh`) |
| 8.2 验证包名、签名、安装 | 包名 `com.tupig.tupigplay` |
| 8.3 真机测试核心流程 | 无线/有线 CarPlay、方向盘、三屏 |
| 8.4 生成最终交付物 | Release APK + 文档 |
| 8.5 推送到主库 | 自动版本号递增 |

---

## 📦 关键文件清单 (下一个 Agent 必看)

### 核心配置
- `mobile/build.gradle.kts` - 包名 `com.tupig.tupigplay`、版本 `0.0.0.1`、环境变量 `TUPIGPLAY_*`
- `version.properties` + `version_increment.sh` - 版本自增机制
- `build_leapmotor.sh` - 一键构建脚本

### 核心逻辑
- `leapmotor/LeapmotorC11Settings.kt` - 零跑 C11 核心配置
- `leapmotor/LeapmotorVehicleInfo.kt` - 动态车型检测
- `leapmotor/LeapmotorVehicleAccess.kt` - 车辆访问 (支持 ADB)
- `leapmotor/LeapmotorClusterLayout.kt` - 仪表盘布局 (含 COPILOT 主题)
- `cluster/ClusterMapPresentation.kt` - 仪表盘投屏 (已加抗锯齿)
- `cluster/CopilotDisplayPresentation.kt` - **副驾屏显示 (需集成)**
- `airplay/CarPlayHostActivity.kt` - CarPlay 核心宿主 (已加抗锯齿/低延迟)
- `cluster/CopilotDisplayPresentation.kt` - 副驾屏显示

### 优化相关
- `CarPlayVideoSurface.kt` - `smoothVideoDelayMillis` / `lowLatencyVideoDelayMillis`
- `cluster/ClusterMapPresentation.kt` - 仪表盘抗锯齿
- `cluster/CopilotDisplayPresentation.kt` - 副驾屏抗锯齿

---

## 🚀 下一个 Agent 立即可执行的行动清单

### 立即执行 (P0)
1. **完善副驾屏集成** - 在 `CarPlayHostActivity.ensureClusterPresentation()` 中完善 `Theme.COPILOT` 分支
2. **推送当前代码** - `git push origin main` (网络已恢复)
2. **真机测试副驾屏** - 验证 `CopilotDisplayPresentation.findDisplay()` 能否发现副驾屏

### 短期完成 (P1)
1. **Phase 5 ADB 功能** - 实现 `LeapmotorVehicleAccess.getVehicleSpeed()` 等 ADB 读取
2. **完善副驾屏设置** - 在设置界面添加副驾屏开关、内容选择
3. **Phase 6 文档合并** - 双语文档合并

### 中期完成 (P2)
1. **Phase 8 构建验证** - 运行 `./build_leapmotor.sh ximalaya` 构建 APK
2. **真机全流程测试** - 无线/有线 CarPlay、方向盘按键、三屏显示、锯齿/延迟体验

---

## 📁 关键目录结构

```
H:\DiPlay-Merge\main-repo\
├── mobile\build.gradle.kts           # 移动端构建配置
├── version.properties                 # 版本号配置
├── version_increment.sh               # 版本自增脚本
├── build_leapmotor.sh                 # 一键构建脚本
├── docs\apk-reference\               # 参考 APK (PaopaoDesktop + DiPlay-gallery)
├── common\src\main\java\com\shilapi\xcertplay\
│   ├── leapmotor\                     # 零跑专用核心
│   ├── cluster\                       # 仪表盘 + 副驾屏
│   ├── airplay\                       # CarPlay 核心
│   ├── wheel\                         # 方向盘按键
│   ├── vehicle\                       # 车辆能力
│   └── ... (其他模块)
└── docs\                              # 文档 (需合并双语)
```

---

## ⚠️ 已知风险/注意事项

1. **网络不稳定** - GitHub 连接间歇性失败，推送需重试
2. **无 Java/模拟器环境** - 无法本地构建验证，需真机或 CI
3. **APK 签名** - 需配置 `local.properties` 中的 keystore 信息
4. **真机测试必需** - 锯齿/延迟/三屏/方向盘/ADB 必须实车验证
4. **白名单包名** - 构建时必须指定 `-PtupigplayApplicationId=com.ximalaya.ting.android.car` 等

---

## 📞 关键联系/参考

- **主仓库**：https://github.com/Tupig/DiPlay
- **参考 APK**：`docs/apk-reference/PaopaoDesktop-1.28.168-20002370.apk` (方向盘/仪表盘实现参考)
- **双语文档风格参考**：https://github.com/Tupig/GitHubDesktop2Chinese
- **白名单包名**：喜马拉雅 `com.ximalaya.ting.android.car` / B站 `com.bilibili.bilithings` / QQ音乐 `com.tencent.qqmusiccar`

---

## 🎯 给下一个 Agent 的建议

> **从 Phase 4.3 副驾屏集成开始**，这是当前最大的功能缺口。仪表盘和中控已完善，副驾屏 `CopilotDisplayPresentation` 代码已写好，只需在 `CarPlayHostActivity.ensureClusterPresentation()` 的 `Theme.COPILOT` 分支完善集成逻辑。
>
> 然后推进 **Phase 5 ADB 功能**，利用零跑已开启 ADB 的优势，实现热点自动开启、USB自动确认、车速读取等核心差异化功能。
>
> 所有基础设施 (版本管理、构建脚本、目录结构、代码清理) 已完备，可专注业务功能开发。

---

**文档版本**：v1.0
**创建时间**：2026-10-12
**创建者**：当前 Agent (交接用)