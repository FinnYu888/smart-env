#!/usr/bin/env bash

docker run -d -p 8115:8115 \
--privileged=true \
-e JVM_OPT="-Xmx512m" -m 1024m \
-e NACOS_ADDR="10.21.33.235:8848" \
-e NACOS_GROUP="DEFAULT_GROUP" \
-e NACOS_PROFILE="test" \
-e NACOS_NAMESPACE="public" \
-v /etc/localtime:/etc/localtime:ro \
--restart=always \
-v /home/smartenv/logs/smartenv-arrange:/smartenv/arrange/target/blade/log \
--name smartenv-arrange smartenv-arrange

docker ps | grep smartenv-arrange