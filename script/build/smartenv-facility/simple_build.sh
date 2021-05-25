#!/usr/bin/env bash

rm -f smartenv-facility-service.jar
sh stop.sh
docker rmi -f smartenv-facility
cp ../../../target/smartenv-facility-service.jar ./
docker build --force-rm -t smartenv-facility ./