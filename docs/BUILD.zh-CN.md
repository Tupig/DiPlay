# 构建 DiPlay

[English](BUILD.md) · **简体中文**

按照本流程为 Android 车机构建 DiPlay 应用。
请在仓库根目录下执行命令，`gradlew` 和 `settings.gradle.kts` 位于该目录。

## 选择应用

本仓库包含多个 Android 应用。
选择 `mobile` 来构建 DiPlay 主应用。

| Gradle 模块 | 用途 | Debug APK |
| --- | --- | --- |
| `:mobile` | 面向 Android 车机的 DiPlay 主应用 | `mobile/build/outputs/apk/debug/mobile-debug.apk` |
| `:maphost` | 显示来自 DiPlay 的地图的示例应用 | `samples/maphost/build/outputs/apk/debug/maphost-debug.apk` |
| `:home` | 可选的 DiPlay Home 启动器 | `samples/home/build/outputs/apk/debug/home-debug.apk` |
| `:automotive` | 从 xcertplay 继承的独立 Android Automotive 应用 | `automotive/build/outputs/apk/debug/automotive-debug.apk` |

`:common` 和 `:shared` 模块是库。
应用会使用这些库。

如果你安装的应用是 **DiPlay map host**，说明你选择了 `:maphost` 或其 APK。
构建 `:mobile` 并选择 `mobile-debug.apk` 即可安装主应用。
诸如 `./gradlew assembleDebug` 这样的命令可能同时构建多个应用。
请在每条构建命令中使用模块名来指定所需的应用。

## 准备构建环境

安装以下工具：

- JDK 25。
- Android SDK Platform 37（`platforms;android-37.0`）。
- Android SDK Build-Tools 36.0.0。
- Android SDK Platform-Tools。
- Android NDK 28.2.13676358。

请使用本仓库自带的 Gradle wrapper。
首次构建需要联网以下载依赖。

1. 在 Android Studio 中打开仓库根目录。
2. 通过 **Tools → SDK Manager** 安装 Android SDK 组件。
3. 在 Android Studio 的 Gradle 设置中将 **Gradle JDK** 设为 JDK 25。
4. 等待 Gradle 同步完成。

若在终端中构建，请将 `JAVA_HOME` 设为你的 JDK 25 目录。
将 `ANDROID_HOME` 设为你的 Android SDK 目录。
也可以在根目录的 `local.properties` 文件中指定 SDK 目录：

```properties
sdk.dir=/absolute/path/to/Android/sdk
```

请将 `local.properties` 排除在 Git 之外。
构建前先检查 Java 版本：

```sh
java -version
```

输出必须显示 Java 25。
在 Windows 上，请在下文命令中用 `gradlew.bat` 代替 `./gradlew`。

## 构建主应用

如需进行不含运行时认证资产的源码构建：

```sh
./gradlew :mobile:assembleDebug
```

检查以下文件是否存在：

```text
mobile/build/outputs/apk/debug/mobile-debug.apk
```

Debug 应用 ID 为 `com.shihab.diplay.hudtest`。
Release 应用 ID 为 `com.shihab.diplay`。

### 覆盖应用 ID

只允许安装厂商认可包名的车机会拒绝默认身份。
`mobile` 接受一个可选的 Gradle 属性，用于替换最终的应用 ID：

```sh
./gradlew :mobile:assembleDebug -PdiplayApplicationId=com.example.app
```

设置该属性后，debug 的 `.hudtest` 后缀会被去掉，APK 将完全使用你传入的值。未设置时，行为与普通构建完全一致，因此 CI 以及 CI 命令中的所有测试都不受影响。仅在车机要求使用不同包名时才使用它；其后果见 [Leapmotor 说明](LEAPMOTOR.md)。

除非你提供运行时认证资产，否则源码构建出的 APK 不含任何配件身份。
独立 CarPlay 需要这些资产才能与 iPhone 连接。
请使用下文的车载测试流程来实现此目的。
测试会在运行时生成合成身份。
请将测试私钥保存在 Git 之外。

### 使用 Android Studio

1. 打开 **Run → Edit Configurations**。
2. 选择一个 **Android App** 配置，或新建一个。
3. 将 **Module** 设为 `mobile`。
4. 在工具栏中选中该配置。
5. 选择 Android 设备。
6. 点击 **Run** 以安装主 debug 应用。

