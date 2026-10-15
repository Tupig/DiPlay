# 构建 TuPigPlay / Build TuPigPlay

按照本流程为 Android 车机构建 TuPigPlay 应用。请在仓库根目录下执行命令，`gradlew` 和 `settings.gradle.kts` 位于该目录。

Use this procedure to build the TuPigPlay app for an Android head unit. Run the commands from the repository root, where `gradlew` and `settings.gradle.kts` are located.

## 选择应用 / Select the application

本仓库包含多个 Android 应用。选择 `mobile` 来构建 TuPigPlay 主应用。

This repository contains several Android applications. Select `mobile` to build the main TuPigPlay app.

| Gradle 模块 | 用途 | Debug APK |
| --- | --- | --- |
| `:mobile` | 面向 Android 车机的 TuPigPlay 主应用 | `mobile/build/outputs/apk/debug/mobile-debug.apk` |
| `:maphost` | 显示来自 TuPigPlay 的地图的示例应用 | `samples/maphost/build/outputs/apk/debug/maphost-debug.apk` |
| `:home` | 可选的 TuPigPlay Home 启动器 | `samples/home/build/outputs/apk/debug/home-debug.apk` |
| `:automotive` | 从 xcertplay 继承的独立 Android Automotive 应用 | `automotive/build/outputs/apk/debug/automotive-debug.apk` |

| Gradle module | Purpose | Debug APK |
| --- | --- | --- |
| `:mobile` | Main TuPigPlay app for Android head units | `mobile/build/outputs/apk/debug/mobile-debug.apk` |
| `:maphost` | Sample application that displays a map from TuPigPlay | `samples/maphost/build/outputs/apk/debug/maphost-debug.apk` |
| `:home` | Optional TuPigPlay Home launcher | `samples/home/build/outputs/apk/debug/home-debug.apk` |
| `:automotive` | Separate Android Automotive application inherited from xcertplay | `automotive/build/outputs/apk/debug/automotive-debug.apk` |

`:common` 和 `:shared` 模块是库，应用会使用这些库。

The `:common` and `:shared` modules are libraries. The applications use these libraries.

如果你安装的应用是 **TuPigPlay map host**，说明你选择了 `:maphost` 或其 APK。构建 `:mobile` 并选择 `mobile-debug.apk` 即可安装主应用。诸如 `./gradlew assembleDebug` 这样的命令可能同时构建多个应用；请在每条构建命令中使用模块名来指定所需的应用。

If the installed app is **TuPigPlay map host**, you selected `:maphost` or its APK. Build `:mobile` and select `mobile-debug.apk` to install the main app. A command such as `./gradlew assembleDebug` can build more than one application; use the module name in each build command to select the required application.

## 准备构建环境 / Prepare the build environment

安装以下工具：

Install these tools:

- JDK 25。
- Android SDK Platform 37（`platforms;android-37.0`）。
- Android SDK Build-Tools 36.0.0。
- Android SDK Platform-Tools。
- Android NDK 28.2.13676358。

- JDK 25.
- Android SDK Platform 37 (`platforms;android-37.0`).
- Android SDK Build-Tools 36.0.0.
- Android SDK Platform-Tools.
- Android NDK 28.2.13676358.

请使用本仓库自带的 Gradle wrapper。首次构建需要联网以下载依赖。

Use the Gradle wrapper included in this repository. The first build needs an internet connection to download dependencies.

1. 在 Android Studio 中打开仓库根目录。
2. 通过 **Tools → SDK Manager** 安装 Android SDK 组件。
3. 在 Android Studio 的 Gradle 设置中将 **Gradle JDK** 设为 JDK 25。
4. 等待 Gradle 同步完成。

1. Open the repository root in Android Studio.
2. Install the Android SDK packages with **Tools → SDK Manager**.
3. Set **Gradle JDK** to JDK 25 in the Android Studio Gradle settings.
4. Wait for Gradle sync to finish.

若在终端中构建，请将 `JAVA_HOME` 设为你的 JDK 25 目录，将 `ANDROID_HOME` 设为你的 Android SDK 目录。也可以在根目录的 `local.properties` 文件中指定 SDK 目录：

For a terminal build, set `JAVA_HOME` to your JDK 25 directory and `ANDROID_HOME` to your Android SDK directory. You can also specify the SDK directory in the root `local.properties` file:

```properties
sdk.dir=/absolute/path/to/Android/sdk
```

请将 `local.properties` 排除在 Git 之外。构建前先检查 Java 版本：

Keep `local.properties` outside Git. Check the Java version before the build:

```sh
java -version
```

输出必须显示 Java 25。在 Windows 上，请在下文命令中用 `gradlew.bat` 代替 `./gradlew`。

The output must identify Java 25. On Windows, use `gradlew.bat` in place of `./gradlew` in the commands below.

## 构建主应用 / Build the main app

如需进行不含运行时认证资产的源码构建：

