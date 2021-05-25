#!/usr/bin/env bash

cd ../../../
rm -rf target
mvn clean package -U -Dmaven.test.skip=true

cd script/build/smartenv-address/
sh build.sh
cd ../../../

cd script/build/smartenv-alarm/
sh build.sh
cd ../../../

cd script/build/smartenv-arrange/
sh build.sh
cd ../../../

cd script/build/smartenv-assessment/
sh build.sh
cd ../../../

cd script/build/smartenv-auth/
sh build.sh
cd ../../../

cd script/build/smartenv-cache/
sh build.sh
cd ../../../

cd script/build/smartenv-device/
sh build.sh
cd ../../../

cd script/build/smartenv-event/
sh build.sh
cd ../../../

cd script/build/smartenv-facility/
sh build.sh
cd ../../../

cd script/build/smartenv-flow/
sh build.sh
cd ../../../

cd script/build/smartenv-gateway/
sh build.sh
cd ../../../

cd script/build/smartenv-inventory/
sh build.sh
cd ../../../

cd script/build/smartenv-statistic/
sh build.sh
cd ../../../

cd script/build/smartenv-job/
sh build.sh
cd ../../../

cd script/build/smartenv-log/
sh build.sh
cd ../../../

cd script/build/smartenv-omnic/
sh build.sh
cd ../../../

cd script/build/smartenv-oss/
sh build.sh
cd ../../../

cd script/build/smartenv-person/
sh build.sh
cd ../../../

cd script/build/smartenv-pushc/
sh build.sh
cd ../../../

cd script/build/smartenv-system/
sh build.sh
cd ../../../

cd script/build/smartenv-user/
sh build.sh
cd ../../../

cd script/build/smartenv-vehicle/
sh build.sh
cd ../../../

cd script/build/smartenv-websocket/
sh build.sh
cd ../../../

cd script/build/smartenv-wechat/
sh build.sh
cd ../../../

cd script/build/smartenv-workarea/
sh build.sh
cd ../../../

cd script/build/smartenv-green/
sh build.sh
cd ../../../

cd script/build/smartenv-security/
sh build.sh
cd ../../../