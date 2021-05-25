FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER qianlong@asiainfo.sg

RUN mkdir -p /smartenv/device

WORKDIR /smartenv/device

EXPOSE 8108

COPY smartenv-device-service.jar ./app.jar
COPY docker-entrypoint.sh ./

RUN chmod +x /smartenv/device/docker-entrypoint.sh

ENV NACOS_ADDR "127.0.0.1:8848"
ENV NACOS_GROUP "DEFAULT_GROUP"
ENV NACOS_NAMESPACE "public"
ENV NACOS_PROFILE "test"


#ENTRYPOINT ["java", "-DAppName=smartenv-device -Djava.security.egd=file:/dev/./urandom -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5555 ", "-Duser.timezone=GMT+08","-jar", "app.jar"]
#CMD ["--spring.profiles.active=dev"]

ENTRYPOINT ["/smartenv/device/docker-entrypoint.sh"]
