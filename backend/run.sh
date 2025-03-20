#!/bin/sh

docker-compose up -d
./gradlew build -x test
./gradlew assemble
./gradlew bootRun -x test
