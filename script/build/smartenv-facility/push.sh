#!/bin/sh
docker login -u bssprd -p bssprd 10.21.35.126:8082
docker tag smartenv-facility:latest 10.21.35.126:8082/iot/smartenv-facility:latest
docker push 10.21.35.126:8082/iot/smartenv-facility:latest && docker rmi 10.21.35.126:8082/iot/smartenv-facility:latest