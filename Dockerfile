FROM openjdk:8u151-jre-alpine3.7

WORKDIR /app
COPY target/tbot-ruler-1.1.0-SNAPSHOT.jar /app/
COPY target/ruler-config /config

EXPOSE 4040 4041

ENTRYPOINT ["java", "-Duser.timezone=Europe/Warsaw", "-jar", "tbot-ruler-1.1.0-SNAPSHOT.jar", "&"]
