#!/bin/sh
docker login -u bssprd -p bssprd 10.21.14.153:8082
docker tag smartenv-statistics:latest 10.21.14.153:8082/iot/smartenv-statistics:latest
docker push 10.21.14.153:8082/iot/smartenv-statistics:latest && docker rmi 10.21.14.153:8082/iot/smartenv-statistics:latest