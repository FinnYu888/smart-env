#!/usr/bin/env bash

rm -f smartenv-device-service.jar
sh stop.sh
docker rmi -f smartenv-device
cp ../../../target/smartenv-device-service.jar ./
docker build --force-rm -t smartenv-device ./