#!/bin/sh
docker login -u bssprd -p bssprd 10.21.14.153:8082
docker tag smartenv-address:latest 10.21.14.153:8082/iot/smartenv-address:latest
docker push 10.21.14.153:8082/iot/smartenv-address:latest && docker rmi 10.21.14.153:8082/iot/smartenv-address:latest