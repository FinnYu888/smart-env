#!/usr/bin/env bash

docker run -d -p 8114:8114 \
--privileged=true \
-e JVM_OPT="-Xmx512m" -m 1024m \
-e NACOS_ADDR="10.21.35.126:8848" \
-e NACOS_GROUP="DEFAULT_GROUP" \
-e NACOS_PROFILE="test" \
-e NACOS_NAMESPACE="public" \
-v /etc/localtime:/etc/localtime \
--restart=always \
-v /home/smartenv/logs/smartenv-person:/smartenv/person/target/blade/log \
--name smartenv-person smartenv-person

docker ps | grep smartenv-person