#!/usr/bin/env bash

rm -f smartenv-pushc-service.jar

docker rmi -f smartenv-pushc
cp ../../../target/smartenv-pushc-service.jar ./
docker build --force-rm -t smartenv-pushc ./

