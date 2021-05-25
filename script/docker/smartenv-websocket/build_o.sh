#!/usr/bin/env bash

rm -f smartenv-websocket-service.jar

docker rmi -f smartenv-websocket
cp ../../../target/smartenv-websocket-service.jar ./
docker build --force-rm -t smartenv-websocket ./

