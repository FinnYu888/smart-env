#!/usr/bin/env bash

cd ../../../
rm -rf target
mvn clean package -U -Dmaven.test.skip=true

cd script/docker/smartenv-address/
sh build_o.sh
cd ../../../

cd script/docker/smartenv-alarm/
sh build_o.sh
cd ../../../

cd script/docker/smartenv-arrange/
sh build_o.sh
cd ../../../

cd script/docker/smartenv-assessment/
sh build_o.sh
cd ../../../

cd script/docker/smartenv-auth/
sh build_o.sh
cd ../../../

cd script/docker/smartenv-cache/
sh build_o.sh
cd ../../../

cd script/docker/smartenv-device/
sh build_o.sh
cd ../../../

cd script/docker/smartenv-event/
sh build_o.sh
cd ../../../

cd script/docker/smartenv-facility/
sh build_o.sh
cd ../../../

cd script/docker/smartenv-flow/
sh build_o.sh
cd ../../../

cd script/docker/smartenv-gateway/
sh build_o.sh
cd ../../../

cd script/docker/smartenv-inventory/
sh build_o.sh
cd ../../../

cd script/docker/smartenv-job/
sh build_o.sh
cd ../../../

cd script/docker/smartenv-log/
sh build_o.sh
cd ../../../

cd script/docker/smartenv-omnic/
sh build_o.sh
cd ../../../

cd script/docker/smartenv-oss/
sh build_o.sh
cd ../../../

cd script/docker/smartenv-person/
sh build_o.sh
cd ../../../

cd script/docker/smartenv-pushc/
sh build_o.sh
cd ../../../

cd script/docker/smartenv-system/
sh build_o.sh
cd ../../../

cd script/docker/smartenv-user/
sh build_o.sh
cd ../../../

cd script/docker/smartenv-vehicle/
sh build_o.sh
cd ../../../

cd script/docker/smartenv-websocket/
sh build_o.sh
cd ../../../

cd script/docker/smartenv-wechat/
sh build_o.sh
cd ../../../

cd script/docker/smartenv-workarea/
sh build_o.sh
cd ../../../

cd script/docker/smartenv-green/
sh build_o.sh
cd ../../../

cd script/docker/smartenv-security/
sh build_o.sh
cd ../../../