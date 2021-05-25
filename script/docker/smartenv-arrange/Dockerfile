FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER qianlong@asiainfo.sg

ENV NACOS_ADDR "127.0.0.1:8848"
ENV NACOS_GROUP "DEFAULT_GROUP"
ENV NACOS_NAMESPACE "public"
ENV NACOS_PROFILE "test"

RUN mkdir -p /smartenv/arrange

WORKDIR /smartenv/arrange

EXPOSE 8115

COPY smartenv-arrange-service.jar ./app.jar
COPY docker-entrypoint.sh ./

RUN chmod +x /smartenv/arrange/docker-entrypoint.sh

#ENTRYPOINT ["java", "-DAppName=smartenv-arrange -Djava.security.egd=file:/dev/./urandom -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5555 ", "-Duser.timezone=GMT+08","-jar", "app.jar"]
#ENTRYPOINT ["java", "-DAppName=smartenv-arrange -Djava.security.egd=file:/dev/./urandom -DNACOS_ADDR=10.21.33.235:8848 -DNACOS_GROUP=PRE_PROD_GROUP ","-Duser.timezone=GMT+08","-jar", "app.jar"]
#CMD ["--spring.profiles.active=dev"]

ENTRYPOINT ["/smartenv/arrange/docker-entrypoint.sh"]