FROM --platform=linux/amd64 amazoncorretto:17-alpine

WORKDIR /logs
WORKDIR /app

COPY tbot-ruler-console.jar /app/

EXPOSE 4140:4140

ENTRYPOINT ["java", "-jar", "/app/tbot-ruler-console.jar"]