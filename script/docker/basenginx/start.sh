#/bin/sh

docker rm -f basenginx
docker run -d -p 8088:80 --name basenginx smartenv/basenginx
