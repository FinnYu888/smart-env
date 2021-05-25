#!/usr/bin/env bash

rm -f smartenv-job-service.jar
sh stop.sh
docker rmi -f smartenv-job
cp ../../../target/smartenv-job-service.jar ./
docker build --force-rm -t smartenv-job ./