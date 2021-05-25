#!/usr/bin/env bash

cd ..
rm -rf target
mvn clean package -U -Dmaven.test.skip=true

