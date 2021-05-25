#!/usr/bin/env bash

img_name='smartenv-flow-design'
img_registry='10.21.35.126:8082'
img_id="$img_registry/smartenv/$img_name:1.0"

c_exist=`docker ps -a|grep $img_name|awk '{print $1}'`
if [ $c_exist ];then
docker rm -f $c_exist
fi

docker login -u bssprd -p bssprd $img_registry

docker pull $img_id

docker run -d -p 8121:8121 \
--privileged=true \
-e JVM_OPT="-Xmx512m" -m 1024m \
-e NACOS_ADDR="10.21.35.126:8848" \
-e NACOS_GROUP="DEFAULT_GROUP" \
-e NACOS_PROFILE="test" \
-e NACOS_NAMESPACE="public" \
-v /etc/localtime:/etc/localtime:ro \
--restart=always \
-v /home/smartenv/logs/$img_name:/smartenv/$img_name/target/blade/log \
--name $img_name $img_id

docker ps | grep $img_name