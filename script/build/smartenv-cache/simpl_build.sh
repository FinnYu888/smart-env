#!/usr/bin/env bash

rm -f smartenv-cache-service.jar
sh stop.sh
docker rmi -f smartenv-cache
cp ../../../target/smartenv-cache-service.jar ./
docker build --force-rm -t smartenv-cache ./