如需只构建 APK 而不安装，请在 Android Studio 终端中运行 `:mobile:assembleDebug`。
然后从 `mobile` 输出目录中选取该 APK。

## 检查源码构建

运行单元测试、lint 检查和主 debug 构建：

```sh
./gradlew :shared:testDebugUnitTest :common:testDebugUnitTest :mobile:lintDebug :mobile:assembleDebug
```

确认 Gradle 报告 `BUILD SUCCESSFUL`。
完整的 CI 流程见 [DiPlay workflow](../.github/workflows/diplay.yml)。
其 `unit-test` job 运行测试与 lint 检查，随后 `build-apk` 为 `mobile`、`home` 和 `maphost` 应用构建 debug APK。

## 构建独立车载测试 APK

准备一个外部目录，放入以下运行时认证文件：

```text
runtime-assets/
└── offline-mfi/
    ├── identity.pk8
    └── certificate.p7b
```

该目录中只放计划使用的运行时文件。
请将该目录及其私钥保存在 Git 之外。
构建会拒绝 APK assets 中意外出现的凭据容器。

1. 将 `DIPLAY_AUTH_ASSETS_DIR` 设为该目录的绝对路径。
2. 运行独立 debug 任务。

```sh
DIPLAY_AUTH_ASSETS_DIR=/absolute/path/to/runtime-assets ./gradlew :mobile:assembleStandaloneDebug
```

若任一必需文件缺失或为空，该任务会中止。
其 APK 输出为 `mobile/build/outputs/apk/debug/mobile-debug.apk`。
请对照你所选的本地输入检查 APK 中的以下两个文件：

- `assets/offline-mfi/identity.pk8`。
- `assets/offline-mfi/certificate.p7b`。

用该 APK 进行独立连接测试。
用相同的签名密钥更新已有的测试应用，以保留其设置。
不同的签名密钥无法更新同一个已安装的应用。

## 构建 release APK

按照车载测试流程准备好运行时资产。
在本地设置以下环境变量：

| 变量 | 值 |
| --- | --- |
| `DIPLAY_AUTH_ASSETS_DIR` | 运行时资产目录的绝对路径 |
| `ANDROID_KEYSTORE_PATH` | 你的 Android 签名 keystore 路径 |
| `ANDROID_KEYSTORE_PASSWORD` | Keystore 密码 |
| `ANDROID_KEY_ALIAS` | 签名密钥别名 |
| `ANDROID_KEY_PASSWORD` | 签名密钥密码 |

请将 keystore 和密码保存在 Git 之外。
运行 release 检查与构建：

```sh
./gradlew :shared:testDebugUnitTest :common:testDebugUnitTest :mobile:lintRelease :mobile:assembleRelease
```

输出为 `mobile/build/outputs/apk/release/mobile-release.apk`。
该 APK 包含你提供的运行时身份。
拿到 APK 的人可以从其中提取该身份。
APK 不包含 Android 签名密钥。
公开发布所使用的实验性身份见 [项目声明](THIRD_PARTY_NOTICES.md)。

公开的源码归档与 release 标签一致。
它不包含运行时身份、签名密钥、本地配置和构建产物。
已弃用的 `build-beta.py` 辅助脚本不再是构建流程的一部分。

## 修复构建问题

| 症状 | 处理 |
| --- | --- |
| 安装的应用是 DiPlay map host | 在 Android Studio 中选择 `mobile`。从 `mobile` 输出目录构建并安装 APK。 |
| Gradle 找不到 Android SDK | 设置 `ANDROID_HOME`，或设置 `local.properties` 中的 `sdk.dir` 值。 |
| 缺少所需的 SDK 或 NDK 组件 | 用 SDK Manager 安装列表中的组件。 |
| Java 或 Gradle JDK 版本不正确 | 将终端 JDK 和 Android Studio Gradle JDK 设为 Java 25。 |
| 源码 APK 无法启动独立 CarPlay | 提供运行时认证资产。使用 `:mobile:assembleStandaloneDebug`。 |
| 独立任务报告缺少认证文件 | 检查外部目录路径和两个必需文件。 |
| Android 拒绝应用更新 | 使用与已安装应用相同的应用 ID 和签名密钥。 |
