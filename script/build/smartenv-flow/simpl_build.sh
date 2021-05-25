#!/usr/bin/env bash

rm -f smartenv-flow-service.jar
sh stop.sh
docker rmi -f smartenv-flow
cp ../../../target/smartenv-flow-service.jar ./
docker build --force-rm -t smartenv-flow ./