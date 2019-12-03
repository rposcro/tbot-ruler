FROM openjdk:8u151-jre-alpine3.7

WORKDIR /app
COPY src/sh/run-ruler.sh /app/
COPY target/tbot-ruler.jar /app/
COPY target/ruler-config /config

EXPOSE 4040 4041

ENTRYPOINT ["./run-ruler.sh"]
