#!/usr/bin/env bash

rm -f smartenv-statistics-service.jar

docker rmi -f smartenv-statistics
cp ../../../target/smartenv-statistics-service.jar ./
docker build --force-rm -t smartenv-statistics ./

