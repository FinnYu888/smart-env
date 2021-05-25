#!/usr/bin/env bash

rm -f smartenv-alarm-service.jar

docker rmi -f smartenv-alarm
cp ../../../target/smartenv-alarm-service.jar ./
docker build --force-rm -t smartenv-alarm ./

