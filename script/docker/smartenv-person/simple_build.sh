#!/usr/bin/env bash

rm -f smartenv-person-service.jar
sh stop.sh
docker rmi -f smartenv-person
cp ../../../target/smartenv-person-service.jar ./
docker build --force-rm -t smartenv-person ./