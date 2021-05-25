#!/usr/bin/env bash

docker run -d -p 8126:8126 \
--privileged=true \
-e JVM_OPT="-Xmx512m" -m 1024m \
-e NACOS_ADDR="10.21.33.235:8848" \
-e NACOS_GROUP="DEFAULT_GROUP" \
-e NACOS_PROFILE="test" \
-e NACOS_NAMESPACE="public" \
-v /etc/localtime:/etc/localtime \
--restart=always \
-v /home/smartenv/logs/smartenv-security:/smartenv/security/target/blade/log \
--name smartenv-security smartenv-security

docker ps | grep smartenv-security