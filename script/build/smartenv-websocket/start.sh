#!/usr/bin/env bash

docker run -d -p 8117:8117 \
--privileged=true \
-e JVM_OPT="-Xmx512m" -m 1024m \
-e NACOS_ADDR="10.21.35.126:8848" \
-e NACOS_GROUP="DEFAULT_GROUP" \
-e NACOS_PROFILE="test" \
-e NACOS_NAMESPACE="public" \
-v /etc/localtime:/etc/localtime:ro \
--restart=always \
-v /home/smartenv/logs/smartenv-websocket:/smartenv/websocket/target/blade/log \
--name smartenv-websocket smartenv-websocket

docker ps | grep smartenv-websocket