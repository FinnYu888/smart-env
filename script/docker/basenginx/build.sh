#/bin/sh

docker rmi -f smartenv/basenginx
docker rmi $(docker images --filter dangling=true -q)
docker build --force-rm --no-cache -t="smartenv/basenginx" .
docker images | grep smartenv/basenginx
