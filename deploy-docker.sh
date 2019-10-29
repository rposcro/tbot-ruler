#!/bin/bash

REF_SCRIPT=./ignore/bash/deploy-docker.sh

if [ -f $REF_SCRIPT ]; then
  $REF_SCRIPT
else
  echo "$REF_SCRIPT doesn't exists"
fi

