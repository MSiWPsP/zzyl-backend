#!/bin/bash

set -e

# 容器名称
container_name="$1"

# 镜像名称
image_name="$1"

# 镜像 tag
image_tag="$2"

# 判断容器是否存在
if docker inspect "$container_name" >/dev/null 2>&1; then
    echo "容器 $container_name 存在，删除旧容器"
    docker rm -f "$container_name"
else
    echo "容器 $container_name 不存在"
fi

if [ -z "$OSS_ACCESS_KEY_ID" ] || \
   [ -z "$OSS_ACCESS_KEY_SECRET" ] || \
   [ -z "$AI_DASHSCOPE_API_KEY" ]; then
    echo "错误：必要的环境变量未注入"
    exit 1
fi

# 启动容器
echo "启动容器 $container_name"

if [ "$container_name" = "zzyl-admin" ]; then
    docker run -d \
        --restart=always \
        --name "$container_name" \
        -v /usr/local/zzyl-admin/logs:/home/ruoyi/logs \
        -p 9000:9000 \
        -e OSS_ACCESS_KEY_ID \
        -e OSS_ACCESS_KEY_SECRET \
        -e AI_DASHSCOPE_API_KEY \
        "$image_name:$image_tag"
fi