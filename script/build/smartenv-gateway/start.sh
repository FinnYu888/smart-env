#!/usr/bin/env bash

#!/usr/bin/env bash

docker rm -f smartenv-gateway
docker run -d -p 28303:80 \
--privileged=true \
-e JVM_OPT="-Xmx512m" -m 1024m \
-e NACOS_ADDR="10.21.35.126:8848" \
-e NACOS_GROUP="DEFAULT_GROUP" \
-e NACOS_PROFILE="test" \
-e NACOS_NAMESPACE="public" \
-v /etc/localtime:/etc/localtime:ro \
--restart=always \
-v /home/smartenv/logs/smartenv-gateway:/smartenv/gateway/target/blade/log \
--name smartenv-gateway smartenv-gateway

docker ps | grep smartenv-gateway