#!/usr/bin/env bash

rm -f smartenv-assessment-service.jar
sh stop.sh
docker rmi -f smartenv-assessment
cp ../../../target/smartenv-assessment-service.jar ./
docker build --force-rm -t smartenv-assessment ./