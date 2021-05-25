#!/usr/bin/env bash

rm -f smartenv-log.jar
sh stop.sh
docker rmi -f blade-log
cp ../../../target/smartenv-log.jar ./
docker build --force-rm -t smartenv-log ./