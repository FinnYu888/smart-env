#!/usr/bin/env bash

docker rmi -f basejava
docker build --force-rm -t basejava ./