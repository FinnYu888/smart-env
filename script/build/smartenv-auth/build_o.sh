#!/usr/bin/env bash

rm -f smartenv-auth.jar

docker rmi -f smartenv-auth
cp ../../../target/smartenv-auth.jar ./
docker build --force-rm -t smartenv-auth ./

