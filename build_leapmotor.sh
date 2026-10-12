#!/bin/bash
# 零跑 C11 2024 专用构建脚本
# 用法: ./build_leapmotor.sh [ximalaya|bilibili|qqmusic|custom包名]

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 白名单包名映射
declare -A PACKAGES=(
    ["ximalaya"]="com.ximalaya.ting.android.car"
    ["bilibili"]="com.bilibili.bilithings"
    ["qqmusic"]="com.tencent.qqmusiccar"
)

# 显示帮助
show_help() {
    echo -e "${BLUE}零跑 C11 2024 (8295P) DiPlay 构建脚本${NC}"
    echo ""
    echo "用法: $0 [包名别名|完整包名]"
    echo ""
    echo "预设别名:"
    echo "  ximalaya  - 喜马拉雅车机版 (com.ximalaya.ting.android.car) - 推荐 ⭐"
    echo "  bilibili  - 哔哩哔哩车机版 (com.bilibili.bilithings)"
    echo "  qqmusic   - QQ音乐车机版 (com.tencent.qqmusiccar)"
    echo ""
    echo "自定义包名:"
    echo "  $0 com.example.custom"
    echo ""
    echo "环境变量:"
    echo "  ANDROID_KEYSTORE_PATH      - 签名密钥库路径"
    echo "  ANDROID_KEYSTORE_PASSWORD  - 密钥库密码"
    echo "  ANDROID_KEY_ALIAS          - 密钥别名"
    echo "  ANDROID_KEY_PASSWORD       - 密钥密码"
    echo ""
    echo "示例:"
    echo "  $0 ximalaya"
    echo "  $0 bilibili"
    echo "  ANDROID_KEYSTORE_PATH=~/release.jks $0 ximalaya"
}

# 检查参数
if [ $# -eq 0 ] || [ "$1" = "-h" ] || [ "$1" = "--help" ]; then
    show_help
    exit 0
fi

PACKAGE_ARG="$1"

# 解析包名
if [[ -n "${PACKAGES[$PACKAGE_ARG]}" ]]; then
    APPLICATION_ID="${PACKAGES[$PACKAGE_ARG]}"
    echo -e "${GREEN}使用预设包名: ${APPLICATION_ID}${NC}"
else
    # 验证包名格式
    if [[ "$PACKAGE_ARG" =~ ^[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)+$ ]]; then
        APPLICATION_ID="$PACKAGE_ARG"
        echo -e "${YELLOW}使用自定义包名: ${APPLICATION_ID}${NC}"
    else
        echo -e "${RED}错误: 无效的包名格式: $PACKAGE_ARG${NC}"
        exit 1
    fi
fi

# 检查签名配置
if [ -z "$ANDROID_KEYSTORE_PATH" ]; then
    echo -e "${YELLOW}警告: 未设置 ANDROID_KEYSTORE_PATH，将使用 debug 签名${NC}"
    BUILD_TYPE="Debug"
    GRADLE_TASK="assembleDebug"
else
    echo -e "${GREEN}使用 Release 签名构建${NC}"
    BUILD_TYPE="Release"
    GRADLE_TASK="assembleRelease"
fi

# 显示构建信息
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}构建配置:${NC}"
echo -e "  包名: ${APPLICATION_ID}"
echo -e "  类型: ${BUILD_TYPE}"
echo -e "  任务: :mobile:${GRADLE_TASK}"
echo -e "${BLUE}========================================${NC}"

# 执行构建
echo -e "${BLUE}开始构建...${NC}"
./gradlew :mobile:${GRADLE_TASK} -PdiplayApplicationId=${APPLICATION_ID} --no-daemon

# 查找输出 APK
if [ "$BUILD_TYPE" = "Release" ]; then
    APK_PATH=$(find mobile/build/outputs/apk/release -name "*.apk" | head -1)
else
    APK_PATH=$(find mobile/build/outputs/apk/debug -name "*.apk" | head -1)
fi

if [ -n "$APK_PATH" ] && [ -f "$APK_PATH" ]; then
    # 重命名为友好名称
    PACKAGE_SUFFIX=$(echo "$APPLICATION_ID" | sed 's/\./_/g')
    NEW_NAME="DiPlay-0.2.18-leapmotor-${PACKAGE_SUFFIX}-${BUILD_TYPE}.apk"
    cp "$APK_PATH" "$NEW_NAME"
    
    echo -e "${GREEN}========================================${NC}"
    echo -e "${GREEN}构建成功!${NC}"
    echo -e "APK: ${NEW_NAME}"
    echo -e "包名: ${APPLICATION_ID}"
    echo -e "大小: $(du -h "$NEW_NAME" | cut -f1)"
    echo -e "${GREEN}========================================${NC}"
    
    # 验证包名
    echo -e "${BLUE}验证 APK 包名...${NC}"
    aapt dump badging "$NEW_NAME" | grep "package: name=" || echo "请安装 aapt 工具验证"
else
    echo -e "${RED}错误: 未找到输出 APK${NC}"
    exit 1
fi

echo ""
echo -e "${YELLOW}下一步: U 盘安装${NC}"
echo "1. 将 ${NEW_NAME} 重命名为对应白名单应用名（如 Ximalaya_Car_v4.7.2.apk）"
echo "2. 放入 FAT32 U 盘根目录"
echo "3. 插入零跑 C11 前排左侧数据口"
echo "4. 车机: 应用 → U盘应用 → 安装"
echo "5. 详见: docs/LEAPMOTOR_C11_2024_INSTALL.md"