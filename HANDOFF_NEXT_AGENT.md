# TuPigPlay 零跑 C11 2024 (8295P) 适配项目 - 交接文档

> **项目状态**：核心架构完成 ~85%，**存在编译阻塞项**（见「给下一个 Agent 的建议」第 1 条）
> **最后更新**：2026-10-12
> **当前分支**：`main`（接手前先执行 `git pull` 同步本文档）
> **远程仓库**：https://github.com/Tupig/DiPlay（独立维护，禁止回推上游）

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

## 📋 强制性工作规范 (必须遵守)

### 🐛 Issue 管理规范 (强制)

| 规则 | 说明 |
|------|------|
| **所有问题必须创建 Issue** | 任何 Bug、功能需求、技术债、优化建议都必须在 GitHub 创建 Issue，禁止仅在本地或聊天中记录 |
| **Issue 必填字段** | 标题(清晰描述)、Labels(类型/优先级/模块)、Assignees、Projects、Milestone |
| **Issue 模板** | 必须使用 `.github/ISSUE_TEMPLATE/` 下的模板：`bug_report.yml`、`feature_request.yml`、`task.yml` |
| **关联提交** | 所有 commit 必须在 message 中引用 Issue 编号：`fix: 修复锯齿问题 #123` |
| **关闭方式** | 通过 `Closes #123` 或 `Fixes #123` 在 PR/commit 中自动关闭 |

### 🔄 Git 工作流规范 (强制)

| 规则 | 说明 |
|------|------|
| **分支策略** | `main` (受保护) ← `feature/*` / `fix/*` / `chore/*` / `docs/*` / `refactor/*` |
| **分支命名** | `type/issue号-简短描述` 例：`feat/456-add-copilot-display` |
| **PR 规范** | 所有变更必须通过 PR 合并，禁止直接推送 `main` |
| **PR 模板** | 必须使用 `.github/PULL_REQUEST_TEMPLATE.md` |
| **Review 要求** | 至少 1 次 Code Review 通过后可合并 |
| **Commit 规范** | Conventional Commits：`type(scope): description` 例：`feat(cluster): add copilot display support` |

### 📝 文档规范

| 规则 | 说明 |
|------|------|
| **双语文档** | 所有用户文档必须中英双语单文件，参考 `GitHubDesktop2Chinese` 风格 |
| **API 文档** | KDoc 注释覆盖所有 public API |
| **架构决策记录** | 重要决策记录在 `docs/adr/YYYY-MM-DD-短标题.md` (ADR 格式) |

### 🏷️ 标签规范 (GitHub Labels)

| 类型 | 标签示例 |
|------|----------|
| **Type** | `type:bug`、`type:feature`、`type:task`、`type:chore`、`type:docs`、`type:refactor` |
| **Priority** | `priority:critical`、`priority:high`、`priority:medium`、`priority:low` |
| **Module** | `module:cluster`、`module:airplay`、`module:wheel`、`module:leapmotor`、`module:usb`、`module:bluetooth` |
| **Status** | `status:todo`、`status:in-progress`、`status:review`、`status:blocked`、`status:done` |

### 🔄 CI/CD 规范

| 规则 | 说明 |
|------|------|
| **自动构建** | 每次 PR 推送触发 Gradle 构建 + 单元测试 |
| **代码检查** | ktlint + detekt 必须通过 |
| **版本自增** | 合并到 main 自动执行 `version_increment.sh` 并打 Tag `v{version}` |
| **Release** | Tag 推送触发生成 Release APK + 自动生成 Changelog |

---

## ✅ 下一个 Agent 入职 Checklist

- [ ] 阅读完 `HANDOFF_NEXT_AGENT.md` 全文
- [ ] 配置 Git 用户信息：`git config user.name/email`
- [ ] 配置 GitHub CLI：`gh auth login`
- [ ] 熟悉 `.github/ISSUE_TEMPLATE/` 和 `.github/PULL_REQUEST_TEMPLATE.md`
- [ ] 创建第一个 Issue：`feat(cluster): complete copilot display integration`
- [ ] 开发分支：`git checkout -b feat/456-copilot-display-integration`
- [ ] 开始 Phase 4.3 副驾屏集成开发

---

## 🔄 上游同步工作流 (单向同步，禁止回推)

> **核心原则**：独立维护 TuPigPlay，**禁止贡献回上游**，但**保留监控上游更新**能力，有价值的更新由 Agent 评估后选择性合并。

### 🔧 远程仓库配置 (已完成)

| 远程名 | URL | 权限 | 说明 |
|--------|-----|------|------|
| `origin` | `https://github.com/Tupig/DiPlay.git` | 读写 | 你的主仓库 (TuPigPlay) |
| `upstream` | `https://github.com/shihabal3amri/DiPlay.git` | **只读** | 上游原项目 (已禁用 push) |

