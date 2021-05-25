#!/usr/bin/env bash

rm -f smartenv-inventory-service.jar

docker rmi -f smartenv-inventory
cp ../../../target/smartenv-inventory-service.jar ./
docker build --force-rm -t smartenv-inventory ./