For a source build without runtime authentication assets:

```sh
./gradlew :mobile:assembleDebug
```

检查以下文件是否存在：

Check that this file exists:

```text
mobile/build/outputs/apk/debug/mobile-debug.apk
```

Debug 应用 ID 为 `com.shihab.diplay.hudtest`，Release 应用 ID 为 `com.shihab.diplay`。

The debug application ID is `com.shihab.diplay.hudtest`. The release application ID is `com.shihab.diplay`.

### 覆盖应用 ID / Override the application ID

只允许安装厂商认可包名的车机会拒绝默认身份。`mobile` 接受一个可选的 Gradle 属性，用于替换最终的应用 ID：

A head unit that only installs vendor-approved packages rejects the default identity. `mobile` accepts an optional Gradle property that replaces the final application ID:

```sh
./gradlew :mobile:assembleDebug -PdiplayApplicationId=com.example.app
```

设置该属性后，debug 的 `.hudtest` 后缀会被去掉，APK 将完全使用你传入的值；未设置时，行为与普通构建完全一致，因此 CI 以及 CI 命令中的所有测试都不受影响。仅在车机要求使用不同包名时才使用它；其后果见 [Leapmotor 说明](LEAPMOTOR.md)。

When the property is set the debug `.hudtest` suffix is dropped, so the APK carries exactly the value you pass. When it is unset, behaviour is identical to a normal build, so CI and every test in the CI command are unaffected. Use it only where the head unit requires a different package name; the consequences are listed in [Leapmotor notes](LEAPMOTOR.md).

除非你提供运行时认证资产，否则源码构建出的 APK 不含任何配件身份。独立 CarPlay 需要这些资产才能与 iPhone 连接；请使用下文的车载测试流程来实现此目的。测试会在运行时生成合成身份，请将测试私钥保存在 Git 之外。

The source APK contains no accessory identity unless you supply runtime authentication assets. Standalone CarPlay needs these assets to connect to an iPhone; use the car-test procedure below for that purpose. Tests generate synthetic identities at runtime. Keep test private keys outside Git.

### 使用 Android Studio / Use Android Studio

1. 打开 **Run → Edit Configurations**。
2. 选择一个 **Android App** 配置，或新建一个。
3. 将 **Module** 设为 `mobile`。
4. 在工具栏中选中该配置。
5. 选择 Android 设备。
6. 点击 **Run** 以安装主 debug 应用。

1. Open **Run → Edit Configurations**.
2. Select an **Android App** configuration, or create one.
3. Set **Module** to `mobile`.
4. Select this configuration in the toolbar.
5. Select the Android device.
6. Click **Run** to install the main debug app.

如需只构建 APK 而不安装，请在 Android Studio 终端中运行 `:mobile:assembleDebug`，然后从 `mobile` 输出目录中选取该 APK。

To build an APK without installation, run `:mobile:assembleDebug` in the Android Studio terminal, then select the APK from the `mobile` output directory.

## 检查源码构建 / Check the source build

运行单元测试、lint 检查和主 debug 构建：

Run the unit tests, lint checks, and main debug build:

```sh
./gradlew :shared:testDebugUnitTest :common:testDebugUnitTest :mobile:lintDebug :mobile:assembleDebug
```

确认 Gradle 报告 `BUILD SUCCESSFUL`。完整的 CI 流程见 [TuPigPlay workflow](../.github/workflows/diplay.yml)：其 `unit-test` job 运行测试与 lint 检查，随后 `build-apk` 为 `mobile`、`home` 和 `maphost` 应用构建 debug APK。

Check that Gradle reports `BUILD SUCCESSFUL`. For the complete CI procedure, see [the TuPigPlay workflow](../.github/workflows/diplay.yml): its `unit-test` job runs the tests and lint checks, and `build-apk` then builds the debug APKs for the `mobile`, `home` and `maphost` applications.

## 构建独立车载测试 APK / Build a standalone car-test APK

准备一个外部目录，放入以下运行时认证文件：

Prepare an external directory with these runtime authentication files:

```text
runtime-assets/
└── offline-mfi/
    ├── identity.pk8
    └── certificate.p7b
```

该目录中只放计划使用的运行时文件，并将该目录及其私钥保存在 Git 之外。构建会拒绝 APK assets 中意外出现的凭据容器。

Use only the intended runtime files in this directory. Keep the directory and its private key outside Git. The build rejects unexpected credential containers in APK assets.

1. 将 `DIPLAY_AUTH_ASSETS_DIR` 设为该目录的绝对路径。
2. 运行独立 debug 任务。

1. Set `DIPLAY_AUTH_ASSETS_DIR` to the absolute directory path.
2. Run the standalone debug task.

```sh
DIPLAY_AUTH_ASSETS_DIR=/absolute/path/to/runtime-assets ./gradlew :mobile:assembleStandaloneDebug
```

