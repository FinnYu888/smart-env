#!/bin/sh
docker login -u bssprd -p bssprd 10.21.35.126:8082
docker tag smartenv-alarm:latest 10.21.35.126:8082/iot/smartenv-alarm:latest
docker push 10.21.35.126:8082/iot/smartenv-alarm:latest && docker rmi 10.21.35.126:8082/iot/smartenv-alarm:latest