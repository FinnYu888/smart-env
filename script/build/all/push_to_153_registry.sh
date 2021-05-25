#!/bin/bash

#local registry info
local_registry='10.21.35.126:8082'
local_username='bssprd'
local_password='bssprd'

#aliyun registry info
aliyun_registry='registry.cn-zhangjiakou.aliyuncs.com'
aliyun_username='亚信创新'
aliyun_password='asiainfo.sg'

env_id=$1
img_version=$2

if [ "$env_id" != "sit" ]&&[ "$env_id" != "prod" ];then
  echo "The env id is not correct. The env id should be sit or prod"
  exit 1
fi

if [ ! $img_version ];then
  echo "The image version is should not be empty."
  exit 1
fi

#delete all local registry images tags
docker rmi $(docker images | grep $local_registry|awk '{print $1":"$2}')
#delete all aliyun registry images tags
docker rmi $(docker images | grep $aliyun_registry|awk '{print $1":"$2}')


#all smartenv image list
img_list=`docker images | grep 'smartenv-'|awk '{print $1}'`

#push images to local registry
echo "***************************Start to push images to local registry: " $local_registry
docker login --username=$local_username --password=$local_password $local_registry
for img in ${img_list[@]}; do
docker tag $img:latest $local_registry/smartenv/$env_id/$img:$img_version
docker push $local_registry/smartenv/$env_id/$img:$img_version
if [ $? -ne 0 ];then
  echo "$img is pushed failed. please check."
  exit 1
fi
done
#delete all local registry images tags
docker rmi $(docker images | grep $local_registry|awk '{print $1":"$2}')

