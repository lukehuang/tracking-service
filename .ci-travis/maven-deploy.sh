#!/bin/bash

export ARTIFACTORY_USERNAME=$1
export ARTIFACTORY_PASSWORD=$2

echo "Using artifactory username: $ARTIFACTORY_USERNAME..."
echo "Using artifactory password: $ARTIFACTORY_PASSWORD..."

echo "Building and deploying Maven and docker artifacts..."
./mvnw deploy --settings ./.mvn/wrapper/settings.xml -Pdocker -DskipITs=true -Ddockerfile.username=${ARTIFACTORY_USERNAME} -Ddockerfile.password=${ARTIFACTORY_PASSWORD};

