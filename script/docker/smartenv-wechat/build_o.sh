#!/usr/bin/env bash

rm -f smartenv-wechat-service.jar

docker rmi -f smartenv-wechat
cp ../../../target/smartenv-wechat-service.jar ./
docker build --force-rm -t smartenv-wechat ./

