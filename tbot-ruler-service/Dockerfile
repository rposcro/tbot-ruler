FROM --platform=linux/amd64 amazoncorretto:17-alpine

WORKDIR /data
WORKDIR /logs
WORKDIR /app

COPY run-ruler-service.sh /app/
COPY tbot-ruler-service.jar /app/

EXPOSE 4040 4041

ENTRYPOINT ["./run-ruler-service.sh"]
