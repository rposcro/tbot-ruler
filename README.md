# ThingBot Ruler

## Profiles
By default profiles are pre-prepared:
 * dev  (development), listening on 4042, running from IDE
 * prod (production), listening on 4040, running in docker on production host

## Run
Note! Running docker on MacOS will fail to access USB devices due to docker's VM limitations, consider Linux based OS 
when Z-Wave or other USB devices should be connected.   

### Development
Simply run from IDE. Check `tbot-ruler-dev.yaml` for configuration paths.

### Production
* Build docker image, run `./build-docker.sh`. You may need to consider platform other than default, for example linux/arm/v7.
* Check with `tbot-host` repo for details on how to set up, deploy and run production environment.

## Logs
For running in DEBUG mode use another env variables:
 * for tbot logs add --env TBOT_LOGGING_LEVEL=DEBUG
 * for all others add --env APP_LOGGING_LEVEL=DEBUG

In order to specify logs directory add --env LOGS_PATH=<path_to_logs_dir>

If you need runnable docker image type:

`./gradlew docker`

Now, you can export the image from local docker images.
Note! Dockerfile specifies *linux/amd64* as the target platform, change it if needed.

## Dynamic Runtime

### Stopping 

# Enjoy!