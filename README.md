# ThingBot Ruler

## Profiles
By default 3 profiles are pre-prepared:
 * dev  (development), listening on 4042
 * stag (staging), listening on 4041
 * prod (production), listening on 4040

# Docker Based Aproach
## Building
Regular docker build is just about `docker build -t tbot-ruler .`

Another option is to build and save image by using included build script `build-docker.sh`

## Running
To run staging profile use command below. For --publish use ports you want to really expose to the world.
   `docker run --publish 4041:4041 --env SPRING_PROFILES_ACTIVE=stag tbot-ruler:latest`

For running in DEBUG mode use another env variables:
 * for tbot logs add --env TBOT_LOGGING_LEVEL=DEBUG
 * for all others add --env APP_LOGGING_LEVEL=DEBUG

In order to specify logs directory add --env LOGS_PATH=<path_to_logs_dir>
In order to map host directory to container's one use option: --volume=<host_dir>:<container_dir>
For running in background use --detach option
It's good to launch it when docker (or all box) reboots, so use: --restart=always

Example:<br>
`docker run --publish 4041:4041 --env SPRING_PROFILES_ACTIVE=stag --env TBOT_LOGGING_LEVEL=DEBUG --env LOGS_PATH=/logs --volume=/var/log/tbot:/logs --restart=always --detach tbot-ruler:latest`

## Export Ruler Image
`docker save tbot-ruler:latest --output tbot-ruler.docker.tar`

## Import Ruler image
Just send exported image to any destination machine, next run
`docker load --input tbot-ruler.docker.tar`

## Attach to Running Container
`docker exec -i -t <container-id> /bin/sh`

# Enjoy!