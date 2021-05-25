#!/usr/bin/env bash

rm -f smartenv-vehicle-service.jar
sh stop.sh
docker rmi -f smartenv-vehicle
cp ../../../target/smartenv-vehicle-service.jar ./
docker build --force-rm -t smartenv-vehicle ./
sh start.sh