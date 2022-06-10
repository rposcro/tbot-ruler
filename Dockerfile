FROM --platform=linux/amd64 openjdk:13-alpine3.9

WORKDIR /cache
WORKDIR /app

COPY run-ruler.sh /app/
COPY tbot-ruler.jar /app/

EXPOSE 4040 4041

ENTRYPOINT ["./run-ruler.sh"]
