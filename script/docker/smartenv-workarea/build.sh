#!/usr/bin/env bash

rm -f smartenv-workarea-service.jar
sh stop.sh
docker rmi -f smartenv-workarea
cp ../../../target/smartenv-workarea-service.jar ./
docker build --force-rm -t smartenv-workarea ./
sh start.sh