#!/usr/bin/env bash

rm -f smartenv-oss-service.jar

docker rmi -f smartenv-oss
cp ../../../target/smartenv-oss-service.jar ./
docker build --force-rm -t smartenv-oss ./

