#!/usr/bin/env bash

rm -f smartenv-pushc-service.jar
sh stop.sh
docker rmi -f smartenv-pushc
cp ../../../target/smartenv-pushc-service.jar ./
docker build --force-rm -t smartenv-pushc ./
sh start.sh