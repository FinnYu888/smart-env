#!/usr/bin/env bash

rm -f smartenv-green-service.jar
sh stop.sh
docker rmi -f smartenv-green
cp ../../../target/smartenv-green-service.jar ./
docker build --force-rm -t smartenv-green ./