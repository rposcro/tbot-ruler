#!/bin/bash
docker build -t tbot-ruler .
docker save --output target/tbot-ruler.docker.tar tbot-ruler:latest
