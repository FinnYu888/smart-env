#!/usr/bin/env bash

cd ../../../
rm -rf target
mvn clean package -U -Dmaven.test.skip=true

cd script/build/smartenv-address/
sh build_o.sh
cd ../../../

cd script/build/smartenv-alarm/
sh build_o.sh
cd ../../../

cd script/build/smartenv-arrange/
sh build_o.sh
cd ../../../

cd script/build/smartenv-assessment/
sh build_o.sh
cd ../../../

cd script/build/smartenv-auth/
sh build_o.sh
cd ../../../

cd script/build/smartenv-cache/
sh build_o.sh
cd ../../../

cd script/build/smartenv-device/
sh build_o.sh
cd ../../../

cd script/build/smartenv-event/
sh build_o.sh
cd ../../../

cd script/build/smartenv-facility/
sh build_o.sh
cd ../../../

cd script/build/smartenv-flow/
sh build_o.sh
cd ../../../

cd script/build/smartenv-gateway/
sh build_o.sh
cd ../../../

cd script/build/smartenv-inventory/
sh build_o.sh
cd ../../../

cd script/build/smartenv-job/
sh build_o.sh
cd ../../../

cd script/build/smartenv-log/
sh build_o.sh
cd ../../../

cd script/build/smartenv-omnic/
sh build_o.sh
cd ../../../

cd script/build/smartenv-oss/
sh build_o.sh
cd ../../../

cd script/build/smartenv-person/
sh build_o.sh
cd ../../../

cd script/build/smartenv-pushc/
sh build_o.sh
cd ../../../

cd script/build/smartenv-system/
sh build_o.sh
cd ../../../

cd script/build/smartenv-user/
sh build_o.sh
cd ../../../

cd script/build/smartenv-vehicle/
sh build_o.sh
cd ../../../

cd script/build/smartenv-websocket/
sh build_o.sh
cd ../../../

cd script/build/smartenv-wechat/
sh build_o.sh
cd ../../../

cd script/build/smartenv-workarea/
sh build_o.sh
cd ../../../

cd script/build/smartenv-green/
sh build_o.sh
cd ../../../

cd script/build/smartenv-security/
sh build_o.sh
cd ../../../