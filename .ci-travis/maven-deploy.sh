#!/bin/bash

ARTIFACTORY_USERNAME=$1
ARTIFACTORY_PASSWORD=$2

echo "Using artifactory username: $ARTIFACTORY_USERNAME..."
echo "Using artifactory password: $ARTIFACTORY_PASSWORD..."

echo "Building and deploying Maven and docker artifacts..."
./mvnw deploy -Pdocker -DskipITs=true -Ddockerfile.username=${ARTIFACTORY_USERNAME} -Ddockerfile.password=${ARTIFACTORY_PASSWORD};

