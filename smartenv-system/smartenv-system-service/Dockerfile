FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER qianlong@asiainfo.sg

RUN mkdir -p /smartenv/system

WORKDIR /smartenv/system

EXPOSE 8106

ADD ./target/smartenv-system-service.jar ./app.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]

CMD ["--spring.profiles.active=test"]
