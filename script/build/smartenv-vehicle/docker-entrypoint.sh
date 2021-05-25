#!/bin/sh

java -DAppName=smartenv-vehicle -Djava.security.egd=file:/dev/./urandom -DNACOS_ADDR=${NACOS_ADDR} -DNACOS_GROUP=${NACOS_GROUP} -Duser.timezone=GMT+08 -jar app.jar --spring.profiles.active=${NACOS_PROFILE}
