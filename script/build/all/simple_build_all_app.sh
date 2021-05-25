#!/usr/bin/env bash

cd ../../../
rm -rf target
mvn clean package -U -Dmaven.test.skip=true

cd script/build/smartenv-address/
sh simple_build.sh
cd ../../../

cd script/build/smartenv-alarm/
sh simple_build.sh
cd ../../../

cd script/build/smartenv-arrange/
sh simple_build.sh
cd ../../../

cd script/build/smartenv-assessment/
sh simple_build.sh
cd ../../../

cd script/build/smartenv-auth/
sh simple_build.sh
cd ../../../

cd script/build/smartenv-cache/
sh simple_build.sh
cd ../../../

cd script/build/smartenv-device/
sh simple_build.sh
cd ../../../

cd script/build/smartenv-event/
sh simple_build.sh
cd ../../../

cd script/build/smartenv-facility/
sh simple_build.sh
cd ../../../

cd script/build/smartenv-flow/
sh simple_build.sh
cd ../../../

cd script/build/smartenv-gateway/
sh simple_build.sh
cd ../../../

cd script/build/smartenv-inventory/
sh simple_build.sh
cd ../../../

cd script/build/smartenv-job/
sh simple_build.sh
cd ../../../

cd script/build/smartenv-log/
sh simple_build.sh
cd ../../../

cd script/build/smartenv-omnic/
sh simple_build.sh
cd ../../../

cd script/build/smartenv-oss/
sh simple_build.sh
cd ../../../

cd script/build/smartenv-person/
sh simple_build.sh
cd ../../../

cd script/build/smartenv-pushc/
sh simple_build.sh
cd ../../../

cd script/build/smartenv-system/
sh simple_build.sh
cd ../../../

cd script/build/smartenv-user/
sh simple_build.sh
cd ../../../

cd script/build/smartenv-vehicle/
sh simple_build.sh
cd ../../../

cd script/build/smartenv-websocket/
sh simple_build.sh
cd ../../../

cd script/build/smartenv-wechat/
sh simple_build.sh
cd ../../../

cd script/build/smartenv-workarea/
sh simple_build.sh
cd ../../../

cd script/build/smartenv-green/
sh simple_build.sh
cd ../../../

cd script/build/smartenv-security/
sh simple_build.sh
cd ../../../