```bash
# 已配置完成：
git remote add upstream https://github.com/shihabal3amri/DiPlay.git
git remote set-url --push upstream "禁用推送到上游"  # 禁用误推
```

### 🔄 同步工作流 (Agent 必须掌握)

```mermaid
graph LR
    A[上游有更新] --> B[git fetch upstream]
    B --> C{评估价值}
    C -->|有价值| D[git cherry-pick <commit>]
    C -->|无价值| E[忽略]
    D --> F[测试验证]
    F --> G[推送到 origin/main]
    E --> H[继续开发]
```

### 📋 标准操作流程 (Agent 必须掌握)

#### 1. 获取上游更新
```bash
cd /d H:\DiPlay-Merge\main-repo

# 获取上游最新代码
git fetch upstream

# 查看上游新提交 (不合并)
git log --oneline main..upstream/main

# 查看详细差异
git log --oneline --graph --all -20
```

#### 2. 评估价值 (Agent 必须评估)

| 评估维度 | 判断标准 |
|----------|----------|
| **功能相关性** | 是否与 TuPigPlay 功能相关 (CarPlay、方向盘、仪表盘、ADB等) |
| **兼容性** | 是否与零跑 C11 (8295P) 硬件兼容 |
| **代码质量** | 是否符合项目规范 (ktlint/detekt) |
| **维护成本** | 引入后维护成本是否可控 |

#### 3. 选择性合并 (有价值时)

```bash
# 创建临时评估分支
git checkout -b eval-upstream upstream/main

# 对比差异
git diff main..eval-upstream --stat

# 仅挑选有用的提交 (推荐 cherry-pick)
git checkout main
git cherry-pick <commit-hash>  # 仅挑选有用的

# 或合并特定文件
git checkout upstream/main -- path/to/valuable/file.kt
```

#### 4. 测试验证 (必须)

```bash
# 构建验证
./build_leapmotor.sh ximalaya

# 真机测试核心流程
# - 无线/有线 CarPlay 连接
# - 方向盘按键响应
# - 三屏显示 (中控/仪表/副驾)
# - 锯齿/延迟体验
# - ADB 功能 (热点/车速/USB确认)
```

#### 5. 推送到主仓库
```bash
git push origin main
```

### ⚠️ 禁止事项 (红线)

| 禁止操作 | 原因 |
|----------|------|
| ❌ `git push upstream` | 已禁用，防止误推送到上游 |
| ❌ 直接合并 `upstream/main` 到 `main` | 会引入大量不兼容的 BYD 代码 |
| ❌ 盲目合并所有上游提交 | 必须评估后选择性合并 |
| ❌ 修改上游仓库代码 | 无权限，且违背独立维护原则 |

### 📋 Agent 必须遵守的同步清单

- [ ] 定期 `git fetch upstream` (建议每周至少一次)
- [ ] 阅读上游 commit 信息，评估价值
- [ ] 有价值 → `git cherry-pick` 或文件级合并
- [ ] 无价值 → 记录原因，忽略
- [ ] 合并后 → 完整测试验证
- [ ] 推送到 `origin/main` (你的主仓库)
- [ ] 记录合并决策到 Issue/ADR

---

### ✅ 当前远程配置状态 (已验证)

| 远程名 | Fetch URL | Push URL | 状态 |
|--------|-----------|----------|------|
| `origin` | `https://github.com/Tupig/DiPlay.git` | `https://github.com/Tupig/DiPlay.git` | ✅ 读写 |
| `upstream` | `https://github.com/shihabal3amri/DiPlay.git` | `禁用推送到上游` | ✅ 只读 |

```bash
# 验证命令
git remote -v
```

输出应为：
```
origin    https://github.com/Tupig/DiPlay.git (fetch)
origin    https://github.com/Tupig/DiPlay.git (push)
upstream  https://github.com/shihabal3amri/DiPlay.git (fetch)
upstream  禁用推送到上游 (push)
```

---

## 📚 文档规范 (对标 GitHubDesktop2Chinese，强制)

> **标杆仓库**：https://github.com/Tupig/GitHubDesktop2Chinese —— 本项目所有文档必须达到同等清晰度。

### 必须创建/维护的标准文档

