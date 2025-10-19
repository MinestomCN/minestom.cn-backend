#!/bin/bash

# 设置变量
KEYSTORE="keystore.jks"
ALIAS="minestomcn"
KEYSTORE_PASS="changeit"
DNAME="CN=localhost,OU=MinestomCN,O=CyanBukkit,L=Beijing,ST=Beijing,C=CN"

# 生成keystore
keytool -genkeypair \
  -alias $ALIAS \
  -keyalg RSA \
  -keysize 2048 \
  -validity 365 \
  -keystore $KEYSTORE \
  -storepass $KEYSTORE_PASS \
  -keypass $KEYSTORE_PASS \
  -dname "$DNAME"

echo "证书已生成到 $KEYSTORE"