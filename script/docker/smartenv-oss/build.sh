#!/usr/bin/env bash

rm -f smartenv-oss-service.jar
sh stop.sh
docker rmi -f smartenv-oss
cp ../../../target/smartenv-oss-service.jar ./
docker build --force-rm -t smartenv-oss ./
sh start.sh