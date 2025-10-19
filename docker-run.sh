#!/bin/bash

# 创建临时目录用于存放证书
mkdir -p cert_temp

# 复制证书生成脚本到临时目录
cp generate-cert.sh cert_temp/

# 在容器内生成证书
docker run --rm \
    -v "$(pwd)/cert_temp":/cert \
    openjdk:17 \
    bash -c "cd /cert && keytool -genkeypair \
        -alias minestomcn \
        -keyalg RSA \
        -keysize 2048 \
        -validity 365 \
        -keystore keystore.jks \
        -storepass changeit \
        -keypass changeit \
        -dname \"CN=localhost,OU=MinestomCN,O=CyanBukkit,L=Beijing,ST=Beijing,C=CN\" && \
        chmod 644 keystore.jks"

# 移动生成的证书到当前目录
mv cert_temp/keystore.jks ./

# 清理临时目录
rm -rf cert_temp

# 运行主应用容器
docker run -d \
    --name minestom-backend \
    -p 9990:9990 \
    -p 9991:9991 \
    -v "$(pwd)":/app \
    -v "$(pwd)/keystore.jks":/app/keystore.jks \
    openjdk:17 \
    java -jar /app/minestomcn-backend-all.jar

echo "服务已启动:"
echo "HTTP端口: 9990"
echo "HTTPS端口: 9991"