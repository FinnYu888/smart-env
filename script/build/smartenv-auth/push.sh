#!/bin/sh
docker login -u bssprd -p bssprd 10.21.35.126:8082
docker tag smartenv-auth:latest 10.21.35.126:8082/iot/smartenv-auth:latest
docker push 10.21.35.126:8082/iot/smartenv-auth:latest && docker rmi 10.21.35.126:8082/iot/smartenv-auth:latest