#!/bin/sh
docker login -u bssprd -p bssprd 10.21.35.126:8082
docker tag smartenv-device:latest 10.21.35.126:8082/iot/smartenv-device:latest
docker push 10.21.35.126:8082/iot/smartenv-device:latest && docker rmi 10.21.35.126:8082/iot/smartenv-device:latest