#!/bin/bash

#local registry info
local_registry='10.21.14.153:8082'
local_username='bssprd'
local_password='bssprd'

#aliyun registry info
aliyun_registry='registry.cn-zhangjiakou.aliyuncs.com'
aliyun_username='亚信创新'
aliyun_password='asiainfo.sg'

img_version=$1

if [ ! $img_version ];then
  img_version='1.0'
fi

#delete all local registry images tags
docker rmi $(docker images | grep $local_registry|awk '{print $1":"$2}')
#delete all aliyun images tags
docker rmi $(docker images | grep $aliyun_registry|awk '{print $1":"$2}')

#all smartenv image list
img_list=`docker images | grep 'smartenv-'|awk '{print $1}'`

#push images to local registry
echo "***************************Start to push images to local registry: " $local_registry
docker login --username=$local_username --password=$local_password $local_registry
for img in ${img_list[@]}; do
docker tag $img:latest $local_registry/smartenv/$img:$img_version
docker push $local_registry/smartenv/$img:$img_version
if [ $? -ne 0 ];then
  echo "$img is pushed failed. please check."
  exit 1
fi
done
#delete all local registry images tags
docker rmi $(docker images | grep $local_registry|awk '{print $1":"$2}')

#push images to aliyun registry
echo "***************************Start to push images to aliyun registry: " $aliyun_registry
#docker login --username=$aliyun_username --password=$aliyun_password $aliyun_registry
docker login --username=亚信创新 --password=asiainfo.sg registry.cn-zhangjiakou.aliyuncs.com
for img in ${img_list[@]}; do
img_tag=$img-$img_version
docker tag $img:latest $aliyun_registry/asiainfo_apac/smartenv:$img_tag
docker push $aliyun_registry/asiainfo_apac/smartenv:$img_tag
if [ $? -ne 0 ];then
  echo "$img is pushed failed. please check."
  exit 1
fi
done
#delete all aliyun images tags
docker rmi $(docker images | grep $aliyun_registry|awk '{print $1":"$2}')