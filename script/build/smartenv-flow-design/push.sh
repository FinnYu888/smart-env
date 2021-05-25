#!/bin/sh

img_name='smartenv-flow-design'
img_registry='10.21.35.126:8082'
img_id="$img_registry/smartenv/$img_name:1.0"

docker login -u bssprd -p bssprd $img_registry
docker tag $img_name:latest $img_id
docker push $img_id && docker rmi $img_id