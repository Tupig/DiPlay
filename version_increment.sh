#!/usr/bin/env bash
# 版本号自动递增脚本
# 用法: ./version_increment.sh [major|minor|patch|build]
# 默认递增 build 号

set -e

VERSION_FILE="version.properties"

# 读取当前版本
source "$VERSION_FILE"

increment_build() {
    VERSION_BUILD=$((VERSION_BUILD + 1))
    if [ $VERSION_BUILD -gt $MAX_BUILD ]; then
        VERSION_BUILD=1
        increment_patch
    fi
}

increment_patch() {
    VERSION_PATCH=$((VERSION_PATCH + 1))
    if [ $VERSION_PATCH -gt $MAX_PATCH ]; then
        VERSION_PATCH=1
        increment_minor
    fi
}

increment_minor() {
    VERSION_MINOR=$((VERSION_MINOR + 1))
    if [ $VERSION_MINOR -gt $MAX_MINOR ]; then
        VERSION_MINOR=1
        increment_major
    fi
}

increment_major() {
    VERSION_MAJOR=$((VERSION_MAJOR + 1))
}

# 解析参数
case "${1:-build}" in
    major)
        increment_major
        VERSION_MINOR=0
        VERSION_PATCH=0
        VERSION_BUILD=1
        ;;
    minor)
        increment_minor
        VERSION_PATCH=0
        VERSION_BUILD=1
        ;;
    patch)
        increment_patch
        VERSION_BUILD=1
        ;;
    build|*)
        increment_build
        ;;
esac

# 生成版本字符串
VERSION_NAME="${VERSION_MAJOR}.${VERSION_MINOR}.${VERSION_PATCH}.${VERSION_BUILD}"
# versionCode = 四段式公式 + 38 偏移（与 0.0.0.1 -> 39 的发布历史衔接；gradle 同公式）
VERSION_CODE=$((VERSION_MAJOR * 1000000 + VERSION_MINOR * 10000 + VERSION_PATCH * 100 + VERSION_BUILD + 38))

# 写回文件
cat > "$VERSION_FILE" <<EOF
# TuPigPlay 版本配置
# 版本规则：0.0.0.1 -> 0.0.0.2 ... -> 0.0.0.100 -> 0.0.1.1 -> 0.0.1.2 ... -> 0.0.100.100 -> 0.1.1.1
# 递增方式：发版前手动执行 ./version_increment.sh [major|minor|patch|build]
# gradle 与 CI 从本文件读取 versionName/versionCode（mobile/build.gradle.kts）

# 当前版本号（四段式：major.minor.patch.build）
VERSION_MAJOR=$VERSION_MAJOR
VERSION_MINOR=$VERSION_MINOR
VERSION_PATCH=$VERSION_PATCH
VERSION_BUILD=$VERSION_BUILD

# 版本名称（用于显示）
VERSION_NAME=$VERSION_NAME

# 版本代码（用于 Android versionCode = 四段式公式 + 38 偏移，0.0.0.1 -> 39）
VERSION_CODE=$VERSION_CODE

# 最大值限制
MAX_BUILD=$MAX_BUILD
MAX_PATCH=$MAX_PATCH
MAX_MINOR=$MAX_MINOR
EOF

echo "版本已更新: $VERSION_NAME (code: $VERSION_CODE)"