若任一必需文件缺失或为空，该任务会中止。其 APK 输出为 `mobile/build/outputs/apk/debug/mobile-debug.apk`。请对照你所选的本地输入检查 APK 中的以下两个文件：

The task stops if either required file is absent or empty. Its APK output is `mobile/build/outputs/apk/debug/mobile-debug.apk`. Check both files in the APK against your selected local inputs:

- `assets/offline-mfi/identity.pk8`
- `assets/offline-mfi/certificate.p7b`

用该 APK 进行独立连接测试。用相同的签名密钥更新已有的测试应用，以保留其设置；不同的签名密钥无法更新同一个已安装的应用。

Use this APK for a standalone connection test. Update the existing test app with the same signing key to keep its settings; different signing keys cannot update the same installed application.

## 构建 release APK / Build a release APK

按照车载测试流程准备好运行时资产，并在本地设置以下环境变量：

Prepare the runtime assets as described in the car-test procedure, and set these environment variables locally:

| 变量 | 值 |
| --- | --- |
| `DIPLAY_AUTH_ASSETS_DIR` | 运行时资产目录的绝对路径 |
| `ANDROID_KEYSTORE_PATH` | 你的 Android 签名 keystore 路径 |
| `ANDROID_KEYSTORE_PASSWORD` | Keystore 密码 |
| `ANDROID_KEY_ALIAS` | 签名密钥别名 |
| `ANDROID_KEY_PASSWORD` | 签名密钥密码 |

| Variable | Value |
| --- | --- |
| `DIPLAY_AUTH_ASSETS_DIR` | Absolute path to the runtime assets directory |
| `ANDROID_KEYSTORE_PATH` | Path to your Android signing keystore |
| `ANDROID_KEYSTORE_PASSWORD` | Keystore password |
| `ANDROID_KEY_ALIAS` | Signing key alias |
| `ANDROID_KEY_PASSWORD` | Signing key password |

请将 keystore 和密码保存在 Git 之外。运行 release 检查与构建：

Keep the keystore and passwords outside Git. Run the release checks and build:

```sh
./gradlew :shared:testDebugUnitTest :common:testDebugUnitTest :mobile:lintRelease :mobile:assembleRelease
```

输出为 `mobile/build/outputs/apk/release/mobile-release.apk`。该 APK 包含你提供的运行时身份，拿到 APK 的人可以从其中提取该身份；APK 不包含 Android 签名密钥。公开发布所使用的实验性身份见[项目声明](THIRD_PARTY_NOTICES.md)。

The output is `mobile/build/outputs/apk/release/mobile-release.apk`. The APK contains the runtime identity you supplied; recipients can extract that identity from the APK. The APK excludes the Android signing key. See [the project notices](THIRD_PARTY_NOTICES.md) for the experimental identity used in public releases.

公开的源码归档与 release 标签一致，不包含运行时身份、签名密钥、本地配置和构建产物。已弃用的 `build-beta.py` 辅助脚本不再是构建流程的一部分。

The public source archive matches the release tag. It excludes runtime identities, signing keys, local configuration, and build output. The retired `build-beta.py` helper is no longer part of the build procedure.

## 修复构建问题 / Correct build problems

| 症状 | 处理 |
| --- | --- |
| 安装的应用是 TuPigPlay map host | 在 Android Studio 中选择 `mobile`，从 `mobile` 输出目录构建并安装 APK |
| Gradle 找不到 Android SDK | 设置 `ANDROID_HOME`，或设置 `local.properties` 中的 `sdk.dir` 值 |
| 缺少所需的 SDK 或 NDK 组件 | 用 SDK Manager 安装列表中的组件 |
| Java 或 Gradle JDK 版本不正确 | 将终端 JDK 和 Android Studio Gradle JDK 设为 Java 25 |
| 源码 APK 无法启动独立 CarPlay | 提供运行时认证资产，使用 `:mobile:assembleStandaloneDebug` |
| 独立任务报告缺少认证文件 | 检查外部目录路径和两个必需文件 |
| Android 拒绝应用更新 | 使用与已安装应用相同的应用 ID 和签名密钥 |

| Symptom | Action |
| --- | --- |
| Installed app is TuPigPlay map host | Select `mobile` in Android Studio. Build and install the APK from the `mobile` output directory. |
| Gradle cannot find the Android SDK | Set `ANDROID_HOME` or the `sdk.dir` value in `local.properties`. |
| A required SDK or NDK package is absent | Install the listed package with SDK Manager. |
| Java or Gradle JDK version is incorrect | Set the terminal JDK and Android Studio Gradle JDK to Java 25. |
| Source APK cannot start standalone CarPlay | Supply runtime authentication assets. Use `:mobile:assembleStandaloneDebug`. |
| Standalone task reports missing authentication files | Check the external directory path and both required files. |
| Android rejects an app update | Use the same application ID and signing key as the installed app. |
