#!/usr/bin/env bash

rm -f smartenv-system-service.jar
sh stop.sh
docker rmi -f smartenv-system
cp ../../../target/smartenv-system-service.jar ./
docker build --force-rm -t smartenv-system ./
sh start.sh