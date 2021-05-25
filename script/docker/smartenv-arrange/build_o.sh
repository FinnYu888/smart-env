#!/usr/bin/env bash

rm -f smartenv-arrange-service.jar

docker rmi -f smartenv-arrange
cp ../../../target/smartenv-arrange-service.jar ./
docker build --force-rm -t smartenv-arrange ./

