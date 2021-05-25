FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER qianlong@asiainfo.sg

RUN mkdir -p /smartenv/wechat

WORKDIR /smartenv/wechat

EXPOSE 8123

COPY smartenv-wechat-service.jar ./app.jar
COPY docker-entrypoint.sh ./

RUN chmod +x /smartenv/wechat/docker-entrypoint.sh

ENV NACOS_ADDR "127.0.0.1:8848"
ENV NACOS_GROUP "DEFAULT_GROUP"
ENV NACOS_NAMESPACE "public"
ENV NACOS_PROFILE "test"


#ENTRYPOINT ["java", "-DAppName=smartenv-wechat -Djava.security.egd=file:/dev/./urandom -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5555 ", "-Duser.timezone=GMT+08","-jar", "app.jar"]
#CMD ["--spring.profiles.active=dev"]

ENTRYPOINT ["/smartenv/wechat/docker-entrypoint.sh"]
