#!/bin/bash

# 简化的构建脚本
echo "开始构建神秘复苏模组..."

# 检查 Java 版本
java -version

# 下载 Gradle（如果不存在）
if [ ! -f "gradle-7.5.1/bin/gradle" ]; then
    echo "下载 Gradle 7.5.1..."
    wget https://services.gradle.org/distributions/gradle-7.5.1-bin.zip
    unzip gradle-7.5.1-bin.zip
    rm gradle-7.5.1-bin.zip
fi

# 使用 Gradle 构建
./gradle-7.5.1/bin/gradle build

echo "构建完成！"