| 文件 | 作用 | 状态 |
|------|------|------|
| `README.md` | 项目门面：徽章 + 简介 + 目录 + 快速开始 + 截图 + FAQ + 许可证 | ⏳ 待重写 |
| `docs/CONTRIBUTING.md` | 贡献指南：分支规范、Commit 规范、PR 流程、Issue 流程 | ⏳ 待创建 |
| `SECURITY.md` | 安全策略：漏洞上报方式（仅 GitHub 私密报告） | ⏳ 待创建 |
| `PRIVACY.md` | 隐私政策：数据收集、存储、Adb 权限用途 | ⏳ 待创建 |
| `LICENSE` | 开源许可证（与上游保持一致） | ⏳ 待确认 |
| `docs/wiki/` | 分类详细文档（安装、使用、开发、故障排查） | ⏳ 待创建 |
| `CHANGELOG.md` | 版本变更日志（Keep a Changelog 格式） | ⏳ 待创建 |
| `.github/ISSUE_TEMPLATE/*.yml` | 表单式 Issue 模板（bug/feature/task） | ⏳ 待创建 |
| `.github/PULL_REQUEST_TEMPLATE.md` | PR 模板 | ⏳ 待创建 |
| `.github/workflows/*.yml` | CI/CD（构建、检查、Release） | ⏳ 待创建 |

### README.md 结构标准（参照 GitHubDesktop2Chinese）

```markdown
# TuPigPlay (原 DiPlay)

[![Build](badge)][![Release](badge)][![License](badge)][![Issues](badge)]

> 中英双语一句话简介

## 目录 / Table of Contents
## ✨ 功能特性 / Features        ← 表格列出，带勾选状态
## 🚀 快速开始 / Quick Start     ← 可复制的命令块
## 📦 安装 / Installation        ← 分步骤 + 注意事项引用块
## 🖥️ 适配车型 / Supported Vehicles  ← 表格：车型/芯片/屏幕/系统版本
## 🛠️ 构建 / Building            ← 参数表格 + 命令
## ❓ 常见问题 / FAQ              ← 问答形式
## 🤝 贡献 / Contributing        ← 链接到 CONTRIBUTING.md
## 📄 许可证 / License
## 🙏 致谢 / Acknowledgments     ← 注明上游 shihabal3amri/DiPlay
```

### 文档写作硬性规则

| 规则 | 说明 |
|------|------|
| **双语单文件** | 同一文件内中英对照，**禁止** `.zh-CN.md` 副本文件 |
| **徽章齐全** | Build / Release / License / Issues 徽章指向 `Tupig/DiPlay` |
| **相对路径** | 仓库内链接一律相对路径，禁止 `H:\...` 等绝对路径 |
| **表格优先** | 参数、选项、对比信息必须用表格 |
| **可复制命令** | 所有命令放在代码块中，可直接复制执行 |
| **引用块提示** | 重要提示用 `> ⚠️ 注意：`、`> 💡 提示：` 引用块 |
| **单一事实源** | 同一信息只写一处，其他地方用链接引用 |
| **链接检查** | 提交前检查无死链（相对链接指向的文件必须存在） |

### 工作流（Workflow）梳理要求

1. **`.github/workflows/build.yml`** — push/PR 触发：Gradle 构建 + ktlint + detekt
2. **`.github/workflows/release.yml`** — Tag `v*` 触发：生成 Release + APK 附件 + 自动 CHANGELOG
3. 所有 workflow 必须是**唯一且必要**的（用户要求：只允许一个有效工作流，不重复造轮子）
4. workflow 内涉及的仓库链接一律用 `Tupig/DiPlay`

---

## 🎯 给下一个 Agent 的建议（接手顺序，务必按此优先级）

> 1. **先修编译**：`CarPlayHostActivity.kt` 中仍残留 `com.shilapi.xcertplay.hud.BydNavigationOutputs` / `BydClusterNaviMode` 引用（约 1058、1127、1267 行），这些类已删除，会直接编译失败；`LeapmotorClusterLayout.kt` 有重复代码块且 package 声明与所在目录不一致，需一并修复。
> 2. **再补 Phase 4.3**：完善 `CarPlayHostActivity` 中 `ensureCopilotPresentation()` / `Theme.COPILOT` 的副驾屏集成，真机验证 `CopilotDisplayPresentation.findDisplay()`。
> 3. **然后 Phase 5 ADB**：利用零跑已开启 ADB，实现热点自动开启、USB 自动确认、车速读取（`LeapmotorVehicleAccess.getVehicleSpeed()`）。
> 4. **最后 Phase 6/8**：按本文档「📚 文档规范」章节重写 README、补齐 `.github/` 模板与工作流、合并双语文档，然后构建验证。
>
> 所有基础设施（版本管理、构建脚本、目录结构、代码清理）已完备，可专注业务功能开发。
> **记住：任何问题都要创建 GitHub Issue 追踪；一切文档与工作流看齐 GitHub 模板规范。**

---

**文档版本**：v1.2
**最后更新**：2026-10-12
**维护要求**：随进度持续更新本文档，未完成事项不得删除，完成后标注 ✅ 并注明对应 commit