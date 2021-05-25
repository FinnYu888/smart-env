#!/usr/bin/env bash

rm -f smartenv-flow-design.jar
sh stop.sh
docker rmi -f smartenv-flow-design
cp ../../../target/smartenv-flow-design.jar ./
docker build --force-rm -t smartenv-flow-design ./
sh start.sh