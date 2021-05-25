#!/usr/bin/env bash

rm -f smartenv-log.jar

docker rmi -f blade-log
cp ../../../target/smartenv-log.jar ./
docker build --force-rm -t smartenv-log ./

