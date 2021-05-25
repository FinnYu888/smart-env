FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER qianlong@asiainfo.sg

RUN mkdir -p /usr/share/fonts

#拷贝字体文件
COPY simhei.ttf simsun.ttf /usr/share/fonts/

#设置字符集
ENV LANG C.UTF-8

#安装字体软件，完成字体配置
RUN apk add --update ttf-dejavu fontconfig && rm -rf /var/cache/apk/*