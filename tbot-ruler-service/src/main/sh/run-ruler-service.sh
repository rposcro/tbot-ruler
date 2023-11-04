#!/bin/sh
# This script is an entrypoint for Ruler's docker image.

# When crashed, dongle device lock is left and due to a bug in neurotics lib, serial port cannot get connected
echo cleaning up dongle device locks
rm -f /var/lock/LCK..jwavez*

# And now go on with the service
echo starting tbot ruler service
java -Duser.timezone=Europe/Warsaw -jar tbot-ruler-service.jar
