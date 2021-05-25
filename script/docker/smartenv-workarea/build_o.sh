#!/usr/bin/env bash

rm -f smartenv-workarea-service.jar

docker rmi -f smartenv-workarea
cp ../../../target/smartenv-workarea-service.jar ./
docker build --force-rm -t smartenv-workarea ./

