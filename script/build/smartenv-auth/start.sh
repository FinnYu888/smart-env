#!/usr/bin/env bash

#!/usr/bin/env bash

docker rm -f smartenv-auth
docker run -d -p 8100:8100 \
--privileged=true \
-e JVM_OPT="-Xmx512m" -m 1024m \
-e NACOS_ADDR="10.21.35.126:8848" \
-e NACOS_GROUP="DEFAULT_GROUP" \
-e NACOS_PROFILE="test" \
-e NACOS_NAMESPACE="public" \
-v /etc/localtime:/etc/localtime:ro \
--restart=always \
-v /home/smartenv/logs/smartenv-auth:/smartenv/auth/target/blade/log \
--name smartenv-auth smartenv-auth

docker ps | grep smartenv-auth