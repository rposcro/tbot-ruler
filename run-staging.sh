#!/bin/bash

CURRDIR="${0%/*}"
WORKDIR="`cd "$CURRDIR" && echo "$(pwd -P)"`/ignore/staging"

if [ -z "$WORKDIR" ]; then
  echo "Something went wrong, no work dir set"
  exit
fi
echo Workdir is $WORKDIR

rm -rf $WORKDIR
mkdir $WORKDIR
mkdir $WORKDIR/logs
mkdir $WORKDIR/config
mkdir $WORKDIR/cache

cp -r ignore/config/config-stag/* $WORKDIR/config/

CONTAINER_ID=$(docker ps --all --quiet --filter name=ruler)
if [ $CONTAINER_ID ]
  then
    echo "removing existing container $CONTAINER_ID"
    docker rm -f $CONTAINER_ID
fi

docker run --name ruler \
  --publish 4041:4041 \
  --device /dev/$(readlink /dev/cu.usbmodem141201):/dev/jwavez \
  --volume $WORKDIR/logs:/logs \
  --volume $WORKDIR/config:/config \
  --volume $WORKDIR/cache:/cache \
  --env SPRING_PROFILES_ACTIVE=stag \
  --env SPRING_CONFIG_NAME=tbot-ruler \
  --env SPRING_CONFIG_LOCATION=classpath:/ \
  --env LOGS_PATH=/logs \
  --env TBOT_LOGGING_LEVEL=DEBUG \
  --restart=always \
  --detach \
  tbot-ruler:latest

#  --device /dev/cu.usbmodem141201:/dev/jwavez \
