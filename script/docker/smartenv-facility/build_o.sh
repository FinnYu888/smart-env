#!/usr/bin/env bash

rm -f smartenv-facility-service.jar

docker rmi -f smartenv-facility
cp ../../../target/smartenv-facility-service.jar ./
docker build --force-rm -t smartenv-facility ./

