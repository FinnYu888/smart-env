#!/usr/bin/env bash

rm -f smartenv-event-service.jar

docker rmi -f smartenv-event
cp ../../../target/smartenv-event-service.jar ./
docker build --force-rm -t smartenv-event ./

