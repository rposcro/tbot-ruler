FROM openjdk:8u151-jre-alpine3.7

WORKDIR /cache
WORKDIR /app

COPY run-ruler.sh /app/
COPY tbot-ruler.jar /app/

EXPOSE 4040 4041

ENTRYPOINT ["./run-ruler.sh"]
