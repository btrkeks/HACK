#!/bin/sh

docker-compose up -d
./gradlew build -x test
./gradlew assemble
