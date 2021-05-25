#!/usr/bin/env bash

docker run --rm -it \
    -v /var/run/docker.sock:/var/run/docker.sock \
    -v /etc/localtime:/etc/localtime \
    -v /:/host:ro \
    --cap-add SYS_ADMIN \
    -e LOGGING_OUTPUT=elasticsearch \
    -e ELASTICSEARCH_HOST=10.21.14.149 \
    -e ELASTICSEARCH_PORT=9200 \
    -d --name log-pilot registry.cn-hangzhou.aliyuncs.com/acs/log-pilot:0.9.7-filebeat