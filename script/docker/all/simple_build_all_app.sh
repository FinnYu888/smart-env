#!/usr/bin/env bash

cd ../../../
rm -rf target
mvn clean package -U -Dmaven.test.skip=true

cd script/docker/smartenv-address/
sh simple_build.sh
cd ../../../

cd script/docker/smartenv-alarm/
sh simple_build.sh
cd ../../../

cd script/docker/smartenv-arrange/
sh simple_build.sh
cd ../../../

cd script/docker/smartenv-assessment/
sh simple_build.sh
cd ../../../

cd script/docker/smartenv-auth/
sh simple_build.sh
cd ../../../

cd script/docker/smartenv-cache/
sh simple_build.sh
cd ../../../

cd script/docker/smartenv-device/
sh simple_build.sh
cd ../../../

cd script/docker/smartenv-event/
sh simple_build.sh
cd ../../../

cd script/docker/smartenv-facility/
sh simple_build.sh
cd ../../../

cd script/docker/smartenv-flow/
sh simple_build.sh
cd ../../../

cd script/docker/smartenv-gateway/
sh simple_build.sh
cd ../../../

cd script/docker/smartenv-inventory/
sh simple_build.sh
cd ../../../

cd script/docker/smartenv-job/
sh simple_build.sh
cd ../../../

cd script/docker/smartenv-log/
sh simple_build.sh
cd ../../../

cd script/docker/smartenv-omnic/
sh simple_build.sh
cd ../../../

cd script/docker/smartenv-oss/
sh simple_build.sh
cd ../../../

cd script/docker/smartenv-person/
sh simple_build.sh
cd ../../../

cd script/docker/smartenv-pushc/
sh simple_build.sh
cd ../../../

cd script/docker/smartenv-system/
sh simple_build.sh
cd ../../../

cd script/docker/smartenv-user/
sh simple_build.sh
cd ../../../

cd script/docker/smartenv-vehicle/
sh simple_build.sh
cd ../../../

cd script/docker/smartenv-websocket/
sh simple_build.sh
cd ../../../

cd script/docker/smartenv-wechat/
sh simple_build.sh
cd ../../../

cd script/docker/smartenv-workarea/
sh simple_build.sh
cd ../../../

cd script/docker/smartenv-green/
sh simple_build.sh
cd ../../../

cd script/docker/smartenv-security/
sh simple_build.sh
cd ../../../