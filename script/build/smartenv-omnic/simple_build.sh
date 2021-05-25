#!/usr/bin/env bash

rm -f smartenv-omnic-service.jar
sh stop.sh
docker rmi -f smartenv-omnic
cp ../../../target/smartenv-omnic-service.jar ./
docker build --force-rm -t smartenv-omnic ./