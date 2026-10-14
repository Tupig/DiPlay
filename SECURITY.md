# 凭据与发布打包 / Credentials and release packaging

公开发布已应维护者要求恢复。APK 有意打包了 [THIRD_PARTY_NOTICES.md](docs/THIRD_PARTY_NOTICES.md) 中所述的实验性配件证书及配套密钥；任何拿到该 APK 的人都可以提取它们。本地编译、从 Git 历史中清除以及混淆，都不会让一个被打包的共享密钥变得机密，也无法收回此前的副本。

Public release distribution has resumed at the maintainer's request. The APK intentionally bundles the experimental accessory certificate and matching key described in [THIRD_PARTY_NOTICES.md](docs/THIRD_PARTY_NOTICES.md). Anyone with the APK can extract them. Local compilation, Git history removal and obfuscation do not make a bundled shared key confidential or revoke previous copies.

公开的 Git 代码树以及对应的源码归档不包含配件密钥和 Android 发布签名密钥。CI 检查会拒绝被跟踪文件中的凭据容器和私钥块。合成的测试身份在运行时生成。源码构建不会自动导入私有资产；发布打包需要一个显式的本地目录，并且只允许放入那两个预期的运行时文件。

The public Git tree and corresponding source archive exclude accessory keys and Android release-signing secrets. CI checks reject credential containers and private-key blocks in tracked files. Synthetic test identities are generated at runtime. Source builds have no automatic private-asset import; release packaging requires an explicit local directory and permits only the two expected runtime files.

Android APK 签名密钥与之分离，始终保持在本地，绝不会被打包进 APK。当前对该实验性配件身份的接受，并不代表 Apple 的认证，也不保证未来的兼容性。

The Android APK-signing key is separate, stays local and is never bundled in the APK. Current acceptance of the experimental accessory identity does not establish Apple certification or guarantee future compatibility.

公开发布前请先检查诊断报告内容。切勿在公开 issue 中包含凭据或配对记录。敏感问题请使用 GitHub 的私密漏洞报告功能。

Review diagnostic reports before posting. Never include credentials or pairing records in public issues. Use GitHub private vulnerability reporting for sensitive findings.
