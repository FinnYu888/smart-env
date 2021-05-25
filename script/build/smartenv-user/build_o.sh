#!/usr/bin/env bash

rm -f smartenv-user-service.jar

docker rmi -f smartenv-user
cp ../../../target/smartenv-user-service.jar ./
docker build --force-rm -t smartenv-user ./

