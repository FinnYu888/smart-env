#!/usr/bin/env bash

rm -f smartenv-user-service.jar
sh stop.sh
docker rmi -f smartenv-user
cp ../../../target/smartenv-user-service.jar ./
docker build --force-rm -t smartenv-user ./
sh start.sh