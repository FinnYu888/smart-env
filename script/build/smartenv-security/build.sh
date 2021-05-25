#!/usr/bin/env bash

rm -f smartenv-security-service.jar
sh stop.sh
docker rmi -f smartenv-security
cp ../../../target/smartenv-security-service.jar ./
docker build --force-rm -t smartenv-security ./
sh start.sh