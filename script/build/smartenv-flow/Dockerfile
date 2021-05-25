FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER qianlong@asiainfo.sg

RUN mkdir -p /smartenv/flow

WORKDIR /smartenv/flow

EXPOSE 8120

COPY smartenv-flow-service.jar ./app.jar
COPY docker-entrypoint.sh ./

RUN chmod +x /smartenv/flow/docker-entrypoint.sh

ENV NACOS_ADDR "127.0.0.1:8848"
ENV NACOS_GROUP "DEFAULT_GROUP"
ENV NACOS_NAMESPACE "public"
ENV NACOS_PROFILE "test"


#ENTRYPOINT ["java", "-DAppName=smartenv-flow -Djava.security.egd=file:/dev/./urandom -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5555 ", "-Duser.timezone=GMT+08","-jar", "app.jar"]
#CMD ["--spring.profiles.active=dev"]

ENTRYPOINT ["/smartenv/flow/docker-entrypoint.sh"]
