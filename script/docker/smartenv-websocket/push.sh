#!/bin/sh
docker login -u bssprd -p bssprd 10.21.14.153:8082
docker tag smartenv-websocket:latest 10.21.14.153:8082/iot/smartenv-websocket:latest
docker push 10.21.14.153:8082/iot/smartenv-websocket:latest && docker rmi 10.21.14.153:8082/iot/smartenv-websocket:latest