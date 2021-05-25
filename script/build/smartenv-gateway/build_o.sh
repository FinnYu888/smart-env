#!/usr/bin/env bash

rm -f smartenv-gateway.jar

docker rmi -f smartenv-gateway
cp ../../../target/smartenv-gateway.jar ./
docker build --force-rm -t smartenv-gateway ./

