#!/usr/bin/env bash

cd ../../../
rm -rf target
mvn clean package -U -Dmaven.test.skip=true

cd script/docker/smartenv-address/
sh build.sh
cd ../../../

cd script/docker/smartenv-alarm/
sh build.sh
cd ../../../

cd script/docker/smartenv-arrange/
sh build.sh
cd ../../../

cd script/docker/smartenv-assessment/
sh build.sh
cd ../../../

cd script/docker/smartenv-auth/
sh build.sh
cd ../../../

cd script/docker/smartenv-cache/
sh build.sh
cd ../../../

cd script/docker/smartenv-device/
sh build.sh
cd ../../../

cd script/docker/smartenv-event/
sh build.sh
cd ../../../

cd script/docker/smartenv-facility/
sh build.sh
cd ../../../

cd script/docker/smartenv-flow/
sh build.sh
cd ../../../

cd script/docker/smartenv-gateway/
sh build.sh
cd ../../../

cd script/docker/smartenv-inventory/
sh build.sh
cd ../../../

cd script/docker/smartenv-job/
sh build.sh
cd ../../../

cd script/docker/smartenv-log/
sh build.sh
cd ../../../

cd script/docker/smartenv-omnic/
sh build.sh
cd ../../../

cd script/docker/smartenv-oss/
sh build.sh
cd ../../../

cd script/docker/smartenv-person/
sh build.sh
cd ../../../

cd script/docker/smartenv-pushc/
sh build.sh
cd ../../../

cd script/docker/smartenv-system/
sh build.sh
cd ../../../

cd script/docker/smartenv-user/
sh build.sh
cd ../../../

cd script/docker/smartenv-vehicle/
sh build.sh
cd ../../../

cd script/docker/smartenv-websocket/
sh build.sh
cd ../../../

cd script/docker/smartenv-wechat/
sh build.sh
cd ../../../

cd script/docker/smartenv-workarea/
sh build.sh
cd ../../../

cd script/docker/smartenv-green/
sh build.sh
cd ../../../

cd script/docker/smartenv-security/
sh build.sh
cd ../../../