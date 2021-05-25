#!/usr/bin/env bash

rm -f smartenv-address-service.jar
sh stop.sh
docker rmi -f smartenv-address
cp ../../../target/smartenv-address-service.jar ./
docker build --force-rm -t smartenv-address ./