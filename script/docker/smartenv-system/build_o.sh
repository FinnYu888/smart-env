#!/usr/bin/env bash

rm -f smartenv-system-service.jar

docker rmi -f smartenv-system
cp ../../../target/smartenv-system-service.jar ./
docker build --force-rm -t smartenv-system ./

